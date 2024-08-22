package ifsp.project.welnessmind.ui.register

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.repository.OfficeRepository
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.data.repository.SyncRepository
import ifsp.project.welnessmind.databinding.FragmentOfficeRegisterBinding
import ifsp.project.welnessmind.domain.OfficeUseCase
import ifsp.project.welnessmind.domain.ProfessionalUseCase
import ifsp.project.welnessmind.ui.register.professional.ProfessionalViewModel
import java.util.Locale

class OfficeRegisterFragment : Fragment() {

    private var _binding: FragmentOfficeRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var tvSelectedLocation: TextView
    private lateinit var etAddress: EditText
    private lateinit var btnSaveLocation: Button
    private lateinit var etDescricao: EditText
    private lateinit var etContato: EditText
    private var selectedLatLng: LatLng? = null

    private val viewModel: ProfessionalViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO: ProfessionalDAO =
                    AppDatabase.getInstance(requireContext()).professionalDao
                val officeLocationDAO: OfficeLocationDAO =
                    AppDatabase.getInstance(requireContext()).officeLocationDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val formsDao: FormsDAO = AppDatabase.getInstance(requireContext()).formsDao
                val repository: ProfessionalRepository =
                    ProfessionalUseCase(professionalDAO, officeLocationDAO, firebaseDatabase)
                val officeRepository: OfficeRepository = OfficeUseCase(officeLocationDAO, firebaseDatabase)
                val syncRepository = SyncRepository(null, professionalDAO, formsDao, officeLocationDAO, firebaseDatabase)
                return ProfessionalViewModel(repository, officeRepository, syncRepository) as T
            }
        }
    }
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions().position(latLng).title("Localização do Consultório")
            )
            selectedLatLng = latLng
        }

        val defaultLocation =
            LatLng(-22.9068, -47.0626) // Por exemplo, coordenadas de São Paulo, Brasil
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfficeRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        tvSelectedLocation = binding.tvSelectedLocation
        etAddress = binding.etAddress
        btnSaveLocation = binding.btnAvancar
        etDescricao = binding.etDescricao
        etContato = binding.etInfoContato

        val professionalId = arguments?.getLong("professional_id") ?: -1L
        Log.d("OfficeRegisterFragment", "ID do profissional: $professionalId")

        btnSaveLocation.setOnClickListener {
            val address = etAddress.text.toString()
            val description = etDescricao.text.toString()
            val contact = etContato.text.toString()

            Log.d("OfficeRegisterFragment", "Endereço: $address, Descrição: $description, Contato: $contact")
            if (address.isNotEmpty()) {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocationName(address, 1)

                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val location = addresses[0]
                        selectedLatLng = LatLng(location.latitude, location.longitude)

                        googleMap.clear()
                        googleMap.addMarker(
                            MarkerOptions().position(selectedLatLng!!)
                                .title("Localização do Consultório")
                        )
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                selectedLatLng!!,
                                15f
                            )
                        )
                        "Localização: ${location.latitude}, ${location.longitude}".also { tvSelectedLocation.text = it }

                        Log.d("OfficeRegisterFragment", "Salvando: Endereço: $address, Descrição: $description, Contato: $contact")
                        viewModel.saveOfficeLocation(professionalId, address, location.latitude, location.longitude, description, contact)

                        val bundle = Bundle()
                        bundle.apply {
                            putBoolean("isProfessionalMode", true)
                            putLong("professional_id", professionalId)
                        }
                        findNavController().navigate(R.id.action_officeRegisterFragment_to_professionalProfileFragment, bundle)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}