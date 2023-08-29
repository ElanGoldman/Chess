import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Bishop implements Piece {
    private final boolean isWhite;
    private final String img;

    public static int value = 3;

    private static final List<Point> moves = bishopMoves();

    public Bishop (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WB" : "BB";
    }

    private static List<Point> bishopMoves(){
        List<Point> moves = new LinkedList();
        int[] ones = new int[]{-1, 1};
        for(int x : ones){
            for(int y : ones){
                moves.add(new Point(x, y));
                moves.add(new Point(x * 7, y * 7));
            }
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
    public boolean hasMoved() {
        return true;
    }

    @Override
    public void move() {

    }

    @Override
    public String getImageFile() {
        return img;
    }

    @Override
    public String getChar() {
        return "B";
    }
}
