package ifsp.project.welnessmind.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ifsp.project.welnessmind.data.db.entity.PatientEntity

@Dao
interface PatientDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: PatientEntity): Long

    @Query("SELECT * FROM patient WHERE id = :id")
    suspend fun getPatientById(id: Long): PatientEntity?

    @Query("SELECT * FROM patient WHERE email = :email")
    suspend fun getPatientByEmail(email: String): PatientEntity?

    @Query("SELECT * FROM patient")
    fun getAll(): LiveData<List<PatientEntity>>

    @Update
    fun update(patient: PatientEntity)
    @Query("DELETE FROM patient WHERE id = :id")
    suspend fun delete(id: Long)
    @Query("DELETE FROM patient")
    suspend fun deleteAll()

}