import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GeneralHelpers {

    private static final int boardWithAndHeight = 9;

    public static boolean isBoardSolved(int[][] board) {

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



    public static boolean isTheBoardErronous(int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {

        boolean isTheBoardErronous = false;

        for (int row = 0; row < boardWithAndHeight; row++) {

            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] == -1 && possibleSolutionCandidates.get(row).get(col).isEmpty()) {
                    isTheBoardErronous = true;
                    break;
                }
            }

            if (isTheBoardErronous)
                break;
        }

        if (!isTheBoardErronous) {

            // check if rows are erronous
            for (int row = 0; row < boardWithAndHeight; row++) {
                SubRange currRowRange = new SubRange(row, row, 0, boardWithAndHeight - 1);
                isTheBoardErronous = isThisPortionOfTheBoardErronous(board, possibleSolutionCandidates, currRowRange);

                if (isTheBoardErronous)
                    break;
            }

            if (!isTheBoardErronous) {

                // check if cols are erronous
                for (int col = 0; col < boardWithAndHeight; col++) {
                    SubRange currColumnRange = new SubRange(0, boardWithAndHeight - 1, col, col);
                    isTheBoardErronous = isThisPortionOfTheBoardErronous(board, possibleSolutionCandidates, currColumnRange);

                    if (isTheBoardErronous)
                        break;
                }

                if (!isTheBoardErronous) {

                    SubRange currBlockRange = new SubRange();

                    // check if blocks are erronous
                    for (int startRow = 0; startRow <= 6; startRow += 3) {

                        currBlockRange.setStartRow(startRow);
                        currBlockRange.setEndRow(startRow + 3 - 1);

                        for (int startCol = 0; startCol <= 6; startCol += 3) {

                            currBlockRange.setStartCol(startCol);
                            currBlockRange.setEndCol(startCol + 3 - 1);

                            if (isThisPortionOfTheBoardErronous(board, possibleSolutionCandidates,  currBlockRange)) {
                                isTheBoardErronous = true;
                                break;
                            }
                        }

                        if (isTheBoardErronous)
                            break;
                    }
                }
            }
        }

        return isTheBoardErronous;
    }

    public static boolean isThisPortionOfTheBoardErronous (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, SubRange subRange) {

        boolean isThisPortionOfTheBoardErronous = false;

        int currSolution = 1;
        while (currSolution < 10 && !isThisPortionOfTheBoardErronous) {

            boolean thisSolutionExistsAsAFilledASolutionOrPossibleCandidateInThisPortion = false;

            for (int row = subRange.getStartRow(); row <= subRange.getEndRow(); row++) {

                for (int col = subRange.getStartCol(); col <= subRange.getEndCol(); col++) {
                    if (board[row][col] == currSolution || possibleSolutionCandidates.get(row).get(col).contains(currSolution)) {
                        thisSolutionExistsAsAFilledASolutionOrPossibleCandidateInThisPortion = true;
                        break;
                    }
                }

                if (thisSolutionExistsAsAFilledASolutionOrPossibleCandidateInThisPortion)
                    break;
            }

            if (!thisSolutionExistsAsAFilledASolutionOrPossibleCandidateInThisPortion)
                isThisPortionOfTheBoardErronous = true;

            currSolution++;
        }

        return isThisPortionOfTheBoardErronous;
    }


    public static ArrayList<CellIndex> findAllCellsRelatedToThisCell (int cellRow, int cellCol) {

        ArrayList<CellIndex> cellsRelatedToThisCell = new ArrayList<>();

        // add all cells in the same row as this cell
        for (int c = 0; c < boardWithAndHeight; c++)
            if (c != cellCol)
                cellsRelatedToThisCell.add(new CellIndex(cellRow, c));

        // add all cells in the same column as this cell
        for (int r = 0; r < boardWithAndHeight; r++)
            if (r != cellRow)
                cellsRelatedToThisCell.add(new CellIndex(r, cellCol));

        // add all cells in the same block as this cell
        SubRange thisBlockRange = GeneralHelpers.getSubRange(cellRow, cellCol);
        for (int r = thisBlockRange.getStartRow(); r < thisBlockRange.getEndRow(); r++) {
            for (int c = thisBlockRange.getStartCol(); c < thisBlockRange.getEndCol(); c++) {
                if (r != cellRow || c != cellCol)
                    cellsRelatedToThisCell.add(new CellIndex(r, c));
            }
        }

        return cellsRelatedToThisCell;
    }


    public static void removeSolutiomCandidates (ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates, HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesToBeRemoved) {

        for (int solutionCandidate : solutionCandidatesToBeRemoved.keySet()) {
            ArrayList<CellIndex> cellsToHaveThisSolutionCandidateRemoved = solutionCandidatesToBeRemoved.get(solutionCandidate);
            for (CellIndex cell : cellsToHaveThisSolutionCandidateRemoved)
                possibleSolutionCandidates.get(cell.getRow()).get(cell.getCol()).remove(solutionCandidate);
        }
    }

    public static void removeSolutionCandidates (int solutionCandidate, ArrayList<CellIndex> cellsToRemoveThisSolutionCandidate, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {
        for (CellIndex cellIndex : cellsToRemoveThisSolutionCandidate)
            possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).remove(solutionCandidate);
    }

    public static void addAll (HashMap<Integer, ArrayList<CellIndex>> mainMap, HashMap<Integer, ArrayList<CellIndex>> subMap) {

        for (int key : subMap.keySet()) {
            if (!mainMap.containsKey(key))
                mainMap.put(key, new ArrayList<>());
            mainMap.get(key).addAll(subMap.get(key));
        }
    }

    public static SubRange getSubRange (int row, int col) {
        int startRow, endRow, startCol, endCol;

        if (row % 3 == 0) {
            startRow = row;
            endRow = startRow + 3;
        } else {
            startRow = roundDown(row, 3);
            endRow = roundUp(row, 3);
        }

        if (col % 3 == 0) {
            startCol = col;
            endCol = startCol + 3;
        } else {
            startCol = roundDown(col, 3);
            endCol = roundUp(col, 3);
        }

        return new SubRange(startRow, endRow, startCol, endCol);
    }

    public static int roundDown (int num, int multipleBase) {
        return (int) Math.floor((double) num/multipleBase) * multipleBase;
    }

    public static int roundUp (int num, int multipleBase) {
        return (int) Math.ceil((double) num/multipleBase) * multipleBase;
    }

}
