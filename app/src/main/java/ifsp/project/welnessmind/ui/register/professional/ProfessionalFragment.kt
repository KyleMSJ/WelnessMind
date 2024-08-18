package ifsp.project.welnessmind.ui.register.professional

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.databinding.FragmentProfessionalBinding
import ifsp.project.welnessmind.domain.ProfessionalUseCase
import ifsp.project.welnessmind.extension.hideKeyboard

class ProfessionalFragment : Fragment() {

private var _binding: FragmentProfessionalBinding? = null
    private val binding get() = _binding!!

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRegistro: EditText
    private lateinit var etEspecialidade: EditText

    private lateinit var btnCadastroPro: Button

    private val viewModel: ProfessionalViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO: ProfessionalDAO =
                    AppDatabase.getInstance(requireContext()).professionalDao
                val officeLocationDAO: OfficeLocationDAO = AppDatabase.getInstance(requireContext()).officeLocationDao

                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: ProfessionalRepository = ProfessionalUseCase(professionalDAO, officeLocationDAO, firebaseDatabase)
                return ProfessionalViewModel(repository) as T
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessionalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializaVariaveis()
        observeEvents()
        setListeners()

        binding.tvLogin.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", "PROFISSIONAL")
            findNavController().navigate(R.id.action_professionalFragment_to_loginFragment, bundle)
        }
    }

    private fun inicializaVariaveis() {
        etNome = binding.professionalName
        etEmail = binding.professionalEmail
        etRegistro = binding.numRegistro
        etEspecialidade = binding.especialidade

        btnCadastroPro = binding.btnCadastroPro
    }

    private fun observeEvents() {
        viewModel.professionalStateEventData.observe(viewLifecycleOwner) { professionalState ->
            when (professionalState) {
                is ProfessionalViewModel.ProfessionalState.Inserted -> {
                    clearFields()
                    hideKeyboard()
                    requireView().requestFocus()
                }
            }
        }

        viewModel.messageEventData.observe(viewLifecycleOwner) { stringResId ->
            Snackbar.make(requireView(), stringResId, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setListeners() {
        btnCadastroPro.setOnClickListener { view ->
            val name = etNome.text.toString()
            val email = etEmail.text.toString()
            val num = etRegistro.text.toString()
            val espec = etEspecialidade.text.toString()

            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etRegistro.text.isNullOrEmpty() || etEspecialidade.text.isNullOrEmpty()) {
                Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
            }
            else {
                viewModel.addProfessional(name, email, num, espec, requireContext()) { userId ->
                    showSuccessPopup(userId)
                }
            }
        }
    }

    private fun showSuccessPopup(userId: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        Log.d("ProfessionalFragment", "ID: $userId")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val bundle = Bundle().apply {
                putString("userType", "PROFISSIONAL")
                putBoolean("isFromSignup", true)
            }
            findNavController().navigate(R.id.action_professionalFragment_to_loginFragment, bundle)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun clearFields() {
        etNome.text?.clear()
        etEmail.text?.clear()
        etRegistro.text?.clear()
        etEspecialidade.text?.clear()
    }

    private fun hideKeyboard() {
        val parentActivity = requireActivity()
        if (parentActivity is AppCompatActivity) {
            parentActivity.hideKeyboard()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}