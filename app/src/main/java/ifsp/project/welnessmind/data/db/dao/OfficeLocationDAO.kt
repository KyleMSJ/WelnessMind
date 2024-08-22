package ifsp.project.welnessmind.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity

@Dao
interface OfficeLocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveOfficeLocation(location: OfficeLocationEntity): Long

    @Update
    suspend fun updateOfficeLocation(location: OfficeLocationEntity)

    @Query("SELECT * FROM office_location WHERE professional_id = :professionalId LIMIT 1")
    suspend fun getOfficelocation(professionalId: Long): OfficeLocationEntity?
}