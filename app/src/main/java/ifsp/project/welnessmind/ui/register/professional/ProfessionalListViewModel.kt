package ifsp.project.welnessmind.ui.register.professional

import androidx.lifecycle.ViewModel
import ifsp.project.welnessmind.data.repository.ProfessionalRepository

class ProfessionalListViewModel(
    private val repository: ProfessionalRepository
) : ViewModel() {

    val allProfessionalsEvent = repository.getAllProfessionals()
}