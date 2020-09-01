import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import static javax.swing.JOptionPane.showMessageDialog;

public class MySudokuSolver implements SudokuImplementation {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];
    private static ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates = new ArrayList<>(boardWithAndHeight);

    public MySudokuSolver() {}

    @Override
    public void goButtonPressed(Integer[][] leftSudokuValues, SudokuController resultAcceptor) {

        // initialize board
        for (int row = 0; row < leftSudokuValues.length; row++)
            for (int col = 0; col < leftSudokuValues.length; col++)
                board[row][col] = leftSudokuValues[row][col] == null ? -1 : leftSudokuValues[row][col];

        // initialize possible solutions
        fillPossibleSolutionsBoard();

        // solve board if a valid board was entered
        String solveTime = "0";
        if (GeneralHelpers.isTheBoardErronous(board, possibleSolutionCandidates))
            showMessageDialog(null, "Input board is erroneous!");
        else {
            long startTime = System.nanoTime();
            solve();
            solveTime = Double.toString((double) (System.nanoTime() - startTime) / Math.pow(10, 9));
        }

        // display results
        resultAcceptor.setSudokuTime(solveTime);
        resultAcceptor.setSudokuResult(board);
        resultAcceptor.setSudokuCompleted(true);

    }

    private void waitSomeTime() {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ex) {ex.printStackTrace();}
    }


    private static void solve() {

        ExactTechniques.trySolve(board, possibleSolutionCandidates);
//        System.out.println("Board after trying to solve without guessing:");
//        printBoard();

        if (!GeneralHelpers.isBoardSolved(board)) {
            TrialAndErrorTechniques.smartDFS(board, possibleSolutionCandidates, new boolean[boardWithAndHeight][boardWithAndHeight]);
            // System.out.println("Board after using smart DFS:");
            // printBoard();

        }
    }

    private static void fillPossibleSolutionsBoard () {
        for (int row = 0; row < boardWithAndHeight; row++) {
            possibleSolutionCandidates.add(new ArrayList<>(boardWithAndHeight));
            for (int col = 0; col < boardWithAndHeight; col++) {
                possibleSolutionCandidates.get(row).add(new HashSet<>());
                if (board[row][col] == -1) {
                    for (int i = 0; i < boardWithAndHeight; i++)
                        possibleSolutionCandidates.get(row).get(col).add(i + 1);
                }
            }
        }
    }

    private static void printBoard() {
        for (int[] row : board) {
            for (int number : row)
                System.out.print((number == -1 ? "_" : number) + " ");
            System.out.println();
        }
    }
}
