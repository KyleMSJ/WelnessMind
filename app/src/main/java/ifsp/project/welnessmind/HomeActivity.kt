package ifsp.project.welnessmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val buttonRegister = findViewById<Button>(R.id.btn_cadastro)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, ConditionalActivity::class.java)
            startActivity(intent)
        }

        // TODO
//        val buttonLogin = findViewById<Button>(R.id.btn_cadastro)
//        buttonLogin.setOnClickListener {
//            val intent = Intent(this, ConditionalActivity::class.java)
//            startActivity(intent)
//        }
        }



}