import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];

    public static void main(String[] args) throws IOException {
	// write your code here

        ArrayList<ArrayList<ArrayList<Integer>>> possibleSolutions = new ArrayList<>();
        fillPossibleSolutionsBoard(possibleSolutions);

        File file = new File("");

        BufferedReader in = new BufferedReader(new FileReader(file));
        for (int row = 0; row < boardWithAndHeight; row++) {

            try {

                String[] strArrLine = in.readLine().split(" ");
                for (int col = 0; col < boardWithAndHeight; col++) {

                    board[row][col] = Integer.parseInt(strArrLine[col]);

                    if (board[row][col] != -1)
                        possibleSolutions.get(row).get(col).remove(board[row][col]);
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
    }

    private static void fillPossibleSolutionsBoard (ArrayList<ArrayList<ArrayList<Integer>>> possibleSolutions) {

        possibleSolutions = new ArrayList<>(boardWithAndHeight);
        for (int row = 0; row < boardWithAndHeight; row++) {
            possibleSolutions.set(row, new ArrayList<>(boardWithAndHeight));
            for (int col = 0; col < boardWithAndHeight; col++) {
                possibleSolutions.get(row).set(col, new ArrayList<>(boardWithAndHeight));
                for (int i = 0; i < boardWithAndHeight; i++)
                    possibleSolutions.get(row).get(col).set(i, i + 1);
            }
        }
    }
}
