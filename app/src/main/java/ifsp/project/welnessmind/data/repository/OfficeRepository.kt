package ifsp.project.welnessmind.data.repository

import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity

interface OfficeRepository {
    suspend fun saveOfficeLocation(professionalId: Long, address: String, latitude: Double, longitude: Double, description: String, contact: String): Long

    suspend fun updateOfficeLocation(id: Long, professionalId: Long, address: String, latitude: Double, longitude: Double, description: String, contact: String)
    suspend fun getOfficeLocation(professionalId: Long): OfficeLocationEntity?
}