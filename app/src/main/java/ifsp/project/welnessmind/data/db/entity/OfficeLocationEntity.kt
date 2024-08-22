    package ifsp.project.welnessmind.data.db.entity

    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.ForeignKey
    import androidx.room.ForeignKey.Companion.CASCADE
    import androidx.room.PrimaryKey

    @Entity(
        tableName = "office_location",
        foreignKeys = [
            ForeignKey(entity = ProfessionalEntity::class, parentColumns = ["id"], childColumns = ["professional_id"], onUpdate = CASCADE, onDelete = CASCADE)
        ]
    )
    data class OfficeLocationEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        @ColumnInfo(defaultValue = "0")
        val professional_id: Long = 0,
        val address: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        var description: String = "",
        var contact: String = ""
    ) {
        constructor() : this(0,0,"",0.0,0.0, "", "")
    }