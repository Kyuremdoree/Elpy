package uqac.dim.elpy.utilitaire;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;

public class MarkerInfo implements Parcelable {

    private String nom;

    private String description;

    private Marker marker;


    protected MarkerInfo(Parcel in) {
        nom = in.readString();
        description = in.readString();
    }

    public static final Creator<MarkerInfo> CREATOR = new Creator<MarkerInfo>() {
        @Override
        public MarkerInfo createFromParcel(Parcel in) {
            return new MarkerInfo(in);
        }

        @Override
        public MarkerInfo[] newArray(int size) {
            return new MarkerInfo[size];
        }
    };

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom.isEmpty()){
            this.nom = "Marqueur";
            return;
        }
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.isEmpty()){
            this.description = "Vide";
            return;
        }
        this.description = description;
    }

    public MarkerInfo(String nom, String description, Marker marker){
        setNom(nom);
        setDescription(description);
        this.marker = marker;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nom);
        dest.writeString(description);
    }

    public Marker getMarker() {
        return marker;
    }

    public MarkerEntity toMarkerEntity() {
        MarkerEntity entity = new MarkerEntity();
        entity.latitude = marker.getPosition().latitude;
        entity.longitude = marker.getPosition().longitude;
        entity.name = nom;
        entity.comment = description;
        return entity;
    }
}
