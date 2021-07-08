package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

public class Circle extends JPanel {
    private final int x, y, width = 24, height = 24;
    private final Color c;
    private final Ellipse2D e;
    
    public Circle(int spotWidth,int spotHeight, Color c)
    {
        //Setting its position
        this.x = (spotWidth - this.width) / 2;
        this.y = (spotHeight - this.height) / 2;
        //////////////////////
        this.c = c;
        e = new Ellipse2D.Double(x, y, width, height);//Intitialize the circle
        this.setOpaque(false); //Making the panel transparent
    }
    
    @Override
    // This function is executed automatically like the main function
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g; //Graphics2D more efficient than Graphics
        g2d.setColor(c); //Setting its color
        g2d.draw(e); //Drawing the square
        g2d.fill(e); //Filling the square
    }
}