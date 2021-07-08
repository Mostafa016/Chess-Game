package GUI;

import Logic.Board;
import Logic.Game;
import Logic.Piece;
import Logic.Position;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Moving implements MouseListener, MouseMotionListener {
    
    private final JFrame frame;
    private final Game game;
    private final Board board;
    private final BoardGUI boardGUI;
    
    private Position Drag = null; /* position of the spot that piece drgged 
                                        from when you choose (Drag and Drop) method*/    
    
    private Position Drop = null; /* position of the spot that piece dropped
                                            on when you choose(Drag and Drop) method*/
    
    private Position firstSelectedSpot = null; /* position of the moving piece
                                                         when you choose 
                                                         click-to-move method*/
    
    private Position secondSelectedSpot = null; /* position of the moving
                                                        piece final destination when
                                                        you choose click-to-move
                                                        method */
    
    private JLabel c; // The moving piece
    
    private int spotWidth; // The spot width
    
    private int spotHeight; // The spot height
    
    private Position mouseSpotPos; // Position of the mouse inside a spot
    
    private Position validPosition[]; //The valid moves of the moving piece
    
    private int validLength; // "validPosition" array length
    
    private boolean moved = false; // Check if a piece dragged on the board
    
    private final Promotion promotionWindow; // The pop up promotion window
    
    private static Position promotionPosition; //last promotion piece position
    
    private Position lastPointedPosition; /* last spot postition that 
                                                    mouse pointed at */
    
    private static final Cursor hand = new Cursor(Cursor.HAND_CURSOR); //hand cursor
    
    private static final Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR); /* default
                                                                             cursor */
    
 /*************************************************************************/
    
    /*Methods*/
    
    public Moving(JFrame frame ,Game g, BoardGUI boardGUI,
            int spotWidth, int spotHeight)
    {
        this.frame = frame;
        this.game = g;
        this.board = game.getBoard();
        this.boardGUI = boardGUI;
        this.spotWidth = spotWidth;
        this.spotHeight = spotHeight;
        this.lastPointedPosition = new Position();
        this.promotionWindow = new Promotion(this.frame, this.board, this.boardGUI);
    }
    
    public void firstTimeselecting(Point p)
    {
        firstSelectedSpot = determineSpot(p); //get spot position and set the piece(c)
        //if firstSelectedSpot was valid and the piece color was the same player color
        if (firstSelectedSpot != null && boardGUI.hasSamePlayerColor(firstSelectedSpot))
        {
            displayValidMoves(firstSelectedSpot); //display piece valid positions
        }
        else
        {
            firstSelectedSpot = null;
        }
    }
    
    public void secondTimeselecting(Point p)
    {
        secondSelectedSpot = determineSpotPosition(p);//get spot position
        
        boardGUI.hideValidMoves(firstSelectedSpot, game.getLastMove(1)); /* hide "firstSelectedSpot" 
                                                        valid Positions */
        
        validPosition = null; /*it have to be null to stop showing "firstSelectedSpot"
                                Pointing rectangles after second click*/
        
        if (secondSelectedSpot != null)
        {
            if (game.move(firstSelectedSpot, secondSelectedSpot)) //if move is done
            {
                boardGUI.updateMove(game.getLastMove(1), false); /* set the piece to
                                                             the new position */
                
                boardGUI.hideCheck(game.getLastMove(2)); /*Hideing the privious check
                                                            square after move*/
                
                boardGUI.removeLastMovePositions(); /*remove the previous move
                                                      (Golden Rectangle)*/
                
                if (promotionWindow.isPromotion(secondSelectedSpot))
                {
                    promotionWindow.showPromotionWindow(secondSelectedSpot);
                    if (!promotionWindow.isPromoted())
                    {
                        boardGUI.Undo();
                    }
                    promotionWindow.setPromoted(false);
                }
                boardGUI.displayCheck(game.getLastMove(1)); /* displying the check
                                                                square if it is check */
                
                boardGUI.changeCursor(); //change all spots cursors
            }
            else
            {
                /* if the second click was on another piece from the same player
                color then we need to consider that second click is the first click */
                if (boardGUI.hasSamePlayerColor(secondSelectedSpot) &&
                        !Position.equals(firstSelectedSpot, secondSelectedSpot))
                {
                    firstSelectedSpot = null;
                    secondSelectedSpot = null;
                }
            }
            boardGUI.displayLastMovePositions(); /*remove the previous move
                                                    (Golden Rectangle)*/
        }
        else
        {
            firstSelectedSpot = null;
        }
    }
    
    public void dragPiece (Point p)
    {
         Drag = determineSpot(p); // determine spot that mouse pressed;
         
         /*Drag was valid and the piece color was the same player color*/
        if (Drag != null && boardGUI.hasSamePlayerColor(Drag)) 
        {
            mouseSpotPos = getMouseSpotPosition(p, Drag);/*get mouse position in
                                                           the spot */
            
            c.setCursor(normal); //change piece cursor
        }
        else
            Drag = null;
    }
    
    public void dropPiece (Point p)
    {
        Drop = determineSpotPosition(p); // determine spot that mouse released in;
        
        // if the Drag Position was valid
        if (Drag != null)
        {
            boardGUI.hideValidMoves(Drag, game.getLastMove(1)); // hide Dragged piece valid positions
            boardGUI.getSpot(Drag).getPiece().setCursor(hand); //change piece cursor
            if (Drop != null && game.move(Drag, Drop)) //if the move is done
            {
                boardGUI.remove(c); // remove the piece from the board
                
                boardGUI.removeLastMovePositions(); /*remove the previous move
                                                      (Golden Rectangle)*/
                
                boardGUI.hideCheck(game.getLastMove(2));
                
                boardGUI.updateMove(game.getLastMove(1), false); /*set the piece to 
                                                            the new position*/
                
                if (promotionWindow.isPromotion(Drop))
                {
                    promotionWindow.showPromotionWindow(Drop);
                    if (!promotionWindow.isPromoted()) 
                    {
                        boardGUI.Undo();
                    }
                    promotionWindow.setPromoted(false);
                }
                
                boardGUI.changeCursor(); //change all spots cursors
            }
            boardGUI.displayCheck(game.getLastMove(1)); /* displying the check
                                                            square if it is check */
            
            boardGUI.displayLastMovePositions(); /* display the last move
                                                    (Golden Rectangle) */
            
            boardGUI.repaint(); boardGUI.validate();
        }
        // if the action wasn't a click
        if (moved)
        {
            validPosition = null; /*it have to be null after dragging a piece to stop
                                showing Pointing rectangles after the mouse released*/
        }
        // setting them to default values
        Drag = null; Drop = null; c = null; mouseSpotPos = null; moved = false;
    }
    public void movePieceOnBoard (Point p)
    {
        boardGUI.hideValidMoves(firstSelectedSpot, game.getLastMove(1));/*hide the first selected piece
                                                     if a piece was selected before
                                                     another was dragged*/
        
        if (moved == false) //to avoid displaying more than one time
        {
            boardGUI.displayLastMovePositions(); /* in case a piece was selected
                                                    before anything else (except
                                                    the selected piece) was dragged
                                                    and some of the selected piece
                                                    valid positions is at a last move
                                                    position as its valid position
                                                    will hide the last move so we
                                                    need to display it again*/
        }
        if (Drag != null)
        {
           // first time dragging
           if (moved == false)
           {
              
              displayValidMoves(Drag); //display the dragged piece colored spots
              boardGUI.add(c); //add the piece dragged to the board
              boardGUI.setComponentZOrder(c, 0); //make the piece in front of the board
           }
           
           /* set the location of the piece according to the mouse position
           (this makes the piece to move with the mouse)*/
           c.setLocation( (p.x) - (mouseSpotPos.x), (p.y) - (mouseSpotPos.y) );
           
            pointing(board.getSpot(Drag).getPiece() ,p); /* display the pointing
                                                            rectangles if the mouse
                                                            pointed to a valid position */
        }
        boardGUI.repaint(); boardGUI.validate();
        firstSelectedSpot = null; /* it needs to be null to avoid hiding
                                     the first selected piece once again */
       
        moved = true; // it will be true if the mouse dragged only one time
    }
    private void displayValidMoves(Position pos)
    {
        //initialize the valid position array to display it on the board
        validPosition = board.getSpot(pos).getPiece().getValidMoves();
        validLength = validPosition.length; 
        boardGUI.displayValidMoves(pos, game.getLastMove(1)); //display the valid positions
    }
    
    //displays a colored square if the mouse pointed to a valid position
    private void pointing(Piece movingPiece,Point p)
    {
        Position pos = determineSpotPosition(p);
        if (pos != null) // if the position was valid
        {
            
            if (!Position.equals(lastPointedPosition,pos))
                /*if the mouse pointed to another spot as "lastPointedPosition"
                wasn't updated to the new position yet to avoid drawing the same
                spot many times*/
            {
                // display the new valid position colord rectangle
                boardGUI.displayValidPointed(movingPiece ,pos,game.getLastMove(1));
                
                // hide the previous valid position colord rectangle
                boardGUI.hideValidPointed(movingPiece ,lastPointedPosition, game.getLastMove(1));
            }
            lastPointedPosition = pos; // Updating "lastPointedPosition"
        }
    }
    public Position determineSpotPosition(Point p)
    {
        Position pos = new Position();
        pos.x = (p.x/spotWidth) + 1; /* frame x-axis starts from left to 
                                        right as we do with spots */
        
        pos.y = 8 - (p.y/spotHeight); /* frame y-axis is inverted as it starts
                                         from up to down so we invert it by
                                         subtracting from 8(number of board rows) */
        
        if (boardGUI.isValidPosition(pos)) 
            //if position was only a spot position (from 1 to 8)
        {
            return pos;
        }
        else
        {
            return null;
        }
    }
    // retrun spot position and set the c value to the spot piece
    private Position determineSpot(Point p)
    {
        Position pos = determineSpotPosition(p);
        if (pos != null) //if position was valid
        {
            c = boardGUI.getSpot(pos).getPiece(); 
            return pos;
        }
        else
        {
            return null;
        }
    }
    /* It returns the position of the mouse inside a spot to know where exactly 
     the mouse pointed on the piece to move the piece from its same point */
    public Position getMouseSpotPosition(Point p, Position spotPos)
    {
        Position pos = new Position();
        pos.x = Math.abs( (p.x) - (boardGUI.getSpot(spotPos).getX()) );
        pos.y = Math.abs( (p.y) - (boardGUI.getSpot(spotPos).getY()) );
        return pos;
    }
    
    //setters
    public static void setPromotionPosition(Position Pos) {
        promotionPosition = Pos;
    }
    ///////////
    
    /**Getters**/
    public Position[] getValidPosition()
    {
        return validPosition;
    }
    public int getValidPositionLength()
    {
        return validLength;
    }
    public static Position getPromotionPosition() 
    {
        return promotionPosition;
    }
    /****************************************************************************/
        
    /*Events*/
    
    @Override
    public void mouseClicked(MouseEvent me)
    {
        if (me.getButton() == 1) // left button is clicked
        {
            // second click when the "firstSelectedSpot" has a value
            if (firstSelectedSpot != null)
            {
                secondTimeselecting(me.getPoint());
            }
            // after second click when "secondSelectedSpot" has a value (if piece moved)
            if (secondSelectedSpot != null)
            {
                firstSelectedSpot = null;
                secondSelectedSpot = null;
            }
            // first click when the "firstSelectedSpot" was null
            else if (firstSelectedSpot == null)
            {
                firstTimeselecting(me.getPoint());
            }
        }
        else if (me.getButton() == 3) // right button is clicked
        {
            boardGUI.hideValidMoves(firstSelectedSpot, game.getLastMove(1));
            firstSelectedSpot = null;
            validPosition = null;
            boardGUI.Undo();
        }
    }
    @Override
    public void mousePressed(MouseEvent me) 
    {
        if (me.getButton() == 1) // left button is clicked
        {
            dragPiece (me.getPoint());
        }
    }
    @Override
    public void mouseReleased(MouseEvent me) 
    {
        dropPiece(me.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent me){}

    @Override
    public void mouseExited(MouseEvent me){}

    @Override
    public void mouseDragged(MouseEvent me) 
    {
        movePieceOnBoard(me.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent me) 
    {
        if (firstSelectedSpot != null)
        pointing(board.getSpot(firstSelectedSpot).getPiece() ,me.getPoint());
    }    
}