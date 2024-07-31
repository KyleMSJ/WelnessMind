package ifsp.project.welnessmind.domain

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.repository.FormsRepository

class FormsUseCase(
    private val formsDAO: FormsDAO
): FormsRepository {
    override suspend fun insertForms(
        horasSono: Int,
        horasEstudo: Int,
        horasTrabalho: Int,
        fazAtividadeFisica: Boolean,
        descricaoAtivFisica: String,
        frequenciaAtivFisica: String,
        hobbies: String,
        tomaMedicamento: Boolean,
        descricaoMedicamento: String
    ): Long {
        val forms = FormsEntity(
            horasSono = horasSono,
            horasTrabalho = horasTrabalho,
            horasEstudo = horasEstudo,
            fazAtividadeFisica = fazAtividadeFisica,
            descricaoAtivFisica = descricaoAtivFisica,
            frequenciaAtivFisica = frequenciaAtivFisica,
            hobbies = hobbies,
            tomaMedicamento = tomaMedicamento,
            descricaoMedicamento = descricaoMedicamento
        )

        return formsDAO.insert(forms)
    }

    override fun getAllForms(): LiveData<List<FormsEntity>> {
        return formsDAO.getAll()
    }

    override suspend fun updateForms(id: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String) {
        val forms = FormsEntity(
            horasSono = horasSono,
            horasTrabalho = horasTrabalho,
            horasEstudo = horasEstudo,
            fazAtividadeFisica = fazAtividadeFisica,
            descricaoAtivFisica = descricaoAtivFisica,
            frequenciaAtivFisica = frequenciaAtivFisica,
            hobbies = hobbies,
            tomaMedicamento = tomaMedicamento,
            descricaoMedicamento = descricaoMedicamento
        )

        formsDAO.update(forms)
    }

    override suspend fun deleteForms(id: Long) {
        return formsDAO.delete(id)
    }
}

