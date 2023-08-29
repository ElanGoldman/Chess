import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RunChess implements Runnable{
    public void run(){
        final JFrame frame = new JFrame("Chess");
        frame.setLocation(100,0);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel();
        JPanel labelHolder = new JPanel(new FlowLayout());
        labelHolder.add(label);

        frame.add(labelHolder, BorderLayout.NORTH);

        JTextPane moves = new JTextPane();
        JScrollPane moveScroll = new JScrollPane(moves);
        frame.add(moveScroll, BorderLayout.EAST);

        final GameBoard board = new GameBoard(label, moves);
        moves.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n'){
                    board.setToPosition(moves.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        moves.setPreferredSize(new Dimension(60 , board.getHeight()));
        frame.add(board, BorderLayout.CENTER);

        JButton reset = new JButton("Reset Game");
        reset.addActionListener(l -> board.reset());
        JButton undo = new JButton("undo");
        undo.addActionListener(l -> board.undo());

        JCheckBox botOn = new JCheckBox();

        JPanel buttonHolder = new JPanel(new FlowLayout());
        buttonHolder.add(reset);
        buttonHolder.add(undo);
        buttonHolder.add(botOn);
        frame.add(buttonHolder, BorderLayout.SOUTH);



        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
