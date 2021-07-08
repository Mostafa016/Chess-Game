package GUI;

import Logic.Board;
import Logic.Game;
import Logic.Move;
import Logic.Piece;
import Logic.Position;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BoardGUI extends JPanel
{
    private final JFrame frame;
    
    private final Game game;
    
    private final Board board;
    
    // this Array starts from 1,1
    private final SpotGUI spots[][];
    
    private final Moving moving; 
    
    private static int spotWidth; // the spot width
    
    private static int spotHeight; // the spot height
    
    private Move lastMove; // it is used to make undo
    
    private static final Cursor hand = new Cursor(Cursor.HAND_CURSOR); //hand cursor
    
    private static final Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR); /* default
                                                                             cursor */

    public BoardGUI(Game g, JFrame frame){
        
        super(null);
        this.frame = frame;
        game = g;
        board = game.getBoard();
        setSize(680,680);
        this.setBackground(new Color(20,20,20));
        spotWidth = this.getWidth()/8;
        spotHeight = this.getHeight()/8;
        spots = new SpotGUI[9][9];
        drawSpots();
        validate();
        changeCursor();
        moving = new Moving(this.frame ,game, this,spotWidth, spotHeight);
        this.addMouseListener(moving);
        this.addMouseMotionListener(moving);
    }
    

    private void drawSpots()
    {
        Position position = new Position();
        
        for (int i = 1; i <= 8 ; i++)
        {
            position.y = i; 
            for (int j = 1; j <= 8; j++) 
            {
                position.x = j;
                spots[i][j] = new SpotGUI(board,position); //initializing a new spot
                add(spots[i][j]); // adding the spot to the board
                
                /* setting spot location on board */
                
                /* as the frame starts drawing from up to down we have to
                    invert i to start from down by subtracting from 8 */
                spots[i][j].setBounds(spotWidth * (j-1),
                        spotHeight * (8-i),spotWidth,spotHeight); 
                
                //////////////////////////////////
            }
        }
    }
    
    public void Undo()
    {
        hideCheck(game.getLastMove(1));
        game.Undo(); /* the last move in the arrayList(moves) deleted but it still
                        stored in the lastMove object */
        
        updateMove(lastMove, true); // update the spots after undo
        displayCheck(game.getLastMove(1)); /* displays the check red square if the
                                                move after undo was a check */
        
        displayLastMovePositions(); /* display the last move positions after undo 
                                        (the move before the last move) */
        
        changeCursor(); // change cursor after undo as the players turns changed
    }
    
    // update the piece first and last positions
    public void updateMove(Move m, boolean undo)
    {
        if (m != null)
        {
            Position first = m.getFirstPosition();
            Position last = m.getLastPosition();
            
            // draw the two spots again
            spots[first.y][first.x].addPieceImage(board, first);
            spots[last.y][last.x].addPieceImage(board, last);
            ///////////////////////////////
            
            if (m.isCastling())// if this move was a Castling
            {
                Position rookPos = m.getCastlingRookFirstPos();
                Position rookNewPos = m.getCastlingRookNewPos();
                
                // draw the two rook move spots again
                spots[rookPos.y][rookPos.x].addPieceImage(board, rookPos);
                spots[rookNewPos.y][rookNewPos.x].addPieceImage(board, rookNewPos);
                /////////////////////////////////////
            }
            
            //Enpassant
            else if (!undo)// if this fuction wasn't called in "Undo" function
            {
                Move previousMove = game.getLastMove(2);
                if (previousMove != null && previousMove.isEnemyHasEnPassant() && 
                        
                    //if the pawn is in the en passant position(a pawn was captured)
                    Position.equals(previousMove.getEnemyEnPassantPos(),
                            m.getLastPosition()))
                {
                    //removing the pawn captured in enpassant
                    getSpot(previousMove.getLastPosition()).addPieceImage(board,
                            previousMove.getLastPosition());
                }
            }
            else if (undo) // if this fuction was called in "Undo" function
            {
                Move previousMove = game.getLastMove(1);/* it is the privious move
                                                            before deleting the last
                                                            move in the Game.Undo
                                                            function */
                
                if (previousMove != null && previousMove.isEnemyHasEnPassant() && 
                        
                    //if the pawn is in the en passant position(a pawn was captured)
                    Position.equals(previousMove.getEnemyEnPassantPos(),
                            m.getLastPosition()))
                {
                    //get the captured pawn back
                    getSpot(previousMove.getLastPosition()).addPieceImage(board,
                            previousMove.getLastPosition());
                }
            }
            repaint();
        }
        lastMove = game.getLastMove(1); // Storing the last move
    }
    
    // it makes the pieces cursor of the player who have to play, hand and the other normal
    public void changeCursor()
    {
        Position pos = new Position();
        for(int i = 1 ; i <= 8; i++)
        {
            pos.y = i;
            for (int j = 1; j <= 8; j++)
            {
                pos.x = j;
                // if the spot has a piece
                if (board.getSpot(pos).getHasPiece())
                {
                    // if the piece color is the same player color
                    if (hasSamePlayerColor(pos))
                    {
                        spots[i][j].getPiece().setCursor(hand); // make it hand
                    }
                    else
                    {
                        spots[i][j].getPiece().setCursor(normal); //make it normal
                    }
                }
            }
        }
    }
    
    // displayes the last move Golden squares
    public void displayLastMovePositions()
    {
        java.awt.Color c = new java.awt.Color(255,215,0,140); // Gold
        Move m = game.getLastMove(1); // the last move on the moves array
        if (m != null) 
        /* "m" may be null if (only one move is or no moves are)
            in the array and we made an undo */
        {
            Position first = m.getFirstPosition();
            Position last = m.getLastPosition();
            
            // display the Golden squares
            spots[first.y][first.x].drawSquare(spotWidth, spotHeight, c, normal);
            spots[last.y][last.x].drawSquare(spotWidth, spotHeight, c, normal);
            //////////////////////////////
        }
    }
    
    // hides the last move Golden squares
    public void removeLastMovePositions()
    {
        Move previousMove = game.getLastMove(2); /* 2 means the second value
                                                    from the end */
        if (previousMove != null)
            // "previousMove" may be null if the array dosn't have at least 2 values
        {
            Position first = previousMove.getFirstPosition();
            Position last = previousMove.getLastPosition();
            
            // delete golden rectangles
            spots[first.y][first.x].drawPieceOnly();
            spots[last.y][last.x].drawPieceOnly();
            ///////////////////////////
        }
    }
    // displaying valid moves and the moving piece green square
    public void displayValidMoves (Position piecePosition, Move m)
    {
        if (isValidPosition(piecePosition) &&
                moving.getValidPosition() != null)
        {
            for (int i = 0; i < moving.getValidPositionLength(); i++)
            {
                // if the array value is valid as it may be (0,0)
                if (isValidPosition(moving.getValidPosition()[i]))
                {
                    //if this valid position is an En Passant move
                    if (m != null && m.isEnemyHasEnPassant() 
                            && Piece.isPawn(board.getSpot(piecePosition).getPiece())
                            && Position.equals(moving.getValidPosition()[i],
                                    m.getEnemyEnPassantPos()))
                    {
                        getSpot(moving.getValidPosition()[i])
                            .drawValidMove(spotWidth, spotHeight, true);
                    }
                    else
                    {
                        // displaying valid moves
                        getSpot(moving.getValidPosition()[i])
                                .drawValidMove(spotWidth, spotHeight, false);
                    }
                }
            }
            java.awt.Color green = new java.awt.Color(0,120,0,60);
            
            /* if there is a check and the checked king was the selected piece 
            to avoid hiding the check red square*/
            if ((m == null) || !(m.isEnemyInCheck()) || 
                (m.isEnemyInCheck() && !Position.equals(piecePosition, m.getCheckedEnemyKing())))
            {
                // displaying green square of the moving piece if wans't a checked king
                spots[piecePosition.y][piecePosition.x].drawSquare(spotWidth,
                        spotHeight, green, hand);   
            }
        }
    }
    
    // hides valid moves and the moving piece green square
    public void hideValidMoves(Position piecePosition, Move m)
    { 
        if (isValidPosition(piecePosition) &&
                moving.getValidPosition() != null)
        {
            for (int i = 0; i < moving.getValidPositionLength(); i++)
            {
                if (isValidPosition(moving.getValidPosition()[i]))
                {
                    spots[moving.getValidPosition()[i].y][moving.getValidPosition()[i].x]
                            .drawPieceOnly(); // Hide valid moves (green circles)
                    if (board.getSpot(moving.getValidPosition()[i]).getHasPiece())
                    {
                        spots[moving.getValidPosition()[i].y][moving.getValidPosition()[i].x]
                            .getPiece().setCursor(normal);
                    }
                }
            }

            /* if there is a check and the checked king was the selected piece 
            to avoid hiding the check red square*/
            if ((m == null) || !(m.isEnemyInCheck()) || 
                (m.isEnemyInCheck() &&
                    !Position.equals(piecePosition, m.getCheckedEnemyKing())))
            {
                // deleting green square of the moving piece if wans't a checked king
                spots[piecePosition.y][piecePosition.x].drawPieceOnly();
            }
        }
    }
    
    // displays a green or a red square and the moving piece green square
    public void displayValidPointed(Piece movingPiece ,Position pos, Move m)
    {
        if (moving.getValidPosition() != null)
        {
            for (int i = 0; i < moving.getValidPositionLength(); i++)
            {
                // if we found the mouse position in the array
                if (isValidPosition(moving.getValidPosition()[i]) &&
                        Position.equals(pos, moving.getValidPosition()[i]))
                {
                    java.awt.Color c;
                    if (board.getSpot(pos).getHasPiece())
                    {
                        c = new java.awt.Color(180,0,0,120); //Red
                    }
                    else
                    {
                        //if this valid position is an En Passant move
                        if (m != null && m.isEnemyHasEnPassant() &&
                                Piece.isPawn(movingPiece)
                                && Position.equals(pos, m.getEnemyEnPassantPos()))
                        {
                            c = new java.awt.Color(180,0,0,120); //Red
                        }
                        else
                        {
                            c = new java.awt.Color(0,80,0,80); //Green
                        }
                    }
                    getSpot(pos).drawSquare(spotWidth, spotHeight, c, hand);
                    break;
                }
            }
        }
    }
    
    // hides a green or a red square if mouse pull away from a pointed spot
    public void hideValidPointed(Piece movingPiece ,Position pos, Move m)
    {
        if (moving.getValidPosition() != null)
        {
            for (int i = 0; i < moving.getValidPositionLength(); i++)
            {
                // if we found the mouse position in the array
                if (isValidPosition(moving.getValidPosition()[i]) &&
                        Position.equals(pos, moving.getValidPosition()[i]))
                {
                    boolean enPassant = false;
                    // displays the valid position color again
                    
                    //if this valid position is an En Passant move
                    if (m != null && m.isEnemyHasEnPassant() &&
                                Piece.isPawn(movingPiece)
                                && Position.equals(pos, m.getEnemyEnPassantPos()))
                    {
                        enPassant = true;
                        getSpot(pos).drawValidMove(spotWidth, spotHeight, enPassant);
                    }
                    else
                    {
                        getSpot(pos).drawValidMove(spotWidth, spotHeight, enPassant);
                    }
                    break;
                }
            }
        }
    }
    
    //Displayes check red square
    public void displayCheck(Move m)
    {
        if (m != null && m.isEnemyInCheck())
        {
            Position checkedKing = m.getCheckedEnemyKing();
            java.awt.Color red = new java.awt.Color(240,0,0,120);
            getSpot(checkedKing).drawSquare(spotWidth, spotHeight, red, hand);
        }
    }
    
    public void hideCheck(Move m)
    {
        if (m != null && m.isEnemyInCheck())
        {
            Position checkedKing = m.getCheckedEnemyKing();
            getSpot(checkedKing).drawPieceOnly();
        }
    }
    
    // Checkers
    public boolean isValidPosition(Position pos)
    {
        if (pos != null)
        {
            return (pos.y > 0 && pos.y < 9 && pos.x > 0 && pos.x < 9);
        }
        else
        {
            return false;
        }
    }
    
    public boolean hasSamePlayerColor(Position pos)
    {
      if (!isValidPosition(pos) || board.getSpot(pos).getHasPiece() == false)
      {
          return false;
      }
      else
      {
        return board.getSpot(pos).getPiece().getColor().equals(
                    game.getCrntPlayerClr());
      }
    }
    /////////////////////////////////////////////////////////
    
    //Getters
    public SpotGUI getSpot(Position pos)
    {
        return spots[pos.y][pos.x];
    }
    
    public Move getLastMove() 
    {
        return lastMove;
    }
    /////////////
}