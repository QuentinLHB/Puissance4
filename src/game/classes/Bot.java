package game.classes;

import java.util.*;

public class Bot extends Joueur {
    private static final int NB_PIECES_A_TRAITER = 3;
    public List<PieceStrategique> getPiecesStrategiques() { // juste pour le test
        return piecesStrategiques;
    }

    private List<PieceStrategique> piecesStrategiques;
    private List<PieceStrategique> piecesAdversaires = new ArrayList<>();
    private Joueur adversaire;


    public Bot(String couleur, String nom) {
        super(couleur, nom);
        piecesStrategiques = new ArrayList<>();
    }

    /**
     * Ajout de la dernière pièce adversaire à la watchlist.
     */
    public void ajoutDernierCoupAdversaire(){
        Case lastCoupAdversaire = adversaire.lastCoup();
        if(lastCoupAdversaire != null) piecesAdversaires.add(lastCoupAdversaire.getPiece().toPieceStrategique());
    }

    /**
     * Ajoute l'adversaire, càd l'autre joueur de Jeu.
     */
    private void setAdversaire(){
        if(jeu.getJoueur1() == this){
            this.adversaire = jeu.getJoueur2();
        }
        else this.adversaire = jeu.getJoueur1();
    }

    /**
     * Pilotage d'un tour du jeu.
     */
    public void tour(){
        char choixColonne;

        if(coups==1) setAdversaire();

        //Ajoute le dernier coup de l'adversaire dans la watchlist de ses pièces stratégiques.
        ajoutDernierCoupAdversaire();

        //Debut du match : Pions au hasard dans les cases centrales.
        if(coups <= 2){
            choixColonne = colonneAleatoire('C', 'E');
        }
        // Puis détermine la meilleure case, en fonction du jeu actuel.
        else {
            refreshValeursStrategiques(piecesStrategiques);
            refreshValeursStrategiques(piecesAdversaires);
            choixColonne = choixStrategie();
        }
        placePiece(choixColonne);
    }

    /**
     * Détermine s'il vaut mieux se défendre (défaite imminente) ou attaquer.
     * @return Colonne à laquelle il vaut mieux placer la pièce.
     */
    public char choixStrategie(){
        char meilleurCoupAdverse = caseLaPlusStrat(piecesAdversaires);

        // Defense
        if(verifieVictoire(meilleurCoupAdverse, adversaire)){
            return meilleurCoupAdverse;
        }else{ //Attaque
            return caseLaPlusStrat(piecesStrategiques);
        }
    }

    /**
     * Regarde les trois pièces les plus stratégiques.
     * Si une case à conquérir est à portée, la choisit.
     * Sinon, choisit la case dont la portée est la plus proche parmi ces trois cases.
     * @return Char représentant la colonne la plus stratégique à jouer.
     */
    private char caseLaPlusStrat(List<PieceStrategique> lstPiecesStrat){
        Piece choixPiece = null;
        int nb = NB_PIECES_A_TRAITER;
        if (lstPiecesStrat.size() < 3) nb = lstPiecesStrat.size();
        var distanceMin = 7;
        var choixColonne = 'D';
        for (int k = 0; k < nb; k++){ // ON vérifie les trois pièces
            PieceStrategique piecestrat = lstPiecesStrat.get(k);

            // Si la pièce a une valeur "gagnante" (+1000), on retourne la colonne où placer la pièce finale.
            if (piecestrat.getValeur() > 1000){
                for (Case caseAPrendre: piecestrat.getCasesAPrendre()){
                    if(testeVictoire(caseAPrendre, piecestrat.joueur)){
                        return caseAPrendre.idColonne();
                    }
                }
            }
            // Si pas gagnante
            for (Case caseAPrendre: piecestrat.getCasesAPrendre()) {
                Case caseDispo = jeu.caseDispo(caseAPrendre.idColonne());
                // Nombre de cases entre la case à prendre et la case sur laquelle on va tomber.
                int distance = caseAPrendre.idLigne() - caseDispo.idLigne();

                // Si elle est dispo immédiatement, on retourne la case où placer la pièce.
                if(distance == 0){
                    choixColonne = caseAPrendre.idColonne();
                    if(!verifieSiBloque(piecestrat.getLaCase(), caseDispo)){
                        return caseAPrendre.idColonne();
                    }
                }
                // Sinon, on garde la case dont la distance est la plus petite.
                else if(distance < distanceMin){
                    distanceMin = distance;
                    choixPiece = piecestrat;
                    choixColonne = caseAPrendre.idColonne();
                }
            }
        }

        if(choixPiece == null){
            caseLaPlusStrat(lstPiecesStrat);
        }
        return choixColonne;
    }

    /**
     * Verifie si le chemin est bloqué dans l'axe de caseRef->caseATester
     * ex: caseRef A1, caseATesterB1, vérifie l'axe HORIZONTAL pour trouver un blocage.
     * @param caseRef Case dé référence
     * @param caseATester Case à tester.
     * @return True si blocage des deux côtés de l'axe, false sinon.
     */
    public boolean verifieSiBloque(Case caseRef, Case caseATester) {
        Case caseDispo = jeu.caseDispo(caseATester.idColonne()); // TODO Pas  utile (déjà testé dans caseLaPlusStrat)
        if(caseDispo != null){
            PieceStrategique faussePiece = new PieceStrategique(this, caseDispo);
            caseDispo.setPiece(faussePiece);
            String direction = Direction.compareCase(caseRef, caseATester);
            String axe = Direction.getAxe(direction);
            var bloque = 0;

            bloque += faussePiece.verifie2Sens(caseDispo, Direction.getDirections(axe))[faussePiece.BLOQUE];
            caseDispo.setPiece();
            return bloque == -2; // true (bloqué) si 2 blocages (-2), false pas bloqué.
        }
        return true;
    }




    /**
     * Point d'accès au tour.
     */
    @Override
    public void placePiece(){
        tour();
    }

    /**
     * Place la pièce choisie la grille.
     * @param nColonne Colonne à laquelle placer la pièce.
     * @return True si la pièce a pu être placée, false sin
     */
    @Override
    public boolean placePiece(char nColonne) {
        boolean ok = super.placePiece(nColonne);
        if(ok) {
            piecesStrategiques.add(lastCoup().getPiece().toPieceStrategique());

        }
        else{ // Si l'ajout n'a pas fonctionné, aucune stratégie : Placement aléatoire.
            do{
                ok = super.placePiece(colonneAleatoire('A', 'G'));
            }while (!ok);
        }
        return ok;
    }

    /**
     * Simule le placement d'une pièce de l'adversaire.
     * @param nColonne Colonne où placer une "fausse pièce"
     * @return True si victoire de l'adversaire, sinon False.
     */
    private boolean verifieVictoire(char nColonne, Joueur joueurATester){
        Case caseDispo = jeu.caseDispo(nColonne);
            if(caseDispo != null){
                return testeVictoire(caseDispo, joueurATester);
            }
        return false;

    }

    /**
     * Simule le placement d'une pièce de l'adversaire sans la physique de la pièce qui tombe (sans tester les cases inférieures)
     * @return True si victoire de l'adversaire, sinon False.
     */
    private boolean testeVictoire(Case laCase, Joueur joueurATester){
        Piece faussePiece = new Piece(joueurATester);
        laCase.setPiece(faussePiece);
        boolean victoire = jeu.finTour(laCase);
        laCase.setPiece();
        return victoire;
    }

    /**
     * Rafraichit la liste des pièces stratégie :
     * Actualise les valeurs de chaque pièce
     * Enlève les pièces de valeur 0
     */
    private void refreshValeursStrategiques(List<PieceStrategique> lstPiecesStrat){
        List<PieceStrategique> aEnlever = new ArrayList<>();
        for (PieceStrategique pieceStrat: lstPiecesStrat) {
            pieceStrat.score();
            if(pieceStrat.getValeur() ==0) aEnlever.add(pieceStrat);
        }
        for (PieceStrategique pieceStrategique : aEnlever) lstPiecesStrat.remove(pieceStrategique);
        lstPiecesStrat.sort(new CompareValeurs());

    }

}
