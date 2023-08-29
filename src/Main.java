import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Runnable chess = new RunChess();

        SwingUtilities.invokeLater(chess);
    }
}