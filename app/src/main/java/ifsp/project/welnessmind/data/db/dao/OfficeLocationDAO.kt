package ifsp.project.welnessmind.data.db.dao

import android.provider.CallLog.Locations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ifsp.project.welnessmind.data.db.entity.OfficeLocationEntity

@Dao
interface OfficeLocationDAO {

    @Insert
    suspend fun insertOfficeLocation(location: OfficeLocationEntity)

    @Query("SELECT * FROM office_location WHERE id = :professionalId")
    suspend fun getOfficelocation(professionalId: Long): OfficeLocationEntity?
}