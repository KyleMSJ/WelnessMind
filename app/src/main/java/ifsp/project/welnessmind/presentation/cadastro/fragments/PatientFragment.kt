package ifsp.project.welnessmind.presentation.cadastro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.databinding.FragmentPatientBinding
import ifsp.project.welnessmind.domain.PatientUseCase
import ifsp.project.welnessmind.extension.hideKeyboard
import ifsp.project.welnessmind.presentation.cadastro.PatientViewModel

class PatientFragment : Fragment() {

    // convenção para facilitar o acesso às views de maneira segura, sem precisar verificar se é nula toda vez que acessar 'binding'
    private var  _binding: FragmentPatientBinding? = null // _binding -> variável privada usada para armazenar a referência ao objeto 'FragmentPatientBinding'
    private val binding get() = _binding!! // !! -> operador de afirmação não nulo, converte qualquer valor para um tipo não-nulo quando acessar a variável

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCPF: EditText
    private lateinit var etIdade: EditText
    private lateinit var radioButtonState: List<RadioButton>
    private lateinit var radioButtonRenda: List<RadioButton>
    private lateinit var radioButtonEscolaridade: List<RadioButton>

    private val viewModel: PatientViewModel by viewModels {
        object : ViewModelProvider.Factory  {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val patientDAO: PatientDAO =
                    AppDatabase.getInstance(requireContext()).patientDao

                val  repository: PatientRepository = PatientUseCase(patientDAO)
                return PatientViewModel(repository) as T
            }
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
        observeEvents()
        setListeners()

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_patientFragment_to_loginFragment)
        }
    }


    private fun inicializaVariaveis() {
        etNome = binding.userNome
        etEmail = binding.userEmail
        etCPF = binding.userDocument
        etIdade = binding.userIdade

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

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            findNavController().navigate(R.id.action_patientFragment_to_loginFragment)
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun observeEvents() {
        viewModel.patientStateEventData.observe(viewLifecycleOwner) { patientState ->
            when(patientState) {
                is PatientViewModel.PatientState.Inserted -> {
                    clearFields()
                    hideKeyboard()
                    requireView().requestFocus()
                }
            }
        }
    }
    private fun clearFields() {
            etNome.text?.clear()
            etEmail.text?.clear()
            etCPF.text?.clear()
            etIdade.text?.clear()
            radioButtonState.forEach {it.isChecked = false}
            radioButtonRenda.forEach {it.isChecked = false}
            radioButtonEscolaridade.forEach {it.isChecked = false}
        }

    private fun hideKeyboard() {
        val parentActivity = requireActivity()
        if (parentActivity is AppCompatActivity) {
            parentActivity.hideKeyboard()
        }
    }

    private fun setListeners() {
        binding.btnCadastroUser.setOnClickListener { view ->
            val name = etNome.text.toString()
            val email = etEmail.text.toString()
            val cpf = etCPF.text.toString()
            val idade = etIdade.id
            val maritalStatus = getSelectedIndex(radioButtonState)
            val income = getSelectedIndex(radioButtonRenda)
            val education = getSelectedIndex(radioButtonEscolaridade)

            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etCPF.text.isNullOrEmpty() || etIdade.text.isNullOrEmpty() ||
                    !radioButtonState.any { it.isChecked } || !radioButtonRenda.any { it.isChecked } || !radioButtonEscolaridade.any { it.isChecked }
                ) {
                    Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addPatient(name, email, cpf, idade, maritalStatus, income, education)
                    showSuccessPopup()
                }
        }
    }

    private fun getSelectedIndex(radioButtons: List<RadioButton>): Int {
        return radioButtons.indexOfFirst { it.isChecked }.takeIf { it >= 0 } ?: -1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // limpa a referência, prevenindo memory leaks
    }

}