package ifsp.project.welnessmind.domain

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.repository.PatientRepository

// Paralelo ao 'DatabaseDataSource' do my-subscribers-app
class PatientUseCase ( // encapsula um caso de uso específico da aplicação. Ele contém a lógica de negócios e a lógica de aplicação. Responsabilidade: Servir como intermediário entre a camada de apresentação (ViewModel) e a camada de dados (Repository).
    private val patientDAO: PatientDAO
): PatientRepository {
    override suspend fun insertPatient(name: String, email: String, cpf: String, idade: Int, estadoCivil: Int, rendaFamiliar: Int, escolaridade: Int): Long {
        val patient = PatientEntity(
            name = name,
            email = email,
            cpf = cpf,
            idade = idade,
            estadoCivil = estadoCivil,
            rendaFamiliar = rendaFamiliar,
            escolaridade = escolaridade
        )

        return patientDAO.insert(patient)
    }

    // TODO ver o uso de 'Flow' do Kotlin coroutines ao invés do LiveData
    override suspend fun getAllPatients(): LiveData<List<PatientEntity>> {
        return patientDAO.getAll()
    }

    override suspend fun updatePatient(
        id: Long,
        name: String,
        email: String,
        cpf: String,
        idade: Int,
        estadoCivil: Int,
        rendaFamiliar: Int,
        escolaridade: Int
    ) {
        val patient = PatientEntity(
            id = id,
            name = name,
            email = email,
            cpf = cpf,
            idade = idade,
            estadoCivil = estadoCivil,
            rendaFamiliar = rendaFamiliar,
            escolaridade = escolaridade
        )
        patientDAO.update(patient)
    }

    override suspend fun deletePatient(id: Long) {
        return patientDAO.delete(id)
    }

    override suspend fun deleteAllPatients() {
        return patientDAO.deleteAll()
    }
}