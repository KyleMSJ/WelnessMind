package ifsp.project.welnessmind.presentation.cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ifsp.project.welnessmind.LoginActivity
import ifsp.project.welnessmind.R

class PacienteActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDoc: EditText
    private lateinit var etIdade: EditText

    private lateinit var radioButtonState: List<RadioButton>
    private lateinit var radioButtonRenda: List<RadioButton>
    private lateinit var radioButtonEscolaridade: List<RadioButton>

    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)

        inicializaVariaveis()
        trataRadioButtons()
        validaCampos()

        val textLogin = findViewById<TextView>(R.id.tv_login)
        textLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun inicializaVariaveis(){
        etNome = findViewById(R.id.user_nome)
        etEmail = findViewById(R.id.user_email)
        etDoc = findViewById(R.id.user_document)
        etIdade = findViewById(R.id.user_idade)

        radioButtonState = listOf<RadioButton>(
            findViewById(R.id.rb_solteiro),
            findViewById(R.id.rb_casado),
            findViewById(R.id.rb_separado),
            findViewById(R.id.rb_divorciado),
            findViewById(R.id.rb_viuvo)
        )

        radioButtonRenda = listOf<RadioButton>(
            findViewById(R.id.rb_renda1),
            findViewById(R.id.rb_renda2),
            findViewById(R.id.rb_renda3),
            findViewById(R.id.rb_renda4)
        )

        radioButtonEscolaridade = listOf<RadioButton>(
            findViewById(R.id.rb_escolaridade1),
            findViewById(R.id.rb_escolaridade2),
            findViewById(R.id.rb_escolaridade3),
            findViewById(R.id.rb_escolaridade4)
        )

        btnCadastrar = findViewById(R.id.btn_cadastroUser)
    }
    private fun trataRadioButtons(){
        radioButtonState.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonState.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonRenda.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonRenda.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }

        radioButtonEscolaridade.forEach { radioButton ->
            radioButton.setOnClickListener {
                radioButtonEscolaridade.forEach { it.isChecked = false }
                (it as RadioButton).isChecked = true
            }
        }
    }
    private fun validaCampos() {
        btnCadastrar.setOnClickListener{ view ->
            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etDoc.text.isNullOrEmpty() || etIdade.text.isNullOrEmpty() ||
                !radioButtonState.any{it.isChecked} || !radioButtonRenda.any{it.isChecked} || !radioButtonEscolaridade.any{it.isChecked} ) {
                Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
            }
            else {
                showSuccessPopup()
            }
        }
    }

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val dialog = builder.create()
        dialog.show()
    }
}
