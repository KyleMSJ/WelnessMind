package ifsp.project.welnessmind.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity

@Dao
interface ProfessionalDAO {
    @Insert
    fun insert(professional: ProfessionalEntity): Long

   @Query("SELECT * FROM professional WHERE id = :id")
    fun getProfessionalById(id: Long): ProfessionalEntity?

    @Query("SELECT * FROM professional")
    fun getAll(): LiveData<List<ProfessionalEntity>> // toda vez que inserir ou atualizar alguma coisa no banco de dados, automaticamente vai disparar o evento e quem tiver escutando o LiveData, vai reagir a esse evento e vai atualizar os dados para o usuário de forma autómática

    @Update
    fun update(professional: ProfessionalEntity)

    @Query("DELETE FROM professional WHERE id = :id")
    fun delete(id: Long): Int

    @Query("DELETE FROM professional")
    fun deleteAll()
}