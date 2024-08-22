package ifsp.project.welnessmind.ui.register.patient

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.databinding.FragmentPatientBinding
import ifsp.project.welnessmind.domain.PatientUseCase
import ifsp.project.welnessmind.extension.hideKeyboard
import java.lang.NumberFormatException

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

              val firebaseDatabase = FirebaseDatabase.getInstance()
              val  repository: PatientRepository = PatientUseCase(patientDAO, firebaseDatabase)
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
            binding.rbSolteiro, // 0
            binding.rbCasado, // 1
            binding.rbSeparado, // 2
            binding.rbDivorciado, // 3
            binding.rbViuvo // 4
        )

        radioButtonRenda = listOf(
            binding.rbRenda1, // Até 2. S.M.
            binding.rbRenda2, // De 2 a 4 S.M.
            binding.rbRenda3, // De 4 a 7 S.M.
            binding.rbRenda4 // + 7 S.M.
        )

        radioButtonEscolaridade = listOf(
            binding.rbEscolaridade1, // Ensino Fundamental
            binding.rbEscolaridade2, // Ensino Médio
            binding.rbEscolaridade3, // Ensino Superior
            binding.rbEscolaridade4 // Pós-graduação
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
                else -> {}
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
            val idadeText = etIdade.text.toString()
            val maritalStatus = getSelectedIndex(radioButtonState)
            val income = getSelectedIndex(radioButtonRenda)
            val education = getSelectedIndex(radioButtonEscolaridade)

            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etCPF.text.isNullOrEmpty() || etIdade.text.isNullOrEmpty() ||
                    !radioButtonState.any { it.isChecked } || !radioButtonRenda.any { it.isChecked } || !radioButtonEscolaridade.any { it.isChecked }
                ) {
                    Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            val idade = idadeText.toInt()

            viewModel.addPatient(name, email, cpf, idade, maritalStatus, income, education, requireContext()) { userId ->
                showSuccessPopup(userId)
            }
        }
    }

    private fun showSuccessPopup(userId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        Log.d("PatientFragment", "ID: $userId")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val bundle = Bundle().apply {
                putString("userType", "PACIENTE")
                putBoolean("isFromSignup", true)
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