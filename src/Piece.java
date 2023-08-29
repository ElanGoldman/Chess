import java.awt.*;
import java.util.List;

public interface Piece {
    List<Point> getMoves();
    List<Point> getCaptures();
    boolean isWhite();

    boolean hasMoved();

    void move();

    int value = 0;

    String getChar();

    public String getImageFile();
}

