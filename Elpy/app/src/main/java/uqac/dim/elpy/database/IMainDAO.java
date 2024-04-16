package uqac.dim.elpy.database;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uqac.dim.elpy.models.Chrono;
import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.Timer;
import uqac.dim.elpy.utilitaire.MarkerEntity;

@Dao
public interface IMainDAO {
    @Insert(onConflict = REPLACE)
    void insertNote(Note note);
    @Query("SELECT * FROM NOTE ORDER BY id DESC")
    List<Note> getAllNotes();
    @Query("UPDATE NOTE SET Title = :title, Content = :content, Color = :color WHERE id = :id")
    void updateNote(int id, String title, String content, int color);
    @Query("UPDATE NOTE SET Color = :color WHERE id = :id")
    void updateNoteColor(int id, int color);
    @Query("UPDATE NOTE SET IsPinned = :isPinned WHERE id = :id")
    void toggleNotePin(int id, boolean isPinned);
    @Delete
    void deleteNote(Note note);

    @Insert(onConflict = REPLACE)
    void insertChrono(Chrono chrono);
    @Query("SELECT * FROM CHRONO WHERE id = 5")
    Chrono getChrono();
    @Insert
    void insertMarker(MarkerEntity marker);
    @Query("SELECT * FROM markers")
    List<MarkerEntity> getAllMarkers();

}
