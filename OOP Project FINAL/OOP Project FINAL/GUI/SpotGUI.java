package GUI;

import Logic.Piece;
import Logic.Position;
import Logic.Board;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
    //old one
    private static final Color LIGHT_COLOR = new Color(215, 190, 148);
    private static final Color DARK_COLOR = new Color(151, 93, 7);

//blue
    private static final Color LIGHT_COLOR = new Color(204,229,255);
    private static final Color DARK_COLOR = new Color(28,142,237);
*/
public class SpotGUI extends JPanel {
    private static final Color LIGHT_COLOR = new Color(215, 190, 148);
    private static final Color DARK_COLOR = new Color(151, 93, 7);
    private JLabel pieceImage;
    private static final Cursor hand = new Cursor(Cursor.HAND_CURSOR);
    private static final Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR);
    
    //Methods
    public SpotGUI(final Board board, Position position) 
    {
        super(new BorderLayout());
        assignSpotColor(position);
        addPieceImage(board, position);
        validate();
    }

    private void assignSpotColor(Position position) 
    {
        if (position.y % 2 == 0) 
        {
            setBackground(position.x % 2 == 0 ? DARK_COLOR : LIGHT_COLOR);
        } 
        else 
        {
            setBackground(position.x % 2 == 0 ? LIGHT_COLOR : DARK_COLOR);
        }
    }

    public void addPieceImage(final Board board,Position position) 
    {
        if (board.getSpot(position).getHasPiece()) 
        {
            removeAll(); // remove all components in the spot
            Piece piece = board.getSpot(position).getPiece();
            String imageName = piece.getColor().name() +
                    piece.getClass().getSimpleName(); //determine piece piecture name
            try 
            {
                final BufferedImage image = ImageIO.read(new File("Images/" + imageName + ".png"));
                Image scaledImage = image.getScaledInstance(88, 88, Image.SCALE_SMOOTH);
                pieceImage = new JLabel(new ImageIcon(scaledImage));
                add(pieceImage, BorderLayout.CENTER);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        else
        {
            pieceImage = null; //set the piece null if there is a piece
            removeAll();
        }
            repaint();
            validate();
    }
    
    //Displaying a valid move position from the valid moves array
    public void drawValidMove(int spotWidth, int spotHeight, boolean enPassant)
    {
        this.removeAll(); //remove all components first
        if (pieceImage == null && !enPassant) // if spot hadn't had a piece
        {
            Color c = new Color(0,80,0,120);// green
            Circle g = new Circle(spotWidth, spotHeight, c);// Green circle
            g.setSize(spotWidth, spotHeight); //Setting green circle panel size
            this.add(g);// adding the green circle
        }
        else if (enPassant)
        {
            Color c = new Color(120,0,0,120); //red
            Circle circle = new Circle(spotWidth, spotHeight, c);// red circle
            circle.setSize(spotWidth, spotHeight); //Setting red circle panel size
            this.add(circle); //adding it to spot
        }
        else //if spot had had a piece
        {
            RoundedTriangles r = new RoundedTriangles(); //Red Triangles
            r.setSize(spotWidth, spotHeight); //Setting red triangles panel size
            this.add(r); //adding it to spot
            this.add(pieceImage); //Adding the piece
        }
        repaint();
    }
    
    /*Displaying a colored Square*/
    public void drawSquare(int spotWidth, int spotHeight, Color c, Cursor cur)
    {
        this.removeAll(); //Removing everything in spot
        ColoredSquare g = new ColoredSquare(spotWidth, spotHeight, c); /*making a new 
                                                                        square*/
        
        g.setSize(spotWidth, spotHeight); //Setting its size the as same as spot size
        g.setCursor(cur); //Setting its cursor
        this.add(g); //Adding the square to spot
        if (pieceImage != null) //if the spot had a piece
        {
            this.add(pieceImage); //Adding the piece again
            setComponentZOrder(pieceImage, 0); /* Making the piece in front of
                                                the square */
            
            pieceImage.setCursor(cur); //Setting its cursor
        }
        repaint();
    }
    
    //Erasing everything and draw piece if there is a piece
    public void drawPieceOnly()
    {
        this.removeAll();
        if (pieceImage != null)
            this.add(pieceImage);
        repaint();
    }
    
    //Setters
    public void setPiece(JLabel label)
    {
        pieceImage = label;
        if (pieceImage != null){
            add(pieceImage);
        }
        repaint();
    }
    ////////////
    
    //Getters
    public JLabel getPiece()
    {
        return pieceImage;
    }
    /////////
}