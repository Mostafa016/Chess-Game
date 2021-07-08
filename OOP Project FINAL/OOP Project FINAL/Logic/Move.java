package Logic;

public class Move 
{
    private final Position firstPosition;
    private final Position lastPosition;
    private final Piece movingPiece;
    private final Piece capturedPiece;
    private final boolean firstMove;
    private boolean castling = false;
    private Position castlingRookFirstPos;
    private Position castlingRookNewPos;
    private boolean enemyInCheck = false;
    private Position checkedEnemyKing;
    private boolean enemyHasEnPassant = false;
    private Position enemyEnPassantPos;
    
    public Move(Position first, Position last, Piece movPiece, Piece capPiece,
            boolean firstMove)
    {
        this.firstPosition = first;
        this.lastPosition = last;
        this.movingPiece = movPiece;
        this.capturedPiece = capPiece;
        this.firstMove = firstMove;
        
    }
    
    public Move (Move m)
    {
        this(m.getFirstPosition(), m.getLastPosition(), m.getMovingPiece(),
                m.getCapturedPiece(), m.isFirstMove());
    }
    
    // Setters
    public void setCastling(boolean b)
    {
        castling = b;
    }
    
    public void setCastlingRookFirstPos(Position position)
    {
        castlingRookFirstPos = new Position(position);
    }
    
    public void setCastlingRookNewPos(Position position)
    {
        castlingRookNewPos = new Position(position);
    }
    
    public void setEnemyInCheck(boolean b)
    {
        this.enemyInCheck = b;
    }
    
    public void setCheckedEnemyKing(Position kingPos)
    {
        this.checkedEnemyKing = kingPos;
    }
    
    public void setEnemyHasEnPassant(boolean b)
    {
        this.enemyHasEnPassant = b;
    }
    
    public void setEnemyEnPassantPos(Position pawnPos)
    {
        this.enemyEnPassantPos = pawnPos;
    }
    
    //////////

    //Gettres
    public Position getFirstPosition() 
    {
        return firstPosition;
    }

    public Piece getMovingPiece() 
    {
        return movingPiece;
    }

    public Position getLastPosition() 
    {
        return lastPosition;
    }

    public Piece getCapturedPiece() 
    {
        return capturedPiece;
    }

    public boolean isFirstMove() 
    {
        return firstMove;
    }
    
    public boolean isCastling()
    {
        return castling;
    }
    
    public Position getCastlingRookFirstPos()
    {
        return castlingRookFirstPos;
    }
    
    public Position getCastlingRookNewPos()
    {
        return castlingRookNewPos;
    }
    
    public boolean isEnemyInCheck()
    {
        return enemyInCheck;
    }
    
    public Position getCheckedEnemyKing()
    {
        return checkedEnemyKing;
    }
    
    public boolean isEnemyHasEnPassant()
    {
        return enemyHasEnPassant;
    }
    
    public Position getEnemyEnPassantPos()
    {
        return enemyEnPassantPos;
    }
    ///////////
}