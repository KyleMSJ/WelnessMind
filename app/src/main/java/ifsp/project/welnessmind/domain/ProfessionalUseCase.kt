package ifsp.project.welnessmind.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfessionalUseCase (
    private val professionalDAO: ProfessionalDAO,
    private val officeLocationDAO: OfficeLocationDAO,
    firebaseDatabase: FirebaseDatabase
): ProfessionalRepository {

    private val professionalsRef = firebaseDatabase.getReference("professional")
    private val TAG = "ProfessionalUseCase"

    private suspend fun syncProfessionalToFirebase(professional: ProfessionalEntity) = withContext(Dispatchers.IO){
        professionalsRef.child(professional.id.toString()).setValue(professional)
            .addOnSuccessListener {
                Log.d("FirebaseSync", "Profissional sincronizado: ${professional.name}")
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseSync", "Falha ao sincronizar o profissional: ${exception.message}")
            }
    }

    override suspend fun insertProfessional(name: String, email: String, credenciais: String, especialidade: String): Long {
        val professional = ProfessionalEntity(
            name = name,
            email = email,
            credenciais = credenciais,
            especialidade = especialidade
        )

        val id = professionalDAO.insert(professional)

        syncProfessionalToFirebase(professional.apply { this.id = id })
        return id
    }

    override fun getAllProfessionals(): LiveData<List<ProfessionalEntity>> {
        return professionalDAO.getAll()
    }

    override suspend fun getProfessionalById(id: Long): ProfessionalEntity? {
        return professionalDAO.getProfessionalById(id)
    }

    override fun observeProfessionalById(id: Long): LiveData<ProfessionalEntity> {
        return professionalDAO.observeProfessionalById(id)
    }

    override suspend fun updateProfessional(
        id: Long,
        name: String,
        email: String,
        credenciais: String,
        especialidade: String
    ) {
        val professional = ProfessionalEntity(
            id = id,
            name = name,
            email = email,
            credenciais = credenciais,
            especialidade = especialidade
        )
        professionalDAO.update(professional)
        syncProfessionalToFirebase(professional)
    }

    override suspend fun deleteProfessional(id: Long) {
        professionalDAO.delete(id)
        professionalsRef.child(id.toString()).removeValue()
    }

    override suspend fun deleteAllProfessionals() {
        professionalDAO.deleteAll()
        professionalsRef.removeValue()
    }

    override suspend fun saveOfficeLocation(location: OfficeLocationEntity) {
        officeLocationDAO.insertOfficeLocation(location)
        syncOfficeLocationToFirebase(location)
    }

    override suspend fun getOfficeLocation(professionalId: Long): OfficeLocationEntity? {
        return officeLocationDAO.getOfficelocation(professionalId)
    }

    private suspend fun syncOfficeLocationToFirebase(location: OfficeLocationEntity) = withContext(Dispatchers.IO) {
        professionalsRef.child(location.id.toString()).child("office_location").setValue(location)
            .addOnSuccessListener {
                Log.d(TAG, "Sincronização do consultório com Firebase feito com sucesso!")
            }
            .addOnFailureListener {
                Log.d(TAG, "Erro ao sincronizar o consultório com Firebase!")
            }
    }
}