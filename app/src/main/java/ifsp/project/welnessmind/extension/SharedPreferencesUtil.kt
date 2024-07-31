package ifsp.project.welnessmind.util

import android.content.Context

object SharedPreferencesUtil {

    private const val PREFS_NAME = "user_prefs"
    private const val USER_ID_KEY = "userId"

    fun saveUserId(context: Context, userId: Long) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong(USER_ID_KEY, userId)
            apply()
        }
    }

    fun getUserId(context: Context): Long {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getLong(USER_ID_KEY, -1)
    }
}
