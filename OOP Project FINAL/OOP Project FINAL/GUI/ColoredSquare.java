package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class ColoredSquare extends JPanel{
    private final Color c;
    private final Rectangle r;
    
    public ColoredSquare(int spotWidth, int spotHeight, Color c)
    {
        this.c = c;
        r = new Rectangle(0, 0, spotWidth, spotHeight); //(x, y, width, height)
        this.setOpaque(false); //Making label transparent
    }
    @Override
    // This function is executed automatically like the main function
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g; //Graphics2D more efficient than Graphics
        g2d.setColor(c); //Setting its color
        g2d.draw(r); //Drawing the square
        g2d.fill(r); //Filling the square
    }
}
