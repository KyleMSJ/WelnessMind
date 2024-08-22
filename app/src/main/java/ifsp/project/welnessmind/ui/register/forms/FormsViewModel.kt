package ifsp.project.welnessmind.ui.register.forms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.repository.FormsRepository
import ifsp.project.welnessmind.data.repository.SyncRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FormsViewModel(
    private val repository: FormsRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _formsStateEventData = MutableLiveData<FormsState>()
    val formsStateEventData: LiveData<FormsState>
        get() = _formsStateEventData

    private val _messageEventData = MutableLiveData<Int>()

    fun addForms(
        userId: Long,
        horasSono: Int,
        horasEstudo: Int,
        horasTrabalho: Int,
        fazAtividadeFisica: Boolean,
        descricaoAtivFisica: String,
        frequenciaAtivFisica: String,
        hobbies: String,
        tomaMedicamento: Boolean,
        descricaoMedicamento: String
    ) = viewModelScope.launch {
        try {
            val id = repository.insertForms(
                userId,
                horasSono,
                horasEstudo,
                horasTrabalho,
                fazAtividadeFisica,
                descricaoAtivFisica,
                frequenciaAtivFisica,
                hobbies,
                tomaMedicamento,
                descricaoMedicamento
            )
            if (id > 0) {
                _formsStateEventData.value = FormsState.Inserted
            }
        } catch (ex: Exception) {
            _messageEventData.value = R.string.forms_error_to_insert
            Log.e(TAG, ex.toString())
        }
    }

    fun getFormsByUserId(userId: Long, callback: (FormsEntity?) -> Unit) = viewModelScope.launch {
        try {
            val syncResult = async { syncRepository.syncForms(userId) }
            syncResult.await()
            Log.d(TAG, "Buscando formulário para o paciente de ID: $userId")

            val forms = repository.getFormsById(userId)
            Log.d(TAG, "Formulário encontrado: $forms")

            callback(forms)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun updateForms(
        userId: Long,
        horasSono: Int,
        horasEstudo: Int,
        horasTrabalho: Int,
        fazAtividadeFisica: Boolean,
        descricaoAtivFisica: String,
        frequenciaAtivFisica: String,
        hobbies: String,
        tomaMedicamento: Boolean,
        descricaoMedicamento: String
    ) = viewModelScope.launch {
        try {
            syncRepository.syncForms(userId)
            val forms = repository.getFormsById(userId)
            forms?.let {
                it.horasSono = horasSono
                it.horasEstudo = horasEstudo
                it.horasTrabalho = horasTrabalho
                it.fazAtividadeFisica = fazAtividadeFisica
                it.descricaoAtivFisica = descricaoAtivFisica
                it.frequenciaAtivFisica = frequenciaAtivFisica
                it.hobbies = hobbies
                it.tomaMedicamento = tomaMedicamento
                it.descricaoMedicamento = descricaoMedicamento
            }
            Log.d(TAG, "Atualizando formulário com o ID: ${forms?.id}")
            repository.updateForms(
                forms!!.id,
                userId,
                horasSono,
                horasEstudo,
                horasTrabalho,
                fazAtividadeFisica,
                descricaoAtivFisica,
                frequenciaAtivFisica,
                hobbies,
                tomaMedicamento,
                descricaoMedicamento
            )
            _formsStateEventData.value = FormsState.Updated
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar o formulário: ${e.message}")
        }
    }

    sealed class FormsState {
        object Inserted : FormsState()
        object Updated : FormsState()
    }

    companion object {
        private val TAG = FormsViewModel::class.java.simpleName
    }
}