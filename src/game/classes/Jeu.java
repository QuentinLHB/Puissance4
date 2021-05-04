package game.classes;

public class Jeu {
    /*
     Grille
     */
    final int NCOLONNES = 7;
    final int NLIGNES = 6;
    Case[][] grille; // Case[2][3] = colonne, 3è ligne


    /*
    Joueurs
     */
    private Joueur joueur1;
    private Joueur joueur2;


    public Joueur getJoueur2() {
        return joueur2;
    }
    public Joueur getJoueur1() {
        return joueur1;
    }

    // Tour
    private int tour = 0;

    public Jeu(Joueur joueur1, Joueur joueur2){
        grille = new Case[NCOLONNES][NLIGNES];
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        initGrille();
    }

    public Jeu(){
        grille = new Case[NCOLONNES][NLIGNES];
        initGrille();
    }

    public int getTour() {
        return tour;
    }

    public void initGrille(){
        for (int colonne = 0; colonne <=6; colonne++){
            for (int ligne=1; ligne<=6; ligne++){
                char c =  (char)(colonne + 65);
                grille[colonne][ligne-1] = new Case(this, String.valueOf(c), String.valueOf(ligne));
            }
        }

        for (int colonne = 0; colonne <=6; colonne++){
            for (int ligne=0; ligne<=5; ligne++){
                grille[colonne][ligne].setVoisins();
            }
        }


    }

    /**
     * Retourne la grille du jeu.
     * @return Case[Colonne][Ligne]
     */
    public Case[][] getGrille(){
        return grille;
    }

    /**
     * @param colonne L'id de la colonne de 0 à 6.
     * @param ligne L'id de la ligne de 0 à 5.
     * @return la case concernée.
     */
    public Case getCase(int colonne, int ligne){
        return grille[colonne][ligne];
    }

    /**
     * @param colonne Charactère réel de A à G (de gauche à droite)
     * @param ligne Ligne réelle de 1 à 6 (de bas en haut)
     * @return la case concernée.
     */
    public Case getCase(char colonne, int ligne){
        return grille[colonne-65][ligne-1];
    }

    /**
     * Détermine la première case disponble sur une colonne.
     * @param nColonne Colonne sur laquelle vérifier.
     * @return Première case disponible, ou null si aucune dispo.
     */
    public Case caseDispo(char nColonne){ // Alléger ?
        boolean libre = false;
        int nLigne = 1;
        Case caseLibre = null;
        while (!libre && nLigne<=6){
            if(this.getCase(nColonne, nLigne).getPiece() == null){
                caseLibre = this.getCase(nColonne, nLigne);
                libre = true;
            }
            else nLigne++;
        }
        return caseLibre;
    }


    public Joueur tourJoueur(){
        tour++;
        if (tour%2 != 0){
            return joueur1;
        }
        else return joueur2;
    }

    /**
     * Clos le trour en vérifiant si un joueur a gagné.
     * @param caseJouee Case jouée lors du tour.
     * @return True si victoire, false sinon.
     */
    public boolean finTour(Case caseJouee){ //TODO Reprendre la case jouée dans la procédure ?
        return caseJouee.compteAllignements() == 3;

    }




    /**
     * Retourne la case à partir d'un identifiant.
     * @param Id Identifiant (A1 .. G6)
     * @return Case
     */
    public Case getCase(String Id){
        return grille[Id.charAt(0)-65][Integer.parseInt(Id.substring(1))-1];
    }


}















