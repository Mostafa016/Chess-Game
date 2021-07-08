package GUI;

import Logic.Game;

import java.awt.Dimension;
import javax.swing.JFrame;

public class GameFrame extends JFrame {
    private final BoardGUI boardGUI;
    private final Dimension d = new Dimension(696,719);
    public GameFrame(Game g){
        super("Chess");
        setResizable(true);
        boardGUI = new BoardGUI(g, this);
        add(boardGUI);
        pack();
        setMinimumSize(d);
        setLocation(400,10);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        validate();
    }
}