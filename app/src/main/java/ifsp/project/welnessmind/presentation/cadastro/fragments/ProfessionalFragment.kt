package ifsp.project.welnessmind.presentation.cadastro.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import ifsp.project.welnessmind.LoginActivity
import ifsp.project.welnessmind.databinding.FragmentProfessionalBinding

class ProfessionalFragment : Fragment() {

private var _binding: FragmentProfessionalBinding? = null
    private val binding get() = _binding!!

    private lateinit var etNome: TextInputLayout
    private lateinit var etEmail: TextInputLayout
    private lateinit var etRegistro: TextInputLayout
    private lateinit var etEspecialidade: TextInputLayout

    private lateinit var btnCadastrar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessionalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializaVariaveis()
        validaCampos()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun inicializaVariaveis() {
        etNome = binding.professionalNome
        etEmail = binding.professionalEmail
        etRegistro = binding.numRegistro
        etEspecialidade = binding.especialidade

        binding.btnCadastroPro
    }
    private fun validaCampos() {
        binding.btnCadastroPro.setOnClickListener { view ->
            if (etNome.editText?.text.isNullOrEmpty() || etEmail.editText?.text.isNullOrEmpty() || etRegistro.editText?.text.isNullOrEmpty() || etEspecialidade.editText?.text.isNullOrEmpty()) {
                Snackbar.make(view, "Preencha todas as informações", Snackbar.LENGTH_SHORT).show()
            }
            else {
                showSuccessPopup()
            }
        }
    } // TODO implementar essa lógica separado da activity (regra de negócio)

    private fun showSuccessPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sucesso")
        builder.setMessage("Cadastro realizado com sucesso!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}