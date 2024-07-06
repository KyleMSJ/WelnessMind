package ifsp.project.welnessmind.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.repository.PatientRepository
import ifsp.project.welnessmind.domain.PatientUseCase
import kotlinx.coroutines.launch


class PatientViewModel(
    private val repository: PatientRepository,
    private val useCase: PatientUseCase
): ViewModel() {

    fun createPatient(name: String, email: String, cpf: String, idade: Int, estadoCivil: Int, rendaFamiliar: Int, escolaridade: Int) = viewModelScope.launch {
        repository.insertPatient(name, email, cpf, idade, estadoCivil, rendaFamiliar, escolaridade)
    }

    fun getAllPatients(callback: (LiveData<List<PatientEntity>>) -> Unit)  {
        viewModelScope.launch {
            val patients = repository.getAllPatients()
            callback(patients)
        }
    }

  /*  fun updatePatient(patient: PatientEntity) {
        viewModelScope.launch {
            useCase.updatePatient(patient)
        }
    }*/
}