package ifsp.project.welnessmind.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun generatePasswordForUserId(userId: Long, userType: UserType) {
        viewModelScope.launch {

            Log.d("LoginViewModel", "Generating password for userId: $userId, userType: $userType")
            val result = when (userType) {
                UserType.PACIENTE -> userRepository.generatePasswordForPatientById(userId)
                UserType.PROFISSIONAL -> userRepository.generatePasswordForProfessionalById(userId)
            }
            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = "", password = result.data)   )
            } else {
                Log.e("LoginViewModel", "Error generating password")
                _loginResult.value = LoginResult(error = R.string.password_generation_failed)
            }
        }
    }

    fun login(context: Context, username: String, password: String, userType: UserType) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "Attempting login for username: $username, userType: $userType")
            val result = when (userType) {
                UserType.PACIENTE -> userRepository.loginPatient(context, username, password)
                UserType.PROFISSIONAL -> userRepository.loginProfessional(context, username, password)
            }
            Log.d("LoginViewModel", "Login result: $result")
            when (result) {
                is Result.Success -> _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.name))
                is Result.Error -> {
                    Log.e("LoginViewModel", "Login failed for username: $username, userType: $userType")
                    val errorMessage = when (result.exception.message) {
                        "Email conflict" -> R.string.email_conflict_error
                        "Senha inválida" -> R.string.invalid_password_error
                        else -> R.string.login_failed
                    }
                    _loginResult.value = LoginResult(error = errorMessage)
                }
            }
        }
    }

    fun retrievePassword(username: String, userType: UserType, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val password = when (userType) {
                UserType.PACIENTE -> userRepository.retrievePasswordByEmail(username, userType)
                UserType.PROFISSIONAL -> userRepository.retrievePasswordByEmail(username, userType)
            }
            callback(password)
        }
    }

    data class LoginFormState(
        val usernameError: Int? = null,
        val passwordError: Int? = null,
        val isDataValid: Boolean = false
    )

    data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
    )

    data class LoggedInUserView(
        val displayName: String,
        val password: String? = null
    )

    enum class UserType {
        PACIENTE, PROFISSIONAL
    }
}
