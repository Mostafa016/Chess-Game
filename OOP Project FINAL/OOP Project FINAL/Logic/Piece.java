package Logic;

public abstract class Piece {

    protected final Position position = new Position();
    protected Position[] validMoves;
    private final Color color;
    protected boolean firstMove = true;
    
    // Methods
    
    public Piece(int maxNumOfPossibleMoves, Color c) 
    {
        validMoves = new Position[maxNumOfPossibleMoves];
        color = c;
    }
    
    // abstract methods
    protected abstract boolean correctMove(Position new_position);

    public abstract boolean isValidMove(Position new_pos, Spot spots[][]);
    // -------------------------
    
    //Initalizing the validMoves array
    protected void initValidMovesArr()
    {
        for (int i = 0; i < validMoves.length; i++) {
            validMoves[i] = new Position();
        }
    }
    
    // Stores the valid moves Positions of a piece in its validMoves array
    public void storeValidMoves(Spot spots[][]) 
    {
        int validMovesCounter = 0;
        Position new_pos = new Position();
        initValidMovesArr();
        for (int y = 1; y < 9; y++) 
        {
            new_pos.y = y;
            for (int x = 1; x < 9; x++) 
            {
                new_pos.x = x;
                if (isValidMove(new_pos, spots)) 
                {
                    validMoves[validMovesCounter] = new Position(new_pos);
                    validMovesCounter++;
                }
            }
        }
    }
    
    //Returns the Subtract of the piece position from the new position
    public static Position Subtract(Position current,Position new_pos) 
    {
        return new Position(new_pos.x - current.x, new_pos.y - current.y);
    }
    
    //Determines the move direction
    public Direction moveDirection(Position new_pos) {
        Position test;
        test = Subtract(position ,new_pos);
        Direction direction;
        if (test.x > 0 && test.y > 0 && Math.abs(test.x) == Math.abs(test.y))
            direction = Direction.North_East;
            
        else if (test.x < 0 && test.y > 0 && Math.abs(test.x) == Math.abs(test.y))
            direction = Direction.North_West;
        
        else if (test.x > 0 && test.y < 0 && Math.abs(test.x) == Math.abs(test.y))
            direction = Direction.South_East;
            
        else if (test.x < 0 && test.y < 0 && Math.abs(test.x) == Math.abs(test.y))
            direction = Direction.South_West;
        
        else if (test.y > 0 && test.x == 0)
            direction = Direction.North;
        
        else if (test.y < 0 && test.x == 0)
            direction = Direction.South;
        
        else if (test.x > 0 && test.y == 0)
            direction = Direction.East;
        
        else if (test.x < 0 && test.y == 0)
            direction = Direction.West;
        
        else
            direction = Direction.None;
        return direction;
    }
    
    //Checkers
    
    //Checks if the path that a piece will cross it is clean or hava another piece
    protected boolean cleanPath(Position new_pos, Spot spots[][]) {
        Direction direction = moveDirection(new_pos);
        Position test = Subtract(position ,new_pos);

        switch (direction.ordinal()) {
            case 0: //if the piece move direction is North
                for (int i = 1; i < test.y; i++) {
                    if (spots[position.y + i][position.x].getHasPiece())
                        return false;
                }
                break;

            case 1: //if the piece move direction is South
                for (int i = 1; i < Math.abs(test.y); i++) {
                    if (spots[position.y - i][position.x].getHasPiece())
                        return false;
                }
                break;

            case 2: //if the piece move direction is East
                for (int i = 1; i < test.x; i++) {
                    if (spots[position.y][position.x + i].getHasPiece())
                        return false;
                }
                break;

            case 3: //if the piece move direction is West
                for (int i = 1; i < Math.abs(test.x); i++) {
                    if (spots[position.y][position.x - i].getHasPiece())
                        return false;
                }
                break;

            case 4: //if the piece move direction is NorthEast
                for (int i = 1; i < test.y; i++) {
                    if (spots[position.y + i][position.x + i].getHasPiece())
                        return false;
                }
                break;

            case 5: //if the piece move direction is NorthWest
                for (int i = 1; i < test.y; i++) {
                    if (spots[position.y + i][position.x - i].getHasPiece())
                        return false;
                }
                break;

            case 6: //if the piece move direction is SouthEast
                for (int i = 1; i < test.x; i++) {
                    if (spots[position.y - i][position.x + i].getHasPiece())
                        return false;
                }
                break;

            case 7: //if the piece move direction is SouthWest
                for (int i = 1; i < Math.abs(test.x); i++) {
                    if (spots[position.y - i][position.x - i].getHasPiece())
                        return false;
                }
                break;
        }
        return true;
    }
    
        // makes sure it is castling
    public boolean isCastling(Position kingNewPos, Spot spots[][])
    {
        Position rookPos;
        if (isKing(spots[position.y][position.x].getPiece()) && firstMove &&
                Math.abs(Subtract(position ,kingNewPos).x) == 2)
        {
            Direction kingMoveDirection = moveDirection(kingNewPos);
            switch(kingMoveDirection.ordinal())
            {
                case 2: //O-O castling
                    rookPos = new Position(8 ,kingNewPos.y);
                    
                    if (
                            isRook(spots[rookPos.y][rookPos.x].getPiece()) &&
                            spots[rookPos.y][rookPos.x].getPiece().isFirstMove() &&
                            cleanPath(rookPos, spots) && 
                            !checkInCastling(position, spots, 1) 
                        ) {return true ;}
                    
                    break;
                case 3: //O-O-O castling
                    rookPos = new Position(1,kingNewPos.y);
                    
                    if (
                            isRook(spots[rookPos.y][rookPos.x].getPiece()) &&
                            spots[rookPos.y][rookPos.x].getPiece().isFirstMove() &&
                            cleanPath(rookPos, spots) &&
                            !checkInCastling(position, spots, -1) 
                        ) {return true ;}
                    break;
            }
        }
        return false;
    }
    
    // it makes sure that the king is not in check when castling
    public boolean checkInCastling(Position kingPos ,
            Spot spots[][], int directionSign)
    {
        final int pathSpotsNumber = 3;
        for(int i = 0; i < pathSpotsNumber; i++)
        {
            Position castlingPos = new Position(kingPos.x + (i*directionSign),
                    kingPos.y);
            if (check(castlingPos, spots))
            {
                return true;
            }
        }
        return false;
    }
    
    private boolean check(Position kingPos, Spot spots[][])
    {
        for (int j = 1; j <= 8; j++)
            {
                for (int k = 1; k <= 8; k++)
                {
                    Position check = new Position(k,j);
                    if (!noPiece(spots[check.y][check.x]) &&
                            oppositeColor(spots[check.y][check.x]))
                    {
                        Piece piece = spots[check.y][check.x].getPiece();
                        if (isPawn(piece))
                        {
                            Position pawn1,pawn2;
                            switch(piece.getColor().ordinal())
                            {
                                case 0: //White pawn
                                    pawn1 = new Position(check.x + 1,check.y + 1);
                                    pawn2 = new Position(check.x - 1, check.y + 1);
                                    break;
                                    
                                default: //Black pawn
                                    pawn1 = new Position(check.x + 1,check.y - 1);
                                    pawn2 = new Position(check.x - 1, check.y - 1);
                                    break;
                            }
                            if (Position.equals(kingPos, pawn1) || 
                                            Position.equals(kingPos, pawn2))
                                    {return true ;}
                        }
                        else
                        {
                            Position validMovesChecker[] = piece.getValidMoves();

                            for(Position validMoveChecker : validMovesChecker)
                            {
                                if (Position.equals(validMoveChecker, kingPos))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        return false;
    }
    
    //Checks if a spot has a piece from its opposite color
    protected boolean oppositeColor(Spot spot) 
    {
      if (noPiece(spot))
        return true;
      else
        return !color.equals(spot.getPiece().getColor());
    }
    
    //Checks if a spot doesn't has a piece
    protected boolean noPiece(Spot spot) 
    {
        return !spot.getHasPiece();
    }
    
    //Checks if a piece is a Rook
    public static boolean isRook(Piece piece)
    {
        return (piece instanceof Rook);
    }
    
    // Checks if a piece is King
    public static boolean isKing(Piece piece)
    {
        return (piece instanceof King);
    }
    
    // Checks if a piece is Pawn
    public static boolean isPawn(Piece piece)
    {   
        return (piece instanceof Pawn);
    }
    
    public static boolean isKnight(Piece piece)
    {
        return (piece instanceof Knight);
    }
    //////////////////
    
    // Getters
    public Position getPosition() {
        return position;
    }
    
    public boolean isFirstMove()
    {
        return this.firstMove;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public Position[] getValidMoves() 
    {
        return validMoves;
    }
    ////////////
    
    //Setters
    public void setFirstMove(boolean b)
    {
        this.firstMove = b;
    }
    
    public void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
    }

    ////////////
}