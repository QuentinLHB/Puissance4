package game.classes;

import java.util.ArrayList;
import java.util.List;

public class PieceStrategique extends Piece{

    private List<Case> casesAPrendre = new ArrayList<>();
    private int valeur;
    private static final int PTS_CASEVIDE = 10;
    private static final int PTS_CASEAMIE = 30;
    public static final int POINTS = 0;
    public static final int BLOQUE = 1;
    public static final int ALLIGNEMENT = 2;


    public PieceStrategique(Joueur joueur, Case laCase) {
        super(joueur);
        setLaCase(laCase);
    }

    public int getValeur() {
        return valeur;
    }

    public List<Case> getCasesAPrendre() {
        return casesAPrendre;
    }


    /**
     * Calcule la valeur stratégique de la pièce
     * 10pts pour chaque case dispo
     * 20pts pour chaque case adjacente de la même couleur, (1.5 pour 2 de la même couleur sur une ligne/col/diag).
     * @return La valeur déterminée.
     */
    public int score(){
        this.casesAPrendre.clear();
        var score = 0;
        String[] directions = Direction.getAxes();
        var dir = 0;
        do{
            score += verifie2Sens(laCase, Direction.getDirections(directions[dir++]))[POINTS];
        }while (dir <= 3);
        valeur = score;
        return score;
    }

    /**
     * Parcourt une direction en partant d'une case, à la recherhce des cases de la même couleur.
     * @param direction Couple de directions qui vont être cherchées. Utiliser les méthodes de classe Direction.
     * @return Le score total des deux sens sd'une direction.
     */
    public int[] verifie2Sens(Case laCase, String[] direction){
        int[] score2Sens = {0, 0, 0};
        int[] scoreTemp;
        for(int k=0; k<2; k++){

             scoreTemp = verifieUnSens(laCase, direction[k], new int[]{0, 0, 0});
             score2Sens[POINTS] += scoreTemp[POINTS];
             score2Sens[BLOQUE] += scoreTemp[BLOQUE];
             score2Sens[ALLIGNEMENT] += scoreTemp[ALLIGNEMENT];
        }
        if(score2Sens[BLOQUE] == -2){
            score2Sens[POINTS] = 0; // Pièces ennemies de part et d'autre
            score2Sens[ALLIGNEMENT] = 0;
        }
        if(score2Sens[ALLIGNEMENT] == 2 ) score2Sens[POINTS]+=1000; // Trois pièces allignées
        return score2Sens;
    }

    /**
     *
     * @param laCase laCase Case de référence (utilisé pour la récursivité de la fonction).
     * @param direction Direction dans laquelle chercher.
     * @param score 1ère valeur : Points de la direction prise: 10pts si case dipo, 20pts par pièce amie.
     *              2è valeur : -1 si mur/pièce ennemie.
     *              3è Valeur : Nombre de pièces amies rencontrées.     *
     * @return Données de scoring.
     */
    public int[] verifieUnSens(Case laCase, String direction, int[] score){
        Case nextCase = laCase.getVoisins(direction);
        if(nextCase != null){
            if (nextCase.getPiece() != null){
                if(nextCase.getPiece().getCouleur().equals(laCase.getPiece().getCouleur())){
                    score[POINTS]+= PTS_CASEAMIE; // Pièce amie
                    score[ALLIGNEMENT] += 1;
                    score = verifieUnSens(nextCase, direction, score);
                }else{
                    score[BLOQUE] = -1;
                    return score;
                }
            }else { // Case dispo
                if(!plafondAtteint(laCase, direction)){
                    casesAPrendre.add(nextCase);
                    Case sautDeCase = nextCase.getVoisins(direction);
                    if (sautDeCase != null && sautDeCase.getPiece() != null && sautDeCase.getPiece().getCouleur().equals(laCase.getPiece().getCouleur())){
                        score[POINTS] += PTS_CASEAMIE;
                        score[ALLIGNEMENT] += 1;
                    }
                    score[POINTS]+= PTS_CASEVIDE;
                    return score;
                }
            }
        }else{ //Bordure : Pas de possibilités.
            score[BLOQUE] = -1;
            return score ;
        }
        return score;
    }

    // TODO NE FONCTIONNE PAS SUR LE DIAGONALES
    /**
     *
     * @param laCase
     * @return True si le plafond est atteint (pas d'allignement colonne possible), fale sinon.
     */
    private boolean plafondAtteint(Case laCase, String direction){
        if (direction.equals(Direction.HAUT) && laCase.idLigne() >= 3){
            int allignement = verifieUnSens(laCase, Direction.BAS, new int[]{0,0,0})[ALLIGNEMENT];
            return 6 - (laCase.idLigne() - allignement) < 3; // Calcul de la distance entre la première de la colonne en cours et le plafond
        }
        return false;
    }
}
