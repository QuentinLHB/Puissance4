package game.classes;

import java.util.*;

public class Joueur {
    protected static int compteur = 0;
    protected int idJoueur;
    protected int coups = 1;
    protected Piece[] pieces;
    protected String couleur;
    protected Jeu jeu;
    protected String nom;
    protected List<Case> histoCoups;


    public Joueur(String couleur){
        idJoueur = compteur;
        compteur++;
        this.couleur = couleur;
        pieces = new Piece[21];
        histoCoups = new ArrayList<>();
        Arrays.fill(pieces, new Piece(this));

    }

    public Joueur(String couleur, String nom){
        this(couleur);
        this.nom = nom;
        initPieces();
    }

    public void setJeu(Jeu jeu){
        this.jeu = jeu;
    }

    public int getCoups() {
        return coups;
    }

    /**
     * Place une pièce dans la grille.
     * @param nColonne Colonne à laquelle placer la pièce.
     * @return true si pièce placée, false si pas de case dispo.
     */
    public boolean placePiece(char nColonne){

        Case caseDispo = jeu.caseDispo(nColonne);
        if(caseDispo != null){
            caseDispo.setPiece(pieces[21-coups]);
            histoCoups.add(caseDispo);
            pieces[21-coups]= null;
            coups++;
            return true;
        }
        return false;
    }
    public void placePiece(){
        placePiece(colonneAleatoire('A', 'G'));
    }

    public void initPieces(){
        Arrays.fill(pieces, new Piece(this));
    }

    public String getCouleur() {
        return couleur;
    }

    public String getNom() {
        return nom;
    }

    /**
     * Obtient la dernière case jouée.
     * @return Case du dernier coup.
     */
    public Case lastCoup(){
        if(histoCoups.size() == 0) return null;
        return histoCoups.get(histoCoups.size()-1);
    }

    protected char colonneAleatoire(char Min, char Max){

        return (char)(Min + (int)(Math.random() * ((Max - Min) + 1)));
    }
}
