package ifsp.project.welnessmind.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.jetbrains.annotations.NotNull

@Entity
open class Pessoa (
    @ColumnInfo(name = "name") open var name: String,
    @ColumnInfo(name = "email") open var email: String
)

