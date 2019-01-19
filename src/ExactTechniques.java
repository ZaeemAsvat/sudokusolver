import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExactTechniques {

    private static final int boardWithAndHeight = 9;

    public static void trySolve(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        removeTrivalImpossibleSolutions(board, possibleSolutionCandidates);
        fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells(board, possibleSolutionCandidates);

        HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell(board, possibleSolutionCandidates);

        while (!solutionsWhichCanOnlyBeFilledInOneCell.isEmpty()) {

            for (int solution : solutionsWhichCanOnlyBeFilledInOneCell.keySet()) {

                ArrayList<CellIndex> cellIndicesForThisSolutiion = solutionsWhichCanOnlyBeFilledInOneCell.get(solution);
                for (CellIndex cellIndex : cellIndicesForThisSolutiion) {
                    board[cellIndex.getRow()][cellIndex.getCol()] = solution;
                    possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();
                    removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(board, possibleSolutionCandidates, cellIndex.getRow(), cellIndex.getCol());
                }
            }

            fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells(board, possibleSolutionCandidates);

            solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell(board, possibleSolutionCandidates);
        }
    }


    public static HashMap<Integer, ArrayList<CellIndex>> findTrivialImpossibleSolutionCandidates(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        HashMap<Integer, ArrayList<CellIndex>> trivialImpossibleSolutionCandidates = new HashMap<>();

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] != -1) {
                    ArrayList<CellIndex> cellsRelatedToThisCell = GeneralHelpers.findAllCellsRelatedToThisCell(row, col);
                    for (CellIndex cell : cellsRelatedToThisCell) {
                        if (possibleSolutionCandidates.get(cell.getRow()).get(cell.getCol()).contains(board[row][col])) {
                            if (!trivialImpossibleSolutionCandidates.containsKey(board[row][col]))
                                trivialImpossibleSolutionCandidates.put(board[row][col], new ArrayList<>());
                            trivialImpossibleSolutionCandidates.get(board[row][col]).add(cell);
                        }
                    }
                }
            }
        }

        return trivialImpossibleSolutionCandidates;
    }

    public static void fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution(board, possibleSolutionCandidates);
        while (!cellIndicesWithOnlyOnePossibleSolution.isEmpty()) {

            for (CellIndex cellIndex : cellIndicesWithOnlyOnePossibleSolution) {

                // this cell only has one possible solution, so we set the cell
                // with the first number in its possible solutions list (since it's the only solution)
                board[cellIndex.getRow()][cellIndex.getCol()] = possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).iterator().next();

                // clear this cells possible solutions list, it's no longer needed
                possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

                // remove this cells solution value from all solutions sets of its relations
                removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(board, possibleSolutionCandidates, cellIndex.getRow(), cellIndex.getCol());
            }

            // some possible solutions could have been removed from various unfilled cells, which
            // may leave some cells with only one solution, so that we can fill them in in the next loop
            cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution(board, possibleSolutionCandidates);
        }

    }

    public static void removeTrivalImpossibleSolutions (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        for (int row = 0; row < boardWithAndHeight; row++)
            for (int col = 0; col < boardWithAndHeight; col++)
                removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(board, possibleSolutionCandidates, row, col);
    }

    public static void removeThisCellsSolutionFromAllSolutionSetsOfItsRelations(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, int cellRow, int cellCol) {

        // if cell isn't blank (there is a number already filled in this cell)
        if (board[cellRow][cellCol] != -1) {

            // remove this number from all possible solution sets of blank cells
            // in the same row as this cell
            for (int c = 0; c < boardWithAndHeight; c++)
                if (board[cellRow][c] == -1)
                    possibleSolutionCandidates.get(cellRow).get(c).remove(board[cellRow][cellCol]);

            // remove this number from all possible solution sets of blank cells
            // in the same column as this cell
            for (int r = 0; r < boardWithAndHeight; r++)
                if (board[r][cellCol] == -1)
                    possibleSolutionCandidates.get(r).get(cellCol).remove(board[cellRow][cellCol]);

            // remove this number from all possible solution sets of cells
            // in the same block as this cell
            SubRange thisBlockRange = GeneralHelpers.getSubRange(cellRow, cellCol);
            for (int r = thisBlockRange.getStartRow(); r < thisBlockRange.getEndRow(); r++) {
                for (int c = thisBlockRange.getStartCol(); c < thisBlockRange.getEndCol(); c++) {
                    if (board[r][c] == -1)
                        possibleSolutionCandidates.get(r).get(c).remove(board[cellRow][cellCol]);
                }
            }

        }
    }

    public static ArrayList<CellIndex> findAllCellsWhichHaveOnlyOnePossibleSolution(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolutiom = new ArrayList<>();

        for (int row = 0; row < boardWithAndHeight; row++)
            for (int col = 0; col < boardWithAndHeight; col++)
                if (board[row][col] == -1 && possibleSolutionCandidates.get(row).get(col).size() == 1)
                    cellIndicesWithOnlyOnePossibleSolutiom.add(new CellIndex(row, col));

        return cellIndicesWithOnlyOnePossibleSolutiom;
    }

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingRows(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows = new HashMap<>();

        for (int row = 0; row < boardWithAndHeight; row++) {
            HashMap<Integer, ArrayList<CellIndex>> solutionsFoundInThisRow = findSolutionsWhichCanOnlyBeFilledInOneCellInThisRow(board, possibleSolutionCandidates, row);
            for (int solution : solutionsFoundInThisRow.keySet()) {
                if (totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.containsKey(solution))
                    totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.get(solution).addAll(solutionsFoundInThisRow.get(solution));
                else totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows.put(solution, solutionsFoundInThisRow.get(solution));
            }
        }

        return totalSolutionsWhichCanOnlyBeFilledInOneCellUsingRows;
    }

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisRow (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, int row) {

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

                if (board[row][col] == -1 && possibleSolutionCandidates.get(row).get(col).contains(solutionToTry)) {

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


    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingColumns(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsUsingsColumns = new HashMap<>();

        for (int col = 0; col < boardWithAndHeight; col++) {
            HashMap<Integer, ArrayList<CellIndex>> solutionsFoundInThisRow = findSolutionsWhichCanOnlyBeFilledInOneCellInThisCol(board, possibleSolutionCandidates, col);
            for (int solution : solutionsFoundInThisRow.keySet()) {
                if (totalSolutionsUsingsColumns.containsKey(solution))
                    totalSolutionsUsingsColumns.get(solution).addAll(solutionsFoundInThisRow.get(solution));
                else totalSolutionsUsingsColumns.put(solution, solutionsFoundInThisRow.get(solution));
            }
        }

        return totalSolutionsUsingsColumns;
    }

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisCol (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, int col) {

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

                if (board[row][col] == -1 && possibleSolutionCandidates.get(row).get(col).contains(solutionToTry)) {

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

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCell(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCell = new HashMap<>();

        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingBlocks = findSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks(board, possibleSolutionCandidates);
        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingRows = findSolutionsWhichCanOnlyBeFilledInOneCellUsingRows(board, possibleSolutionCandidates);
        HashMap<Integer, ArrayList<CellIndex>> solutionsUsingColumns = findSolutionsWhichCanOnlyBeFilledInOneCellUsingColumns(board, possibleSolutionCandidates);

        GeneralHelpers.addAll(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingBlocks);
        GeneralHelpers.addAll(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingRows);
        GeneralHelpers.addAll(totalSolutionsWhichCanOnlyBeFilledInOneCell, solutionsUsingColumns);

        return totalSolutionsWhichCanOnlyBeFilledInOneCell;
    }


    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        HashMap<Integer, ArrayList<CellIndex>> totalSolutionsWhichCanOnlyBeFilledInOneCellUsingBlocks = new HashMap<>();

        SubRange currBlockRange = new SubRange();

        for (int startRow = 0; (startRow + 3) <= 9; startRow += 3) {

            currBlockRange.setStartRow(startRow);
            currBlockRange.setEndRow(startRow + 3 - 1);

            for (int startCol = 0; (startCol + 3) <= 9; startCol += 3) {

                currBlockRange.setStartCol(startCol);
                currBlockRange.setEndCol(startCol + 3 - 1);

                HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCellFoundInThisBlock
                        = findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock(board, possibleSolutionCandidates, currBlockRange);

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

    public static HashMap<Integer, ArrayList<CellIndex>> findSolutionsWhichCanOnlyBeFilledInOneCellInThisBlock (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, SubRange blockRange) {

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

                    if (board[row][col] == -1 && possibleSolutionCandidates.get(row).get(col).contains(solutionToTry)) {

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

}
