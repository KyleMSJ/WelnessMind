package ifsp.project.welnessmind.domain

import android.location.Address
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
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

        val professionalRef = FirebaseDatabase.getInstance().getReference("professional").child(id.toString())
        var existingPassword: String? = null

        professionalRef.child("password").get().addOnSuccessListener { snapshot ->
            existingPassword = snapshot.getValue(String::class.java)
        }.await()

        val professional = ProfessionalEntity(
            id = id,
            name = name,
            email = email,
            credenciais = credenciais,
            especialidade = especialidade
        )

        Log.d(TAG, "Atualizando profissional: $professional")
        professionalDAO.update(professional)
        Log.d(TAG, "Profissional atualizado no banco de dados local")
        syncProfessionalToFirebase(professional)

        val officeLocationRef = professionalRef.child("office_location")
        officeLocationDAO.getOfficelocation(id)?.let { officeLocation ->
            val officeData = mapOf(
                "id" to officeLocation.id,
                "professional_id" to officeLocation.professional_id,
                "address" to officeLocation.address,
                "latitude" to officeLocation.latitude,
                "longitude" to officeLocation.longitude,
                "description" to officeLocation.description,
                "contact" to officeLocation.contact
            )
            officeLocationRef.setValue(officeData)
            Log.d(TAG, "Localização do consultório atualizada no Firebase: $officeData")
        }

        Log.d(TAG, "Profissional sincronizado com Firebase")
        professionalRef.child("password").setValue(existingPassword)
    }

    override suspend fun deleteProfessional(id: Long) {
        professionalDAO.delete(id)
        professionalsRef.child(id.toString()).removeValue()
    }

    override suspend fun deleteAllProfessionals() {
        professionalDAO.deleteAll()
        professionalsRef.removeValue()
    }


}