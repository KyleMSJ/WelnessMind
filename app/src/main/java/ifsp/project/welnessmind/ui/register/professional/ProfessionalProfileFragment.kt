package ifsp.project.welnessmind.ui.register.professional

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.databinding.FragmentProfessionalProfileBinding
import ifsp.project.welnessmind.domain.ProfessionalUseCase

class ProfessionalProfileFragment : Fragment() {

    private var _binding: FragmentProfessionalProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfessionalViewModel by viewModels {
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO = AppDatabase.getInstance(requireContext()).professionalDao
                val officeLocationDAO: OfficeLocationDAO = AppDatabase.getInstance(requireContext()).officeLocationDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: ProfessionalRepository = ProfessionalUseCase(professionalDAO, officeLocationDAO, firebaseDatabase)
                return ProfessionalViewModel(repository) as T
            }
        }
    }

    private val places = arrayListOf(
//        Place(name = "Parque Lago Azul", LatLng = LatLng(-22.4015596, -47.5731215), address = "13500-512,, R. 2 A, 897-957 - Vila Aparecida, Rio Claro - SP"),
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

            places.forEach() { place ->
                bounds.include(place.LatLng)

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
            marcarConsulta(it)
        }
    }

     private fun marcarConsulta(view: View) = with(binding) {
        // Cria um Intent para inserir um evento no Calendar
         val packageManager = view.context.packageManager
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            // Opcionalmente, adicionar dados de exemplo para o evento do calendário
            putExtra(CalendarContract.Events.TITLE, "Consulta com ${textProName.text}")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Consultório")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 60 * 60 * 1000) // 1 hora a partir de agora
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 2 * 60 * 60 * 1000) // Duração de 2 horas
        }

         val calendarPackage = "com.google.android.apps.maps"
         val isInstalled = isAppInstalled(calendarPackage, packageManager)

        // Verifica se o app de Calendário está disponível
        if (isInstalled) {
            val chooser = Intent.createChooser(intent, "Escolha um aplicativo de calendário")
            startActivity(chooser)
        } else {
            Log.e("Calendar App", "Não foi possível encontrar o app")
            intent.data = Uri.parse("https://calendar.google.com/calendar/u/0/r/week")
            startActivity(intent)
        }
    }

    private fun isAppInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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