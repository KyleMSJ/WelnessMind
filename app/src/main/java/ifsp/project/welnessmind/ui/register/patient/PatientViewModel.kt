package ifsp.project.welnessmind.ui.register.patient

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

    // Insert
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

    //** Read Functions **//
    fun loadUserInfo(context: Context, callback: (Long) -> Unit) = viewModelScope.launch {
        try {
            val userId = SharedPreferencesUtil.getUserId(context)
            Log.d(TAG, "ID encontrado: $userId")
            val patient = repository.getPatientById(userId)
            val patientID = patient?.id ?: "ID não encontrado"
            callback(patientID as Long)
            if (patient != null) {
                _patientStateEventData.postValue(PatientState.Loaded(patient))
            } else {
                _patientStateEventData.postValue(PatientState.Error("Paciente não encontrado"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar informações do paciente: ${e.message}")
            _patientStateEventData.postValue(PatientState.Error("Erro ao carregar informações do paciente"))
        }
    }


    fun getMaritalStatusText(index: Int): String {
        return when (index) {
            0 -> "Solteiro(a)"
            1 -> "Casado(a)"
            2 -> "Separado(a)"
            3 -> "Divorciado(a)"
            4 -> "Viúvo(a)"
            else -> "Não especificado"
        }
    }

    fun getIncomeText(index: Int): String {
        return when (index) {
            0 -> "Até 2 S.M."
            1 -> "De 2 a 4 S.M."
            2 -> "De 4 a 7 S.M."
            3 -> "Mais de 7 S.M."
            else -> "Não especificado"
        }
    }

    fun getEducationText(index: Int): String {
        return when (index) {
            0 -> "Ensino Fundamental"
            1 -> "Ensino Médio"
            2 -> "Ensino Superior"
            3 -> "Pós-graduação"
            else -> "Não especificado"
        }
    }

    //** Update Functions **//

    fun updateName(newName: String) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.name = newName
            updatePatient(patient)
        }
    }

    fun updateEmail(newEmail: String) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.email = newEmail
            updatePatient(patient)
        }
    }

    fun updateCPF(newCPF: String) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.cpf = newCPF
            updatePatient(patient)
        }
    }
    fun updateAge(newAge: Int) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.idade = newAge
            updatePatient(patient)
        }
    }

    fun updateMaritalStatus(newStatus: Int) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.estadoCivil = newStatus
            updatePatient(patient)
        }
    }

    fun updateIncome(newIncome: Int) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.rendaFamiliar = newIncome
            updatePatient(patient)
        }
    }

    fun updateEducation(newEducation: Int) {
        val currentPatientState = _patientStateEventData.value
        if (currentPatientState is PatientState.Loaded) {
            val patient = currentPatientState.patient
            patient.escolaridade = newEducation
            updatePatient(patient)
        }
    }

    private fun updatePatient(patient: PatientEntity) = viewModelScope.launch {
        try {
            Log.d(TAG, "Atualizando paciente com ID: ${patient.id}")
            repository.updatePatient(patient.id, patient.name, patient.email, patient.cpf, patient.idade, patient.estadoCivil, patient.rendaFamiliar, patient.escolaridade)
            _patientStateEventData.value = PatientState.Updated // Atualiza o LiveData após a atualização no banco
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao atualizar informações do paciente: ${e.message}")
        }
    }
    fun deletePatient(id: Long) {
        viewModelScope.launch {
            repository.deletePatient(id)
            _patientStateEventData.value = PatientState.Deleted
        }
    }

    sealed class PatientState {
        data class Loaded(val patient: PatientEntity) : PatientState()
        object Inserted : PatientState()
        object Updated : PatientState()
        object Deleted : PatientState()
        data class Error(val message: String) : PatientState()
    }


    companion object {
        private val TAG = PatientViewModel::class.java.simpleName
    }
}