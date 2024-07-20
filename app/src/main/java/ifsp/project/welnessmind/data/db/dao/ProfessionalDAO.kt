package ifsp.project.welnessmind.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity

@Dao
interface ProfessionalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Se já existir um item com a mesma chave,
    // ele será substituído pelo novo item devido à estratégia de conflito REPLACE.
    suspend fun insert(professional: ProfessionalEntity): Long

   @Query("SELECT * FROM professional WHERE id = :id")
    suspend fun getProfessionalById(id: Long): ProfessionalEntity?

    @Query("SELECT * FROM professional")
    fun getAll(): LiveData<List<ProfessionalEntity>> // toda vez que inserir ou atualizar alguma coisa no banco de dados, automaticamente vai disparar o evento e quem tiver escutando o LiveData, vai reagir a esse evento e vai atualizar os dados para o usuário de forma autómática

    @Update
    suspend fun update(professional: ProfessionalEntity)

    @Query("DELETE FROM professional WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM professional")
    suspend fun deleteAll()
}