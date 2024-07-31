package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "patient_password",
    foreignKeys = [ForeignKey(entity = PatientEntity::class, parentColumns = ["id"], childColumns = ["id"], onUpdate = CASCADE, onDelete = CASCADE)])
data class PatientPassword (
    @PrimaryKey
    val id: Long,
    val password: String
)
