package ifsp.project.welnessmind.ui.cadastro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isFirstTime()) {
            Log.d(TAG, "Primeiro acesso, mostrando pop-up informativo.")
            showInfoPopup()
            setFirstTime(false)
        } else {
            Log.d(TAG, "Não é a primeira vez acessando a tela, não mostrando o pop-up")
        }
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnCadastro.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_conditionalFragment)
        }

        binding.btnLogin.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userType", "PACIENTE")

            findNavController().navigate(R.id.action_homeFragment_to_loginFragment, bundle)
        }
    }

    private fun showInfoPopup() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Info")
        builder.setMessage("Esse aplicativo interage com a plataforma do Google para realizar consultas e diagnósticos de saúde mental. " +
                "\n\nCertifique de verificar se possui uma conta do Google para continuar.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun isFirstTime(): Boolean {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val firstTime = sharedPref.getBoolean("first_time", true)
        Log.d(TAG, "isFirstTime() retornou $firstTime")
        return firstTime
    }

    private fun setFirstTime(isFirstTime: Boolean) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("first_time", isFirstTime)
            apply()
        }
        Log.d(TAG, "setFirstTime() definido para $isFirstTime")
    }
}