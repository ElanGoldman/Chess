import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Rook implements Piece {

    private final boolean isWhite;
    private final String img;

    public static int value = 5;
    private Boolean hasMoved = false;

    private static final List<Point> moves = rookMoves();

    public Rook (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WR" : "BR";
    }

    private static List<Point> rookMoves(){
        List<Point> moves = new LinkedList();
        for (int i : new int[]{-1, 1}){
            moves.add(new Point(i, 0));
            moves.add(new Point(i * 7, 0));
            moves.add(new Point(0, i));
            moves.add(new Point(0, i * 7));
        }

        return moves;
    }
    @Override
    public List<Point> getMoves() {
        return moves;
    }

    @Override
    public List<Point> getCaptures() {
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

    @Override
    public String getChar() {
        return "R";
    }

    public boolean hasMoved(){
        return hasMoved;
    }

}
