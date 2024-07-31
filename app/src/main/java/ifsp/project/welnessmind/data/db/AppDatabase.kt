@file:OptIn(InternalCoroutinesApi::class, InternalCoroutinesApi::class,
    InternalCoroutinesApi::class
)

package ifsp.project.welnessmind.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ifsp.project.welnessmind.data.db.dao.FormsDAO
import ifsp.project.welnessmind.data.db.dao.PatientDAO
import ifsp.project.welnessmind.data.db.dao.PatientPasswordDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalDAO
import ifsp.project.welnessmind.data.db.dao.ProfessionalPasswordDAO
import ifsp.project.welnessmind.data.db.entity.FormsEntity
import ifsp.project.welnessmind.data.db.entity.PatientEntity
import ifsp.project.welnessmind.data.db.entity.PatientPassword
import ifsp.project.welnessmind.data.db.entity.ProfessionalPassword
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [PatientEntity::class, ProfessionalEntity::class, FormsEntity::class, PatientPassword::class, ProfessionalPassword::class], version = 6,
    autoMigrations = [
        AutoMigration (from = 5, to = 6, spec = AppDatabase.AutoMigration::class)
    ])
abstract class AppDatabase : RoomDatabase() {
    @DeleteTable(tableName = "user_password")
    class AutoMigration : AutoMigrationSpec

    abstract val patientDao: PatientDAO
    abstract val professionalDao: ProfessionalDAO
    abstract val formsDao: FormsDAO
    abstract val patientPasswordDao: PatientPasswordDAO
    abstract val professionalPasswordDao: ProfessionalPasswordDAO

    companion object { // Usado para definir membros estáticos dentro da classe AppDatabase. Esses membros são compartilhados entre todas as instâncias da classe.
        @Volatile // Indica que a variável INSTANCE (abaixo) pode ser modificada por diferentes threads e garante que todas as threads vejam a mesma versão dessa variável. Isso é importante para evitar problemas de concorrência.
        private var INSTANCE: AppDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) { // Garante que apenas uma thread possa executar o bloco de código dentro do synchronized ao mesmo tempo, garantindo segurança em um ambiente multi-thread.
                var instance: AppDatabase? = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                }

                return instance
                }
            }
        }

    }