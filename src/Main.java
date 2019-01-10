import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];
    private static ArrayList<ArrayList<HashSet<Integer>>> possibleSolutions = new ArrayList<>(boardWithAndHeight);

    public static void main(String[] args) throws IOException {
	// write your code here

        File file = new File("/home/zaeemasvat_/IdeaProjects/sudokosolver/src/extreme.txt");

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

        if (isTheBoardErronous())
            System.out.println("Input board is erroneous");
        else solve();
    }

    private static void solve() {

        trySolveWithoutGuessing();
        System.out.println("Board after trying to solve without guessing:");
        printBoard();

        smartDFS(new boolean[boardWithAndHeight][boardWithAndHeight]);
        System.out.println("Board after using smart DFS:");
        printBoard();
    }





    private static void advancedRemovePossibleSolutionCandidates() {

    }

    private static void fillPossibleSolutionsBoard () {
        for (int row = 0; row < boardWithAndHeight; row++) {
            possibleSolutions.add(new ArrayList<>(boardWithAndHeight));
            for (int col = 0; col < boardWithAndHeight; col++) {
                possibleSolutions.get(row).add(new HashSet<>());
                if (board[row][col] == -1) {
                    for (int i = 0; i < boardWithAndHeight; i++)
                        possibleSolutions.get(row).get(col).add(i + 1);
                }
            }
        }
    }



    private static void addSolutions (HashMap<Integer, ArrayList<CellIndex>> major, HashMap<Integer, ArrayList<CellIndex>> minor) {
        for (int solution : minor.keySet()) {
            if (!major.containsKey(solution))
                major.put(solution, new ArrayList<>());
            major.get(solution).addAll(minor.get(solution));
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

