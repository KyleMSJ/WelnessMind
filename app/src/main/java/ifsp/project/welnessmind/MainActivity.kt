package ifsp.project.welnessmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ifsp.project.welnessmind.presentation.cadastro.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        // TODO Deixar apenas essa activity e o resto como fragment
        android.os.Handler().postDelayed({
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }

}