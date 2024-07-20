package ifsp.project.welnessmind.data.repository

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.entity.FormsEntity
interface FormsRepository {

    //  horasSono: Int,
    // val horasEstudo: Int,
    // val horasTrabalho: Int,
    // val fazAtividadeFisica: Boolean,
    // val descricaoAtivFisica: String,
    // val frequenciaAtivFisica: String,
    // val hobbies: String,
    // val tomaMedicamento: Boolean,
    // val descricaoMedicamento: String
    suspend fun insertForms(horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String)
    suspend fun getAllForms(): LiveData<List<FormsEntity>>
    suspend fun updateForms(id: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String)
    suspend fun deleteForms(id: Long)

}