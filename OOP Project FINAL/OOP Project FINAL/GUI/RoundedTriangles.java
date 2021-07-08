package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class RoundedTriangles extends JPanel{
    
    private final int width = 30, height = 300;
    private final Color c = new Color(180,0,0,120);
    private final Rectangle upLeft, upRight, downLeft, downRight;
    
    public RoundedTriangles ()
    {
        //Initializing the triangles
        this.upLeft = new Rectangle(-19,-50,width,height);
        this.upRight = new Rectangle(49,0,width,height);
        this.downLeft = new Rectangle(-79,-50,width,height);
        this.downRight = new Rectangle(109,-60,width,height);
        ////////////////////////////
        
        this.setOpaque(false);//Making the panel transparent
    }
    
    @Override
    // This function is executed automatically like the main function
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g; //Graphics2D more efficient than Graphics
        g2d.setColor(c); //Setting its color
        
        g2d.rotate(Math.toRadians(45)); /*rotating it 45 degrees and hiding most of 
                                        it in the up-left or the down-right to act
                                        like a triangle*/
        
        g2d.draw(upLeft);//Drawing the up-left triangle
        g2d.fill(upLeft);//Filling the up-left triangle
        
        g2d.draw(downRight);//Drawing the down-right triangle
        g2d.fill(downRight);//Filling the down-right triangle
        
        g2d.rotate(Math.toRadians(-90));/*rotating it -90 degrees (-45 to set it to 
                                        the normal position and -45 to rotate it to
                                        other side) and hiding most of it in the 
                                        up-right or the down-left to act like a
                                        triangle*/
        
        g2d.draw(upRight);//Drawing the up-right triangle
        g2d.fill(upRight);//Filling the up-right triangle
        
        g2d.draw(downLeft);//Drawing the down-left triangle
        g2d.fill(downLeft);//Filling the down-left triangle
    }
}