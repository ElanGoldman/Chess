import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameBoard extends JPanel {
    private final Chess chess;
    private final JLabel status;

    private final JTextPane moves;
    private Piece selected;
    private Point selectedPos;
    private  Piece hovered;
    private Point hoveredPos;

    private String pieceFile = "Standard";

    private boolean orientation = true;

    private final Map <Piece, BufferedImage> images = new HashMap<>();

    public final static int SIDE_LENGTH = 60 * 8;

    public GameBoard (JLabel status, JTextPane moves){
        setFocusable(true);
        this.status = status;
        this.moves = moves;

        chess = new Chess();

        chess.startBoard();
        status.setText(getTurnString());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                Point p = e.getPoint();
                int tileLength = getTileLength();
                selectedPos = new Point(orient(p.x / tileLength), orient(p.y/ tileLength));
                selected = chess.getPiece(selectedPos.x, selectedPos.y);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selected == null){
                    return;
                }

                Point p = e.getPoint();
                int tileLength = getTileLength();

                if (chess.move(selected, orient(p.x / tileLength), orient(p.y/ tileLength))) {
                    status.setText(getTurnString());
                    updateMoves();
                }
                selected = null;
                hoveredPos = new Point (orient(p.x / tileLength), orient(p.y/ tileLength));
                hovered = chess.getPiece(hoveredPos.x, hoveredPos.y);
                repaint(); // repaints the game board
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public  void mouseDragged(MouseEvent e){
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e){
                Point p = e.getPoint();
                int tileLength = getTileLength();

                hoveredPos = new Point (orient(p.x / tileLength), orient(p.y/ tileLength));
                hovered = chess.getPiece(hoveredPos.x, hoveredPos.y);
                repaint();
            }
        });

    }

    public void reset(){
        chess.reset();
        status.setText(getTurnString());
        repaint();
    }

    public void undo(){
        chess.undo();
        status.setText(getTurnString());
        updateMoves();
        repaint();
    }

    public void setToPosition (String s){
        chess.setToPosition(Arrays.stream(s.split("\n")).toList());
        repaint();
    }

    private String getTurnString(){
        return chess.inCheckmate(chess.isWhiteTurn()) ?
                (!chess.isWhiteTurn() ? "White" : "Black") + " wins!"
                : chess.isWhiteTurn() ? "White's turn" : "Black's turn";
    }

    private void updateMoves() {
        moves.setText(getMovesString());
    }

    private void flip(){
        if (orientation == chess.isWhiteTurn()){
            return;
        }

        int tileLength = getTileLength();
        orientation = !orientation;
        Point p = getMousePosition();
        if (p == null){
            repaint();
            return;
        }
        hoveredPos = new Point (orient(p.x / tileLength), orient(p.y/ tileLength));
        hovered = chess.getPiece(hoveredPos.x, hoveredPos.y);
        repaint();
    }
    private int orient(int x){
        if (orientation) {
            return keepInBounds(7 - x);
        } else {
            return keepInBounds(x);
        }
    }

    private int keepInBounds(int x){
        return Math.max(0, Math.min(7, x));
    }
    private void addSpecial(Color[][] specials, int r, int c, Color co){
        if (r >= 0 && c >= 0 && r < 8 && c < 8){
            specials[r][c] = co;
        }
    }
    private void addSpecialPiece(Piece s, Color[][] specials, Point p){
        if (s==null){
            return;
        }
        addSpecial(specials, p.x, p.y, Color.CYAN);
        for (Point move : chess.getMoves(s)) {
            addSpecial(specials, move.x, move.y, Color.green);
        }
        for (Point cap : chess.getCaptures(s)) {
            addSpecial(specials, cap.x, cap.y, Color.red);
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        flip();

        Color[][] specials = new Color[8][8];
        if (selected != null) {
            addSpecialPiece(selected, specials, selectedPos);
        } else if (hovered != null){
            addSpecialPiece(hovered, specials, hoveredPos);
        }

        int tileLength = getTileLength();
        for(int i = 0; i<8; i++){
            for(int j = 0; j<8; j++){
                int a = orient(i);
                int b = orient(j);
                if(a % 2 == b % 2){
                    g.setColor(Color.WHITE);
                    if (specials[a][b] != null){
                        g.setColor(specials[a][b].brighter());
                    }
                } else {
                    g.setColor(Color.GRAY);
                    if (specials[a][b] != null){
                        g.setColor(specials[a][b].darker());
                    }
                }

                g.fillRect(i * tileLength,j * tileLength, tileLength, tileLength);
                Piece p = chess.getPiece(a, b);
                if (p == null) {
                    continue;
                }
                if (images.get(p) == null){
                    String file = pieceFile + "/" +  p.getImageFile() + ".png";
                    try {
                        images.put(p, ImageIO.read(new File(file)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (p != selected){
                    g.drawImage(
                            images.get(p), tileLength * i,
                            tileLength * j, tileLength, tileLength, null
                    );
                }
            }
        }
        if (selected != null) {
            Point m = getMousePosition();
            if (m != null) {
                g.drawImage(
                        images.get(selected), m.x - tileLength / 2, m.y - tileLength / 2,
                        tileLength, tileLength, null
                );
            }
        }
    }

    private String getMovesString () {
        String mo = "";
        for (String move : chess.getMoveHistory()){
            mo += move + "\n";
        }
        return mo;
    }

    private int getTileLength() {
        return Math.min(getSize().height/8, getSize().width/8);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIDE_LENGTH, SIDE_LENGTH);
    }
}
