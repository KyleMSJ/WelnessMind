package ifsp.project.welnessmind.data.repository

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.room.Query
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity

interface ProfessionalRepository {
    suspend fun insertProfessional(name: String, email: String, credenciais: String, especialidade: String): Long // 'suspend' - usada para execuções assíncronas sem interromper a thread principal (melhor performance e responsividade na UI)
    fun  getAllProfessionals(): LiveData<List<ProfessionalEntity>>
    fun observeProfessionalById(id: Long): LiveData<ProfessionalEntity>
    suspend fun getProfessionalById(id: Long): ProfessionalEntity?
    suspend fun  updateProfessional(id: Long, name: String, email: String, credenciais: String, especialidade: String)
    suspend fun deleteProfessional(id: Long)
    suspend fun deleteAllProfessionals()
}