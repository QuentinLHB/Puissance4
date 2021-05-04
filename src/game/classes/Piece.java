package game.classes;

public class Piece {
    protected Joueur joueur;
    protected String couleur;
    protected Case laCase;


    public Case getLaCase() {
        return laCase;
    }

    public void setLaCase(Case laCase) {
        this.laCase = laCase;
    }


    public Piece(Joueur joueur){
        this.joueur = joueur;
        couleur = joueur.getCouleur();
    }

    public String getCouleur() {
        return couleur;
    }

    public PieceStrategique toPieceStrategique(){
        return new PieceStrategique(joueur, laCase);
    }
}
