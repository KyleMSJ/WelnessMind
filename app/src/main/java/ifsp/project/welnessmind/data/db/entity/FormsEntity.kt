package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forms")
data class FormsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val horasSono: Int,
    val horasEstudo: Int,
    val horasTrabalho: Int,
    val fazAtividadeFisica: Boolean,
    val descricaoAtivFisica: String,
    val frequenciaAtivFisica: String,
    val hobbies: String,
    val tomaMedicamento: Boolean,
    val descricaoMedicamento: String
    )