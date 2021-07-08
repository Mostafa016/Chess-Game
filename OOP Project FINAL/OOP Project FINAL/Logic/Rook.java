package Logic;

public class Rook extends Piece {
    private static final int MAX_NUM_OF_POSSIBLE_MOVES = 14;
    public Rook(Color c) 
    {
        super(MAX_NUM_OF_POSSIBLE_MOVES, c);
    }

    protected boolean correctMove(Position new_pos) {
        Position test = Subtract(position, new_pos);
        return (test.x == 0 && test.y != 0) || (test.x != 0 && test.y == 0);
    }

    public boolean isValidMove(Position new_pos, Spot spots[][]) {
        // Rook
        return correctMove(new_pos) && oppositeColor(spots[new_pos.y][new_pos.x]) &&
                cleanPath(new_pos, spots);
    }
}