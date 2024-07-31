package ifsp.project.welnessmind.ui.cadastro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.databinding.FragmentConditionalBinding

class ConditionalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentConditionalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConditionalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPaciente.setOnClickListener {
            findNavController().navigate(R.id.action_conditionalFragment_to_patientFragment)
        }

        binding.btnProfissional.setOnClickListener {
            findNavController().navigate(R.id.action_conditionalFragment_to_professionalFragment)
        }
    }
}