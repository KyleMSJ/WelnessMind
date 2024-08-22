package ifsp.project.welnessmind.ui.register.forms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
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
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.repository.FormsRepository
import ifsp.project.welnessmind.data.repository.SyncRepository
import ifsp.project.welnessmind.databinding.FragmentFormsBinding
import ifsp.project.welnessmind.domain.FormsUseCase
import ifsp.project.welnessmind.extension.hideKeyboard
import ifsp.project.welnessmind.util.SharedPreferencesUtil

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

    private var isUserRegistered = false
    private var currentForm: FormsEntity? = null

    private val viewModel: FormsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val formsDAO: FormsDAO =
                    AppDatabase.getInstance(requireContext()).formsDao
                val professionalDAO: ProfessionalDAO =
                    AppDatabase.getInstance(requireContext()).professionalDao
                val officeLocationDAO: OfficeLocationDAO =
                    AppDatabase.getInstance(requireContext()).officeLocationDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: FormsRepository = FormsUseCase(formsDAO, firebaseDatabase)
                val syncRepository = SyncRepository(
                    null,
                    professionalDAO,
                    formsDAO,
                    officeLocationDAO,
                    firebaseDatabase
                )
                return FormsViewModel(repository, syncRepository) as T
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

        isUserRegistered = arguments?.getBoolean("userRegistered") ?: false
        Log.d("FormsFragment", "Usuário já registrado?\n$isUserRegistered")

        inicializaVariaveis()
        observeEvents()
        setListeners()
        val id = SharedPreferencesUtil.getUserId(requireContext())

        if (isUserRegistered) {
            viewModel.getFormsByUserId(id) { forms ->
                forms?.let {
                    currentForm = it
                    readForms(it)
                }
            }
        }
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

    private fun readForms(forms: FormsEntity) {
        horasSono.setText(forms.horasSono.toString())
        horasEstudo.setText(forms.horasEstudo.toString())
        horasTrabalho.setText(forms.horasEstudo.toString())
        if (forms.fazAtividadeFisica) {
            radioButtonTreino[0].isChecked = true
        } else {
            radioButtonTreino[1].isChecked = true
        }
        descricaoTreino.setText(forms.descricaoAtivFisica)
        frequenciaTreino.setText(forms.frequenciaAtivFisica)
        hobbies.setText(forms.hobbies)
        if (forms.tomaMedicamento) {
            radioButtonMedicamento[0].isChecked = true
        } else {
            radioButtonMedicamento[1].isChecked = true
        }
        descricaoMedicamento.setText(forms.descricaoMedicamento)
    }

    private fun observeEvents() {
        viewModel.formsStateEventData.observe(viewLifecycleOwner) { formsState ->
            when (formsState) {
                is FormsViewModel.FormsState.Inserted -> {
                    clearFields()
                    hideKeyboard()
                    requireView().requestFocus()
                }

                is FormsViewModel.FormsState.Updated -> {
                    Snackbar.make(
                        requireView(),
                        "Formulário atualizado com sucesso!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                else -> {}
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
        radioButtonTreino.forEach { it.isChecked = false }
        radioButtonMedicamento.forEach { it.isChecked = false }
    }

    private fun hideKeyboard() {
        val parentActivity = requireActivity()
        if (parentActivity is AppCompatActivity) {
            parentActivity.hideKeyboard()
        }
    }

    private fun setListeners() {
        setupRadioButtonListeners(radioButtonTreino)
        setupRadioButtonListeners(radioButtonMedicamento)

        binding.btnAvancar.setOnClickListener { view ->
            val sonoText = horasSono.text.toString()
            val estudoText = horasEstudo.text.toString()
            val trabalhoText = horasTrabalho.text.toString()
            val descTreino = descricaoTreino.text.toString()
            val freqTreino = frequenciaTreino.text.toString()
            val hobbies = hobbies.text.toString()
            val descMed = descricaoMedicamento.text.toString()
            val treina = opcaoSimSelecionada(radioButtonTreino)
            val tomaMed = opcaoSimSelecionada(radioButtonMedicamento)

            val userId = arguments?.getLong("userID")
                ?: throw IllegalArgumentException("UserId é necessário!")
            Log.d("FormsFragment", "User ID encontrado: $userId")

            if (horasSono.text.isNullOrEmpty() || horasEstudo.text.isNullOrEmpty() || horasTrabalho.text.isNullOrEmpty() || !radioButtonTreino.any { it.isChecked } || !radioButtonMedicamento.any { it.isChecked }) {
                Snackbar.make(
                    view,
                    "Os campos com asterisco são obrigatórios!",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val sono = sonoText.toInt()
                val estudo = estudoText.toInt()
                val trabalho = trabalhoText.toInt()
                if (isUserRegistered) {
                    viewModel.updateForms(userId, sono, estudo, trabalho, treina, descTreino, freqTreino, hobbies, tomaMed, descMed)
                }
                else {
                    viewModel.addForms(
                        userId,
                        sono,
                        estudo,
                        trabalho,
                        treina,
                        descTreino,
                        freqTreino,
                        hobbies,
                        tomaMed,
                        descMed
                    )
                }
                findNavController().navigate(R.id.action_formsFragment_to_professionalListFragment)
            }
        }
    }

    private fun setupRadioButtonListeners(radioButtons: List<RadioButton>) {
        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                radioButton.isChecked = true
            }
        }
    }

    private fun opcaoSimSelecionada(radioButtons: List<RadioButton>): Boolean {
        return radioButtons[0].isChecked
    }
}