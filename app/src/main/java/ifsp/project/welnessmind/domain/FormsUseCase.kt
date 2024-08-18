package ifsp.project.welnessmind.domain

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.repository.FormsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FormsUseCase(
    private val formsDAO: FormsDAO,
    private val firebaseDatabase: FirebaseDatabase
): FormsRepository {

    private fun getFormsRef(userId: Long) = firebaseDatabase
        .getReference("patient")
        .child(userId.toString())
        .child("forms")

    private suspend fun syncFormsToFirebase(userId: Long, forms: FormsEntity) = withContext(Dispatchers.IO) {
        val formsRef = getFormsRef(userId)
        Log.d("FirebaseSync", "Sincronizando formulário para paciente $userId com dados: $forms")
        formsRef.child(forms.id.toString())
            .setValue(forms)
                .addOnSuccessListener {
                    Log.d("FirebaseSync", "Formulário sincronizado para o paciente $userId: Form ID ${forms.id}")
                }
                .addOnFailureListener {exception ->
                Log.e("FirebaseSync", "Falha ao sincronizar o formulário para o paciente $userId: ${exception.message}")
                }
    }

    override suspend fun insertForms(
        userId: Long,
        horasSono: Int,
        horasEstudo: Int,
        horasTrabalho: Int,
        fazAtividadeFisica: Boolean,
        descricaoAtivFisica: String,
        frequenciaAtivFisica: String,
        hobbies: String,
        tomaMedicamento: Boolean,
        descricaoMedicamento: String
    ): Long {
        val forms = FormsEntity(
            userId = userId,
            horasSono = horasSono,
            horasTrabalho = horasTrabalho,
            horasEstudo = horasEstudo,
            fazAtividadeFisica = fazAtividadeFisica,
            descricaoAtivFisica = descricaoAtivFisica,
            frequenciaAtivFisica = frequenciaAtivFisica,
            hobbies = hobbies,
            tomaMedicamento = tomaMedicamento,
            descricaoMedicamento = descricaoMedicamento
        )

        val id = formsDAO.insert(forms)
        forms.id = id

        syncFormsToFirebase(userId, forms)
        return id
    }

    override fun getAllForms(): LiveData<List<FormsEntity>> {
        return formsDAO.getAll()
    }

    override suspend fun updateForms(id: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String) {
        val forms = FormsEntity(
            id  = id,
            horasSono = horasSono,
            horasTrabalho = horasTrabalho,
            horasEstudo = horasEstudo,
            fazAtividadeFisica = fazAtividadeFisica,
            descricaoAtivFisica = descricaoAtivFisica,
            frequenciaAtivFisica = frequenciaAtivFisica,
            hobbies = hobbies,
            tomaMedicamento = tomaMedicamento,
            descricaoMedicamento = descricaoMedicamento
        )

        formsDAO.update(forms)
        syncFormsToFirebase(userId = id, forms = forms)
    }

    override suspend fun deleteForms(id: Long) {
        formsDAO.delete(id)
        val formsRef = getFormsRef(id)
        formsRef.child(id.toString()).removeValue()
    }
}

