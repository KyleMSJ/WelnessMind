package ifsp.project.welnessmind.domain

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
class ProfessionalUseCase (
    private val professionalDAO: ProfessionalDAO
): ProfessionalRepository {
    override suspend fun insertProfessional(name: String, email: String, credenciais: String, especialidade: String): Long {
        val professional = ProfessionalEntity(
            name = name,
            email = email,
            credenciais = credenciais,
            especialidade = especialidade
        )

        return professionalDAO.insert(professional)
    }

    override fun getAllProfessionals(): LiveData<List<ProfessionalEntity>> {
        return professionalDAO.getAll()
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
    }

    override suspend fun deleteProfessional(id: Long) {
        professionalDAO.delete(id)
    }

    override suspend fun deleteAllProfessionals() {
        professionalDAO.deleteAll()
    }
}