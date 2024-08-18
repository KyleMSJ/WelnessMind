package ifsp.project.welnessmind.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forms")
data class FormsEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(defaultValue = "0")
    val userId: Long = 0, // para associar o formulário ao paciente
    val horasSono: Int = 0,
    val horasEstudo: Int = 0,
    val horasTrabalho: Int = 0,
    val fazAtividadeFisica: Boolean = false,
    val descricaoAtivFisica: String = "",
    val frequenciaAtivFisica: String = "",
    val hobbies: String = "",
    val tomaMedicamento: Boolean = false,
    val descricaoMedicamento: String = ""
    ) {
    constructor() : this(0, 0, 0, 0, 0, false, "",
        "","", false,"")
}