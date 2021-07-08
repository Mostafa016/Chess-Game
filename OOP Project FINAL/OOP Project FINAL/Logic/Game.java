package Logic;

import GUI.GameFrame;
import java.util.ArrayList;

public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Board board;
    private ArrayList<Move> moves;
    private Result result;

    public static void main(String[] args) {
          Game g = new Game();
          GameFrame f = new GameFrame(g);
    }

    private enum Result{
        BLACK_WIN,
        WHITE_WIN,
        DRAW
    }

    public Game(){
      player1 = new Player(Color.White);
      player2 = new Player(Color.Black);
      currentPlayer = player1;
      board = new Board();
      moves = new ArrayList<Move>();
      result = null;
    }

   public boolean move(Position startingPosition, Position endingPosition) {
        if(board.movePiece(currentPlayer.getColor(),startingPosition, endingPosition))
        {
           //Here I can't call this function without it being static which is illogical
          if(result == null){
                determineGameResult(currentPlayer.getColor());
            }
          if(currentPlayer == player1)
          {
            currentPlayer = player2;
          }
          else
          {
            currentPlayer = player1;
          }
          moves.add(board.getLastMove());
          return true;
        }
        return false;
    }
   public Color getCrntPlayerClr()
   {
       return currentPlayer.getColor();
   }
   public void Undo()
   {
       if (moves.size() > 0)
       {
            board.Undo(moves.get(moves.size()-1), this);
            if(currentPlayer == player1)
            {
              currentPlayer = player2;
            }
            else
            {
              currentPlayer = player1;
            }
            moves.remove(moves.size()-1);
            result = null;
       }
   }
   public Move getLastMove(int move)
   {
       if (moves.size() - move >= 0 && moves.size() - move < moves.size())
       {
            return moves.get(moves.size() - move);
       }
       else
       {
            return null;
       }
   }
   private void determineGameResult(Color currentPlayerColor) {
        if (board.getIsCheckmate()) {
            if (currentPlayerColor == Color.White) 
            {
                result = Result.WHITE_WIN;
            } 
            else 
            {
                result = Result.BLACK_WIN;
            }
        } 
        else if (board.getIsStalemate()) 
        {
            result = Result.DRAW;
        }
        //For testing only
        if (result != null) 
        {
            System.out.println(result.name());
        }
    }

   //Getters
    public Board getBoard() {
        return board;
    }
    ////////
    
}