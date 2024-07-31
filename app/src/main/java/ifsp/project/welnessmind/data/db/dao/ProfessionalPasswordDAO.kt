package ifsp.project.welnessmind.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ifsp.project.welnessmind.data.db.entity.ProfessionalPassword

@Dao
interface ProfessionalPasswordDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(professionalPassword: ProfessionalPassword): Long

    @Query("SELECT * FROM patient_password WHERE id = :id LIMIT 1")
    suspend fun getPasswordByUserId(id: Long): ProfessionalPassword?
}