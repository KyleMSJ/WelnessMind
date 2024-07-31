package ifsp.project.welnessmind.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ifsp.project.welnessmind.data.repository.UserRepository
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.PatientPasswordDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalPasswordDAO

/**
 * ViewModel provider factory para instanciar LoginViewModel.
 * Obrigatório, visto que LoginViewModel possui um construtor não vazio
 */
class LoginViewModelFactory(
    private val patientDAO: PatientDAO,
    private val professionalDAO: ProfessionalDAO,
    private val patientPasswordDao: PatientPasswordDAO,
    private val professionalPasswordDao: ProfessionalPasswordDAO
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                userRepository = UserRepository(
                    patientDao = patientDAO,
                    professionalDao = professionalDAO,
                    patientPasswordDao = patientPasswordDao,
                    professionalPasswordDao = professionalPasswordDao
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}