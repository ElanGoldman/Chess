import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class King implements Piece{
    private final boolean isWhite;
    private final String img;

    public static int value = 0;
    private Boolean hasMoved = false;

    private static final List<Point> moves = kingMoves();

    public King (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WK" : "BK";
    }
    private static List<Point> kingMoves(){
        List<Point> moves = new LinkedList();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                moves.add(new Point(i, j));
                moves.add(new Point(i, j));
            }
        }
        return moves;
    }

    public List<Point> getMoves(){
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
        hasMoved = true;
    }

    public boolean hasMoved(){
        return hasMoved;
    }

    @Override
    public String getChar() {
        return "K";
    }

}
