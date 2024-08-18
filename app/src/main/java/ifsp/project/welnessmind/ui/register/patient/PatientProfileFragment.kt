package ifsp.project.welnessmind.ui.register.patient

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.databinding.FragmentPatientProfileBinding
import ifsp.project.welnessmind.domain.PatientUseCase
import java.lang.IllegalArgumentException

class PatientProfileFragment : Fragment() {

    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val patientDAO = AppDatabase.getInstance(requireContext()).patientDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: PatientRepository = PatientUseCase(patientDAO, firebaseDatabase)
                return PatientViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val info = binding.ivInfo
        info.setOnClickListener {
            Toast.makeText(context?.applicationContext , "Clique em qualquer um dos campos ao lado para editar", Toast.LENGTH_SHORT).show()
        }
        val delete = binding.ivDelete
        delete.setOnClickListener {
            viewModel.loadUserInfo(requireContext()) { patientID ->
                showWarningDialog(patientID)
            }

        }
        readUserData()
        updateUserData()

        binding.btnSalvar.setOnClickListener {
            readUserData()
        }
    }

    private fun showWarningDialog(userID: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ALERTA!")
        builder.setMessage("Você está prestes a apagar este usuário, e todas as suas informações... está certo disso?")
        builder.setPositiveButton("Sim") { dialog, _ ->
            viewModel.deletePatient(id = userID)
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun readUserData() {
        viewModel.patientStateEventData.observe(viewLifecycleOwner) { state ->
            when(state) {
                is PatientViewModel.PatientState.Loaded -> {
                    "Olá, ${state.patient.name}".also { binding.tvGreeting.text = it }
                    "Email: ${state.patient.email}".also { binding.tvEmail.text = it }
                    "CPF: ${state.patient.cpf}".also { binding.tvCpf.text = it }
                    "Estado civil: ${viewModel.getMaritalStatusText(state.patient.estadoCivil)}".also {
                        binding.tvEstadoCivil.text = it
                    }
                    "Idade: ${state.patient.idade}".also { binding.tvIdade.text = it }
                    "Renda Familiar: ${viewModel.getIncomeText(state.patient.rendaFamiliar)}".also {
                        binding.tvRenda.text = it
                    }
                    "Escolaridade: ${viewModel.getEducationText(state.patient.escolaridade)}".also {
                        binding.tvEscolaridade.text = it
                    }
                }
                is PatientViewModel.PatientState.Updated -> {
                    Log.d("PatientProfileFragment", "Usuário atualizado com sucesso")
                }
                else -> {
                    Log.e("PatientProfileFragment", "Erro ao atualizar o usuário")
                }
            }
        }
        viewModel.loadUserInfo(requireContext()) { patientID ->
            Log.d("PatientProfileFragment", "User ID encontrado: $patientID")
        }
    }

    private fun updateUserData() {
        binding.tvGreeting.setOnClickListener {
            showNameEditDialog()
        }

        binding.tvEmail.setOnClickListener {
            showEmailEditDialog()
        }

        binding.tvCpf.setOnClickListener {
            showCpfEditDialog()
        }

        binding.tvEstadoCivil.setOnClickListener {
            showMaritalStatusDialog()
        }

        binding.tvIdade.setOnClickListener {
            showAgeEditDialog()
        }

        binding.tvRenda.setOnClickListener {
            showIncomeDialog()
        }

        binding.tvEscolaridade.setOnClickListener {
            showEducationDialog()
        }
    }

    private fun showNameEditDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Nome")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newName = editText.text.toString()
                viewModel.updateName(newName)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showEmailEditDialog() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Email")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newEmail = editText.text.toString()
                viewModel.updateEmail(newEmail)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showCpfEditDialog() {
        val editText = EditText(requireContext()) // TODO limitar o número de caracteres a serem digitados
        AlertDialog.Builder(requireContext())
            .setTitle("Editar CPF")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newCPF = editText.text.toString()
                viewModel.updateCPF(newCPF)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showMaritalStatusDialog() {
        val options = arrayOf("Solteiro(a)", "Casado(a)", "Separado(a)", "Divorciado(a)", "Viúvo(a)")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecione o Estado Civil")
            .setItems(options) { _, which ->
                viewModel.updateMaritalStatus(which)
            }
            .create()
            .show()
    }

    private fun showAgeEditDialog() {
        val editText = EditText(requireContext())
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Idade")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newAge = editText.text.toString().toIntOrNull()
                newAge?.let { viewModel.updateAge(it) }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showIncomeDialog() {
        val options = arrayOf("Até 2 S.M.", "De 2 a 4 S.M.", "De 4 a 7 S.M.", "Mais de 7 S.M.")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecione a Renda Familiar")
            .setItems(options) { _, which ->
                viewModel.updateIncome(which)
            }
            .create()
            .show()
    }

    // Exemplo de diálogo para editar escolaridade
    private fun showEducationDialog() {
        val options = arrayOf("Ensino Fundamental", "Ensino Médio", "Ensino Superior", "Pós-graduação")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecione a Escolaridade")
            .setItems(options) { _, which ->
                viewModel.updateEducation(which)
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}