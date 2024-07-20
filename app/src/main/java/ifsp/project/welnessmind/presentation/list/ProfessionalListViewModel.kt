package ifsp.project.welnessmind.presentation.list

import androidx.lifecycle.ViewModel
import ifsp.project.welnessmind.data.repository.ProfessionalRepository

class ProfessionalListViewModel(
    private val repository: ProfessionalRepository
) : ViewModel() {

    val allProfessionalsEvent = repository.getAllProfessionals()
}