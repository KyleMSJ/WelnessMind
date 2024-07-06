package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "professional")
data class ProfessionalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    override var name: String,
    override var email: String,
    val credenciais: String,
    val especialidade: String
) : Pessoa(name = "Professional_name", email = "Professional_email")