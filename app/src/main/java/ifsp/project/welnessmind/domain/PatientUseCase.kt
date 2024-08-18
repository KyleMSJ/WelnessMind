package ifsp.project.welnessmind.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.repository.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PatientUseCase ( // encapsula um caso de uso específico da aplicação. Ele contém a lógica de negócios e a lógica de aplicação. Responsabilidade: Servir como intermediário entre a camada de apresentação (ViewModel) e a camada de dados (Repository).
    private val patientDAO: PatientDAO,
    firebaseDatabase: FirebaseDatabase
): PatientRepository {

    private val patientsRef = firebaseDatabase.getReference("patient")

    private suspend fun syncPatientToFirebase(patient: PatientEntity) =
        withContext(Dispatchers.IO) {
            patientsRef.child(patient.id.toString()).setValue(patient)
                .addOnSuccessListener {
                    Log.d("FirebaseSync", "Paciente sincronizado: ${patient.name}")
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseSync", "Falha ao sincronizar o paciente: ${exception.message}")
                }
        }

    override suspend fun insertPatient(
        name: String,
        email: String,
        cpf: String,
        idade: Int,
        estadoCivil: Int,
        rendaFamiliar: Int,
        escolaridade: Int
    ): Long {
        val patient = PatientEntity(
            name = name,
            email = email,
            cpf = cpf,
            idade = idade,
            estadoCivil = estadoCivil,
            rendaFamiliar = rendaFamiliar,
            escolaridade = escolaridade
        )

        val id = patientDAO.insert(patient)

        syncPatientToFirebase(patient.apply { this.id = id })
        return id
    }


    override fun getAllPatients(): LiveData<List<PatientEntity>> {
        return patientDAO.getAll()
    }

    override suspend fun getPatientById(id: Long): PatientEntity? {
        return patientDAO.getPatientById(id)
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

        // Recupera a senha existente no Firebase
        val patientRef = FirebaseDatabase.getInstance().getReference("patient").child(id.toString())
        var existingPassword: String? = null

        patientRef.child("password").get().addOnSuccessListener { snapshot ->
            existingPassword = snapshot.getValue(String::class.java)
        }.await()

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

        Log.d("PatientUseCase", "Atualizando paciente: $patient")

        patientDAO.update(patient)
        Log.d("PatientUseCase", "Paciente atualizado no banco de dados local")

        syncPatientToFirebase(patient)
        Log.d("PatientUseCase", "Paciente sincronizado com Firebase")
        patientRef.child("password").setValue(existingPassword)
    }


    override suspend fun deletePatient(id: Long) {
        patientDAO.delete(id)
        patientsRef.child(id.toString()).removeValue()
    }

    override suspend fun deleteAllPatients() {
        patientDAO.deleteAll()
        patientsRef.removeValue()
    }
}