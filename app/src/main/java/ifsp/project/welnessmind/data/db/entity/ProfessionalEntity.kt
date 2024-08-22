package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "professional")
data class ProfessionalEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    override var name: String = "",
    override var email: String = "",
    var credenciais: String = "",
    var especialidade: String = ""
) : Pessoa(name = "Professional_name", email = "Professional_email") {
    constructor() : this(0, "", "", "", "") // Necessário para o correto funcionamento do Firebase
}