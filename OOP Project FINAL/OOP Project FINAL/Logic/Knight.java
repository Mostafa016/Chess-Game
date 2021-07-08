package Logic;

public class Knight extends Piece {

    private static final int MAX_NUM_OF_POSSIBLE_MOVES = 8;

    public Knight(Color c) 
    {
        super(MAX_NUM_OF_POSSIBLE_MOVES, c);
    }

    protected boolean correctMove(Position new_position) {
        Position test = Subtract(position, new_position);
        return (Math.abs(test.x) == 1 && Math.abs(test.y) == 2) || (Math.abs(test.x) == 2 && Math.abs(test.y) == 1);
    }

    public boolean isValidMove(Position new_pos, Spot spots[][]) {
        // King

        return  correctMove(new_pos) && oppositeColor(spots[new_pos.y][new_pos.x]);
    }
}