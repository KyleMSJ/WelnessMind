package ifsp.project.welnessmind.data.repository

import android.content.Context
import android.util.Log
import androidx.annotation.ReturnThis
import androidx.room.Transaction
import com.google.firebase.database.FirebaseDatabase
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
import ifsp.project.welnessmind.ui.login.LoginViewModel
import ifsp.project.welnessmind.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(
    private val patientDao: PatientDAO,
    private val professionalDao: ProfessionalDAO,
    private val patientPasswordDao: PatientPasswordDAO,
    private val professionalPasswordDao: ProfessionalPasswordDAO,
) {

    private var loggedInUser: LoggedInUser? = null
    private val TAG = "UserRepository"

    @Transaction
    suspend fun generatePasswordForPatientById(userId: Long): Result<String> {
        return withContext(Dispatchers.IO) {
            val existingUser = patientDao.getPatientById(userId)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))

            val password = generateRandomPassword()
            try {
                patientPasswordDao.insert(PatientPassword(id = existingUser.id, password = password))
                Log.d(TAG, "userPasswordId = ${patientPasswordDao.getPasswordByUserId(existingUser.id)}")

                val database = FirebaseDatabase.getInstance().reference
                val patientRef = database.child("patient").child(existingUser.id.toString())
                patientRef.child("password").setValue(password)
                    .addOnSuccessListener {
                        Log.d(TAG, "Senha inserida no firebase para o usuário ${existingUser.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Erro ao atualizar senha no Firebase: ${e.message}")
                        return@addOnFailureListener
                    }
                Result.Success(password)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inserir senha: ${e.message})}")
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
                Log.d(TAG, "userPasswordId = ${professionalPasswordDao.getPasswordByUserId(existingUser.id)}")

                val database = FirebaseDatabase.getInstance().reference
                val professionalRef = database.child("professional").child(existingUser.id.toString())
                professionalRef.child("password").setValue(password)
                    .addOnSuccessListener {
                        Log.d(TAG, "Senha inserida no firebase para o usuário ${existingUser.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Erro ao atualizar senha no Firebase: ${e.message}")
                        return@addOnFailureListener
                    }
                        Result.Success(password)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inserir senha: ${e.message}")
                Result.Error(e)
            }
        }
    }

    suspend fun loginPatient(context: Context, username: String, password: String): Result<PatientEntity> {
        return withContext(Dispatchers.IO) {
            val patient = patientDao.getPatientByEmail(username)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))
            Log.d(TAG, "patient value: $patient")
            SharedPreferencesUtil.saveUserId(context, patient.id)
            // busca a senha armazenada para um determinado usuário no banco de dados
            var storedPassword = patientPasswordDao.getPasswordByUserId(patient.id)?.password // ?. password: acessa a propriedade password do objeto retornado. Se  retornar null (não há senha armazenada para aquele usuário), a expressão inteira também retorna null.

            if (storedPassword == null) {
                Log.d(TAG, "Senha não encontrada localmente, buscando no Firebase")
            }
            try {
                val passwordRef = FirebaseDatabase.getInstance().reference
                    .child("patient")
                    .child(patient.id.toString())
                    .child("password")

                val firebasePassword = passwordRef.get().await().getValue(String::class.java)

                if (firebasePassword != null) {
                    Log.d(TAG, "Senha encontrada no Firebase para o usuário $username")
                    storedPassword = firebasePassword

                    patientPasswordDao.insert(
                        PatientPassword(
                            id = patient.id,
                            password = firebasePassword
                        )
                    )
                } else {
                    return@withContext Result.Error(Exception("Senha não encontrada no Firebase"))
                }
            }
            catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar senha no Firebase: ${e.message}")
                return@withContext Result.Error(Exception("Erro ao buscar senha no Firebase"))
            }
            Log.d(TAG, "Senha armazenada para o usuário: $username: $storedPassword")

            return@withContext if (storedPassword == password) {
                loggedInUser = LoggedInUser(patient.id, patient.name)
                Result.Success(patient)
            } else {
                Log.e(TAG, "Invalid password for user $username")
                Result.Error(Exception("Senha inválida"))
            }
        }
    }

    suspend fun loginProfessional(context: Context, username: String, password: String): Result<ProfessionalEntity> {
        return withContext(Dispatchers.IO) {
            val professional = professionalDao.getProfessionalByEmail(username)
                ?: return@withContext Result.Error(Exception("Usuário não encontrado"))
            Log.d(TAG, "professional value: $professional")
            SharedPreferencesUtil.saveUserId(context, professional.id)
            var storedPassword = professionalPasswordDao.getPasswordByUserId(professional.id)?.password

            if (storedPassword == null) {
                Log.d(TAG, "Senha não encontrada localmente, buscando no Firebase")
                try {
                    val passwordRef = FirebaseDatabase.getInstance().reference
                        .child("professional")
                        .child(professional.id.toString())
                        .child("password")

                    val firebasePassword = passwordRef.get().await().getValue(String::class.java)

                    if (firebasePassword != null) {
                        Log.d(TAG, "Senha encontrada no Firebase para o usuário $username")
                        storedPassword = firebasePassword

                        // Armazena a senha localmente para acesso offline no futuro
                        professionalPasswordDao.insert(
                            ProfessionalPassword(
                                id = professional.id,
                                password = firebasePassword
                            )
                        )
                    } else {
                        return@withContext Result.Error(Exception("Senha não encontrada no Firebase"))
                    }
                }
                catch (e: Exception) {
                    Log.e(TAG, "Erro ao buscar senha no Firebase: ${e.message}")
                    return@withContext Result.Error(Exception("Erro ao buscar senha no Firebase"))
                }
            }
            Log.d(TAG, "Senha armazenada para o profissional: $username: $storedPassword")

            return@withContext if (storedPassword == password) {
                loggedInUser = LoggedInUser(professional.id, professional.name)
                Result.Success(professional)
            } else {
                Log.e(TAG, "Invalid password for professional $username")
                Result.Error(Exception("Senha inválida"))
            }
        }
    }

    suspend fun retrievePasswordByEmail(username: String, userType: LoginViewModel.UserType): String? {
        return withContext(Dispatchers.IO) {
            try {
                val storedPassword = when (userType) {
                    LoginViewModel.UserType.PACIENTE -> {
                        val patient = patientDao.getPatientByEmail(username)
                            ?: return@withContext null

                        var password = patientPasswordDao.getPasswordByUserId(patient.id)?.password

                        if (password == null) {
                            val passwordRef = FirebaseDatabase.getInstance().reference
                                .child("patient")
                                .child(patient.id.toString())
                                .child("password")

                            password = passwordRef.get().await().getValue(String::class.java)
                        }
                        password
                    }
                    LoginViewModel.UserType.PROFISSIONAL -> {
                        val professional = professionalDao.getProfessionalByEmail(username)
                            ?: return@withContext null

                        var password = professionalPasswordDao.getPasswordByUserId(professional.id)?.password

                        if (password == null) {
                            val passwordRef = FirebaseDatabase.getInstance().reference
                                .child("professional")
                                .child(professional.id.toString())
                                .child("password")

                            password = passwordRef.get().await().getValue(String::class.java)
                        }
                        password
                    }
                }

                storedPassword
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar senha no Firebase: ${e.message}")
                null
            }
        }
    }



    private fun generateRandomPassword(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..5)
        .map { allowedChars.random() }
        .joinToString("")
    }

}