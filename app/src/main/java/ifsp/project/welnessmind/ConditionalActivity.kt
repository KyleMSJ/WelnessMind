package ifsp.project.welnessmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ConditionalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conditional)

        val buttonPacient = findViewById<Button>(R.id.btn_paciente)
        buttonPacient.setOnClickListener {
            val intent = Intent(this, PacienteActivity::class.java)
            startActivity(intent)
        }

        val buttonProfessional = findViewById<Button>(R.id.btn_profissional)
        buttonProfessional.setOnClickListener{
            val intent = Intent(this, ProfissionalActivity::class.java)
            startActivity(intent)
        }
    }
}