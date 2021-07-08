package Logic;

public class Position {
    public int x;
    public int y;

    public Position(){ }

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Position(Position position){
        x = position.x;
        y = position.y;
    }
    public static boolean equals(Position first, Position second)
    {
        return (first.x == second.x && first.y == second.y);
    }
}
