package ifsp.project.welnessmind.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient")
data class PatientEntity( // data class trabalham apenas com dados sem o uso de funções (servem como registro de dados)
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    override var name: String,
    override var email: String,
    val cpf: String,
    val idade: Int,
    val estadoCivil: Int,
    val rendaFamiliar: Int,
    val escolaridade: Int
) : Pessoa(name = "Patient_name", email = "Patient_email")