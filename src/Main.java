import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];
    private static ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates = new ArrayList<>(boardWithAndHeight);

    public static void main(String[] args) throws IOException {
	// write your code here

        File file = new File("/home/zaeemasvat_/IdeaProjects/sudokosolver/src/test.txt");

        BufferedReader in = new BufferedReader(new FileReader(file));
        for (int row = 0; row < boardWithAndHeight; row++) {

            try {

                String[] strArrLine = in.readLine().split(" ");
                for (int col = 0; col < boardWithAndHeight; col++) {

                    if (strArrLine[col].equals("_"))
                        strArrLine[col] = "-1";

                    board[row][col] = Integer.parseInt(strArrLine[col]);
                }

            } catch (NullPointerException e) {
                System.out.println("Not enough lines for a sudoku board!");
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Line " + row + " has too few blocks filled!");
                e.printStackTrace();
            } catch (ClassCastException e) {
                System.out.println("Board contains invalid values!");
                e.printStackTrace();
            }
        }

        fillPossibleSolutionsBoard();

        if (GeneralHelpers.isTheBoardErronous(board, possibleSolutionCandidates))
            System.out.println("Input board is erroneous");
        else solve();
    }

    private static void solve() {

        ExactTechniques.trySolve(board, possibleSolutionCandidates);
        System.out.println("Board after trying to solve without guessing:");
        printBoard();

        if (!GeneralHelpers.isBoardSolved(board)) {
            TrialAndErrorTechniques.smartDFS(board, possibleSolutionCandidates, new boolean[boardWithAndHeight][boardWithAndHeight]);
            System.out.println("Board after using smart DFS:");
            printBoard();

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

