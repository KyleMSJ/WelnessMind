package ifsp.project.welnessmind.ui.cadastro

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.util.SharedPreferencesUtil
import kotlinx.coroutines.launch
import java.lang.Exception


class PatientViewModel(
    private val repository: PatientRepository,
): ViewModel() {

    private val _patientStateEventData = MutableLiveData<PatientState>()
    val patientStateEventData: LiveData<PatientState>
        get() = _patientStateEventData

    private val _messageEventData = MutableLiveData<Int>()

    fun addPatient(name: String, email: String, cpf: String, idade: Int, estadoCivil: Int, rendaFamiliar: Int, escolaridade: Int, context: Context, callback: (Long) -> Unit) = viewModelScope.launch {
        try {
            val id = repository.insertPatient(name, email, cpf, idade, estadoCivil, rendaFamiliar, escolaridade)
            Log.d("PatientViewModel", "Inserted Patient ID: $id")
            if (id > 0) {
                SharedPreferencesUtil.saveUserId(context, id)
                _patientStateEventData.value = PatientState.Inserted
                callback(id)
            }
        } catch (ex: Exception) {
            _messageEventData.value = R.string.patient_error_to_insert
            Log.e(TAG, ex.toString())
        }

    }

    fun getAllPatients(callback: (LiveData<List<PatientEntity>>) -> Unit)  {
        viewModelScope.launch {
            val patients = repository.getAllPatients()
            callback(patients)
        }
    }

   /* fun updatePatient(patient: PatientEntity) {
        viewModelScope.launch {
            repository.updatePatient(patient)
        }
    }*/

  /*  fun deletePatient(id: Long) {
        viewModelScope.launch {
            repository.deletePatient(id)
        }
    }

    fun deleteAllPatients() {
        viewModelScope.launch {
            repository.deleteAllPatients()
        }
    }*/

    sealed class PatientState {
        object Inserted: PatientState()
    }

    companion object {
        private val TAG = PatientViewModel::class.java.simpleName
    }
}