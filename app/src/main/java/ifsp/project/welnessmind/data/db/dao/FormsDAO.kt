    package ifsp.project.welnessmind.data.db.dao

    import androidx.room.Dao
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import androidx.room.Update
    import ifsp.project.welnessmind.data.db.entity.FormsEntity

    @Dao
    interface FormsDAO {

         @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(forms: FormsEntity): Long

        @Query("SELECT * FROM forms WHERE userId = :userID LIMIT 1")
        suspend fun getFormsByUserId(userID: Long): FormsEntity?

        @Update
        suspend fun update(forms: FormsEntity)

        @Query("DELETE FROM forms WHERE id = :id")
        suspend fun delete(id: Long)

        @Query("DELETE FROM forms")
        suspend fun deleteAll()
    }