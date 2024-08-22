package ifsp.project.welnessmind.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.databinding.FragmentLoginBinding
import ifsp.project.welnessmind.ui.login.LoginViewModel.LoggedInUserView
import ifsp.project.welnessmind.ui.login.LoginViewModel.UserType
import ifsp.project.welnessmind.util.SharedPreferencesUtil

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth

    private val binding get() = _binding!!

    private var isLoginAttempted = false
    private var isFromSignup = false
    private var passwordGenerated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appContext = requireContext().applicationContext
        val db = AppDatabase.getInstance(appContext)
        val factory = LoginViewModelFactory(
            patientDAO = db.patientDao,
            professionalDAO = db.professionalDao,
            patientPasswordDao = db.patientPasswordDao,
            professionalPasswordDao = db.professionalPasswordDao
        )
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        val  usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val ic_info = binding.ivInfo

        // identifica se veio de uma tela de cadastro
        isFromSignup = arguments?.getBoolean("isFromSignup") ?: false
        // Identificar o tipo de usuário a partir dos argumentos passados para o fragmento
            val userId = SharedPreferencesUtil.getUserId(requireContext())
            val professionalId = SharedPreferencesUtil.getUserId(requireContext())
            val userType = when (arguments?.getString("userType")) {
                "PACIENTE" -> UserType.PACIENTE
                "PROFISSIONAL" -> UserType.PROFISSIONAL
                else -> throw IllegalArgumentException("Tipo de usuário inválido")
            }

        ic_info.setOnClickListener {
            if (usernameEditText.text.isNullOrEmpty())
            Snackbar.make(view, "Insira seu e-mail e clique novamente para recuperar\n sua senha", Snackbar.LENGTH_LONG).show()
            else {
                loginViewModel.retrievePassword(
                    usernameEditText.text.toString(),
                    userType
                ) { password ->
                    if (password != null) {
                        Toast.makeText(appContext, "Sua senha é: $password", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(appContext, "Senha não encontrada", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.textCadastrar.setOnClickListener {
            if (userType == UserType.PACIENTE) {
                findNavController().navigate(R.id.patientFragment)
            } else if (userType == UserType.PROFISSIONAL) {
                findNavController().navigate(R.id.professionalFragment)
            }
        }

        if (userId != -1L) {
            Log.d("LoginFragment", "User ID: $userId, UserType: $userType")
            // Gerar a senha ao acessar a tela de login
            if (isFromSignup && !passwordGenerated) {
                Log.d("LoginFragment", "Usuário veio de uma tela de cadastro, gerando senha")
                Log.d("LoginFragment", "isFromSignup? ${isFromSignup}")
                loginViewModel.generatePasswordForUserId(userId, userType)
                Log.d("LoginFragment", "Usuário autenticado: $userType")
            }
        } else {
            Log.e("LoginFragment", "Erro ao recuperar o ID do usuário")
        }

        // Observa as mudanças no estado do formulário de login
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                // Habilita/desabilita o botão de login com base na validade dos dados
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let { loggedInUserView ->
                    updateUiWithUser(loggedInUserView)
                    loggedInUserView.password?.let { password ->
                        ShowPasswordDialog(password)
                        passwordGenerated = true
                        ic_info.setOnClickListener {
                            ShowPasswordDialog(password)
                        }
                    }
                    if (isLoginAttempted && userType == UserType.PACIENTE) {
                        if (!isFromSignup) {
                            SharedPreferencesUtil.getUserId(appContext)
                            val bundle = Bundle()
                            bundle.putLong("userID", userId)
                            findNavController().navigate(R.id.action_loginFragment_to_professionalListFragment, bundle)
                        } else {
                            SharedPreferencesUtil.getUserId(appContext)
                            val bundle = Bundle()
                            bundle.putLong("userID", userId)
                            findNavController().navigate(R.id.action_loginFragment_to_formsFragment, bundle)
                        }
                    }
                    else if (isLoginAttempted && userType == UserType.PROFISSIONAL) {
                        SharedPreferencesUtil.getUserId(appContext)
                        val bundle = Bundle()
                        bundle.putLong("professional_id", professionalId)
                        if (!isFromSignup) {
                            SharedPreferencesUtil.getUserId(appContext)
                            val bundle = Bundle()
                            bundle.putLong("professional_id", professionalId)
                            bundle.putBoolean("isProfessionalMode", true)
                            findNavController().navigate(R.id.action_loginFragment_to_professionalProfileFragment, bundle)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_officeRegisterFragment, bundle)
                        }
                    }
                }
                isLoginAttempted = false
            })

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            isLoginAttempted = true
            Log.d("LoginFragment", "Login button clicked, attempting login")
            loginViewModel.login(
                appContext,
                usernameEditText.text.toString(),
                passwordEditText.text.toString(),
                userType
            )
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_SHORT).show()
    }

    private fun ShowPasswordDialog(password: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Senha Gerada")
            .setMessage("Sua senha é: $password \nEssa é uma senha única! Deixe registrado conforme preferir.")
            .setPositiveButton(android.R.string.ok , null)
            .show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}