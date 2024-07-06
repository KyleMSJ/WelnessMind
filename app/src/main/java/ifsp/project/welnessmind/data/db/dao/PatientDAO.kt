package ifsp.project.welnessmind.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ifsp.project.welnessmind.data.db.entity.PatientEntity

@Dao
interface PatientDAO { // interface que fornece métodos para realizar operações de acesso a dados no banco de dados. Define as operações CRUD (Create, Read, Update, Delete) diretamente no banco de dados.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(patient: PatientEntity): Long

    @Query("SELECT * FROM patient WHERE id = :id")
    fun getPatientById(id: Long): PatientEntity?

    @Query("SELECT * FROM patient")
    fun getAll(): LiveData<List<PatientEntity>>

    @Update
    fun update(patient: PatientEntity)
    @Query("DELETE FROM patient WHERE id = :id")
    fun delete(id: Long)
    @Query("DELETE FROM patient")
    fun deleteAll()

}