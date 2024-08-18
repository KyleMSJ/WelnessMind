package ifsp.project.welnessmind.ui.register.forms

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.repository.FormsRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class FormsViewModel(
    private val repository: FormsRepository
) : ViewModel() {

    private val _formsStateEventData = MutableLiveData<FormsState>()
    val formsStateEventData: LiveData<FormsState>
        get() = _formsStateEventData

    private val _messageEventData = MutableLiveData<Int>()

    fun addForms(userId: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String) = viewModelScope.launch {
        try {
            val id = repository.insertForms(userId, horasSono, horasEstudo, horasTrabalho, fazAtividadeFisica, descricaoAtivFisica, frequenciaAtivFisica, hobbies, tomaMedicamento, descricaoMedicamento)
            if (id > 0) {
                _formsStateEventData.value = FormsState.Inserted
            }
        } catch (ex: Exception) {
            _messageEventData.value = R.string.forms_error_to_insert
            Log.e(TAG, ex.toString())
        }
    }

    sealed class FormsState {
        object Inserted: FormsState()
    }

    companion object {
        private val TAG = FormsViewModel::class.java.simpleName
    }
}