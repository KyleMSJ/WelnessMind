package ifsp.project.welnessmind.ui.list

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.databinding.FragmentProfessionalProfileBinding
import ifsp.project.welnessmind.domain.ProfessionalUseCase
import ifsp.project.welnessmind.ui.cadastro.ProfessionalViewModel

class ProfessionalProfileFragment : Fragment() {

    private var _binding: FragmentProfessionalProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfessionalViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO = AppDatabase.getInstance(requireContext()).professionalDao

                val repository: ProfessionalRepository = ProfessionalUseCase(professionalDAO)
                return ProfessionalViewModel(repository) as T
            }
        }
    }

    private val places = arrayListOf(
        Place(name = "Parque Lago Azul", LatLng = LatLng(-22.4015596, -47.5731215), address = "13500-512,, R. 2 A, 897-957 - Vila Aparecida, Rio Claro - SP"),
        Place(name = "IFSP - Câmpus Piracicaba", LatLng = LatLng(-22.6939676, -47.6260501), address = "Rua, Av. Diácono Jair de Oliveira, 1005 - Santa Rosa, Piracicaba - SP")
    )

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach() { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .snippet(place.address)
                    .position(place.LatLng)
            )
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->

        addMarkers(googleMap)

        googleMap.setOnMapLoadedCallback {
            val bounds = LatLngBounds.builder()

            places.forEach() {
                bounds.include(it.LatLng)

                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessionalProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val professionalId = arguments?.getLong("professional_id") ?: -1
        viewModel.getProfessionalById(professionalId).observe(viewLifecycleOwner) { professional ->
            displayProfessionalDetails(professional)
        }

        binding.btnConsulta.setOnClickListener {
            val intent = Intent()
            intent.data = Uri.parse("https://calendar.google.com/")
            startActivity(intent)
        }
    }

    private fun displayProfessionalDetails(professional: ProfessionalEntity) = with(binding) {
        textProName.text = professional.name
        textProEspecialidade.text = professional.especialidade
        textProEmail.text = professional.email
    }

    data class Place(
        val name: String,
        val LatLng: LatLng,
        val address: String
    )
}