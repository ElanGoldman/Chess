import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Queen implements Piece {
    private final boolean isWhite;
    private final String img;

    public static int value = 0;

    private static final java.util.List<Point> moves = queenMoves();

    public Queen (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WQ" : "BQ";
    }
    private static java.util.List<Point> queenMoves(){
        java.util.List<Point> moves = new LinkedList();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                moves.add(new Point(i, j));
                moves.add(new Point(i * 7, j * 7));
            }
        }
        return moves;
    }

    public java.util.List<Point> getMoves(){
        return moves;
    }

    public List<Point> getCaptures(){
        return moves;
    }

    @Override
    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public String getImageFile() {
        return img;
    }

    public void move(){

    }

    public boolean hasMoved(){
        return true;
    }

    @Override
    public String getChar() {
        return "Q";
    }

}
