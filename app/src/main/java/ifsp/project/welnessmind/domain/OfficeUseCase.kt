package ifsp.project.welnessmind.domain

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.repository.OfficeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfficeUseCase(
    private val officeLocationDAO: OfficeLocationDAO,
    private val firebaseDatabase: FirebaseDatabase
): OfficeRepository {

    private fun getOfficeRef(professionalId: Long) = firebaseDatabase
        .getReference("professional")
        .child(professionalId.toString())
        .child("office_location")

    private suspend fun syncOfficeToFirebase(professionalId: Long, office: OfficeLocationEntity) = withContext(Dispatchers.IO) {
        val officeRef = getOfficeRef(professionalId)
        Log.d("FirebaseSync", "Sincronizando consultório para profissional $professionalId com dados: $office")

        val officeData = mapOf(
            "address" to office.address,
            "latitude" to office.latitude,
            "longitude" to office.longitude,
            "description" to office.description,
            "contact" to office.contact,
            "id" to office.id,
            "professional_id" to office.professional_id
        )
        officeRef.child(office.id.toString())
            .setValue(officeData)
            .addOnSuccessListener {
                Log.d("FirebaseSync", "Consultório sincronizado para o profissional $professionalId: Office ID ${office.id}")
            }
    }

    override suspend fun saveOfficeLocation(professionalId: Long, address: String, latitude: Double, longitude: Double, description: String, contact: String): Long {
        val office = OfficeLocationEntity(
            professional_id = professionalId,
            address = address,
            latitude = latitude,
            longitude = longitude,
            description = description,
            contact = contact
        )

        val id = officeLocationDAO.saveOfficeLocation(office)
        office.id = id

        syncOfficeToFirebase(professionalId, office)
        Log.d("OfficeUseCase", "ID: ${office.id}" +
                "\ndados: $office.")
        return id
    }

    override suspend fun updateOfficeLocation(
        id: Long,
        professionalId: Long,
        address: String,
        latitude: Double,
        longitude: Double,
        description: String,
        contact: String
    ) { withContext(Dispatchers.IO) {
        val office = OfficeLocationEntity(
            id ,
            professionalId,
            address,
            latitude,
            longitude,
            description,
            contact
        )

        officeLocationDAO.updateOfficeLocation(office)
        syncOfficeToFirebase(professionalId, office)
    }
    }

    override suspend fun getOfficeLocation(professionalId: Long): OfficeLocationEntity? {
        return officeLocationDAO.getOfficelocation(professionalId)
    }
}