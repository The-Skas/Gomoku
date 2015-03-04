
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author skas
 */


public class MyPlayer_130796000 extends GomokuPlayer {

   
    

    /***
     * My Heuristic Method
     * 
     * Return a number where the higher would signify a better
     * play. 
     */
    
    public enum SCORE {
        INVALID_MOVE(0),
        
        //Such that a winning move will always be picked over
        //preventing a winning move
        IMMEDIATE_WINNING_MOVE(101),
        PREVENT_WINNING_MOVE(100),
        NEXT_TURN_WINNING_MOVE(99),
        //Line_team_sequence * sequence length;
        LINE_TEAM_SEQUENCE(2),
        ADJACENT_ENEMY_CELL(1);
        
        
        private final int value;

        private SCORE(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
    public enum Player{
        NONE,
        BLACK, 
        WHITE
    }
  
    private int curr_row = -1;
    private int curr_col = -1;
    
    private final int ROW_SIZE = 8;
    private final int COL_SIZE = 8;
    
    private int [][] currBoardState;
    private int     teamColor;
    private int enemyColor;
    private ArrayList<MiniMaxNode> maximumNodes;
    private ArrayList<MiniMaxNode> minimumNodes;
    
   
    
    public boolean isBoardEmpty(Color [][] board)
    {
        for(int i = 0; i < ROW_SIZE; i++)
        {
            for(int j = 0; j < COL_SIZE; j++)
            {
                if(board[i][j] != null)
                {
                    return false;
                }
            }
        }
        
        return true;
    }
            
            
    @Override
    public Move chooseMove(Color[][] board, Color me) {
        this.currBoardState = new int[ROW_SIZE][COL_SIZE];
        
        
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                if(board[i][j] == Color.BLACK)
                {
                    currBoardState[i][j] = Player.BLACK.ordinal();
                }
                else if(board[i][j] == Color.WHITE)
                {
                    currBoardState[i][j] = Player.WHITE.ordinal();
                }
                else if(board[i][j] == null)
                {
                    currBoardState[i][j] = Player.NONE.ordinal();
                }
                
            }
        }
        maximumNodes = new ArrayList<>();
        minimumNodes = new ArrayList<>();

        if(me == Color.BLACK)
        {
            this.enemyColor = Player.WHITE.ordinal();
            this.teamColor  = Player.BLACK.ordinal();
        }
        else
        {
            this.enemyColor = Player.BLACK.ordinal();
            this.teamColor  = Player.WHITE.ordinal();
        }
        
        System.out.println("Before Minimax");
        
        if(me == Color.WHITE && isBoardEmpty(board))
        {
            return new Move(4,4);
        }
        
         
        try
        {
            MiniMaxNode bestMove =  minimax(3, currBoardState, -1,-1, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

            System.out.println("After Minimax");
            bestMove.printBranch();
            return new Move(bestMove.row, bestMove.col);
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        return new Move(-1, -1);
       
    }
    
    
    public double gokmokuHeuristicMethod(int [][] boardState, int move_row,int move_col, int playerColor)
    {
        //TODO: Create a heurtisc for ordering what nodes should be searched
        //first. The idea is to have it find clusterned nodes over single ones.
        
        
        //Start with the best values to do.
        //So first we need to evaluate, the
        //state of the game.
        
        //Does it win the game?
        
        //THIS IS BAD, CHANGE IT AS IT PRIORITISES DEFENCE!
        //Make it such that Scores determines what
        
        
        double block_score = blockEnemyScore(boardState, move_row, move_col, playerColor);
       
      
        
        
        
        double adj_score =  getAdjacentScore(boardState, move_row, move_col, playerColor);
        
        if(block_score >= adj_score)
        {
            return block_score;
        }
        else
        {
            return adj_score;
        }
    }
    
    //Node 
    class MiniMaxNode
    {
        public int row = -1;
        public int col = -1;
        public double heuristicValue = 0;
        
        //Debugging
        public int [][] boardState;
        public int _myColor;
        public MiniMaxNode child;

        public MiniMaxNode(double heurs, int row,int col)
        {
            this.row = row;
            this.col = col;
            this.heuristicValue = heurs;
        }
        
        public MiniMaxNode setState(int[][] boardState)
        {
            this.boardState = boardState;

            
            return this;
        }
        
        public MiniMaxNode setColor(int tempColor)
        {
            this._myColor = tempColor;
            return this;
        }
        
        public MiniMaxNode clone()
        {
            int [][]copyState = null;
            if(boardState != null)
            {
                copyState = this.boardState.clone();
            }
            int copyMyColor = this._myColor;
            
            MiniMaxNode myMiniMax = new MiniMaxNode(heuristicValue, row, col)
                    .setColor(copyMyColor).setState(copyState);
            
            myMiniMax.child = this.child;
            
            return myMiniMax;
            
            
        }
        
        public void printBranch()
        {
            MiniMaxNode _this = this;
            while(_this != null)
            {
                
                System.out.println(_this);
                this.boardState[_this.row][_this.col] = _this._myColor;
                this.debugBoard(this.boardState);
                
                
                System.out.println("--------");
                
                //go to next
                _this = _this.child;
                
            }
        }
        @Override
        public String toString()
        {
            String color;
            if(this._myColor == Player.WHITE.ordinal())
            {
                color = "White";
            }
            else
            {
                color = "Black";
            }
            
            return  (color +"\n"
                    +this.row+ " - "+this.col+ "\n"
                    +this.heuristicValue+"\n");
        }
        
        public void debugBoard(int[][] board)
        {
            for(int i = 0; i < ROW_SIZE; i++)
            {
                for(int j = 0; j < COL_SIZE; j++)
                {
                    if(board[i][j] == Player.BLACK.ordinal())
                    {
                        System.out.print(" B ");
                    }
                    else if(board[i][j] == Player.WHITE.ordinal())
                    {
                        System.out.print(" W ");
                    }
                    else
                    {
                        System.out.print(" * ");
                    }

                }
                System.out.println();
            }
        }
    }
    
    class BranchTraceNode
    {
        public Color [][] boardState;
        public Color myColor;
        
        
        public int heuristicScore;
        public int row = -1;
        public int col = -1;
        BranchTraceNode child;
        
    }
    
    public MiniMaxNode minimax(int depth, int[][]boardState, int row, int col, boolean maximizingPlayer, double alpha, double beta )
    {
        if(depth == 0)
        {
            double heursVal = 0;
            int tempColor;
            //Confusing boolean, but we need to flip it
            //(This is for the MAXIMIZING player 
            if(!maximizingPlayer)
            {
                //gets normal maximum
                heursVal= gokmokuHeuristicMethod(boardState,row,col, this.teamColor);
                tempColor = this.teamColor;
                //What if i accidently flip row with col?
            }
            else
            {
               //returns negative, since were at the enemy
               heursVal= -gokmokuHeuristicMethod(boardState, row, col, this.enemyColor);
               tempColor = enemyColor;
            }
            return new MiniMaxNode(heursVal, row, col).setState(boardState).setColor(tempColor);
        }
        else
        {
            if(maximizingPlayer)
            {
                //Search through the list again.. although create a
                //search heuristic.. meaning order the branch to visit first.
                
                //visitNextNode (sorted in importance)
                //Todo: Setting a visitNode method would be easier, as to
                //change orders of branches.
                
                MiniMaxNode maximumNode = new MiniMaxNode(Double.NEGATIVE_INFINITY,-1,-1);

                //Todo (Possibly): Higher level abstraction is a beauty,
                //it would be interesting to merge the minimum and maximum 
                //loops into a simple flipflop method.
                for(int i = 0; i < ROW_SIZE; i++)
                {
                    for(int j = 0; j < COL_SIZE; j++)
                    {
                        if(boardState[i][j] != 0)
                            continue;
                        
                        
                        boardState[i][j] = this.teamColor;
                        
                        //The maximizingPlayers turn has finished, so now
                        //it is the minimizingPlayer's turn
                        MiniMaxNode temp_node = minimax(depth - 1, Arrays.copyOf(boardState, boardState.length),
                            i, j, !maximizingPlayer, alpha, beta);
                        
                        //Such that we evaluate our current space as well to
                        //calculate the score.
                        if(depth - 1 != 0)
                        {
                            temp_node.heuristicValue += gokmokuHeuristicMethod(boardState,i,j, this.teamColor); 
                        }
                        //Revert PreviousColor
                        boardState[i][j] = 0;
                        
                        if(temp_node.heuristicValue > maximumNode.heuristicValue)
                        {
                            maximumNode = temp_node;
                            //alpha = temp_node.heuristicValue;
                            maximumNode.child = maximumNode.clone();
                            //maximumNode.child.heuristicValue -= gokmokuHeuristicMethod(boardState,i,j, this.teamColor);
                            maximumNode.row = i;
                            maximumNode.col = j;
                            maximumNode._myColor = this.teamColor;
                        }
                        
                        if(alpha > beta)
                            return maximumNode;
                       
                    }
                }
                //For every decision made here...
                //Understand what is the state recursively.
                
                
                return maximumNode;
            }
            else //minimizingPlayer
            {
                //Similar thoughts are grouped together.. 
                //even though the else statemenet speerates minimumNode..
                MiniMaxNode minimumNode = new MiniMaxNode(Double.POSITIVE_INFINITY,-1,-1);
                
                for(int i = 0; i < ROW_SIZE; i++)
                {
                    for( int j = 0; j < COL_SIZE; j++)
                    {
                        if(boardState[i][j] != Player.NONE.ordinal())
                            continue;
                        
                        boardState[i][j] = this.enemyColor;
                        //The maximizingPlayers turn has finished, so now
                        //it is the minimizingPlayer's turn
                        MiniMaxNode temp_node = minimax(depth - 1, boardState.clone(),
                            i, j, !maximizingPlayer, alpha,beta);
                        
                       //Such that we evaluate our current space as well to
                       //calculate the score.
                        if(depth - 1 != 0)
                        {
                            temp_node.heuristicValue -= gokmokuHeuristicMethod(boardState,i,j, this.enemyColor); 
                        }
                        //Revert PreviousColor
                        boardState[i][j] = Player.NONE.ordinal();
                        
                        if(temp_node.heuristicValue < minimumNode.heuristicValue)
                        {
                            //found a smaller node
                            minimumNode = temp_node;
                            //beta = temp_node.heuristicValue;
                            minimumNode.child = minimumNode.clone();
                            //minimumNode.child.heuristicValue += gokmokuHeuristicMethod(boardState,i,j, this.enemyColor);
                            minimumNode.row = i;
                            minimumNode.col = j;
                            minimumNode._myColor = this.enemyColor;
                        }
                        
                        if(alpha > beta)
                            return minimumNode;
                    }
                }
           
                return minimumNode;
            }
            
        }
    }
    
    public boolean isMoveOutOfBounds(int move_row, int move_col)
    {
        return (move_row >= ROW_SIZE || move_col >= COL_SIZE ||
               move_row < 0         || move_col < 0);
    }
    public boolean isMoveInBounds(int move_row, int move_col)
    {
       return !isMoveOutOfBounds(move_row, move_col);
    }
    public boolean isValidMove(int move_row, int move_col, int[][]boardState)
    {
        boolean is_move_in_bounds = true;
        boolean is_space_free = true;
        
        is_move_in_bounds = isMoveInBounds(move_row, move_col);
        
        
        
        if(is_move_in_bounds &&
                boardState[move_row][move_col] != 0)
        {
            is_space_free = false;
        }
        
        return is_move_in_bounds && is_space_free;
            
    }
    
    boolean isValidTeamCell(int move_row, int move_col, int myTeam, int[][]boardState)
    {
        if(isMoveInBounds(move_row, move_col) )
        {
            //Error: Forgot that the array can have null values.
            return boardState[move_row][move_col] == myTeam;
        }
        
        return false;
    }
    
    
    public int getFreeBlocksInDirection(int[][] boardState, int rowDir, int colDir, 
            int row_start_pos, int col_start_pos, int teamColor) {
        
        int blocks = 0;
        for(int i = 0; i < 5; i++)
        {
            int row = row_start_pos + i*rowDir;
            int col = col_start_pos + i*colDir;
            
            if(isValidTeamCell(row, col, teamColor, boardState)
                    || isValidMove(row, col, boardState))
            {
                blocks += 1;
            }
            else
            {
                return blocks;
            }
        }
        
        return blocks;
    }
    
    public double calculateLineScore(int move_row, int move_col, 
                            int rowDir, int colDir, 
                            double best_val, 
                            int curColor,
                            int [][] boardState)
    {
        int adjacent_number = 0;
        best_val = 0;
        //This bool is to make sure the move(row,col) 
        //has been visited.
        boolean have_visited_move = false;
        
        outerloop:
        for(int i = -5; i < 6; i++)
        {
            //bug is due to to when both xdir and ydir
            //are 0, we stay in the same place and loop.
            //incrementing.. yet then it should increment up to?
            
            //LEARN? Code seems obscure.. there was a lack of understanding
            //when implementing the idea.. it wouldve been better to
            //make it simpler.
            int next_row = move_row + i*rowDir;
            int next_col = move_col + i*colDir;
            
            if(next_row == move_row && 
                    next_col == move_col)
            {
                have_visited_move = true;
                adjacent_number++;
                
                //continue to next loop;
            } 
            else if(isValidTeamCell(next_row,  next_col,
                    curColor , boardState))
            {
                //Increment adjacent number
                adjacent_number++;
            }
            else
            {
                
                    if(best_val < adjacent_number &&
                            have_visited_move)
                    {
                        best_val = adjacent_number;

                        //Check winning conditions
                        int end_line = i;
                        int start_line = ((i) - adjacent_number) - 1;

                        int row_start_pos = next_row;
                        int col_start_pos = next_col;

                        int blocks_after = getFreeBlocksInDirection(boardState, rowDir, colDir,
                                    row_start_pos, col_start_pos, curColor);

                        row_start_pos = move_row  +  start_line*(-rowDir);
                        col_start_pos = move_col  +  start_line*(-colDir);

                        int blocks_before = getFreeBlocksInDirection(boardState, -rowDir, -colDir,
                                    row_start_pos, col_start_pos, curColor);


                        //This scenario is -XXXX-
                        if (best_val == 4)
                        {
                            //Set winning move
                            if(blocks_after >= 1 &&
                                    blocks_before >= 1)
                            {
                                //Has won
                                return SCORE.NEXT_TURN_WINNING_MOVE.getValue();
                            }
                        }
                        //EXIT LOOP
                        break outerloop;
                    }
                    adjacent_number = 0;

            }
        }
        
        
        
        //Check winning conditions
        if(best_val >= 5)
        {
            //Signifying a win.
            return SCORE.IMMEDIATE_WINNING_MOVE.getValue();
        }
        return best_val;
    }
    
    public double getAdjacentScore(int [][] boardState,
                                      int move_row, 
                                      int move_col,
                                      int curColor)
    {
        double best_val = 1;
 
        //X,Y -> 1,0 - 0,1 - 1,1 - -1,1
        int[] arr_row_dir = {1, 0, -1};
        int[] arr_col_dir = {1, 0, -1};
        for(int xdir = 0; xdir < arr_row_dir.length; xdir++)
        {
            for(int ydir = 0; ydir < arr_col_dir.length; ydir++)
            {
                //I want to get the position of the board,
                //without worrying if its the wrong position.
                
                //explore position pattern From current position
                double temp_val = calculateLineScore(move_row, move_col, 
                                        arr_row_dir[xdir], arr_col_dir[ydir], best_val, curColor, boardState);
                
                // for temp_val > then N.
                if(temp_val > best_val)
                {
                    best_val = temp_val;
                }
            }
        }
        
        return best_val;
    }
    
    int oppositeColor(int color)
    {
        if(color  == Player.BLACK.ordinal())
        {
            return Player.WHITE.ordinal();
        }
        else if(color == Player.WHITE.ordinal())
        {
            return Player.BLACK.ordinal();
        }
        else
        {
            System.out.println("Skas: Oops, Invalid team color!");
            return -1; 
        }
    }
    
    public double blockEnemyScore(int [][] boardState,
                               int move_row,
                               int move_col,
                               int curColor)
    {
        
        double score = this.getAdjacentScore(boardState, move_row, move_col, 
                            this.oppositeColor(curColor));
        
        
        if(score == SCORE.PREVENT_WINNING_MOVE.getValue())
        {
            return SCORE.PREVENT_WINNING_MOVE.getValue();
        }
        else if(score == SCORE.NEXT_TURN_WINNING_MOVE.getValue())
        {
           return SCORE.NEXT_TURN_WINNING_MOVE.getValue();
        }
        //Returns true if enemy has won, false otherwise.
        
        
        return score*0.4;
       
        
    }
    
    public boolean hasAdjacentEnemyCell(int [][] boardState,
                                        int move_row,
                                        int move_col,
                                        int curColor)
    {
//        int [] arr_xdir = {-1
//        for(int )
//        return true;
          return true;
    }
    
    public void debugBoard(int[][] board)
        {
            for(int i = 0; i < ROW_SIZE; i++)
            {
                for(int j = 0; j < COL_SIZE; j++)
                {
                    if(board[i][j] == Player.BLACK.ordinal())
                    {
                        System.out.print(" B ");
                    }
                    else if(board[i][j] == Player.WHITE.ordinal())
                    {
                        System.out.print(" W ");   
                    }
                    else
                    {
                        System.out.print(" * ");
                    }

                }
                System.out.println();
            }
        }
    
    
}
