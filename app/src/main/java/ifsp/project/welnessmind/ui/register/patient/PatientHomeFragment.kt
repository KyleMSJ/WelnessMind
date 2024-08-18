package ifsp.project.welnessmind.ui.register.patient

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.databinding.FragmentPatientHomeBinding
import ifsp.project.welnessmind.domain.PatientUseCase

class PatientHomeFragment : Fragment() {

    private var  _binding: FragmentPatientHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientHomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val patientDAO = AppDatabase.getInstance(requireContext()).patientDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: PatientRepository = PatientUseCase(patientDAO, firebaseDatabase)
                return PatientHomeViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPatientName(requireContext()) { patientName ->
            "Olá $patientName".also { binding.tvGreeting.text = it }
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {  item ->
            when (item.itemId) {
                R.id.navigation_consultas -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val packageManager = view.context.packageManager
                    val calendarPackage = "com.google.android.calendar"

                    try {
                        packageManager.getPackageInfo(calendarPackage, 0)
                        val launchIntent = packageManager.getLaunchIntentForPackage(calendarPackage)
                        if (launchIntent != null) {
                            startActivity(launchIntent)
                        } else {
                            Log.e("Calendar App", "Erro ao abrir o Google Calendar")
                        }
                    } catch (e: Exception) {
                        Log.e("Calendar App", "Não foi possível encontrar o app", e)
                        intent.data = Uri.parse("https://calendar.google.com/calendar/u/0/r/week")
                        startActivity(intent)
                    }
                    true
                }
                R.id.navigation_formulario -> {
                    // TODO criar tela com o formulário já cadastrado para aquele usuário
                    findNavController().navigate(R.id.formsFragment)
                    true
                }
                R.id.navigation_dados -> {
                    findNavController().navigate(R.id.action_patientHomeFragment_to_patientProfileFragment)
                    true
                }
                R.id.navigation_profissionais -> {
                    findNavController().navigate(R.id.professionalListFragment)
                    true
                }
                else -> { false }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

