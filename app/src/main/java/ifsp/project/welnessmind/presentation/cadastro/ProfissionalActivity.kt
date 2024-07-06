package ifsp.project.welnessmind.presentation.cadastro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ifsp.project.welnessmind.LoginActivity
import ifsp.project.welnessmind.R

class ProfissionalActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etRegistro: EditText
    private lateinit var etEspecialidade: EditText

    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profissional)

        iniciaizaVariaveis()
        validaCampos()

        val textLogin = findViewById<TextView>(R.id.tv_login)
        textLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun iniciaizaVariaveis() {
        etNome = findViewById(R.id.professional_nome)
        etEmail = findViewById(R.id.professional_email)
        etRegistro = findViewById(R.id.num_registro)
        etEspecialidade = findViewById(R.id.especialidade)

        btnCadastrar = findViewById(R.id.btn_cadastroPro)
    }
    private fun validaCampos() {
        btnCadastrar.setOnClickListener { view ->
            if (etNome.text.isNullOrEmpty() || etEmail.text.isNullOrEmpty() || etRegistro.text.isNullOrEmpty() || etEspecialidade.text.isNullOrEmpty()) {
                Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
            }
            else {
                showSuccessPopup()
            }
            }
        } // TODO implementar essa lógica separado da activity (regra de negócio)

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
