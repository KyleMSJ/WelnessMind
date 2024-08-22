package ifsp.project.welnessmind.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.dao.OfficeLocationDAO
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SyncRepository(
    private val patientDAO: PatientDAO?,
    private val professionalDao: ProfessionalDAO,
    private val formsDao: FormsDAO,
    private val officeLocationDAO: OfficeLocationDAO,
    private val firebaseDatabase: FirebaseDatabase
) {

    private val TAG = "SyncRepository"

    suspend fun syncPatients() {
        withContext(Dispatchers.IO) {
            firebaseDatabase.getReference("patient")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val patient = it.getValue(PatientEntity::class.java)
                            patient?.let {
                                CoroutineScope(Dispatchers.IO).launch {
                                    patientDAO?.insert(it)
                                }
                            }
                        }
                        Log.d(TAG, "Dados de pacientes sincronizados com sucesso")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Erro ao sincronizar dados: ${error.message}")
                    }
                })
        }
    }

    suspend fun syncProfessionals() {
        withContext(Dispatchers.IO) {
            firebaseDatabase.getReference("professional")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val professional = it.getValue(ProfessionalEntity::class.java)
                            professional?.let {
                                CoroutineScope(Dispatchers.IO).launch {
                                    professionalDao.insert(it)
                                }
                            }
                        }
                        Log.d(TAG, "Dados de profissionais sincronizados com sucesso")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Erro ao sincronizar dados: ${error.message}")
                    }
                })
        }
    }

    suspend fun syncForms(userID: Long) {
        withContext(Dispatchers.IO) {
            firebaseDatabase.getReference("patient").child(userID.toString()).child("forms")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val forms = FormsEntity(
                                id = it.child("id").getValue(Long::class.java) ?: 0,
                                userId = it.child("userId").getValue(Long::class.java) ?: 0,
                                horasSono = it.child("horasSono").getValue(Int::class.java) ?: 0,
                                horasEstudo = it.child("horasEstudo").getValue(Int::class.java)
                                    ?: 0,
                                horasTrabalho = it.child("horasTrabalho").getValue(Int::class.java)
                                    ?: 0,
                                fazAtividadeFisica = it.child("fazAtividadeFisica")
                                    .getValue(Boolean::class.java) ?: false,
                                descricaoAtivFisica = it.child("descricaoAtivFisica")
                                    .getValue(String::class.java) ?: "",
                                frequenciaAtivFisica = it.child("frequenciaAtivFisica")
                                    .getValue(String::class.java) ?: "",
                                hobbies = it.child("hobbies").getValue(String::class.java) ?: "",
                                tomaMedicamento = it.child("tomaMedicamento")
                                    .getValue(Boolean::class.java) ?: false,
                                descricaoMedicamento = it.child("descricaoMedicamento")
                                    .getValue(String::class.java) ?: ""
                            )
                            forms.let {
                                CoroutineScope(Dispatchers.IO).launch {
                                    formsDao.insert(forms)
                                }
                            }
                            Log.d(TAG, "Forms: $forms")
                        }
                        Log.d(TAG, "Dados de formulários sincronizados com sucesso")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Erro ao sincronizar dados: ${error.message}")
                    }
                })
        }
    }

    suspend fun syncOfficeLocations(professional_id: Long) {
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                firebaseDatabase.getReference("professional").child(professional_id.toString())
                    .child("office_location")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val office = OfficeLocationEntity(
                                    id = it.child("id").getValue(Long::class.java) ?: 0,
                                    professional_id = it.child("professional_id")
                                        .getValue(Long::class.java) ?: 0,
                                    address = it.child("address").getValue(String::class.java)
                                        ?: "",
                                    latitude = it.child("latitude").getValue(Double::class.java)
                                        ?: 0.0,
                                    longitude = it.child("longitude").getValue(Double::class.java)
                                        ?: 0.0,
                                    description = it.child("description")
                                        .getValue(String::class.java)
                                        ?: "",
                                    contact = it.child("contact").getValue(String::class.java)
                                        ?: ""
                                )
                                office.let {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        officeLocationDAO.saveOfficeLocation(it)
                                        continuation.resume(Unit)
                                    }
                                }
                            }
                            Log.d(TAG, "Endereço do consultório sincronizado com sucesso")
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Erro ao sincronizar dados: ${error.message}")
                            continuation.resumeWithException(Exception("Erro ao sincronizar dados: ${error.message}"))
                        }
                    })
            }
        }
    }
}