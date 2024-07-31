package ifsp.project.welnessmind.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.databinding.FragmentLoginBinding
import ifsp.project.welnessmind.ui.login.LoginViewModel.LoggedInUserView
import ifsp.project.welnessmind.ui.login.LoginViewModel.UserType
import ifsp.project.welnessmind.util.SharedPreferencesUtil

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private var isLoginAttempted = false
    private var isFromSignup = false
    private var passwordGenerated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        // identifica se veio de uma tela de cadastro
        isFromSignup = arguments?.getBoolean("isFromSignup") ?: false
        // Identificar o tipo de usuário a partir dos argumentos passados para o fragmento
            val userId = SharedPreferencesUtil.getUserId(requireContext())
            val userType = when (arguments?.getString("userType")) {
                "PACIENTE" -> UserType.PACIENTE
                "PROFISSIONAL" -> UserType.PROFISSIONAL
                else -> throw IllegalArgumentException("Tipo de usuário inválido")
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
            Toast.makeText(appContext, "Erro ao recuperar o ID do usuário", Toast.LENGTH_LONG).show()
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
                    }
                    if (isLoginAttempted && userType == UserType.PACIENTE) {
                        if (!isFromSignup) {
                            findNavController().navigate(R.id.action_loginFragment_to_professionalListFragment)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_formsFragment)
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