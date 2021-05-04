package game.classes;

import java.util.Comparator;

public class CompareValeurs implements Comparator<PieceStrategique> {

    @Override
    public int compare(PieceStrategique piece1, PieceStrategique piece2) {
        if(piece1.getValeur() > piece2.getValeur()) return -1;
        else if(piece1.getValeur() < piece1.getValeur()) return 1;
        else return 0;
    }
}
