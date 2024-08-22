package ifsp.project.welnessmind.ui.register.professional

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.R
import ifsp.project.welnessmind.data.db.AppDatabase
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.data.repository.OfficeRepository
import ifsp.project.welnessmind.data.repository.ProfessionalRepository
import ifsp.project.welnessmind.data.repository.SyncRepository
import ifsp.project.welnessmind.databinding.FragmentProfessionalProfileBinding
import ifsp.project.welnessmind.domain.OfficeUseCase
import ifsp.project.welnessmind.domain.ProfessionalUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfessionalProfileFragment : Fragment() {

    private var _binding: FragmentProfessionalProfileBinding? = null
    private val binding get() = _binding!!
    private var isProfessionalMode: Boolean = false

    private val job = Job()

    private val viewModel: ProfessionalViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val professionalDAO = AppDatabase.getInstance(requireContext()).professionalDao
                val officeLocationDAO: OfficeLocationDAO =
                    AppDatabase.getInstance(requireContext()).officeLocationDao
                val formsDAO = AppDatabase.getInstance(requireContext()).formsDao
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val repository: ProfessionalRepository =
                    ProfessionalUseCase(professionalDAO, officeLocationDAO, firebaseDatabase)
                val officeRepository: OfficeRepository =
                    OfficeUseCase(officeLocationDAO, firebaseDatabase)
                val syncRepository =
                    SyncRepository(null, professionalDAO, formsDAO, officeLocationDAO, firebaseDatabase)
                return ProfessionalViewModel(repository, officeRepository, syncRepository) as T
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

        isProfessionalMode = arguments?.getBoolean("isProfessionalMode") ?: false

        setupUI()
        val callback = OnMapReadyCallback { googleMap ->
            googleMap.setOnMapLoadedCallback {
            }
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        var professionalId = arguments?.getLong("professional_id") ?: -1
        if (isProfessionalMode) professionalId = viewModel.getProfessionalID(requireContext())
        viewModel.getProfessionalById(professionalId).observe(viewLifecycleOwner) { professional ->
            displayProfessionalDetails(professional)
        }

        viewModel.officeStateEventData.observe(viewLifecycleOwner) { officeLocation ->
            binding.tvDescricao.text = officeLocation.description
            binding.tvInfoContato.text = officeLocation.contact
        }

        viewModel.getOfficeLocation(professionalId) { location ->
            location?.let {
                updateMapWithLocation(it)
                if (isProfessionalMode) {
                    updateInfoCompl(professionalId)
                }
                Log.d(
                    "Teste", "" +
                            "Localização: ${location.address}\nDescrição: ${location.description}\nContato: ${location.contact}"
                )
            } ?: run {
                Log.d("ProfessionalProfileFragment", "professional ID: $professionalId")
                Toast.makeText(requireContext(), "Localização não encontrada", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnConsulta.setOnClickListener {
            marcarConsulta(it)
        }

        binding.icCalendar.setOnClickListener {
            marcarConsulta(it)
        }

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                saveProfileChanges()
            }

            val delete = binding.ivDelete
            delete.setOnClickListener {
                showWarningDialog(professionalId)
            }
        }

    }

    private fun showWarningDialog(userID: Long) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ALERTA!")
        builder.setMessage("Você está prestes a apagar este usuário, e todas as suas informações... está certo disso?")
        builder.setPositiveButton("Sim") { dialog, _ ->
            viewModel.deleteProfessional(id = userID)
            dialog.dismiss()
            findNavController().navigate(R.id.homeFragment)
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupUI() = with(binding) {
        if (isProfessionalMode) {
            editProName.visibility = View.VISIBLE
            editProCredenciais.visibility = View.VISIBLE
            editProEspecialidade.visibility = View.VISIBLE
            editProEmail.visibility = View.VISIBLE
            icCalendar.visibility = View.VISIBLE
            icNotes.visibility = View.VISIBLE
            btnSave.visibility = View.VISIBLE
            tvDescricao.visibility = View.VISIBLE
            tvInfoContato.visibility = View.VISIBLE
            ivDelete.visibility = View.VISIBLE
            textProName.visibility = View.GONE
            textProCrenciais.visibility = View.GONE
            textProEspecialidade.visibility = View.GONE
            textProEmail.visibility = View.GONE
            btnConsulta.visibility = View.GONE
        } else {
            textProName.visibility = View.VISIBLE
            textProCrenciais.visibility = View.VISIBLE
            textProEspecialidade.visibility = View.VISIBLE
            textProEmail.visibility = View.VISIBLE
            btnConsulta.visibility = View.VISIBLE
            tvDescricao.visibility = View.VISIBLE
            tvInfoContato.visibility = View.VISIBLE
            editProName.visibility = View.GONE
            editProEspecialidade.visibility = View.GONE
            editProCredenciais.visibility = View.GONE
            editProEmail.visibility = View.GONE
            icCalendar.visibility = View.GONE
            icNotes.visibility = View.GONE
            btnSave.visibility = View.GONE
            ivDelete.visibility = View.GONE
        }
    }

    private suspend fun saveProfileChanges() = with(binding) {
        val name = editProName.text.toString()
        val email = editProEmail.text.toString()
        val credentials = editProCredenciais.text.toString()
        val specialty = editProEspecialidade.text.toString()

        // Atualiza os dados no banco de dados
        val id = viewModel.getProfessionalID(requireContext())
        viewModel.updateProfessionalDetails(id, name, email, credentials, specialty)

        // Mostra feedback para o profissional
        Toast.makeText(requireContext(), "Informações atualizadas com sucesso", Toast.LENGTH_SHORT)
            .show()

        // Atualiza UI para refletir os dados salvos
        textProName.text = name
        textProEspecialidade.text = specialty
        textProEmail.text = email
    }

    private fun updateInfoCompl(professionalID: Long) {
        binding.tvDescricao.setOnClickListener {
            showDescEditDialog(professionalID)
        }
        binding.tvInfoContato.setOnClickListener {
            showContactEditDalog(professionalID)
        }
    }

    private fun showDescEditDialog(professionalID: Long) {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Descrição")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newDescription = editText.text.toString()
                viewModel.updateDescription(professionalID, newDescription)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun showContactEditDalog(professionalID: Long) {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Contato")
            .setView(editText)
            .setPositiveButton("Salvar") { _, _ ->
                val newContact = editText.text.toString()
                viewModel.updateContact(professionalID, newContact)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun updateMapWithLocation(location: OfficeLocationEntity) {
        val latLng = LatLng(location.latitude, location.longitude)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            googleMap.addMarker(MarkerOptions().position(latLng).title(location.address))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
        "Descrição: ${location.description}".also { binding.tvDescricao.text = it }
        "Contato: ${location.contact}".also { binding.tvInfoContato.text = it }
    }

    private fun marcarConsulta(view: View) = with(binding) {
        // Cria um Intent para inserir um evento no Calendar
        val packageManager = view.context.packageManager
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            // Opcionalmente, adicionar dados de exemplo para o evento do calendário
            putExtra(CalendarContract.Events.TITLE, "Consulta com ${textProName.text}")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Consultório")
            putExtra(
                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                System.currentTimeMillis() + 60 * 60 * 1000
            ) // 1 hora a partir de agora
            putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                System.currentTimeMillis() + 2 * 60 * 60 * 1000
            ) // Duração de 2 horas
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
        "CRP: ${professional.credenciais}".also { textProCrenciais.text = it }
        textProEspecialidade.text = professional.especialidade
        textProEmail.text = professional.email

        if (isProfessionalMode) {
            editProName.setText(professional.name)
            editProCredenciais.setText(professional.credenciais)
            editProEspecialidade.setText(professional.especialidade)
            editProEmail.setText(professional.email)
        }

        //textHorariosDisponiveis.text = professional.horariosDisponiveis ?: "Não especificado"
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        _binding = null
    }

}