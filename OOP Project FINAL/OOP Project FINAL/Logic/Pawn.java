package Logic;

public class Pawn extends Piece 
{
    private static final int MAX_NUM_OF_POSSIBLE_MOVES = 4;

    public Pawn(Color c) 
    {
        super(MAX_NUM_OF_POSSIBLE_MOVES, c);
    }


    @Override
    protected boolean correctMove(Position new_pos) 
    {    
        Position test = this.Subtract(position, new_pos);

        if (Math.abs(test.x) == 1 && Math.abs(test.y) == 1) //Diagnolly
            return true;
        
        else if (Math.abs(test.x) == 0 && Math.abs(test.y) == 1)//one vertical spot
            return true;
        
        //tow vertical spots
        else if (Math.abs(test.x) == 0 && Math.abs(test.y) == 2 && firstMove())
            return true;
        
        return false;
    }

    @Override
    public boolean isValidMove(Position new_pos, Spot spots[][]) 
    {
        Direction direction = moveDirection(new_pos);
        
        //Moving diagnolly
        if ((direction.ordinal() == 4 || direction.ordinal() == 5 ||
                direction.ordinal() == 6 || direction.ordinal() == 7)) 
        {
            if (noPiece(spots[new_pos.y][new_pos.x])) //Not has piece (May be en passant)
            {
                return correctMove(new_pos) && isValidDirection(new_pos) &&
                        isEnPassant(new_pos, spots);
            }
            else //Has piece (can be captured)
            {
                return correctMove(new_pos) && isValidDirection(new_pos) &&
                        oppositeColor(spots[new_pos.y][new_pos.x]);
            }
        }
        
        //Moving vertically
        else if ((direction.ordinal() == 0 || direction.ordinal() == 1) &&
                noPiece(spots[new_pos.y][new_pos.x]) )
        {
            return correctMove(new_pos) && isValidDirection(new_pos) &&
                    cleanPath(new_pos, spots);
        }
        
        return false;
    }
    
    //return true if the pawn was at its starting position
    private boolean firstMove()
    {
        return ((getColor().equals(Color.White) && position.y == 2)) ||
                (getColor().equals(Color.Black) && position.y == 7);
    }
    
    
    private boolean isValidDirection(Position new_pos) 
    {
        Direction direction = moveDirection(new_pos);
        
        // White pawn Moves (North or North East or North West)
        if (this.getColor().ordinal() == 0 && ( direction.ordinal() == 0 ||
                direction.ordinal() == 4 || direction.ordinal() == 5 ) )
            return true;
        
        // Black pawn Moves (South or South East or South West)
        else if (this.getColor().ordinal() == 1 && ( direction.ordinal() == 1 ||
                direction.ordinal() == 6 || direction.ordinal() == 7) )
            return true;
        
        else
            return false;
    }
    
    private boolean isEnPassant(Position newPos, Spot spots[][])
    {
        Piece capturedPawn;
        switch(getColor().ordinal())
        {
            case 0: //White
                capturedPawn = spots[newPos.y - 1][newPos.x].getPiece();
                if (newPos.y == 6 && isPawn(capturedPawn) && capturedPawn.isFirstMove())
                    return true;
                
                break;
                
            default: //Black
                capturedPawn = spots[newPos.y + 1][newPos.x].getPiece();
                if (newPos.y == 3 && isPawn(capturedPawn) && capturedPawn.isFirstMove())
                    return true;
                
                break;
        }
        return false;
    }
}