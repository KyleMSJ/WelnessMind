package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ifsp.project.welnessmind.data.db.entity.Pessoa

@Entity(tableName = "patient")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    override var name: String = "",
    override var email: String = "",
    var cpf: String = "",
    var idade: Int = 0,
    var estadoCivil: Int = 0,
    var rendaFamiliar: Int = 0,
    var escolaridade: Int = 0
) : Pessoa(name = "Patient_name", email = "Patient_email") {
    constructor() : this(0, "", "", "", 0, 0,0,0)
}