package Logic;

public class Board 
{
    private final int NUM_OF_ROWS = 8;
    private final int NUM_OF_COLUMNS = 8;
    private final Spot[][] spots;
    private  Position whiteKingPos = new Position(5,1);
    private  Position blackKingPos = new Position(5,8);
    private  boolean isWhiteKingInCheck = false;
    private  boolean isBlackKingInCheck = false;
    private  Position checkingPiecePos;
    private boolean isCheckmate = false;
    private boolean isStalemate = false;
    private Move lastMove;

    public Board() 
    {
        // indexing starts from spots[1][1]
        spots = new Spot[NUM_OF_ROWS + 1][NUM_OF_COLUMNS + 1];
        createNullSpots();
        setValuesOfSpots();
        updatePiecesValidMoves();
    }
    
    private void createNullSpots()
    {
        //Remove unused column
        for(int i = 0; i <= NUM_OF_ROWS; i++)
        {
            spots[i][0] = null;
        }
        //Remove unused row
        for(int i = 0; i <= NUM_OF_COLUMNS; i++)
        {
            spots[0][i] = null;
        }
    }
    
    private void setValuesOfSpots()
    {
        // Empty Spots (Spots with no pieces)
        for(int i = 3; i <= 6; i++)
        {
            for(int j = 1; j <= NUM_OF_COLUMNS; j++)
            {
                spots[i][j] = new Spot();
            }
        }
        // White Pieces
        spots[1][1] = new Spot(new Position(1,1), new Rook(Color.White));
        spots[1][2] = new Spot(new Position(2,1), new Knight(Color.White));
        spots[1][3] = new Spot(new Position(3,1), new Bishop(Color.White));
        spots[1][4] = new Spot(new Position(4,1), new Queen(Color.White));
        spots[1][5] = new Spot(new Position(5,1), new King(Color.White));
        spots[1][6] = new Spot(new Position(6,1), new Bishop(Color.White));
        spots[1][7] = new Spot(new Position(7,1), new Knight(Color.White));
        spots[1][8] = new Spot(new Position(8,1), new Rook(Color.White));
        
        for(int i = 1; i <= NUM_OF_COLUMNS; i++)
        {
            spots[2][i] = new Spot(new Position(i,2), new Pawn(Color.White));
        }
        ///////////////
        
        // Black Pieces
        spots[8][1] = new Spot(new Position(1,8), new Rook(Color.Black));
        spots[8][2] = new Spot(new Position(2,8), new Knight(Color.Black));
        spots[8][3] = new Spot(new Position(3,8), new Bishop(Color.Black));
        spots[8][4] = new Spot(new Position(4,8), new Queen(Color.Black));
        spots[8][5] = new Spot(new Position(5,8), new King(Color.Black));
        spots[8][6] = new Spot(new Position(6,8), new Bishop(Color.Black));
        spots[8][7] = new Spot(new Position(7,8), new Knight(Color.Black));
        spots[8][8] = new Spot(new Position(8,8), new Rook(Color.Black));
        for(int i = 1; i <= NUM_OF_COLUMNS; i++)
        {
            spots[7][i] = new Spot(new Position(i,7), new Pawn(Color.Black));
        }
        ///////////////
    }
    
  private void updatePiecesValidMoves() {
        for (int i = 1; i <= NUM_OF_ROWS; i++) {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++) {
                if (spots[i][j].getHasPiece()  && !Piece.isKing(spots[i][j].getPiece())) {
                    spots[i][j].getPiece().storeValidMoves(spots);
                }
            }
        }
        //This is done so the validation of not castling into check is right
        getSpot(whiteKingPos).getPiece().storeValidMoves(spots);
        getSpot(blackKingPos).getPiece().storeValidMoves(spots);
    }
    
    public boolean movePiece(Color currentPlayerColor, Position startingPosition,
        Position endingPosition) 
    {
        if(isValidPosition(startingPosition) && isValidPosition(endingPosition) &&
            getSpot(startingPosition).getHasPiece() &&
            currentPlayerColor.equals(getSpot(startingPosition).getPiece().getColor())) 
          {
            Piece movingPiece = getSpot(startingPosition).getPiece();
            Position[] validMoves = movingPiece.getValidMoves();
            if (searchPositionInArray(endingPosition, validMoves)) 
            {
                /* storing the last move in lastMove object to use it in the
                Game class to be stored in the arrayList */
                Piece capturedPiece = getSpot(endingPosition).getPiece();
                lastMove = new Move(startingPosition, endingPosition,
                        movingPiece, capturedPiece, movingPiece.isFirstMove());
                
                if (enemyHasEnPassant(startingPosition, endingPosition))
                {
                    lastMove.setEnemyHasEnPassant(true);
                    lastMove.setEnemyEnPassantPos(determineEnPassantPos(startingPosition));
                }
                ///////////////////////////////////////
                
                //Castling
                if (isCastling(movingPiece, endingPosition))
                {
                    Castling(startingPosition, endingPosition);
                }
                /////////
                
                //Enpassant
                else if (isEnPassant(startingPosition, endingPosition))
                {
                    enPassant(startingPosition, endingPosition);
                }
                ///////////
                
                updateMove(startingPosition, endingPosition);
                updateKingPosition(movingPiece);
                updatePiecesValidMoves();
                isBlackKingInCheck = false;
                isWhiteKingInCheck = false;
                
                //Check
                if (isEnemyInCheck(currentPlayerColor)) 
                {
                    applyCheckRestrictionsNextTurn(currentPlayerColor);
                }
                applyNonCheckRestrictionsNextTurn(currentPlayerColor);
                ////////
                
                getSpot(endingPosition).getPiece().setFirstMove(false);
                determineGameResult(currentPlayerColor);
                return true;
            }
        }
        return false;
    }
    
    public void Castling(Position kingPos, Position castlingPos)
    {
        Position rookPos, rookNewPos;
        Direction kingMoveDirection = getSpot(kingPos).getPiece().
                moveDirection(castlingPos);
        
        switch(kingMoveDirection.ordinal())
        {
            case 2: //King is moving East
                rookPos = new Position(8, kingPos.y);
                rookNewPos = new Position(6, kingPos.y);
                break;
                
            default: //King is moving West
                rookPos = new Position(1, kingPos.y);
                rookNewPos = new Position(4, kingPos.y);
                break;
        }
        updateMove(rookPos, rookNewPos); //Moving rook
        getSpot(rookNewPos).getPiece().setFirstMove(false); /* it isn't its first 
                                                                move anymore */
        
        // setting values of lastMove
        lastMove.setCastling(true);
        lastMove.setCastlingRookFirstPos(rookPos);
        lastMove.setCastlingRookNewPos(rookNewPos);
        //////////////////////////////
    }
    
    public void promotion(Piece piece, Position pawnPos)
    {
        getSpot(pawnPos).setPiece(piece, pawnPos);
        piece.setFirstMove(false);
        updatePiecesValidMoves();
        isBlackKingInCheck = false;
        isWhiteKingInCheck = false;
        
        if (isEnemyInCheck(piece.getColor()))
        {
            applyCheckRestrictionsNextTurn(piece.getColor());
        }
        applyNonCheckRestrictionsNextTurn(piece.getColor());
    }
    
    //removing the captured pawn of the En Passant Move
    private void enPassant(Position pawnPos, Position newPos)
    {
        Position capturedPawn;
        switch(getSpot(pawnPos).getPiece().getColor().ordinal())
        {
            case 0: //White
                capturedPawn = new Position (newPos.x , newPos.y - 1);
                getSpot(capturedPawn).setPiece(null, null);
                break;
                
            default: //Black
                capturedPawn = new Position (newPos.x , newPos.y + 1);
                getSpot(capturedPawn).setPiece(null, null);
                break;
        }
    }

    private void updateMove(Position firstPos, Position newPos)
    {
        Piece movingPiece = getSpot(firstPos).getPiece();
        getSpot(firstPos).setPiece(null, null);
        getSpot(newPos).setPiece(movingPiece, newPos);
    }
  
    public void Undo(Move m, Game game)
    {
        Move previousMove = game.getLastMove(2);
        //Updating the move spots (first Position and last Position)
        getSpot(m.getFirstPosition()).setPiece(m.getMovingPiece(), 
                m.getFirstPosition());

        getSpot(m.getLastPosition()).setPiece(m.getCapturedPiece(),
                m.getLastPosition());
        ///////////////////////////

        m.getMovingPiece().setFirstMove(m.isFirstMove());

        if (m.isCastling()) //if this move was castling
        {
            //get the rook back to its first Position
            updateMove(m.getCastlingRookNewPos(), m.getCastlingRookFirstPos());

            //it is sure that the rook before the castling had never moved
            getSpot(m.getCastlingRookFirstPos()).getPiece().setFirstMove(true);
        }
        //if the player after prevoius move can play enpassant
        else if (previousMove != null && previousMove.isEnemyHasEnPassant())
        {
            //assuming that the last move was enpassant
            getSpot(previousMove.getLastPosition()).setPiece
                (previousMove.getMovingPiece(), previousMove.getLastPosition());
            previousMove.getMovingPiece().setFirstMove(true);
        }
        ///////

        updateKingPosition(m.getMovingPiece());
        updatePiecesValidMoves();
        isBlackKingInCheck = false;
        isWhiteKingInCheck = false;
        Color previousPlayerColor;
        switch(m.getMovingPiece().getColor().ordinal()) //Current Player
        {
            case 0: // White
                previousPlayerColor = Color.Black; break;
                
            default: // Black
                previousPlayerColor = Color.White;
        }
        if (isEnemyInCheck(previousPlayerColor))
        {
            applyCheckRestrictionsNextTurn(previousPlayerColor);
        }
        applyNonCheckRestrictionsNextTurn(previousPlayerColor);
        
        //set the pawn first move false again after updating valid moves
        if (previousMove != null && previousMove.isEnemyHasEnPassant())
        {
            previousMove.getMovingPiece().setFirstMove(false);
        }
        isCheckmate = false;
        isStalemate = false;
    }

    private boolean searchPositionInArray(Position positionSearchedFor,
            Position[] positionsSearchedIn) 
    {
       for (Position position : positionsSearchedIn) 
       {
           if (Position.equals(positionSearchedFor, position)) 
           {
               return true;
           }
       }
       return false;
   }

    private void updateKingPosition(Piece movingPiece)
    {
        if(Piece.isKing(movingPiece))
        {
            switch(movingPiece.getColor().ordinal())
            {
                case 0:
                    whiteKingPos = movingPiece.getPosition(); break;
                    
                default:
                    blackKingPos = movingPiece.getPosition(); break;
            }
        }
    }
    
    private boolean isEnemyInCheck(Color currentPlayerColor) 
    {
        Position enemyKingPos;
        switch(currentPlayerColor.ordinal())
        {
            case 0: //White
                enemyKingPos = blackKingPos; break;
                
            default: //Black
                enemyKingPos = whiteKingPos; break;
        }
        
        for (int i = 1; i <= NUM_OF_ROWS; i++) 
        {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++) 
            {
                if (spots[i][j].getHasPiece() &&
                        spots[i][j].getPiece().getColor().equals(currentPlayerColor)) 
                {
                    Piece piece = spots[i][j].getPiece();
                    Position[] validMoves = piece.getValidMoves();
                    if (Piece.isPawn(piece)) 
                    {
                        for (Position validMove : validMoves) 
                        {
                            switch(piece.moveDirection(validMove).ordinal())
                            {
                                case 4:
                                case 5:
                                case 6:
                                case 7:
                                    if (Position.equals(validMove, enemyKingPos)) 
                                    {
                                        checkingPiecePos = piece.getPosition();
                                        return true; 
                                    } break;
                            }
                        }
                    }
                    else if (searchPositionInArray(enemyKingPos, validMoves)) 
                    {
                        checkingPiecePos = piece.getPosition();
                        return true;
                    }
                }
            }
        }
        return false;
    }

   //Returns the positions between two pieces
    private Position[] getPositionsBetweenTwoPieces(Position firstPiecePosition, Position secondPiecePosition) {
        Piece firstPiece = spots[firstPiecePosition.y][firstPiecePosition.x].getPiece();
        Position difference = new Position(Math.abs(firstPiecePosition.x - secondPiecePosition.x)
                , Math.abs(firstPiecePosition.y - secondPiecePosition.y));
        Direction firstToSecondPieceDirection = firstPiece.moveDirection(secondPiecePosition);
        Position[] positionsInBetween = null;
        switch(firstToSecondPieceDirection.ordinal())
        {
            case 0: //North
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x,
                            firstPiecePosition.y + i);
                }
                break;

            case 1: //South
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x,
                            firstPiecePosition.y - i);
                }
                break;

            case 2: //East
                positionsInBetween = new Position[difference.x - 1];
                for (int i = 1; i < difference.x; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x + i,
                            firstPiecePosition.y);
                }
                break;

            case 3: //West
                positionsInBetween = new Position[difference.x - 1];
                for (int i = 1; i < difference.x; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x - i ,
                            firstPiecePosition.y);
                }
                break;

            case 4: //North-East
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x + i,
                            firstPiecePosition.y + i);
                }
                break;

            case 5: //North-West
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x - i,
                            firstPiecePosition.y + i);
                }
                break;

            case 6: //South-East
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x + i,
                            firstPiecePosition.y - i);
                }
                break;

            case 7: //South-West
                positionsInBetween = new Position[difference.y - 1];
                for (int i = 1; i < difference.y; i++)
                {
                    positionsInBetween[i - 1] = new Position(firstPiecePosition.x - i,
                            firstPiecePosition.y - i);
                }
                break;
        }
        return positionsInBetween;
    }

    //Returns the positions the checking piece is attacking that are between the checked king and the checking piece
    private Position[] getPositionsToBlockCheck() {
        Piece checkingPiece = spots[checkingPiecePos.y][checkingPiecePos.x].getPiece();
        if (checkingPiece instanceof Pawn || checkingPiece instanceof Knight)
            return null;
        Position checkedKingPos = null;
        if (isWhiteKingInCheck) {
            checkedKingPos = whiteKingPos;
        } else if (isBlackKingInCheck) {
            checkedKingPos = blackKingPos;
        }
        return getPositionsBetweenTwoPieces(checkingPiecePos, checkedKingPos);
    }
    
    private boolean canCaptureCheckingPiece(Position pieceValidMove) 
    {
        return Position.equals(checkingPiecePos, pieceValidMove);
    }

    private void restrictNonKingPiecesMoves(Color checkedPlayerColor)
    {
        Position[] positionsToBlockCheck = getPositionsToBlockCheck();
        for (int i = 1; i <= NUM_OF_ROWS; i++) 
        {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++)
            {
                Piece piece = spots[i][j].getPiece();
                
                if (piece != null && !Piece.isKing(piece) &&
                        piece.getColor().equals(checkedPlayerColor))
                {
                    Position[] currentValidMoves = spots[i][j].getPiece().getValidMoves();
                    if (positionsToBlockCheck != null)
                    {
                        for (Position validMove : currentValidMoves)
                        {
                            if (!searchPositionInArray(validMove, positionsToBlockCheck) && !canCaptureCheckingPiece(validMove))
                            {
                                validMove.x = 0;
                                validMove.y = 0;
                            }
                        }
                    }
                    else
                        {
                        for (Position validMove : currentValidMoves)
                        {
                            if (!canCaptureCheckingPiece(validMove))
                            {
                                validMove.x = 0;
                                validMove.y = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    // Get the positions the pawn would be attacking if they had an enemy in them
    private Position[] getPawnAttackPositions(Piece pawn) {
        Position[] pawnAttackPositions = new Position[2];
        if (pawn.getColor() == Color.White) {
            pawnAttackPositions[0] = new Position(pawn.getPosition().x + 1, pawn.getPosition().y + 1);
            pawnAttackPositions[1] = new Position(pawn.getPosition().x - 1, pawn.getPosition().y + 1);
        } else {
            pawnAttackPositions[0] = new Position(pawn.getPosition().x + 1, pawn.getPosition().y - 1);
            pawnAttackPositions[1] = new Position(pawn.getPosition().x - 1, pawn.getPosition().y - 1);
        }
        return pawnAttackPositions;
    }

    /* Removes all positions from the enemy king's validMoves array that
    would put him in check. */
    private void restrictKingMovesIntoCheck(Color currentPlayerColor) 
    {
        Piece enemyKing, piece;
        Position[] enemyKingValidMoves;
        switch(currentPlayerColor.ordinal())
        {
            case 0:
                enemyKing = spots[blackKingPos.y][blackKingPos.x].getPiece(); break;
                
            default:
                enemyKing = spots[whiteKingPos.y][whiteKingPos.x].getPiece(); break;
        }
        enemyKingValidMoves = enemyKing.getValidMoves();
        for (int i = 1; i <= NUM_OF_ROWS; i++) 
        {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++) 
            {
                piece = spots[i][j].getPiece();
                if (spots[i][j].getHasPiece() && 
                        piece.getColor().equals(currentPlayerColor)) 
                {
                    if (Piece.isPawn(piece)) 
                    {
                        Position[] pawnAttackPositions = getPawnAttackPositions(piece);

                        for (Position validMove : enemyKingValidMoves) 
                        {
                            if (searchPositionInArray(validMove,
                                    pawnAttackPositions)) 
                            {
                                validMove.x = 0;
                                validMove.y = 0;
                            }
                        }
                    } 
                    else 
                    {
                        for (Position validMove : enemyKingValidMoves) 
                        {
                            if(searchPositionInArray(validMove,
                                    piece.getValidMoves()))
                            {
                                validMove.x = 0;
                                validMove.y = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    /* Removes a position from the checked king validMoves array that would
    make him still in check after the move */
    private void restrictKingStayChecked() 
    {
        Piece checkingPiece = getSpot(checkingPiecePos).getPiece();
        // Exit the function if the piece checking is a pawn or a knight
        if (Piece.isPawn(checkingPiece) || Piece.isKnight(checkingPiece))
            return;
        Piece checkedKing = null;
        if (isWhiteKingInCheck) 
        {
            checkedKing = getSpot(whiteKingPos).getPiece();
        }
        else if (isBlackKingInCheck) 
        {
            checkedKing = getSpot(blackKingPos).getPiece();
        }
        Position checkedKingPos = checkedKing.getPosition();
        Direction checkingDirection = checkingPiece.moveDirection(checkedKingPos);
        for (Position validMove : checkedKing.getValidMoves()) 
        {
            if (checkingDirection.equals(checkedKing.moveDirection(validMove))) 
            {
                validMove.x = 0;
                validMove.y = 0;
            }
        }
    }

    private boolean areSpotsEmpty(Position[] spotPositions) {
        for (Position position : spotPositions)
        {
            if (spots[position.y][position.x].getHasPiece())
            {
                return false;
            }
        }
        return true;
    }

  // Removes all positions from the validMoves array of the ally pieces that would put the king in check
    private void pinPiecesToKing(Color currentPlayerColor) {
        Position enemyKingPos;
        if (currentPlayerColor == Color.White) {
            enemyKingPos = blackKingPos;
        } else {
            enemyKingPos = whiteKingPos;
        }
        for (int i = 1; i <= NUM_OF_ROWS; i++) {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++) {
                if (spots[i][j].getHasPiece()) {
                    // Searching for a piece that can pin a piece to the enemy king
                    if (!(spots[i][j].getPiece() instanceof Pawn || spots[i][j].getPiece() instanceof Knight)
                            && spots[i][j].getPiece().getColor().equals(currentPlayerColor)) {
                        Piece attackingPiece = spots[i][j].getPiece();
                        for (Position validMove : attackingPiece.getValidMoves()) {
                            if (validMove.x != 0 && spots[validMove.y][validMove.x].getHasPiece()) {
                              //Direction from the attackingPiece to the piece we want to check is pinned or not
                                Direction attackingDirection = attackingPiece.moveDirection(validMove);
                                Piece attackedPiece = spots[validMove.y][validMove.x].getPiece();
                                Direction attackedPieceToKingDirection = attackedPiece.moveDirection(enemyKingPos);
                                if (attackingDirection == attackedPieceToKingDirection) {
                                    Position[] positionsBetweenAttackedPieceAndKing = getPositionsBetweenTwoPieces(attackedPiece.getPosition(), enemyKingPos);
                                    if (areSpotsEmpty(positionsBetweenAttackedPieceAndKing)) {
                                        Position[] positionsBetweenEnemyAndAttackedPiece = getPositionsBetweenTwoPieces(attackingPiece.getPosition()
                                                , attackedPiece.getPosition());
                                        for (Position move : attackedPiece.getValidMoves()) {
                                          // Accounts for the position of the pinning piece (the pinning piece can be captured)
                                            if (!searchPositionInArray(move, positionsBetweenEnemyAndAttackedPiece)
                                                    && !Position.equals(attackingPiece.getPosition(), move)) {
                                                move.x = 0;
                                                move.y = 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

   private boolean isPieceProtected(Piece protectingPiece, Position protectedPiecePos) {
        final String pieceClassName = protectingPiece.getClass().getSimpleName();
        Position difference = new Position(Math.abs(protectingPiece.getPosition().x - protectedPiecePos.x)
                , Math.abs(protectingPiece.getPosition().y - protectedPiecePos.y));
        switch (pieceClassName) {
            case "King":
                // The king must be one spot away from the protected piece position
                if (difference.x <= 1 && difference.y <= 1) {
                    return true;
                }
                break;
            case "Pawn":
                Position[] pawnAttackPositions = getPawnAttackPositions(protectingPiece);
                for (Position position : pawnAttackPositions) {
                    if (position.x == protectedPiecePos.x && position.y == protectedPiecePos.y) {
                        return true;
                    }
                }
                break;
            case "Knight":
                // The protected piece is in a position the knight can move to
                if ((difference.x == 1 && difference.y == 2) || (difference.x == 2 && difference.y == 1)) {
                    return true;
                }
                break;
            //case "Rook":
            //case "Bishop":
            //case "Queen":
            default:
                Position[] positions = getPositionsBetweenTwoPieces(protectingPiece.getPosition(), protectedPiecePos);
                if (positions != null) {
                    if (positions.length != 0) {
                        Position spotBeforeProtectedPiecePos = positions[positions.length - 1];
                        Position[] protectingPieceValidMoves = protectingPiece.getValidMoves();
                        for (Position position : protectingPieceValidMoves) {
                      /*
                         NOTE: We check if the spot doesn't have a piece because if it does that means
                         it is in the validMoves array of the protectingPiece which would not allow the king to capture
                         the protectedPiece although it can be.
                      */
                            if (!spots[spotBeforeProtectedPiecePos.y][spotBeforeProtectedPiecePos.x].getHasPiece()
                                    && position.x == spotBeforeProtectedPiecePos.x && position.y == spotBeforeProtectedPiecePos.y) {
                                return true;
                            }
                        }
                    }
                    // positions.length == 0 which means the protecting piece is in one of the eight spots around the protected piece
                    else {
                        Direction protectionDirection = protectingPiece.moveDirection(protectedPiecePos);
                        switch (pieceClassName) {
                            case "Rook":
                                return protectionDirection == Direction.East || protectionDirection == Direction.West
                                        || protectionDirection == Direction.North || protectionDirection == Direction.South;
                            case "Bishop":
                                return protectionDirection == Direction.North_East || protectionDirection == Direction.North_West
                                        || protectionDirection == Direction.South_East || protectionDirection == Direction.South_West;
                            // case "Queen":
                            default:
                                return protectionDirection != Direction.None;
                        }
                    }
                }
        }
        return false;
    }


    private void restrictKingCapturingProtectedPiece(Color currentPlayerColor) {
        Piece enemyKing;
        if (currentPlayerColor.equals(Color.White)) {
            enemyKing = spots[blackKingPos.y][blackKingPos.x].getPiece();

        } else {
            enemyKing = spots[whiteKingPos.y][whiteKingPos.x].getPiece();
        }
        Position[] enemyKingValidMovesArray = enemyKing.getValidMoves();

        for (Position validMove : enemyKingValidMovesArray)
        {
            if (validMove.x != 0 && spots[validMove.y][validMove.x].getHasPiece())
            {
                for (int i = 1; i <= NUM_OF_ROWS; i++)
                {
                    for (int j = 1; j <= NUM_OF_COLUMNS; j++)
                    {
                        // The first condition is to avoid checking if the piece can protect itself which is illogical
                        if (!(validMove.x == j && validMove.y == i) && spots[i][j].getHasPiece())
                        {
                            if (spots[i][j].getPiece().getColor().equals(currentPlayerColor))
                            {
                              //this validMove is consider or equal for Position of Piece attacked from king
                                if (isPieceProtected(spots[i][j].getPiece(), validMove))
                                {
                                    validMove.x = 0;
                                    validMove.y = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyCheckRestrictionsNextTurn(Color currentPlayerColor) {
        // The functions beginning with the word "restrict" restrict the moves NEXT turn (after the current move has been made)
        lastMove.setEnemyInCheck(true);
        switch (currentPlayerColor.ordinal())
        {
            case 0:
                isBlackKingInCheck = true;
                lastMove.setCheckedEnemyKing(blackKingPos);
                restrictNonKingPiecesMoves(Color.Black);
                break;

            default:
                isWhiteKingInCheck = true;
                lastMove.setCheckedEnemyKing(whiteKingPos);
                restrictNonKingPiecesMoves(Color.White);
        }
        restrictKingStayChecked();
    }

    private void applyNonCheckRestrictionsNextTurn(Color currentPlayerColor) {
        // The functions beginning with the word "restrict" restrict the moves NEXT turn (after the current move has been made)
        restrictKingMovesIntoCheck(currentPlayerColor);
        pinPiecesToKing(currentPlayerColor);
        restrictKingCapturingProtectedPiece(currentPlayerColor);
    }

    private boolean doesEnemyHaveLegalMoves(Color currentPlayerColor) {
        Color enemyColor;
        if (currentPlayerColor == Color.White) {
            enemyColor = Color.Black;
        } else {
            enemyColor = Color.White;
        }
        for (int i = 1; i <= NUM_OF_ROWS; i++) {
            for (int j = 1; j <= NUM_OF_COLUMNS; j++) {
                if(spots[i][j].getHasPiece()){
                    if(spots[i][j].getPiece().getColor().equals(enemyColor)){
                        Piece enemyPiece = spots[i][j].getPiece();
                        for (Position validMove : enemyPiece.getValidMoves()) {
                            if(validMove.x != 0){
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void determineGameResult(Color currentPlayerColor){
        boolean doesEnemyHaveLegalMoves = doesEnemyHaveLegalMoves(currentPlayerColor);
        if(isEnemyInCheck(currentPlayerColor) && doesEnemyHaveLegalMoves){
            isCheckmate = true;
        }
        else if(doesEnemyHaveLegalMoves){
            isStalemate = true;
        }
    }

    private Position determineEnPassantPos(Position pawnPos)
    {
        Position enPassantPos;
        switch(getSpot(pawnPos).getPiece().getColor().ordinal())
        {
            case 0: //White
                enPassantPos = new Position (pawnPos.x, pawnPos.y + 1);
                break;
                
            default: //Black
                enPassantPos = new Position (pawnPos.x, pawnPos.y - 1);
                break;
        }
        return enPassantPos;
    }
    
    // Checkers
    private boolean isValidPosition(Position position) 
    {
      return position.x >= 1 && position.x <= NUM_OF_COLUMNS
              && position.y >= 1 && position.y <= NUM_OF_ROWS;
    }
    
    public boolean isCastling(Piece kingChecker, Position castlingPos)
    {
        return ( Piece.isKing(kingChecker) ) &&
                (Math.abs(kingChecker.Subtract(kingChecker.getPosition(),
                        castlingPos).x) == 2);
    }
    
    //if a pawn moved to an En Passant position
    private boolean isEnPassant(Position pawnPos, Position newPos)
    {
        Position difference = Piece.Subtract(newPos, pawnPos);
        difference.x = Math.abs(difference.x); difference.y = Math.abs(difference.y);
        return (Piece.isPawn(getSpot(pawnPos).getPiece()) && 
                !getSpot(newPos).getHasPiece()&& difference.x == 1 && 
                difference.y == 1);
    }
    
    // return if the enemy has an En Passant move to be stored in the moves(ArrayList)
    private boolean enemyHasEnPassant(Position pawnPos, Position newPos)
    {
        Position difference = Piece.Subtract(pawnPos, newPos);
        // if the moved piece was pawn and if it moved 2 spots vertically
        if (Piece.isPawn(getSpot(pawnPos).getPiece()) && Math.abs(difference.y) == 2)
        {
            Position rightSpot = new Position (newPos.x + 1, newPos.y);
            Position leftSpot = new Position (newPos.x - 1, newPos.y);
            //if the new spot has a pawn next to it (in the right spot or left)
            return (isValidPosition(rightSpot) &&
                    Piece.isPawn(getSpot(rightSpot).getPiece())) ||
                    (isValidPosition(leftSpot) &&
                    Piece.isPawn(getSpot(leftSpot).getPiece()));
        }
        return false;
    }
    ///////////
    
    //Getters
    public Move getLastMove()
    {
        return lastMove;
    }
    
    public Spot getSpot(Position position) 
    {
        return spots[position.y][position.x];
    }
    
    public boolean getIsCheckmate(){ return isCheckmate; }

    public boolean getIsStalemate(){ return isStalemate; }
    
    //////////
}