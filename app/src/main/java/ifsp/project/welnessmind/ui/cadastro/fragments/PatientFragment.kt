package ifsp.project.welnessmind.ui.cadastro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
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
import ifsp.project.welnessmind.ui.cadastro.PatientViewModel

class PatientFragment : Fragment() {

    private var  _binding: FragmentPatientBinding? = null
    private val binding get() = _binding!!

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
            val bundle = Bundle()
            bundle.putString("userType", "PACIENTE")
            findNavController().navigate(R.id.action_patientFragment_to_loginFragment, bundle)
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
            val idade = etIdade.text.toString().toInt()
            val maritalStatus = getSelectedIndex(radioButtonState)
            val income = getSelectedIndex(radioButtonRenda)
            val education = getSelectedIndex(radioButtonEscolaridade)

            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etCPF.text.isNullOrEmpty() || etIdade.text.isNullOrEmpty() ||
                    !radioButtonState.any { it.isChecked } || !radioButtonRenda.any { it.isChecked } || !radioButtonEscolaridade.any { it.isChecked }
                ) {
                    Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addPatient(name, email, cpf, idade, maritalStatus, income, education, requireContext()) { userId ->
                        showSuccessPopup(userId)
                    }
                }
        }
    }

    private fun showSuccessPopup(userId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso! ID: $userId")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val bundle = Bundle().apply {
                putBoolean("isFromSignup", true)
                putString("userType", "PACIENTE")
            }
            findNavController().navigate(R.id.action_patientFragment_to_loginFragment, bundle)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getSelectedIndex(radioButtons: List<RadioButton>): Int {
        return radioButtons.indexOfFirst { it.isChecked }.takeIf { it >= 0 } ?: -1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // limpa a referência, prevenindo memory leaks
    }

}