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

    @Query("SELECT EXISTS(SELECT 1 FROM films WHERE id = :filmId)")
    fun checkFilmExists(filmId: Int): Boolean

    @Query("DELETE FROM films WHERE id = :filmId")
    fun deleteFilm(filmId: Int)
}
