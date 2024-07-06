package ifsp.project.welnessmind.data.repository

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity

interface ProfessionalRepository {
    suspend fun insertProfessional(name: String, email: String, credenciais: String, especialidade: String): Long // 'suspend' - usada para execuções assíncronas sem interromper a thread principal (melhor performance e responsividade na UI)
    suspend fun  getAllProfessionals(): LiveData<List<ProfessionalEntity>>
    suspend fun  updateProfessional(id: Long, name: String, email: String, credenciais: String, especialidade: String)
    suspend fun deleteProfessional(id: Long)
    suspend fun deleteAllProfessionals()
}