package ifsp.project.welnessmind.data.repository

import androidx.lifecycle.LiveData
import ifsp.project.welnessmind.data.db.entity.PatientEntity

interface PatientRepository  // O 'Repository': Abstrai a origem dos dados, fornece uma API limpa: Oferece métodos claros e fáceis de usar para a camada de domínio e apresentação e gerencia dados complexos: Combina, filtra, e manipula dados antes de passá-los para outras camadas.
    // Responsabilidade: Lidar com a lógica de acesso aos dados e operações CRUD básicas
{
    suspend fun insertPatient(name: String, email: String, cpf: String, idade: Int, estadoCivil: Int, rendaFamiliar: Int, escolaridade: Int): Long // 'suspend' - usada para execuções assíncronas sem interromper a thread principal (melhor performance e responsividade na UI)
    fun  getAllPatients(): LiveData<List<PatientEntity>>
    suspend fun getPatientById(id: Long): PatientEntity?
    suspend fun  updatePatient(id: Long, name: String, email: String, cpf: String, idade: Int, estadoCivil: Int, rendaFamiliar: Int, escolaridade: Int)
    suspend fun deletePatient(id: Long)
    suspend fun deleteAllPatients()
}
