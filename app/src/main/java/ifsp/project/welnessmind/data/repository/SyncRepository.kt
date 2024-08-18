package ifsp.project.welnessmind.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncRepository(
    private val professionalDao: ProfessionalDAO,
    private val patientDAO: PatientDAO,
    private val firebaseDatabase: FirebaseDatabase
) {

    private val TAG = "SyncRepository"

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
    suspend fun syncPatients() {
        withContext(Dispatchers.IO) {
            firebaseDatabase.getReference("patient")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val patient = it.getValue(PatientEntity::class.java)
                            patient?.let {
                                CoroutineScope(Dispatchers.IO).launch {
                                    patientDAO.insert(it)
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

}