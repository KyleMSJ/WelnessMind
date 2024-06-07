package ifsp.project.welnessmind

import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class PacienteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)

        trataRadioButtons()
    }

    //TODO - validação de campos sem preencher
    fun trataRadioButtons(){
        val radioButtonsState = listOf<RadioButton>(
            findViewById(R.id.rb_solteiro),
            findViewById(R.id.rb_casado),
            findViewById(R.id.rb_separado),
            findViewById(R.id.rb_divorciado),
            findViewById(R.id.rb_viuvo)
        )

        val radioButtonsRenda = listOf<RadioButton>(
            findViewById(R.id.rb_renda1),
            findViewById(R.id.rb_renda2),
            findViewById(R.id.rb_renda3),
            findViewById(R.id.rb_renda4)
        )

        val radioButtonsEscolaridade = listOf<RadioButton>(
            findViewById(R.id.rb_escolaridade1),
            findViewById(R.id.rb_escolaridade2),
            findViewById(R.id.rb_escolaridade3),
            findViewById(R.id.rb_escolaridade4)
        )

        radioButtonsState.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonsState.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonsRenda.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonsRenda.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonsEscolaridade.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonsEscolaridade.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }
    }
}
