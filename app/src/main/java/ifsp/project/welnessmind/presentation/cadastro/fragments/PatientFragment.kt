package ifsp.project.welnessmind.presentation.cadastro.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ifsp.project.welnessmind.LoginActivity
import ifsp.project.welnessmind.databinding.FragmentPatientBinding
import ifsp.project.welnessmind.presentation.cadastro.PatientViewModel

class PatientFragment : Fragment() {

    // convenção para facilitar o acesso às views de maneira segura, sem precisar verificar se é nula toda vez que acessar 'binding'
    private var  _binding: FragmentPatientBinding? = null // _binding -> variável privada usada para armazenar a referência ao objeto 'FragmentPatientBinding'
    private val binding get() = _binding!! // !! -> operador de afirmação não nulo, converte qualquer valor para um tipo não-nulo quando acessar a variável

    private lateinit var radioButtonState: List<RadioButton>
    private lateinit var radioButtonRenda: List<RadioButton>
    private lateinit var radioButtonEscolaridade: List<RadioButton>

    private val viewModel: PatientViewModel by viewModels {
        object : ViewModelProvider.Factory  {
         /*  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val patientDAO: PatientDAO =
                    AppDatabase.getInstance(requireContext()).patientDao

                val  repository: PatientRepository = PatientUseCase(patientDAO)
                return PatientViewModel(repository) as T
            }*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializaVariaveis()
        trataRadioButtons()
        validaCampos()
        observeEvents()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun inicializaVariaveis() {
        radioButtonState = listOf(
            binding.rbSolteiro,
            binding.rbCasado,
            binding.rbSeparado,
            binding.rbDivorciado,
            binding.rbViuvo
        )

        radioButtonRenda = listOf(
            binding.rbRenda1,
            binding.rbRenda2,
            binding.rbRenda3,
            binding.rbRenda4
        )

        radioButtonEscolaridade = listOf(
            binding.rbEscolaridade1,
            binding.rbEscolaridade2,
            binding.rbEscolaridade3,
            binding.rbEscolaridade4
        )
    }



    private fun trataRadioButtons() {
        radioButtonState.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonState.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonRenda.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonRenda.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonEscolaridade.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonEscolaridade.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }
    }

    private fun validaCampos() {
        binding.btnCadastroUser.setOnClickListener { view ->
            if (binding.userNome.editText?.text.isNullOrEmpty() || binding.userEmail.editText?.text.isNullOrEmpty() ||
                binding.userDocument.editText?.text.isNullOrEmpty() || binding.userIdade.editText?.text.isNullOrEmpty() ||
                !radioButtonState.any { it.isChecked } || !radioButtonRenda.any { it.isChecked } || !radioButtonEscolaridade.any { it.isChecked }
            ) {
                Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
            } else {
                showSuccessPopup()
            }
        }
    }

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun observeEvents() {
        //viewModel.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // limpa a referência, prevenindo memory leaks
    }

}