package Logic;

public class Spot {
    private Piece piece = null; // the piece, it's obvious

    private boolean hasPiece = false; // checks if the spot has a piece or not

    public Spot(){
    }

    public Spot(Position position, Piece piece) // constructor of Spots
    {
        this.setPiece(piece, position);
        this.getPiece().setPosition(position.x, position.y);
    }

    public Piece getPiece() // returns the value of the piece
    {
        return piece;
    }

    public void setPiece(Piece piece, Position position) // takes the parameter (piece) and assigns it to the piece variable
    {
        this.piece = piece;
        checkHasPiece(piece);
        if (piece != null)
        {
            piece.setPosition(position.x, position.y);
        }
    }

    public boolean getHasPiece() // returns the value of the boolean hasPiece
    {
        return hasPiece;
    }

    private void checkHasPiece(Piece piece){
        hasPiece = piece != null;
    }

    // This method should be removed it's used anywhere in the code right now
    public void setHasPiece(boolean hasPiece) // takes the parameter (hasPiece) and assigns it to the hasPiece variable
    {
        this.hasPiece = hasPiece;
    }
}