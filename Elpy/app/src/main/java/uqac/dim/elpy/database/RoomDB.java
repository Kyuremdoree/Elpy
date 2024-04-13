package uqac.dim.elpy.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uqac.dim.elpy.models.Chrono;
import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.Timer;

@Database(entities = {Note.class, Chrono.class}, version = 3, exportSchema = false)
@TypeConverters({Timer.class})
public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    private static final String DATABASE_NAME = "Elpy";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract IMainDAO mainDAO();
}
