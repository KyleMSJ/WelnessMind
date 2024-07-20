package ifsp.project.welnessmind.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import java.text.Normalizer.Form

interface FormsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(forms: FormsEntity): Long

    @Query("SELECT * FROM patient WHERE id = :id")
    fun getFormsById(id: Long): FormsEntity?

    @Query("SELECT * FROM forms")
    fun getAll(): LiveData<List<FormsEntity>>

    @Update
    fun update(forms: FormsEntity)

    @Query("DELETE FROM forms WHERE id = :id")
    fun delete(id: Long)

    @Query("DELETE FROM forms")
    fun deleteAll()
}