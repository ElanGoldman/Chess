import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Pawn implements Piece{
    private final boolean isWhite;
    private final String img;

    public static int value = 1;

    private int modifier;
    private Boolean hasMoved = false;

    public Pawn (Boolean isWhite){
        this.isWhite = isWhite;

        img = isWhite ? "WP" : "BP";
        modifier = this.isWhite ? 1: -1;
    }

    public List<Point> getMoves(){
        List<Point> moves = new LinkedList();
        moves.add(new Point(0,modifier * 1));
        if (!hasMoved){
            moves.add(new Point(0,modifier * 2));
        } else {
            moves.add(new Point(0,modifier * 1));
        }
        return moves;
    }

    public List<Point> getCaptures(){
        List<Point> captures = new LinkedList();

        captures.add(new Point(1, 1 * modifier));
        captures.add(new Point(1, 1 * modifier));

        captures.add(new Point(-1, 1 * modifier));
        captures.add(new Point(-1, 1 * modifier));

        return captures;
    }

    public boolean isWhite(){
        return isWhite;
    }
    public void move(){
        hasMoved = true;
    }

    public boolean hasMoved(){
        return hasMoved;
    }

    public String getImageFile(){
        return img;
    }

    @Override
    public String getChar() {
        return "P";
    }
}
