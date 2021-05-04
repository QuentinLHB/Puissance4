package game;
import game.classes.*;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static Jeu jeu;
    public static Joueur j1;
    public static Joueur j2;
    public static void main(String[] args)
    {

        /*
        Initialisation
         */

        choixMode();
        jeu = new Jeu(j1, j2);
        j1.setJeu(jeu);
        j2.setJeu(jeu);

        /*
        Jeu
         */
        boolean victoire;
        afficheGrille();
        do {
            victoire = tour();
            afficheGrille();
        }while (!victoire);

        /*
        Résultat
         */
        if(j2.getCoups() != 21){ // pas match nul
            if(j1.getCoups() == j2.getCoups()){ // j2 gagne quand il a joué autant de coups que j1
                annonceVictoire(j2);
            }
            else annonceVictoire(j1);
        }

    }

    public static void choixMode(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choix du mode de jeu :");
        System.out.println("Joueur VS Joueur : 1");
        System.out.println("Joueur VS Ordi : 2");
        System.out.println("Ordi VS Ordi : 3");
        int choix;
        do {
            System.out.println(">>");
            choix = scanner.nextInt();
        }while (choix != 1 && choix != 2 && choix != 3);

        j1 = initJoueur(1, "X", choix !=3);
        j2 = initJoueur(2, "O", choix == 1);

    }

    public static void annonceVictoire(Joueur gagnant){
        System.out.println(gagnant.getNom() + " gagne en " + gagnant.getCoups() + " coups !");
    }

    public static Joueur initJoueur(int idJoueur, String couleur, boolean humain){

        if(humain){
            Scanner scanner = new Scanner(System.in);
            System.out.println(String.format("Nom joueur %d :", idJoueur));
            String nom = scanner.nextLine();
            return new Joueur(couleur, nom);
        }
        else return new Bot(couleur, "ordinateur" + idJoueur
        );

    }

    public static void afficheGrille(){

        Case[][] grille = jeu.getGrille();
        String laGrille = "    A   B   C   D   E   F   G\n"; // A renommer
        //String colonne = "";
        for (int l=6; l>=1; l--){
            String ligne = String.valueOf(l) + " |";
            for (int c = 0; c <=6; c++){
                Case laCase= grille[c][l-1];
                String affiche;
                if(laCase.getPiece() == null) affiche = " ";
                else affiche = laCase.getPiece().getCouleur();
                ligne += " " + affiche + " |";
            }
            laGrille += ligne + "\n";
        }
        System.out.println(laGrille);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static boolean tour(){
        Scanner scanner = new Scanner(System.in);
        Joueur joueur = jeu.tourJoueur();
        if(!(joueur instanceof Bot)){

            boolean ok;
            do {
                char choixColonne;
                do {
                    System.out.println(String.format("Coup n°%d de %s :", joueur.getCoups(), joueur.getNom()));
                    choixColonne = scanner.next().toUpperCase(Locale.ROOT).charAt(0);
                }while (choixColonne <'A' || choixColonne > 'G');
                ok = joueur.placePiece(choixColonne);
            }while (!ok);
        }
        else{ //ICI TEST DU BOT

            Bot j = (Bot)joueur;
            pause();
            j.tour();
            System.out.println(j.getNom() + " place en case" + j.lastCoup().getIdCase());
        }


        return jeu.finTour(joueur.lastCoup());
    }

    public static void pause(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Tapez 'Entrer' pour le prochain coup");
        String nom = scanner.nextLine();
    }

}
