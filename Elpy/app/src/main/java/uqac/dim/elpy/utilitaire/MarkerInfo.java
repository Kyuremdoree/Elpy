package uqac.dim.elpy.utilitaire;

public class MarkerInfo {

    private String nom;

    private String description;

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

    public MarkerInfo(String nom, String description){
        setNom(nom);
        setDescription(description);
    }
}
