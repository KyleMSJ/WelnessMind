package ifsp.project.welnessmind.presentation.cadastro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.databinding.FragmentProfessionalListBinding
import ifsp.project.welnessmind.domain.ProfessionalUseCase
import ifsp.project.welnessmind.presentation.cadastro.ProfessionalViewModel
import ifsp.project.welnessmind.presentation.list.ProfessionalListAdapter
import ifsp.project.welnessmind.presentation.list.ProfessionalListViewModel

class ProfessionalListFragment : Fragment(R.layout.fragment_professional_list) {

    private var _binding: FragmentProfessionalListBinding? = null
    private val binding get() = _binding!! //

    private val viewModel: ProfessionalListViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO =
                    AppDatabase.getInstance(requireContext()).professionalDao

                val repository: ProfessionalRepository = ProfessionalUseCase(professionalDAO)
                return ProfessionalListViewModel(repository) as T
            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessionalListBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModelEvents()

    }

    private fun observeViewModelEvents() {
        viewModel.allProfessionalsEvent.observe(viewLifecycleOwner) { allProfessionals ->
            val professionalListAdapter = ProfessionalListAdapter(allProfessionals)

            binding.recyclerProfessionals.run {
                setHasFixedSize(true)
                adapter = professionalListAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}