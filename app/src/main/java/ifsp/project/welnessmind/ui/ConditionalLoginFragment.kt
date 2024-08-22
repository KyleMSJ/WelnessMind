package ifsp.project.welnessmind.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.databinding.FragmentConditionalLoginBinding


class ConditionalLoginFragment : Fragment() {
    private var _binding: FragmentConditionalLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConditionalLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        binding.btnPaciente.setOnClickListener {
            bundle.putString("userType", "PACIENTE")
            findNavController().navigate(R.id.loginFragment, bundle)
        }

        binding.btnProfissional.setOnClickListener {
            bundle.putString("userType", "PROFISSIONAL")
            findNavController().navigate(R.id.loginFragment, bundle)
        }
    }
}