package ifsp.project.welnessmind.presentation.cadastro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfessionalViewModel(private val repository: ProfessionalRepository) : ViewModel() {

    private val _professionalStateEventData = MutableLiveData<ProfessionalState>()
    val professionalStateEventData: LiveData<ProfessionalState>
        get() = _professionalStateEventData

    private val _messageEventData = MutableLiveData<Int>()
    val messageEventData: LiveData<Int>
        get() = _messageEventData

    fun addProfessional(name: String, email: String, credenciais: String, especialidade: String) = viewModelScope.launch {
        try {
            val id = repository.insertProfessional(name, email, credenciais, especialidade)
            if (id > 0) {
                _professionalStateEventData.value = ProfessionalState.Inserted
            }
        } catch (ex: Exception) {
             _messageEventData.value = R.string.professional_error_to_insert
            Log.e(TAG, ex.toString())
        }
    }
    sealed class ProfessionalState {
        object Inserted: ProfessionalState()
    }

    companion object {
        private val TAG = ProfessionalViewModel::class.java.simpleName
    }
}