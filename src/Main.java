import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Main {

    private static final int boardWithAndHeight = 9;
    private static final int[][] board = new int[boardWithAndHeight][boardWithAndHeight];
    private static ArrayList<ArrayList<HashSet<Integer>>> possibleSolutions = new ArrayList<>(boardWithAndHeight);

    public static void main(String[] args) throws IOException {
	// write your code here

        fillPossibleSolutionsBoard();

        File file = new File("/home/zaeemasvat_/IdeaProjects/sudokosolver/src/medium.txt");

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

        solve();
        printBoard();


    }

    private static void fillPossibleSolutionsBoard () {
        for (int row = 0; row < boardWithAndHeight; row++) {
            possibleSolutions.add(new ArrayList<>(boardWithAndHeight));
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] != -1) {
                    possibleSolutions.get(row).add(new HashSet<>(boardWithAndHeight));
                    for (int i = 0; i < boardWithAndHeight; i++)
                        possibleSolutions.get(row).get(col).add(i + 1);
                }
            }
        }
    }

    private static void solve() {

        removeTrivalImpossibleSolutions();
        fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells();

        HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell();

        while (!solutionsWhichCanOnlyBeFilledInOneCell.isEmpty()) {

            for (int solution : solutionsWhichCanOnlyBeFilledInOneCell.keySet()) {

                ArrayList<CellIndex> cellIndicesForThisSolutiion = solutionsWhichCanOnlyBeFilledInOneCell.get(solution);
                for (CellIndex cellIndex : cellIndicesForThisSolutiion) {
                    board[cellIndex.getRow()][cellIndex.getCol()] = solution;
                    possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();
                    removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(cellIndex.getRow(), cellIndex.getCol());
                }
            }

            fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells();

            solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell();
        }
    }

    private static void removeAllTrivialImpossibleSolutionsAndFillInAllCurrentTrivialSolutions() {
        removeTrivalImpossibleSolutions();
        fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells();
    }

    private static void fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells() {

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution();
        while (!cellIndicesWithOnlyOnePossibleSolution.isEmpty()) {

            for (CellIndex cellIndex : cellIndicesWithOnlyOnePossibleSolution) {

                // this cell only has one possible solution, so we set the cell
                // with the first number in its possible solutions list (since it's the only solution)
                board[cellIndex.getRow()][cellIndex.getCol()] = possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).iterator().next();

                // clear this cells possible solutions list, it's no longer needed
                possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

                // remove this cells solution value from all solutions sets of its relations
                removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(cellIndex.getRow(), cellIndex.getCol());
            }

            // some possible solutions could have been removed from various unfilled cells, which
            // may leave some cells with only one solution, so that we can fill them in in the next loop
            cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution();
        }

    }

    private static void removeTrivalImpossibleSolutions () {

        for (int row = 0; row < boardWithAndHeight; row++)
            for (int col = 0; col < boardWithAndHeight; col++)
                removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(row, col);
    }

    private static void removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(int cellRow, int cellCol) {

        // if cell isn't blank (there is a number already filled in this cell)
        if (board[cellRow][cellCol] != -1) {

            // remove this number from all possible solution sets of blank cells
            // in the same row as this cell
            for (int c = 0; c < boardWithAndHeight; c++)
                if (board[cellRow][c] == -1)
                    possibleSolutions.get(cellRow).get(c).remove(board[cellRow][cellCol]);

            // remove this number from all possible solution sets of blank cells
            // in the same column as this cell
            for (int r = 0; r < boardWithAndHeight; r++)
                if (board[r][cellCol] == -1)
                    possibleSolutions.get(r).get(cellCol).remove(board[cellRow][cellCol]);

            // remove this number from all possible solution sets of cells
            // in the same block as this cell
            SubRange thisBlockRange = getSubRange(cellRow, cellCol);
            for (int r = thisBlockRange.getStartRow(); r < thisBlockRange.getEndRow(); r++)
                for (int c = thisBlockRange.getStartCol(); c < thisBlockRange.getEndCol(); c++)
                    if (board[r][c] == -1)
                        possibleSolutions.get(r).get(c).remove(board[cellRow][cellCol]);

        }
    }

    private static ArrayList<CellIndex> findAllCellsWhichHaveOnlyOnePossibleSolution() {

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolutiom = new ArrayList<>();

        for (int row = 0; row < boardWithAndHeight; row++)
            for (int col = 0; col < boardWithAndHeight; col++)
                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).size() == 1)
                    cellIndicesWithOnlyOnePossibleSolutiom.add(new CellIndex(row, col));

        return cellIndicesWithOnlyOnePossibleSolutiom;
    }

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCell() {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        SubRange currBlockRange = new SubRange();

        for (int startRow = 0; (startRow + 3) < 9; startRow += 3) {

            currBlockRange.setStartRow(startRow);
            currBlockRange.setEndRow(startRow + 3);

            for (int startCol = 0; (startCol + 3) < 9; startCol += 3) {

                currBlockRange.setStartCol(startCol);
                currBlockRange.setEndCol(startCol + 3);

                HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock
                        = findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock(currBlockRange);

                for (int solution : solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock.keySet()) {

                    ArrayList<CellIndex> cellIndicesForThisSolutionFoundInThisBlock = solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock.get(solution);

                    if (totalSolutionsWhichCanOnlyBeFilledInOneCell.containsKey(solution))
                        totalSolutionsWhichCanOnlyBeFilledInOneCell.get(solution).addAll(cellIndicesForThisSolutionFoundInThisBlock);
                    else
                        totalSolutionsWhichCanOnlyBeFilledInOneCell.put(solution, cellIndicesForThisSolutionFoundInThisBlock);
                }
            }
        }

        return totalSolutionsWhichCanOnlyBeFilledInOneCell;
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock (SubRange blockRange) {

        HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        for (int solutionToTry = 1; solutionToTry < 10; solutionToTry++) {

            CellIndex cellForWhichThisSolutiomIsPossible = new CellIndex(-1, -1);
            boolean thisSolutionCanOnlyBeFilledInOneCell = true;
            boolean thisSolutionHasAlreadyBeenFilledInThisBlock = false;

            for (int row = blockRange.getStartRow(); row <= blockRange.getEndRow(); row++) {
                for (int col = blockRange.getStartCol(); col < blockRange.getEndCol(); col++) {

                    if (board[row][col] != -1) {
                        thisSolutionHasAlreadyBeenFilledInThisBlock = true;
                        break;
                    }

                    if (possibleSolutions.get(row).get(col).contains(solutionToTry)) {

                        if (cellForWhichThisSolutiomIsPossible.getRow() != -1 && cellForWhichThisSolutiomIsPossible.getCol() != -1) {
                            thisSolutionCanOnlyBeFilledInOneCell = false;
                            break;
                        }

                        cellForWhichThisSolutiomIsPossible.setRow(row);
                        cellForWhichThisSolutiomIsPossible.setCol(col);
                    }
                }

                if (thisSolutionHasAlreadyBeenFilledInThisBlock || !thisSolutionCanOnlyBeFilledInOneCell)
                    break;
            }

            if (!thisSolutionHasAlreadyBeenFilledInThisBlock && thisSolutionCanOnlyBeFilledInOneCell) {
                board[cellForWhichThisSolutiomIsPossible.getRow()][cellForWhichThisSolutiomIsPossible.getCol()] = solutionToTry;

                ArrayList<CellIndex> currCellIndicesForThisSoliution;
                if (solutionsWhichCanOnlyBeFilledInOneCell.containsKey(solutionToTry))
                    currCellIndicesForThisSoliution = solutionsWhichCanOnlyBeFilledInOneCell.get(solutionToTry);
                else currCellIndicesForThisSoliution = new ArrayList<>();

                currCellIndicesForThisSoliution.add(cellForWhichThisSolutiomIsPossible);
                solutionsWhichCanOnlyBeFilledInOneCell.put(solutionToTry, currCellIndicesForThisSoliution);
            }
        }

        return solutionsWhichCanOnlyBeFilledInOneCell;
    }

    private static void printBoard() {
        for (int[] row : board) {
            for (int number : row)
                System.out.print((number == -1 ? "_" : number) + " ");
            System.out.println();
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

