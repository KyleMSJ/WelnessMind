package ifsp.project.welnessmind.ui.register.professional

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.util.SharedPreferencesUtil
import kotlinx.coroutines.launch
import java.lang.Exception

class ProfessionalViewModel(private val repository: ProfessionalRepository) : ViewModel() {

    private val _professionalStateEventData = MutableLiveData<ProfessionalState>()
    val professionalStateEventData: LiveData<ProfessionalState>
        get() = _professionalStateEventData

    private val _messageEventData = MutableLiveData<Int>()
    val messageEventData: LiveData<Int>
        get() = _messageEventData

    fun addProfessional(name: String, email: String, credenciais: String, especialidade: String, context: Context, callback: (Long) -> Unit) = viewModelScope.launch {
        try {
            val id = repository.insertProfessional(name, email, credenciais, especialidade)
            if (id > 0) {
                SharedPreferencesUtil.saveUserId(context, id)
                _professionalStateEventData.value = ProfessionalState.Inserted
                callback(id)
            }
        } catch (ex: Exception) {
             _messageEventData.value = R.string.professional_error_to_insert
            Log.e(TAG, ex.toString())
        }
    }

    fun saveOfficeLocation(professionalId: Long, address: String, latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            val location = OfficeLocationEntity(professionalId, address, latitude, longitude)
            repository.saveOfficeLocation(location)
            _messageEventData.value = R.string.location_saved_successfully
        } catch (ex: Exception) {
            _messageEventData.value = R.string.location_error_to_save
            Log.e(TAG, ex.toString())
        }
    }

    // TODO
    fun getOfficeLocation(professionalId: Long, callback: (OfficeLocationEntity?) -> Unit) = viewModelScope.launch {
        try {
            val location = repository.getOfficeLocation(professionalId)
            callback(location)
        } catch (ex: Exception) {
            _messageEventData.value = R.string.location_error_to_fetch
            Log.e(TAG, ex.toString())
        }
    }
    fun getProfessionalById(id: Long): LiveData<ProfessionalEntity> {
        return repository.observeProfessionalById(id)
    }

    sealed class ProfessionalState {
        object Inserted: ProfessionalState()
    }

    companion object {
        private val TAG = ProfessionalViewModel::class.java.simpleName
    }
}