package ifsp.project.welnessmind.presentation.cadastro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ifsp.project.welnessmind.ConditionalActivity
import ifsp.project.welnessmind.LoginActivity
import ifsp.project.welnessmind.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonRegister = findViewById<Button>(R.id.btn_cadastro)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, ConditionalActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin = findViewById<Button>(R.id.btn_profissional)
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

}