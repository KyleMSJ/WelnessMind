package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "professional_password",
    foreignKeys = [ForeignKey(entity = ProfessionalEntity::class, parentColumns = ["id"], childColumns = ["id"], onUpdate = CASCADE, onDelete = CASCADE)])
data class ProfessionalPassword (
    @PrimaryKey
    val id: Long,
    val password: String
)