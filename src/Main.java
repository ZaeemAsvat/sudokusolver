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

        File file = new File("/home/zaeemasvat_/IdeaProjects/sudokosolver/src/hard.txt");

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

        solve();
        board[8][5] = 5;
        possibleSolutions.get(8).get(5).clear();


//        solve();

        /*
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (board[i][j] == -1)
                    System.out.println(possibleSolutions.get(i).get(j).clone());


        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                System.out.println((i+1) + " " + (j+1) + ": " + possibleSolutions.get(i).get(j).clone());
                */

/*
            for (int row = 0; row < boardWithAndHeight; row++) {
                for (int sol = 1; sol < 10; sol++) {
                    boolean b = false;
                    for (int col = 0; col < 9; col++) {
                        if (board[col][row] == sol || possibleSolutions.get(col).get(row).contains(sol)) {
                            b = true;
                            break;
                        }
                    }

                    System.out.println(b);
                }
            }
*/


        SubRange currBlockRange = new SubRange();

        for (int startRow = 0; (startRow + 3) <= 9; startRow += 3) {

            currBlockRange.setStartRow(startRow);
            currBlockRange.setEndRow(startRow + 3 - 1);

            for (int startCol = 0; (startCol + 3) <= 9; startCol += 3) {

                currBlockRange.setStartCol(startCol);
                currBlockRange.setEndCol(startCol + 3 - 1);

                for (int sol = 1; sol < 10; sol++) {
                    boolean b = false;
                    for (int r = currBlockRange.getStartRow(); r <= currBlockRange.getEndRow(); r++) {
                        for (int c = currBlockRange.getStartCol(); c <= currBlockRange.getEndCol(); c++) {
                            if (board[r][c] == sol || possibleSolutions.get(r).get(c).contains(sol)) {
                                b = true;
                                break;
                            }
                        }

                        if (b)
                            break;
                    }

                    System.out.println(b);
                }

            }
        }

 printBoard();


    }

    private static boolean failSafeTechnique(Stack<CellIndex> myStack) {

        if (myStack == null)
            myStack = new Stack<>();

        CellIndex currCellWithLeastPossibleSolutionCandidates = findCellWithLeastPossibleSolutionCandidates();

        myStack.push(currCellWithLeastPossibleSolutionCandidates);

        Stack<Integer> possibleSolutionCandidatesForThisCell = new Stack<>();
        for (int possibleSolutionCandidate : possibleSolutions.get(currCellWithLeastPossibleSolutionCandidates.getRow()).get(currCellWithLeastPossibleSolutionCandidates.getCol()))
            possibleSolutionCandidatesForThisCell.push(possibleSolutionCandidate);

        board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = possibleSolutionCandidatesForThisCell.pop();

        HashMap<Integer, ArrayList<CellIndex>> solutionsFilled = new HashMap<>();
        HashMap<Integer, ArrayList<CellIndex>> solutionCandidnatesRemoved = new HashMap<>();

        boolean thisSolutionResultedInProblems = !trySolve(solutionCandidnatesRemoved;

        while ((thisSolutionResultedInProblems && !possibleSolutionCandidatesForThisCell.empty())
                || (!isBoardSolved() && !failSafeTechnique(myStack))) {

            board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = possibleSolutionCandidatesForThisCell.pop();

            for (int solutionFilled : solutionsFilled.keySet()) {
                ArrayList<CellIndex> cellsFilled = solutionsFilled.get(solutionFilled);
                for (CellIndex cellFilled : cellsFilled)
                    board[cellFilled.getRow()][cellFilled.getCol()] = -1;
            }

            for (int solutionCandidateRemoved : solutionCandidnatesRemoved.keySet()) {
                ArrayList<CellIndex> cellsThatHadThisSolutionRemoved = solutionCandidnatesRemoved.get(solutionCandidateRemoved);
                for (CellIndex cellThatHadThisSolutiomRemoved : cellsThatHadThisSolutionRemoved)
                    possibleSolutions.get(cellThatHadThisSolutiomRemoved.getRow()).get(cellThatHadThisSolutiomRemoved.getCol()).add(solutionCandidateRemoved);
            }
        }

        return !thisSolutionResultedInProblems && possibleSolutionCandidatesForThisCell.empty();
    }

    private static boolean trySolve(HashMap<Integer, ArrayList<CellIndex>> solutionsRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

    }

    private static boolean isBoardSolved() {
        boolean isSolved = true;

        for (int[] row : board) {
            for (int cell : row) {
                if (cell == -1) {
                    isSolved = false;
                    break;
                }
            }

            if (!isSolved)
                break;
        }

        return isSolved;
    }

    private static CellIndex findCellWithLeastPossibleSolutionCandidates() {

        CellIndex cellWithLeastPosibleSolutiomCandidates = new CellIndex(-1, -1);
        int minPossibleSolutionCandidates = Integer.MAX_VALUE;

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).size() < minPossibleSolutionCandidates) {

                    cellWithLeastPosibleSolutiomCandidates.setRow(row);
                    cellWithLeastPosibleSolutiomCandidates.setCol(col);

                    minPossibleSolutionCandidates = possibleSolutions.get(row).get(col).size();
                }
            }
        }

        return cellWithLeastPosibleSolutiomCandidates;
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

                System.out.println();
                printBoard();

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

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingRows() {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows = new HashMap<>();

        for (int row = 0; row < boardWithAndHeight; row++) {
            HashMap<Integer, ArrayList<CellIndex>> solutionsFoundInThisRow = findSolutionsWhichCanOnlyBeFilledInOneCellInThisRow(row);
            for (int solution : solutionsFoundInThisRow.keySet()) {
                if (totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.containsKey(solution))
                    totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.get(solution).addAll(solutionsFoundInThisRow.get(solution));
                else totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.put(solution, solutionsFoundInThisRow.get(solution));
            }
        }

        return totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows;
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisRow (int row) {

        HashMap<Integer, ArrayList<CellIndex>> solutionsInThisRowWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        for (int solutionToTry = 1; solutionToTry <= boardWithAndHeight; solutionToTry++) {

            CellIndex cellThatCanBeFilledWithThisSolution = new CellIndex(-1, -1);

            boolean solutionHasAlreadyBeenFilledInThisRow = false;
            boolean thisSolutionCanOnlyBeFilledInOneCell = true;

            for (int col = 0; col < boardWithAndHeight; col++) {

                if (board[row][col] == solutionToTry) {
                    solutionHasAlreadyBeenFilledInThisRow = true;
                    break;
                }

                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).contains(solutionToTry)) {

                    if (cellThatCanBeFilledWithThisSolution.getRow() != -1 && cellThatCanBeFilledWithThisSolution.getCol() != -1) {
                        thisSolutionCanOnlyBeFilledInOneCell = false;
                        break;
                    }

                    cellThatCanBeFilledWithThisSolution.setRow(row);
                    cellThatCanBeFilledWithThisSolution.setCol(col);
                }
            }

            if (!solutionHasAlreadyBeenFilledInThisRow && thisSolutionCanOnlyBeFilledInOneCell) {
                if (!solutionsInThisRowWhichCanOnlyBeFilledInOneCell.containsKey(solutionToTry))
                    solutionsInThisRowWhichCanOnlyBeFilledInOneCell.put(solutionToTry, new ArrayList<>());
                solutionsInThisRowWhichCanOnlyBeFilledInOneCell.get(solutionToTry).add(cellThatCanBeFilledWithThisSolution);
            }
        }

        return solutionsInThisRowWhichCanOnlyBeFilledInOneCell;
    }


    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingColumns() {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsUsingsColumns = new HashMap<>();

        for (int col = 0; col < boardWithAndHeight; col++) {
            HashMap<Integer, ArrayList<CellIndex>> solutionsFoundInThisRow = findSolutionsWhichCanOnlyBeFilledInOneCellInThisCol(col);
            for (int solution : solutionsFoundInThisRow.keySet()) {
                if (totalSolutionsUsingsColumns.containsKey(solution))
                    totalSolutionsUsingsColumns.get(solution).addAll(solutionsFoundInThisRow.get(solution));
                else totalSolutionsUsingsColumns.put(solution, solutionsFoundInThisRow.get(solution));
            }
        }

        return totalSolutionsUsingsColumns;
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisCol (int col) {

        HashMap<Integer, ArrayList<CellIndex>> solutionsInThisColumnWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        for (int solutionToTry = 1; solutionToTry <= boardWithAndHeight; solutionToTry++) {

            CellIndex cellThatCanBeFilledWithThisSolution = new CellIndex(-1, -1);

            boolean solutionHasAlreadyBeenFilledInThisCol = false;
            boolean thisSolutionCanOnlyBeFilledInOneCell = true;

            for (int row = 0; row < boardWithAndHeight; row++) {

                if (board[row][col] == solutionToTry) {
                    solutionHasAlreadyBeenFilledInThisCol = true;
                    break;
                }

                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).contains(solutionToTry)) {

                    if (cellThatCanBeFilledWithThisSolution.getRow() != -1 && cellThatCanBeFilledWithThisSolution.getCol() != -1) {
                        thisSolutionCanOnlyBeFilledInOneCell = false;
                        break;
                    }

                    cellThatCanBeFilledWithThisSolution.setRow(row);
                    cellThatCanBeFilledWithThisSolution.setCol(col);
                }
            }

            if (!solutionHasAlreadyBeenFilledInThisCol && thisSolutionCanOnlyBeFilledInOneCell) {
                if (!solutionsInThisColumnWhichCanOnlyBeFilledInOneCell.containsKey(solutionToTry))
                    solutionsInThisColumnWhichCanOnlyBeFilledInOneCell.put(solutionToTry, new ArrayList<>());
                solutionsInThisColumnWhichCanOnlyBeFilledInOneCell.get(solutionToTry).add(cellThatCanBeFilledWithThisSolution);
            }
        }

        return solutionsInThisColumnWhichCanOnlyBeFilledInOneCell;
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCell() {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingBlocks = findSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks();
        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingRows = findSolutionsWhichCanOnlyBeFilledInOneCellUsingRows();
        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingColumns = findSolutionsWhichCanOnlyBeFilledInOneCellUsingColumns();

        addSolutions(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingBlocks);
        addSolutions(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingRows);
        addSolutions(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingColumns);

        return totalSolutionsWhichCanOnlyBeFilledInOneCell;
    }

    private static void addSolutions (HashMap<Integer, ArrayList<CellIndex>> major, HashMap<Integer, ArrayList<CellIndex>> minor) {
        for (int solution : minor.keySet()) {
            if (!major.containsKey(solution))
                major.put(solution, new ArrayList<>());
            major.get(solution).addAll(minor.get(solution));
        }
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks() {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks = new HashMap<>();

        SubRange currBlockRange = new SubRange();

        for (int startRow = 0; (startRow + 3) <= 9; startRow += 3) {

            currBlockRange.setStartRow(startRow);
            currBlockRange.setEndRow(startRow + 3 - 1);

            for (int startCol = 0; (startCol + 3) <= 9; startCol += 3) {

                currBlockRange.setStartCol(startCol);
                currBlockRange.setEndCol(startCol + 3 - 1);

                HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock
                        = findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock(currBlockRange);

                for (int solution : solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock.keySet()) {

                    ArrayList<CellIndex> cellIndicesForThisSolutionFoundInThisBlock = solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock.get(solution);

                    if (totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks.containsKey(solution))
                        totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks.get(solution).addAll(cellIndicesForThisSolutionFoundInThisBlock);
                    else
                        totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks.put(solution, cellIndicesForThisSolutionFoundInThisBlock);
                }
            }
        }

        return totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks;
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock (SubRange blockRange) {

        HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        for (int solutionToTry = 1; solutionToTry < 10; solutionToTry++) {

            CellIndex cellForWhichThisSolutiomIsPossible = new CellIndex(-1, -1);
            boolean thisSolutionCanOnlyBeFilledInOneCell = true;
            boolean thisSolutionHasAlreadyBeenFilledInThisBlock = false;

            for (int row = blockRange.getStartRow(); row <= blockRange.getEndRow(); row++) {
                for (int col = blockRange.getStartCol(); col <= blockRange.getEndCol(); col++) {

                    if (board[row][col] == solutionToTry) {
                        thisSolutionHasAlreadyBeenFilledInThisBlock = true;
                        break;
                    }

                    if (board[row][col] == -1 && possibleSolutions.get(row).get(col).contains(solutionToTry)) {

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

