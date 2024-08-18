package ifsp.project.welnessmind.ui.register.patient

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class PatientHomeViewModel(
    private val repository: PatientRepository,
) : ViewModel() {

    private val TAG = "PatientHomeViewModel"
    fun getPatientName(context: Context, callback: (String) -> Unit) = viewModelScope.launch {
        try {
                val userId = SharedPreferencesUtil.getUserId(context)
                Log.d(TAG, "ID encontrado: $userId")
                val patient = repository.getPatientById(userId)
                val patientName = patient?.name ?: "Nome não encontrado"
                callback(patientName)
                 Log.d(TAG, "Nome do paciente: $patientName, ID: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar o nome do paciente.", e)
        }
    }
}