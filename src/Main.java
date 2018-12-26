import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Main {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];
    private static final ArrayList<ArrayList<HashSet<Integer>>> possibleSolutions = new ArrayList<>(boardWithAndHeight);

    public static void main(String[] args) throws IOException {
	// write your code here

        fillPossibleSolutionsBoard();

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

    private static void fillPossibleSolutionsBoard () {
        for (int row = 0; row < boardWithAndHeight; row++) {
            possibleSolutions.set(row, new ArrayList<>(boardWithAndHeight));
            for (int col = 0; col < boardWithAndHeight; col++) {
                possibleSolutions.get(row).set(col, new HashSet<>(boardWithAndHeight));
                for (int i = 0; i < boardWithAndHeight; i++)
                    possibleSolutions.get(row).get(col).add(i + 1);
            }
        }
    }

    private static void removeTrivalImpossibleSolutions () {

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {

                // if cell isn't blank (there is a number already filled in this cell)
                if (board[row][col] != -1) {

                    // remove this number from all possible solution sets of cells
                    // in the same row as this cell
                    for (int c = 0; c < boardWithAndHeight; c++)
                        possibleSolutions.get(row).get(c).remove(board[row][col]);

                    // remove this number from all possible solution sets of cells
                    // in the same column as this cell
                    for (int r = 0; r < boardWithAndHeight; r++)
                        possibleSolutions.get(r).get(col).remove(board[row][col]);

                    // remove this number from all possible solution sets of cells
                    // in the same block as this cell
                    SubRange thisBlockRange = getSubRange(row, col);
                    for (int r = thisBlockRange.getStartRow(); r <= thisBlockRange.getEndRow(); r++)
                        for (int c = thisBlockRange.getStartCol(); c <= thisBlockRange.getEndCol(); c++)
                            possibleSolutions.get(r).get(c).remove(board[row][col]);

                }
            }
        }
    }

    private static SubRange getSubRange (int row, int col) {
        return new SubRange(roundDown(row, 3), roundUp(row, 3),
                            roundDown(col, 3), roundUp(col, 3));
    }

    private static int roundDown (int num, int multipleBase) {
        return (int) Math.floor((double) num/multipleBase) * multipleBase;
    }

    private static int roundUp (int num, int multipleBase) {
        return (int) Math.ceil((double) num/multipleBase) * multipleBase;
    }

}

