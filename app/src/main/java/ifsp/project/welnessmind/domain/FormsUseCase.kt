package ifsp.project.welnessmind.domain

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
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

        val formsData = mapOf(
            "id" to forms.id,
            "userId" to forms.userId,
            "horasSono" to forms.horasSono,
            "horasEstudo" to forms.horasEstudo,
            "horasTrabalho" to forms.horasTrabalho,
            "fazAtividadeFisica" to forms.fazAtividadeFisica,
            "descricaoAtivFisica" to forms.descricaoAtivFisica,
            "frequenciaAtivFisica" to forms.frequenciaAtivFisica,
            "hobbies" to forms.hobbies,
            "tomaMedicamento" to forms.tomaMedicamento,
            "descricaoMedicamento" to forms.descricaoMedicamento
        )
        formsRef.child(forms.id.toString())
            .setValue(formsData)
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

    override suspend fun getFormsById(id: Long): FormsEntity? {
        return formsDAO.getFormsByUserId(id)
    }

    override suspend fun updateForms(id: Long, userId: Long, horasSono: Int, horasEstudo: Int, horasTrabalho: Int, fazAtividadeFisica: Boolean, descricaoAtivFisica: String, frequenciaAtivFisica: String, hobbies: String, tomaMedicamento: Boolean, descricaoMedicamento: String) {
        withContext(Dispatchers.IO) {
            val forms = FormsEntity(
                id = id,
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

            formsDAO.update(forms)
            syncFormsToFirebase(userId = id, forms = forms)
        }
    }

    override suspend fun deleteForms(id: Long) {
        formsDAO.delete(id)
        val formsRef = getFormsRef(id)
        formsRef.child(id.toString()).removeValue()
    }
}

