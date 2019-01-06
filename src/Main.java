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

        /*
        solve();
        board[3][4] = 8;
        possibleSolutions.get(8).get(5).clear();
        solve();
*/

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
        } */

    solve();
    printBoard();
    CellIndex c = findCellWithLeastPossibleSolutionCandidates(new boolean[9][9]);

    System.out.println(c.getRow() + " " + c.getCol());
    System.out.println(possibleSolutions.get(c.getRow()).get(c.getCol()).clone());
    failSafeTechnique(new boolean[boardWithAndHeight][boardWithAndHeight]);
    System.out.println();
    printBoard();


    }

    private static boolean failSafeTechnique(boolean[][] visited) {

        if (visited == null)
            visited = new boolean[boardWithAndHeight][boardWithAndHeight];

        CellIndex currCellWithLeastPossibleSolutionCandidates = findCellWithLeastPossibleSolutionCandidates(visited);
        visited[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = true;

        Stack<Integer> possibleSolutionCandidatesForThisCell = new Stack<>();
        for (int possibleSolutionCandidate : possibleSolutions.get(currCellWithLeastPossibleSolutionCandidates.getRow()).get(currCellWithLeastPossibleSolutionCandidates.getCol()))
            possibleSolutionCandidatesForThisCell.push(possibleSolutionCandidate);

        if (possibleSolutionCandidatesForThisCell.empty()) {
            visited[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = false;
            return false;
        }

        board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = possibleSolutionCandidatesForThisCell.pop();
        ArrayList<CellIndex> cellsRelatedToThisCell = findAllCellsRelatedToThisCell (currCellWithLeastPossibleSolutionCandidates.getRow(), currCellWithLeastPossibleSolutionCandidates.getCol());

        HashMap<Integer, ArrayList<CellIndex>> solutionsFilled = new HashMap<>();
        solutionsFilled.put(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()], new ArrayList<>());
        solutionsFilled.get(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()]).add(currCellWithLeastPossibleSolutionCandidates);

        HashMap<Integer, ArrayList<CellIndex>> solutionCandidnatesRemoved = new HashMap<>();
        solutionCandidnatesRemoved.put(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()], cellsRelatedToThisCell);
        solutionCandidnatesRemoved.get(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()]).add(currCellWithLeastPossibleSolutionCandidates);

        boolean thisSolutionResultedInProblems = trySolve(solutionCandidnatesRemoved, solutionsFilled);

        while (thisSolutionResultedInProblems || (!isBoardSolved() && !failSafeTechnique(visited))) {

            if (possibleSolutionCandidatesForThisCell.empty())
                break;

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

            solutionCandidnatesRemoved.clear();
            solutionsFilled.clear();

            board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = possibleSolutionCandidatesForThisCell.pop();
            cellsRelatedToThisCell = findAllCellsRelatedToThisCell(currCellWithLeastPossibleSolutionCandidates.getRow(), currCellWithLeastPossibleSolutionCandidates.getCol());
            removeSolutionCandidates(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()], cellsRelatedToThisCell);

            solutionsFilled.put(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()], new ArrayList<>());
            solutionsFilled.get(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()]).add(currCellWithLeastPossibleSolutionCandidates);

            solutionCandidnatesRemoved.put(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()], cellsRelatedToThisCell);
            solutionCandidnatesRemoved.get(board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()]).add(currCellWithLeastPossibleSolutionCandidates);

            thisSolutionResultedInProblems = trySolve(solutionCandidnatesRemoved, solutionsFilled);
        }

        System.out.println(currCellWithLeastPossibleSolutionCandidates.getRow() + " " + currCellWithLeastPossibleSolutionCandidates.getCol());

        if (!isBoardSolved() && possibleSolutionCandidatesForThisCell.empty()) {
            visited[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = false;
            return false;
        }

        return isBoardSolved();
    }

    private static boolean trySolve(HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean thisSolutionResultedInProblems;

        HashMap<Integer, ArrayList<CellIndex>> trivialImpossibleSolutionCandidates = findTrivialImpossibleSolutionCandidates();
        removeSolutiomCandidates(trivialImpossibleSolutionCandidates);
        addAll(solutionCandidatesRemoved, trivialImpossibleSolutionCandidates);

        thisSolutionResultedInProblems = findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells(solutionCandidatesRemoved, solutionsFilled)
        ||isTheBoardErronous();

        if (!thisSolutionResultedInProblems) {

            HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell();

            while (!solutionsWhichCanOnlyBeFilledInOneCell.isEmpty()) {

                thisSolutionResultedInProblems = fillInSolutionCandidatesWhichCanOnlyBeFilledInOneCell(solutionsWhichCanOnlyBeFilledInOneCell, solutionCandidatesRemoved, solutionsFilled)
                || isTheBoardErronous();

                if (thisSolutionResultedInProblems)
                    break;

                thisSolutionResultedInProblems = findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (solutionCandidatesRemoved, solutionsFilled)
            || isTheBoardErronous();

                if (thisSolutionResultedInProblems)
                    break;

                solutionsWhichCanOnlyBeFilledInOneCell = findSolutionsWhichCanOnlyBeFilledInOneCell();
            }
        }

        return thisSolutionResultedInProblems;
    }


    private static boolean fillInSolutionCandidatesWhichCanOnlyBeFilledInOneCell (HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell, HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean didAnyProblemsOccur = false;

        for (int solution : solutionsWhichCanOnlyBeFilledInOneCell.keySet()) {

            ArrayList<CellIndex> cellIndicesForThisSolutiion = solutionsWhichCanOnlyBeFilledInOneCell.get(solution);
            for (CellIndex cellIndex : cellIndicesForThisSolutiion) {

                if (cellIndex.getRow() == -1 || cellIndex.getCol() == -1) {
                    didAnyProblemsOccur = true;
                    break;
                }

                board[cellIndex.getRow()][cellIndex.getCol()] = solution;

                possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

                ArrayList<CellIndex> cellsRelatedToThisCell = findAllCellsRelatedToThisCell(cellIndex.getRow(), cellIndex.getCol());
                removeSolutionCandidates(board[cellIndex.getRow()][cellIndex.getCol()], cellsRelatedToThisCell);

                if (!solutionCandidatesRemoved.containsKey(board[cellIndex.getRow()][cellIndex.getCol()]))
                    solutionCandidatesRemoved.put(board[cellIndex.getRow()][cellIndex.getCol()], new ArrayList<>());
                solutionCandidatesRemoved.get(board[cellIndex.getRow()][cellIndex.getCol()]).add(cellIndex);
                solutionCandidatesRemoved.get(board[cellIndex.getRow()][cellIndex.getCol()]).addAll(cellsRelatedToThisCell);
            }

            if (didAnyProblemsOccur)
                break;

            if (!solutionsFilled.containsKey(solution))
                solutionsFilled.put(solution, new ArrayList<>());
            solutionsFilled.get(solution).addAll(cellIndicesForThisSolutiion);
        }

        return didAnyProblemsOccur;
    }

    private static boolean findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean didAnyProblemsOccur = false;

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution();

        while (!cellIndicesWithOnlyOnePossibleSolution.isEmpty()) {

            didAnyProblemsOccur = fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (cellIndicesWithOnlyOnePossibleSolution, solutionCandidatesRemoved, solutionsFilled);
            if (didAnyProblemsOccur)
                break;

            // some possible solutions could have been removed from various unfilled cells, which
            // may leave some cells with only one solution, so that we can fill them in in the next loop
            cellIndicesWithOnlyOnePossibleSolution = findAllCellsWhichHaveOnlyOnePossibleSolution();
        }

        return didAnyProblemsOccur;

    }

    private static boolean fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution, HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean didAnyProblemsOccur = false;

        for (CellIndex cellIndex : cellIndicesWithOnlyOnePossibleSolution) {

            if (possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).isEmpty()) {
                didAnyProblemsOccur = true;
                break;
            }

            // this cell only has one possible solution, so we set the cell
            // with the first number in its possible solutions list (since it's the only solution)
            board[cellIndex.getRow()][cellIndex.getCol()] = possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).iterator().next();

            if (!solutionsFilled.containsKey(board[cellIndex.getRow()][cellIndex.getCol()]))
                solutionsFilled.put(board[cellIndex.getRow()][cellIndex.getCol()], new ArrayList<>());
            solutionsFilled.get(board[cellIndex.getRow()][cellIndex.getCol()]).add(cellIndex);

            // clear this cells possible solutions list, it's no longer needed
            possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

            // find cells related to this cell
            ArrayList<CellIndex> cellsRelatedToThisCell = findAllCellsRelatedToThisCell(cellIndex.getRow(), cellIndex.getCol());

            // remove this cells solution as a solution candidate to all related cells
            removeSolutionCandidates(board[cellIndex.getRow()][cellIndex.getCol()], cellsRelatedToThisCell);

            if (!solutionCandidatesRemoved.containsKey(board[cellIndex.getRow()][cellIndex.getCol()]))
                solutionCandidatesRemoved.put(board[cellIndex.getRow()][cellIndex.getCol()], new ArrayList<>());
            solutionCandidatesRemoved.get(board[cellIndex.getRow()][cellIndex.getCol()]).add(cellIndex);
            solutionCandidatesRemoved.get(board[cellIndex.getRow()][cellIndex.getCol()]).addAll(cellsRelatedToThisCell);

        }

        return didAnyProblemsOccur;
    }

    private static void removeSolutiomCandidates (HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesToBeRemoved) {

        for (int solutionCandidate : solutionCandidatesToBeRemoved.keySet()) {
            ArrayList<CellIndex> cellsToHaveThisSolutionCandidateRemoved = solutionCandidatesToBeRemoved.get(solutionCandidate);
            for (CellIndex cell : cellsToHaveThisSolutionCandidateRemoved)
                possibleSolutions.get(cell.getRow()).get(cell.getCol()).remove(solutionCandidate);
        }
    }

    private static void removeSolutionCandidates (int solutionCandidate, ArrayList<CellIndex> cellsToRemoveSolutionCandidate) {
        for (CellIndex cellIndex : cellsToRemoveSolutionCandidate)
            possibleSolutions.get(cellIndex.getRow()).get(cellIndex.getCol()).remove(solutionCandidate);
    }

    private static void addAll (HashMap<Integer, ArrayList<CellIndex>> mainMap, HashMap<Integer, ArrayList<CellIndex>> subMap) {

        for (int key : subMap.keySet()) {
            if (!mainMap.containsKey(key))
                mainMap.put(key, new ArrayList<>());
            mainMap.get(key).addAll(subMap.get(key));
        }
    }

    private static HashMap<Integer, ArrayList<CellIndex>> findTrivialImpossibleSolutionCandidates() {

        HashMap<Integer, ArrayList<CellIndex>> trivialImpossibleSolutionCandidates = new HashMap<>();

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] != -1) {
                    ArrayList<CellIndex> cellsRelatedToThisCell = findAllCellsRelatedToThisCell(row, col);
                    for (CellIndex cell : cellsRelatedToThisCell) {
                        if (possibleSolutions.get(cell.getRow()).get(cell.getCol()).contains(board[row][col])) {
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

    private static ArrayList<CellIndex> findAllCellsRelatedToThisCell (int cellRow, int cellCol) {

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
        SubRange thisBlockRange = getSubRange(cellRow, cellCol);
        for (int r = thisBlockRange.getStartRow(); r < thisBlockRange.getEndRow(); r++) {
            for (int c = thisBlockRange.getStartCol(); c < thisBlockRange.getEndCol(); c++) {
                if (r != cellRow || c != cellCol)
                    cellsRelatedToThisCell.add(new CellIndex(r, c));
            }
        }

        return cellsRelatedToThisCell;
    }

    private static boolean isTheBoardErronous() {

        boolean isTheBoardErronous = false;

        for (int row = 0; row < boardWithAndHeight; row++) {

            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).isEmpty()) {
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
                isTheBoardErronous = isThisPortionOfTheBoardErronous(currRowRange);

                if (isTheBoardErronous)
                    break;
            }

            if (!isTheBoardErronous) {

                // check if cols are erronous
                for (int col = 0; col < boardWithAndHeight; col++) {
                    SubRange currColumnRange = new SubRange(0, boardWithAndHeight - 1, col, col);
                    isTheBoardErronous = isThisPortionOfTheBoardErronous(currColumnRange);

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

                            if (isThisPortionOfTheBoardErronous(currBlockRange)) {
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

    private static boolean isThisPortionOfTheBoardErronous (SubRange subRange) {

        boolean isThisPortionOfTheBoardErronous = false;

        int currSolution = 1;
        while (currSolution < 10 && !isThisPortionOfTheBoardErronous) {

            boolean thisSolutionExistsAsAFilledASolutionOrPossibleCandidateInThisPortion = false;

            for (int row = subRange.getStartRow(); row <= subRange.getEndRow(); row++) {

                for (int col = subRange.getStartCol(); col <= subRange.getEndCol(); col++) {
                    if (board[row][col] == currSolution || possibleSolutions.get(row).get(col).contains(currSolution)) {
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

    private static CellIndex findCellWithLeastPossibleSolutionCandidates(boolean[][] visited) {

        CellIndex cellWithLeastPosibleSolutiomCandidates = new CellIndex(-1, -1);
        int minPossibleSolutionCandidates = Integer.MAX_VALUE;

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] == -1 && !visited[row][col] && possibleSolutions.get(row).get(col).size() < minPossibleSolutionCandidates) {

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
            for (int r = thisBlockRange.getStartRow(); r < thisBlockRange.getEndRow(); r++) {
                for (int c = thisBlockRange.getStartCol(); c < thisBlockRange.getEndCol(); c++) {
                    if (board[r][c] == -1)
                        possibleSolutions.get(r).get(c).remove(board[cellRow][cellCol]);
                }
            }

        }
    }

    private static ArrayList<CellIndex> findAllCellsWhichHaveOnlyOnePossibleSolution() {

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolutiom = new ArrayList<>();

        for (int row = 0; row < boardWithAndHeight; row++)
            for (int col = 0; col < boardWithAndHeight; col++)
                if (board[row][col] == -1 && possibleSolutions.get(row).get(col).size() == 1) {
                    cellIndicesWithOnlyOnePossibleSolutiom.add(new CellIndex(row, col));}

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

    private static int roundDown (int num, int multipleBase) {
        return (int) Math.floor((double) num/multipleBase) * multipleBase;
    }

    private static int roundUp (int num, int multipleBase) {
        return (int) Math.ceil((double) num/multipleBase) * multipleBase;
    }

}

