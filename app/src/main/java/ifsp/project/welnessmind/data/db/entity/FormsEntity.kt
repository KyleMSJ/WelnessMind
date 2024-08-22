package ifsp.project.welnessmind.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "forms",
    foreignKeys = [
        ForeignKey(entity = PatientEntity::class, parentColumns = ["id"], childColumns = ["userId"], onUpdate = CASCADE, onDelete = CASCADE)
    ])
data class FormsEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val userId: Long = 0, // para associar o formulário ao paciente
    var horasSono: Int = 0,
    var horasEstudo: Int = 0,
    var horasTrabalho: Int = 0,
    var fazAtividadeFisica: Boolean = false,
    var descricaoAtivFisica: String = "",
    var frequenciaAtivFisica: String = "",
    var hobbies: String = "",
    var tomaMedicamento: Boolean = false,
    var descricaoMedicamento: String = ""
    ) {
    constructor() : this(0, 0, 0, 0, 0, false, "",
        "","", false,"")
}