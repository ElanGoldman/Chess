import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Chess {
    private Piece[][] board;
    private List<String> moves;
    private boolean whiteTurn = true;
    private Pawn enPassantR;
    private Pawn enPassantL;
    private enum pieces {
        ROOK, BISHOP, KNIGHT, KING, QUEEN
    }
    private final pieces[] placement;

    public static final String[] columnNames = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};

    public Chess(){
        placement = new pieces[8];
        placement[0] = pieces.ROOK;
        placement[1] = pieces.KNIGHT;
        placement[2] = pieces.BISHOP;

        placement[3] = pieces.KING;
        placement[4] = pieces.QUEEN;

        placement[5] = pieces.BISHOP;
        placement[6] = pieces.KNIGHT;
        placement[7] = pieces.ROOK;

        board = startBoard();
    }
    public Piece[][] startBoard() {
        board = new Piece[8][8];
        whiteTurn = true;
        enPassantR = null;
        enPassantL = null;
        moves = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            board[i][1] = new Pawn(true);
            board[i][6] = new Pawn(false);
            if (placement[i] == null){
                continue;
            }
            switch (placement[i]){
                case ROOK -> {
                    board[i][0] = new Rook(true);
                    board[i][7] = new Rook(false);
                }
                case KNIGHT -> {
                    board[i][0] = new Knight(true);
                    board[i][7] = new Knight(false);
                }
                case BISHOP -> {
                    board[i][0] = new Bishop(true);
                    board[i][7] = new Bishop(false);
                }
                case KING -> {
                    board[i][0] = new King(true);
                    board[i][7] = new King(false);
                }
                case QUEEN -> {
                    board[i][0] = new Queen(true);
                    board[i][7] = new Queen(false);
                }
            }
        }
        return board;
    }

    public void reset () {
        board = startBoard();
    }

    public void setToPosition (List<String> newMoves){
        Piece[][] newBoard = startBoard();
        for(String move : newMoves){
            if (!move(move)){
                System.out.println(move + " was invalid.");
                break;
            }
        }
    }

    public boolean move(Piece p, int x, int y) {
        if (whiteTurn != p.isWhite()){
            return false;
        }

        Point endLoc = new Point(x, y);
        if (!getMoves(p).contains(endLoc) && !getCaptures(p).contains(endLoc)){
            return false;
        }

        Point startLoc = getLocation(p);
        String moveName = getMoveName(p, startLoc, endLoc);

        board[startLoc.x][startLoc.y] = null;
        board[x][y] = p;

        if (p == enPassantL && x == startLoc.x - 1){
            board[x][y + (p.isWhite() ? -1 : 1)] = null;
        } else if (p == enPassantR && x == startLoc.x + 1){
            board[x][y + (p.isWhite() ? -1 : 1)] = null;
        }

        enPassantR = null;
        enPassantL = null;

        p.move();

        if (p instanceof Pawn){
            if (y == startLoc.y + 2){
                Piece trg = getPiece(x + 1, 3);
                if (trg instanceof Pawn && !trg.isWhite()){
                    enPassantL = (Pawn) trg;
                }
                trg = getPiece(x - 1, 3);
                if (trg instanceof Pawn && !trg.isWhite()){
                    enPassantR = (Pawn) trg;
                }
            } else if (y == startLoc.y - 2 && !p.isWhite()) {
                Piece trg = getPiece(x + 1, 4);
                if (trg instanceof Pawn && trg.isWhite()){
                    enPassantL = (Pawn) trg;
                }
                trg = getPiece(x - 1, 4);
                if (trg instanceof Pawn && trg.isWhite()){
                    enPassantR = (Pawn) trg;
                }
            }

            if(y == (p.isWhite() ? 7 : 0)){
                board[x][y] = new Queen(p.isWhite());
            }
        }

        //castling
        if (p instanceof King){
            switch (startLoc.x - endLoc.x){
                case 2 -> {
                    board[2][y] = board[0][y];
                    board[0][y] = null;
                }
                case -2 -> {
                    board[4][y] = board[7][y];
                    board[7][y] = null;
                }
            }

        }
        if (inCheck(isWhiteTurn())){
            moves.add(moveName);
            undo();
            return false;
        }

        moves.add(moveName);
        whiteTurn = !whiteTurn;

        if (inCheck(isWhiteTurn())){
            moveName += "+";
            moves.set(moves.size() - 1, moveName);
        }

        return true;
    }

    public boolean move(Piece p, Point e) {
        return move(p, e.x, e.y);
    }

    public void undo(){
        if (moves.isEmpty()){
            return;
        }
        moves.remove(moves.size() - 1);
        setToPosition(moves);
    }

    public boolean isValidMove(int x, int y){
        if(!(inBounds(x) && inBounds(y))){
            return false;
        }
        return getPiece(x, y) == null;
    }
    public boolean inBounds(int a){
        return a >= 0 && a < 8;
    }

    public List<Point> getMoves(Piece p){
        Point po = getLocation(p);
        List<Point> pMoves = new LinkedList<>();
        if (po == null){
            return pMoves;
        }
        Iterator<Point> potentialMoves = p.getMoves().iterator();
        for (Point s = potentialMoves.next(); potentialMoves.hasNext();){
            Point e = potentialMoves.next();
            while(!s.equals(e) && isValidMove(po.x + s.x, po.y + s.y)){
                pMoves.add(new Point(po.x + s.x, po.y + s.y));
                s = approach(s, e);
            }
            if (isValidMove(po.x + s.x, po.y + s.y)){
                pMoves.add(new Point(po.x + s.x, po.y + s.y));
            }
            if (potentialMoves.hasNext()){
                s = potentialMoves.next();
            }
        }
        if (!(p instanceof King)){
            return pMoves;
        }

        for (Point m : List.copyOf(pMoves)){
            if (isAttacked(m, !p.isWhite())){
                pMoves.remove(m);
            }
        }

        if (p.hasMoved()) {
            return pMoves;
        }

        //castling
        Piece rook = getPiece(7, po.y);
        if (rook instanceof Rook && !rook.hasMoved() && pathIsClear(new Point(6, po.y), po, p.isWhite())){
            pMoves.add(new Point(5, po.y));
        }
        rook = getPiece(0, po.y);
        if (rook instanceof Rook && !rook.hasMoved() && pathIsClear(new Point(1, po.y), po, p.isWhite())){
            pMoves.add(new Point(1, po.y));
        }

        return pMoves;
    }

    public boolean move(String rawMove) {
        String move = rawMove.replace("+", "").replace("=", "");

        if (move.length() < 2 ){
            System.out.println(rawMove + " is not formatted correctly.");
            return false;
        }
        List<String> moveParts = Arrays.asList(move.split(""));
        int y = Integer.parseInt(moveParts.get(moveParts.size() - 1));
        int x = getColumnIndex(moveParts.get(moveParts.size() - 2));
        Point end = new Point(x, y);
        int pos = 0;
        List<Piece> potentials = getPiecesThatCanMoveTo(end);
        Piece type = null;
        switch (moveParts.get(0)){
            case "K" -> type = new King(isWhiteTurn());
            case "Q" -> type = new Queen(isWhiteTurn());
            case "B" -> type = new Bishop(isWhiteTurn());
            case "N" -> type = new Knight(isWhiteTurn());
            case "R" -> type = new Rook(isWhiteTurn());
            default -> {
                if (moveParts.contains("x")){
                    for (Piece p : potentials.stream().filter(
                            p -> p instanceof Pawn && p.isWhite() == isWhiteTurn()).toList()){
                        if (getCaptures(p).contains(end)){
                            type = p;
                            break;
                        }
                    }
                } else {
                    for (Piece p : potentials.stream().filter(
                            p -> p instanceof Pawn && p.isWhite() == isWhiteTurn()).toList()
                    ){
                        if (getMoves(p).contains(end)){
                            type = p;
                            break;
                        }
                    }
                }
                return move (type, x, y);
            }

        }

        List<Piece> pWithCorrectType = getPiecesOfType(type);

        potentials = potentials.stream().filter(p -> pWithCorrectType.contains(p)).toList();
        if(potentials.size() > 1){
            try {
                int col = Integer.parseInt(moveParts.get(1));
                type = potentials.stream().filter(a -> getLocation(a).y == col).findFirst().get();
            } catch (NumberFormatException e){
                try {
                    int col = Integer.parseInt(moveParts.get(2));
                    Point start = new Point(getColumnIndex(moveParts.get(1)), col);
                    type = potentials.stream().filter(a -> getLocation(a) == start).findFirst().get();
                } catch (NumberFormatException f){
                    int row = getColumnIndex(moveParts.get(1));
                    type =  potentials.stream().filter(a -> getLocation(a).x == row).findFirst().get();

                }
            }
        } else if (potentials.isEmpty()) {
            System.out.println(rawMove + " is not a valid move.");
        } else {
            type = potentials.get(0);
        }
        return move(type, x, y);
    }

    private int getColumnIndex(String ele){
        for (int i =0; i < columnNames.length; i++){
            if (columnNames[i].equals(ele)){
                return i;
            }
        }
        return -1;
    }

    public boolean isValidCapture(Piece p, int x, int y){
        if(!(inBounds(x) && inBounds(y)) || getPiece(x, y) == null){
            return false;
        }
        return getPiece(x, y).isWhite() != p.isWhite();
    }

    public List<Point> getCaptures(Piece p){
        Point po = getLocation(p);
        List<Point> caps = new LinkedList<>();
        if (po == null){
            return caps;
        }
        Iterator<Point> potentialCaps = p.getCaptures().iterator();
        for (Point s = potentialCaps.next(); potentialCaps.hasNext();){
            Point e = potentialCaps.next();
            while(!s.equals(e) && isValidMove(po.x + s.x, po.y + s.y)){
                s = approach(s, e);
            }
            if (isValidCapture(p, po.x + s.x, po.y + s.y)){
                caps.add(new Point(po.x + s.x, po.y + s.y));
            }
            if (potentialCaps.hasNext()){
                s = potentialCaps.next();
            }
        }
        if (p == enPassantR){
            caps.add(new Point(po.x + 1, p.isWhite() ? 5 : 2 ));
        } else if (p == enPassantL) {
            caps.add(new Point(po.x - 1, p.isWhite() ? 5 : 2 ));
        }
        return caps;
    }

    public static Point approach(Point s, Point e){
        int x = Integer.compare(e.x, s.x);
        int y = Integer.compare(e.y, s.y);
        return new Point(s.x + x, s.y + y);
    }

    public Boolean pathIsClear(Point s, Point e, boolean isWhite){
        if (s.equals(e)){
            return true;
        }
        if (getPiece(s) != null || isAttacked(s, !isWhite)){
            return false;
        }
        int x = Integer.compare(e.x, s.x);
        int y = Integer.compare(e.y, s.y);
        return pathIsClear(new Point(s.x + x, s.y + y), e, isWhite);
    }

    public boolean isAttacked(Point p, boolean isWhite){
        //Returns true if a piece on the given point could be captured by a piece of color isWhite
        for (Piece piece : getColorPieces(isWhite)){
            if (piece instanceof Pawn){
                Point loc = getLocation(piece);
                if (loc.y == p.y + (isWhite ? -1 : 1) && (loc.x == p.x + 1 || loc.x == p.x - 1)){
                    return true;
                }
            }
            if (piece instanceof King){

            } else if (getMoves(piece).contains(p) || getCaptures(piece).contains(p)){
                return true;
            }
        }
        return false;
    }

    public String getMoveName(Piece p, Point start, Point end){
        String move = "";
        if (!(p instanceof Pawn)){
            move += p.getChar();

            List<Piece> viablePieces = getPiecesOfType(p).stream().filter(
                    a -> a != p && (getMoves(a).contains(end) || getCaptures(a).contains(end))
            ).toList();
            if (!viablePieces.isEmpty()){
                List<Piece> shareX = viablePieces.stream().filter(a -> getLocation(a).x == start.x).toList();
                List<Piece> shareY = viablePieces.stream().filter(a -> getLocation(a).y == start.y).toList();
                if (shareX.isEmpty()){
                    move += columnNames[start.x];
                } else if (shareY.isEmpty()) {
                    move += start.y;
                } else {
                    move += columnNames[start.x] + start.y;
                }
            }
        } else if (getPiece(end) != null){
            move += columnNames[start.x];
        }

        if (getPiece(end) != null){
            move += "x";
        }

        move += columnNames[end.x];
        move += end.y;
        return move;
    }

    public List<String> getMoveHistory () {
        return moves;
    }


    public Piece getPiece(int x, int y){
        if (!inBounds(x) || !inBounds(y)){
            return null;
        }
        return board[x][y];
    }

    public List<Piece> getPiecesOfType(Piece p) {
        List<Piece> pieces = new LinkedList<>();
        Arrays.stream(board).forEach(
                a -> pieces.addAll(Arrays.stream(a).filter(
                        b -> {
                            if (b==null) {
                                return false;
                            }
                            return b.getClass().equals(p.getClass()) && b.isWhite() == p.isWhite();
                        }
                ).toList())
        );

        return pieces;
    }

    public List<Piece> getPiecesThatCanMoveTo(Point pos) {
        List<Piece> pieces = new LinkedList<>();
        Arrays.stream(board).forEach(c -> pieces.addAll(Arrays.stream(c).filter(
                p -> getMoves(p).contains(pos) || getCaptures(p).contains(pos)
        ).toList()));
        return pieces;
    }

    public Piece getPiece(@NotNull Point p){
        return getPiece(p.x, p.y);
    }
    public Point getLocation(Piece p){
        if (p == null){
            return null;
        }
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                if (board[x][y] == p){
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public List<Piece> getColorPieces(Boolean isWhite){
        List<Piece> pieces = new LinkedList<>();
        Arrays.stream(board).forEach(
                a -> pieces.addAll(Arrays.stream(a).filter(p -> p != null &&p.isWhite() == isWhite).toList())
        );
//        for (Piece[] r : board){
//            for (Piece p : r) {
//                if (p == null) {
//                    continue;
//                }
//                if (p.isWhite() == isWhite) {
//                    pieces.add(p);
//                }
//            }
//        }
        return pieces;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public boolean inCheck(King k) {
        Point kingLoc = getLocation(k);
        for (Piece p : getColorPieces(!k.isWhite())){
            if (getCaptures(p).contains(kingLoc)){
                return true;
            }
        }
        return false;
    }

    public boolean inCheck(boolean isWhite) {
        for (Piece p : getColorPieces(isWhite)){
            if (p instanceof King){
                return inCheck((King) p);
            }
        }
        return false;
    }

    public boolean inCheckmate(King k){
        if (!inCheck(k)){
            return false;
        }
        Set<Point> locations = new HashSet<>();
        getColorPieces(k.isWhite()).forEach(p -> locations.add(getLocation(p)));
        for (Point po : locations){
            Piece p = getPiece(po);

            List<Point> pMoves = getMoves(p);
            pMoves.addAll(getCaptures(p));

            for (Point m : pMoves){
                p = getPiece(po);
                if (move(p, m)){
                    System.out.println(moves.get(moves.size() - 1));
                    undo();
                    return false;
                }
            }
        }
        System.out.println("checkmate");

        moves.set(moves.size() - 1, moves.get(moves.size() - 1).replace("+", "") + "=");

        return true;
    }

    public boolean inCheckmate(boolean isWhite) {
        for (Piece p : getColorPieces(isWhite)){
            if (p instanceof King){
                return inCheckmate((King) p);
            }
        }
        return false;
    }

    public boolean inStalemate(){
        for (Piece p : getColorPieces(isWhiteTurn())){
            if (!getMoves(p).isEmpty() || !getCaptures(p).isEmpty()){
                return false;
            }
        }
        return true;
    }
}
