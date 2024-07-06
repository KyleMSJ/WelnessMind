@file:OptIn(InternalCoroutinesApi::class, InternalCoroutinesApi::class,
    InternalCoroutinesApi::class
)

package ifsp.project.welnessmind.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [PatientEntity::class, ProfessionalEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract val patientDao: PatientDAO
    abstract val professionalDao: ProfessionalDAO

    companion object { // Usado para definir membros estáticos dentro da classe AppDatabase. Esses membros são compartilhados entre todas as instâncias da classe.
        @Volatile // Indica que a variável INSTANCE (abaixo) pode ser modificada por diferentes threads e garante que todas as threads vejam a mesma versão dessa variável. Isso é importante para evitar problemas de concorrência.
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) { // Garante que apenas uma thread possa executar o bloco de código dentro do synchronized ao mesmo tempo, garantindo segurança em um ambiente multi-thread.
                var instance: AppDatabase? = INSTANCE
                if (instance == null)
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()

                return instance
                }
            }
        }
    }