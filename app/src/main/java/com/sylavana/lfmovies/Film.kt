import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "films")
@Parcelize
data class Film(
    @PrimaryKey val id: Int,
    val title: String,
) : Parcelable
