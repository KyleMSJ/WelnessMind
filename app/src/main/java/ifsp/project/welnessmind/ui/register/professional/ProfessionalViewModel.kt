package ifsp.project.welnessmind.ui.register.professional

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.OfficeRepository
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.data.repository.SyncRepository
import ifsp.project.welnessmind.ui.register.patient.PatientViewModel
import ifsp.project.welnessmind.util.SharedPreferencesUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfessionalViewModel(
    private val repository: ProfessionalRepository,
    private val officeRepository: OfficeRepository,
    private val syncRepository: SyncRepository
    ) : ViewModel() {

    private val _professionalStateEventData = MutableLiveData<ProfessionalState>()
    val professionalStateEventData: LiveData<ProfessionalState>
        get() = _professionalStateEventData

    private val  _officeStateEventData = MutableLiveData<OfficeLocationEntity>()
    val officeStateEventData: LiveData<OfficeLocationEntity>
        get() = _officeStateEventData

    private val _messageEventData = MutableLiveData<Int>()
    val messageEventData: LiveData<Int>
        get() = _messageEventData

    fun getProfessionalID(context: Context): Long {
            val professionalId = SharedPreferencesUtil.getUserId(context)
            Log.d(TAG, "ID encontrado: $professionalId")
            return professionalId
    }
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

    suspend fun updateProfessionalDetails(id: Long, name: String, email: String, credentials: String, specialty: String) = viewModelScope.launch {
        val professional = repository.getProfessionalById(id)
        professional?.let {
            it.name = name
            it.credenciais = credentials
            it.especialidade = specialty
            it.email = email
            repository.updateProfessional(id, name, email, credentials, specialty)
            _professionalStateEventData.value = ProfessionalState.Updated
        }
    }
    fun saveOfficeLocation(professionalId: Long, address: String, latitude: Double, longitude: Double, description: String, contact: String) = viewModelScope.launch {
        try {
            Log.d(TAG, "ID do profissional : $professionalId \n endereço: $address \n latitude: $latitude e longitude: $longitude" +
                    "descrição: $description e contato: $contact")
            val location = OfficeLocationEntity(professional_id = professionalId, address = address, latitude = latitude, longitude = longitude,
                description = description, contact = contact)
            Log.d(TAG, "ID: ${location.id}")
            officeRepository.saveOfficeLocation(professionalId, address, latitude, longitude, description, contact)
            _messageEventData.value = R.string.location_saved_successfully
        } catch (ex: Exception) {
            _messageEventData.value = R.string.location_error_to_save
            Log.e(TAG, " Erro: $ex.toString()")
        }
    }

    fun updateDescription(professionalId: Long, newDescription: String) {
        viewModelScope.launch {
            try {
                val officeLocation = officeRepository.getOfficeLocation(professionalId)
                officeLocation?.let {
                    it.description = newDescription
                    updateOffice(it)
                    _officeStateEventData.value = it
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao atualizar a descrição: ${e.message}")
            }
        }
    }

    fun updateContact(professionalId: Long, newContact: String) {
        viewModelScope.launch {
            try {
                val officeLocation = officeRepository.getOfficeLocation(professionalId)
                officeLocation?.let {
                    it.contact = newContact
                    updateOffice(it)
                    _officeStateEventData.value = it
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao atualizar o contato: ${e.message}")
            }
        }
    }

    private fun updateOffice(office: OfficeLocationEntity)  = viewModelScope.launch {
        try {
            Log.d(TAG,"Atualizando informações complementares de ID: ${office.id}")
            officeRepository.updateOfficeLocation(office.id, office.professional_id, office.address, office.latitude, office.longitude, office.description, office.contact)
            _officeStateEventData.value = office
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar info complementares: ${e.message}")
        }
    }

    fun getOfficeLocation(professionalId: Long, callback: (OfficeLocationEntity?) -> Unit) = viewModelScope.launch {
        try {
            val syncResult = async { syncRepository.syncOfficeLocations(professionalId) }
            syncResult.await() // Garantir que a sincronização foi concluída
            Log.d(TAG, "Buscando localização para Professional ID: $professionalId")

            val location = async { officeRepository.getOfficeLocation(professionalId) }
            Log.d(TAG, "Localização encontrada: $location")

            callback(location.await())
        } catch (ex: Exception) {
            _messageEventData.value = R.string.location_error_to_fetch
            Log.e(TAG, ex.toString())
        }
    }
    fun getProfessionalById(id: Long): LiveData<ProfessionalEntity> {
        return repository.observeProfessionalById(id)
    }

    fun deleteProfessional(id: Long) {
        viewModelScope.launch {
            repository.deleteProfessional(id)
            _professionalStateEventData.value = ProfessionalState.Deleted
        }
    }

    sealed class ProfessionalState {
        object Inserted: ProfessionalState()
        object Updated: ProfessionalState()
        object Deleted: ProfessionalState()
    }

    sealed class OfficeState {
        data class Updated(val office: OfficeLocationEntity) : OfficeState()
    }

    companion object {
        private val TAG = ProfessionalViewModel::class.java.simpleName
    }
}