package ifsp.project.welnessmind.data.repository

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.entity.FormsEntity
interface FormsRepository {
    suspend fun insertForms(userId: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String): Long
    suspend fun getFormsById(userId: Long): FormsEntity?
    suspend fun updateForms(id: Long, userId: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String)
    suspend fun deleteForms(id: Long)

}