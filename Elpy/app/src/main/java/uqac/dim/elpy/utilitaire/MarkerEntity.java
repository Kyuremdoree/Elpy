package uqac.dim.elpy.utilitaire;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "markers")
public class MarkerEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;
    public double longitude;
    public String name;
    public String comment;
}

