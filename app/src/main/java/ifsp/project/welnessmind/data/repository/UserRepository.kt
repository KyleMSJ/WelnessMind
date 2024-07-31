package ifsp.project.welnessmind.data.repository

import android.util.Log
import androidx.room.Transaction
import ifsp.project.welnessmind.ui.login.Result
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.PatientPasswordDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalPasswordDAO
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.PatientPassword
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalPassword
import ifsp.project.welnessmind.ui.login.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val patientDao: PatientDAO,
    private val professionalDao: ProfessionalDAO,
    private val patientPasswordDao: PatientPasswordDAO,
    private val professionalPasswordDao: ProfessionalPasswordDAO,
) {

    private var loggedInUser: LoggedInUser? = null

    @Transaction
    suspend fun generatePasswordForPatientById(userId: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            val existingUser = patientDao.getPatientById(userId)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))

            val password = generateRandomPassword()
            try {
                patientPasswordDao.insert(PatientPassword(id = existingUser.id, password = password))
                Log.d("UserRepository", "userPasswordId = ${patientPasswordDao.getPasswordByUserId(existingUser.id)}")
                Result.Success(password)
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao inserir senha: ${e.message})}")
                Result.Error(e)
            }
        }
    }

    @Transaction
    suspend fun generatePasswordForProfessionalById(userId: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            val existingUser = professionalDao.getProfessionalById(userId)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))

            val password = generateRandomPassword()
            try {
                professionalPasswordDao.insert(ProfessionalPassword(id = existingUser.id, password = password))
                Log.d("UserRepository", "userPasswordId = ${professionalPasswordDao.getPasswordByUserId(existingUser.id)}")
                Result.Success(password)
            } catch (e: Exception) {
                Log.e("UserRepository", "Erro ao inserir senha: ${e.message}")
                Result.Error(e)
            }
        }
    }

    suspend fun loginPatient(username: String, password: String): Result<PatientEntity> {
        return withContext(Dispatchers.IO) {
            val patient = patientDao.getPatientByEmail(username)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))
            Log.d("UserRepository", "patient value: ${patient}")

            // busca a senha armazenada para um determinado usuário no banco de dados
            val storedPassword = patientPasswordDao.getPasswordByUserId(patient.id)?.password // ?. password: acessa a propriedade password do objeto retornado. Se  retornar null (não há senha armazenada para aquele usuário), a expressão inteira também retorna null.
                ?: return@withContext Result.Error(Exception("Senha não encontrada"))

            Log.d("UserRepository", "Stored password for user $username: $storedPassword")

            return@withContext if (storedPassword == password) {
                loggedInUser = LoggedInUser(patient.id, patient.name)
                Result.Success(patient)
            } else {
                Log.e("UserRepository", "Invalid password for user $username")
                Result.Error(Exception("Senha inválida"))
            }
        }
    }

    suspend fun loginProfessional(username: String, password: String): Result<ProfessionalEntity> {
        return withContext(Dispatchers.IO) {
            val professional = professionalDao.getProfessionalByEmail(username)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))

            val storedPassword = professionalPasswordDao.getPasswordByUserId(professional.id)?.password
                ?: return@withContext Result.Error(Exception("Senha não encontrada"))

            Log.d("UserRepository", "Stored password for professional $username: $storedPassword")

            return@withContext if (storedPassword == password) {
                loggedInUser = LoggedInUser(professional.id, professional.name)
                Result.Success(professional)
            } else {
                Log.e("UserRepository", "Invalid password for professional $username")
                Result.Error(Exception("Senha inválida"))
            }
        }
    }

private fun generateRandomPassword(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..9)
        .map { allowedChars.random() }
        .joinToString("")
    }

}