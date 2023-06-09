import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun ajouterFilm(film: Film)

    @Query("SELECT * FROM films")
    fun obtenirFilms(): List<Film>
}
