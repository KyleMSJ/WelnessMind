package ifsp.project.welnessmind.ui.register.forms

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.repository.FormsRepository
import ifsp.project.welnessmind.databinding.FragmentFormsBinding
import ifsp.project.welnessmind.domain.FormsUseCase
import ifsp.project.welnessmind.extension.hideKeyboard
import java.lang.IllegalArgumentException

class FormsFragment : Fragment() {

    private var _binding: FragmentFormsBinding? = null
    private val binding get() = _binding!!

    private lateinit var horasSono: EditText
    private lateinit var horasEstudo: EditText
    private lateinit var horasTrabalho: EditText
    private lateinit var radioButtonTreino: List<RadioButton>
    private lateinit var descricaoTreino: EditText
    private lateinit var frequenciaTreino: EditText
    private lateinit var hobbies: EditText
    private lateinit var radioButtonMedicamento: List<RadioButton>
    private lateinit var descricaoMedicamento: EditText

    private val viewModel: FormsViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val formsDAO: FormsDAO =
                    AppDatabase.getInstance(requireContext()).formsDao

                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: FormsRepository = FormsUseCase(formsDAO, firebaseDatabase)
                return FormsViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializaVariaveis()
        observeEvents()
        setListeners()
    }

    private fun inicializaVariaveis() {
        horasSono = binding.etSono
        horasEstudo = binding.etEstudo
        horasTrabalho = binding.etTrabalho
        descricaoTreino = binding.etAtividadeFisica2
        frequenciaTreino = binding.etAtividadeFisica3
        hobbies = binding.etHobbies
        descricaoMedicamento = binding.etMedicamento2

        radioButtonTreino = listOf(
            binding.rbTreina,
            binding.rbNaoTreina
        )

        radioButtonMedicamento = listOf(
            binding.rbTomaMedicamento,
            binding.rbNaoTomaMedicamento
        )
    }

    private fun observeEvents() {
        viewModel.formsStateEventData.observe(viewLifecycleOwner) { formsState ->
             when(formsState) {
                 is FormsViewModel.FormsState.Inserted -> {
                     clearFields()
                     hideKeyboard()
                     requireView().requestFocus()
                 }
             }
        }
    }

    private fun clearFields() {
        horasSono.text?.clear()
        horasEstudo.text?.clear()
        horasTrabalho.text?.clear()
        descricaoTreino.text?.clear()
        frequenciaTreino.text?.clear()
        descricaoMedicamento.text?.clear()
        radioButtonTreino.forEach{it.isChecked = false}
        radioButtonMedicamento.forEach{it.isChecked = false}
    }

    private fun hideKeyboard() {
        val parentActivity = requireActivity()
        if (parentActivity is AppCompatActivity) {
            parentActivity.hideKeyboard()
        }
    }

    private fun setListeners() {
        binding.btnAvancar.setOnClickListener { view ->
            val sono = horasSono.text.toString().toInt()
            val estudo = horasEstudo.text.toString().toInt()
            val trabalho = horasTrabalho.text.toString().toInt()
            val descTreino = descricaoTreino.text.toString()
            val freqTreino = frequenciaTreino.text.toString()
            val hobbies = hobbies.text.toString()
            val descMed = descricaoMedicamento.text.toString()
            val treina = OpcaoSimSelecionada(radioButtonTreino)
            val tomaMed = OpcaoSimSelecionada(radioButtonMedicamento)

            val userId = arguments?.getLong("userID") ?: throw IllegalArgumentException("UserId é necessário!")
            Log.d("FormsFragment","User ID encontrado: $userId")

            if (horasSono.text.isNullOrEmpty() || horasEstudo.text.isNullOrEmpty() || horasTrabalho.text.isNullOrEmpty() || !radioButtonTreino.any { it.isChecked } || !radioButtonMedicamento.any{ it.isChecked })
            {
                Snackbar.make(view, "Os campos com asterisco são obrigatórios!", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.addForms(userId, sono, estudo, trabalho, treina, descTreino, freqTreino, hobbies, tomaMed, descMed)
                findNavController().navigate(R.id.action_formsFragment_to_professionalListFragment)
            }
        }
    }

    private fun OpcaoSimSelecionada(radioButtons: List<RadioButton>): Boolean {
        val yesButton = radioButtons[0]
        return yesButton.isChecked
    }
}