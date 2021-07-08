package Logic;

public class King extends Piece {

    private static final int MAX_NUM_OF_POSSIBLE_MOVES = 8;

    public King(Color c) 
    {
        super(MAX_NUM_OF_POSSIBLE_MOVES, c);
    }
    
    protected boolean correctMove(Position new_pos) {

        Position test = this.Subtract(position ,new_pos);
        if (Math.abs(test.y) == 0 && Math.abs(test.x) == 1) {
            return true;
        } else if (Math.abs(test.y) == 1 && Math.abs(test.x) == 0) {
            return true;
        } else
            return Math.abs(test.y) == 1 && Math.abs(test.x) == 1;
    }

    public boolean isValidMove(Position new_pos, Spot spots[][]) {
        // King
        return ( (correctMove(new_pos) && oppositeColor(spots[new_pos.y][new_pos.x])) || 
                (isCastling(new_pos, spots)) );
    }
}
