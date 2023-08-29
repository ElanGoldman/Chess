import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Knight implements Piece {
    private final boolean isWhite;
    private final String img;

    public static int value = 3;

    private static final java.util.List<Point> moves = knightMoves();

    public Knight (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WN" : "BN";
    }
    private static java.util.List<Point> knightMoves(){
        java.util.List<Point> moves = new LinkedList();
        for (int i : new int[] {2, -2}){
            for (int j : new int[] {1, -1}){
                moves.add(new Point(i, j));
                moves.add(new Point(i , j ));

                moves.add(new Point(j, i));
                moves.add(new Point(j , i ));
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
        return "N";
    }
}
