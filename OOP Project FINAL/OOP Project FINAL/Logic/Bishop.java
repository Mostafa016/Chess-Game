package Logic;

public class Bishop extends Piece {

    private static final int MAX_NUM_OF_POSSIBLE_MOVES = 13;

    public Bishop(Color c) 
    {
        super(MAX_NUM_OF_POSSIBLE_MOVES, c);
    }

    protected boolean correctMove(Position new_pos) 
    {
        Position test = Subtract(position ,new_pos);
        return (Math.abs(test.x) == Math.abs(test.y) && test.x != 0);
    }

    @Override
    public boolean isValidMove(Position new_pos, Spot spots[][]) 
    {
        return correctMove(new_pos) && oppositeColor(spots[new_pos.y][new_pos.x]) &&
                cleanPath(new_pos, spots);
    }
}