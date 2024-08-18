package ifsp.project.welnessmind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.data.repository.SyncRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val syncRepository: SyncRepository
) : ViewModel() {

    fun syncData() {
        viewModelScope.launch {
            syncRepository.syncProfessionals()
            syncRepository.syncPatients()
        }
    }
}
