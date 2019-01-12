import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class TrialAndErrorTechniques {

    private static final int boardWithAndHeight = 9;
    private final int[][] board;
    private final ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates;

    TrialAndErrorTechniques (int[][] board, ArrayList<ArrayList<HashSet<Integer>>> possibleSolutionCandidates) {
        this.board = board;
        this.possibleSolutionCandidates = possibleSolutionCandidates;
    }

    public int[][] getBoard() {
        return board;
    }

    public ArrayList<ArrayList<HashSet<Integer>>> getPossibleSolutionCandidates() {
        return possibleSolutionCandidates;
    }

    public boolean smartDFS(boolean[][] cellsFilled) {

        boolean isTheBoardErroneous = false;

        if (cellsFilled == null)
            cellsFilled = new boolean[boardWithAndHeight][boardWithAndHeight];

        CellIndex currCellWithLeastPossibleSolutionCandidates = findCellWithLeastPossibleSolutionCandidates(cellsFilled);

        Stack<Integer> possibleSolutionCandidatesForThisCell = new Stack<>();
        for (int possibleSolutionCandidate : possibleSolutionCandidates.get(currCellWithLeastPossibleSolutionCandidates.getRow()).get(currCellWithLeastPossibleSolutionCandidates.getCol()))
            possibleSolutionCandidatesForThisCell.push(possibleSolutionCandidate);

        if (possibleSolutionCandidatesForThisCell.empty())
            isTheBoardErroneous = true;

        if (!isTheBoardErroneous) {

            int currSolutionCandidateGuessed = possibleSolutionCandidatesForThisCell.pop();

            board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = currSolutionCandidateGuessed;
            cellsFilled[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = true;
            HashMap<Integer, ArrayList<CellIndex>> solutionsFilledAsAResultOfThisGuess = new HashMap<>();
            solutionsFilledAsAResultOfThisGuess.put(currSolutionCandidateGuessed, new ArrayList<>());
            solutionsFilledAsAResultOfThisGuess.get(currSolutionCandidateGuessed).add(currCellWithLeastPossibleSolutionCandidates);

            ArrayList<CellIndex> cellsRelatedToThisCell = GeneralHelpers.findAllCellsRelatedToThisCell (currCellWithLeastPossibleSolutionCandidates.getRow(), currCellWithLeastPossibleSolutionCandidates.getCol());
            GeneralHelpers.removeSolutionCandidates(currSolutionCandidateGuessed, cellsRelatedToThisCell, possibleSolutionCandidates);
            HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemovedAsAResultOfThisGuess = new HashMap<>();
            solutionCandidatesRemovedAsAResultOfThisGuess.put(currSolutionCandidateGuessed, cellsRelatedToThisCell);
            solutionCandidatesRemovedAsAResultOfThisGuess.get(currSolutionCandidateGuessed).add(currCellWithLeastPossibleSolutionCandidates);

            boolean didThisGuessImmediatelyResultInAnErroneousBoard = trySolveTheBoardAfterGuessMade (solutionCandidatesRemovedAsAResultOfThisGuess, solutionsFilledAsAResultOfThisGuess);
            boolean didThisGuessEventuallyResultInAnErroneousBoard;

            while (didThisGuessImmediatelyResultInAnErroneousBoard
                    || (!GeneralHelpers.isBoardSolved(board) && (didThisGuessEventuallyResultInAnErroneousBoard = !smartDFS(cellsFilled)))) {

                if (possibleSolutionCandidatesForThisCell.empty()) {
                    isTheBoardErroneous = true;
                    break;
                }

                for (int solutionFilled : solutionsFilledAsAResultOfThisGuess.keySet()) {
                    ArrayList<CellIndex> cellsFilledWithThisSolution = solutionsFilledAsAResultOfThisGuess.get(solutionFilled);
                    for (CellIndex cellIndex : cellsFilledWithThisSolution)
                        board[cellIndex.getRow()][cellIndex.getCol()] = -1;
                }

                for (int solutionCandidateRemoved : solutionCandidatesRemovedAsAResultOfThisGuess.keySet()) {
                    ArrayList<CellIndex> cellsThatHadThisSolutionRemoved = solutionCandidatesRemovedAsAResultOfThisGuess.get(solutionCandidateRemoved);
                    for (CellIndex cellIndex : cellsThatHadThisSolutionRemoved)
                        possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).add(solutionCandidateRemoved);
                }

                solutionCandidatesRemovedAsAResultOfThisGuess.clear();
                solutionsFilledAsAResultOfThisGuess.clear();

                currSolutionCandidateGuessed = possibleSolutionCandidatesForThisCell.pop();
                board[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = currSolutionCandidateGuessed;
                solutionsFilledAsAResultOfThisGuess.put(currSolutionCandidateGuessed, new ArrayList<>());
                solutionsFilledAsAResultOfThisGuess.get(currSolutionCandidateGuessed).add(currCellWithLeastPossibleSolutionCandidates);

                cellsRelatedToThisCell = GeneralHelpers.findAllCellsRelatedToThisCell (currCellWithLeastPossibleSolutionCandidates.getRow(), currCellWithLeastPossibleSolutionCandidates.getCol());
                GeneralHelpers.removeSolutionCandidates(currSolutionCandidateGuessed, cellsRelatedToThisCell, possibleSolutionCandidates);
                solutionCandidatesRemovedAsAResultOfThisGuess.put(currSolutionCandidateGuessed, cellsRelatedToThisCell);
                solutionCandidatesRemovedAsAResultOfThisGuess.get(currSolutionCandidateGuessed).add(currCellWithLeastPossibleSolutionCandidates);

                didThisGuessImmediatelyResultInAnErroneousBoard = trySolveTheBoardAfterGuessMade (solutionCandidatesRemovedAsAResultOfThisGuess, solutionsFilledAsAResultOfThisGuess);
            }

        }

        if (isTheBoardErroneous) {
            cellsFilled[currCellWithLeastPossibleSolutionCandidates.getRow()][currCellWithLeastPossibleSolutionCandidates.getCol()] = false;
            return false;
        }

        return GeneralHelpers.isBoardSolved(board);
    }

    public CellIndex findCellWithLeastPossibleSolutionCandidates(boolean[][] visited) {

        CellIndex cellWithLeastPosibleSolutiomCandidates = new CellIndex(-1, -1);
        int minPossibleSolutionCandidates = Integer.MAX_VALUE;

        for (int row = 0; row < boardWithAndHeight; row++) {
            for (int col = 0; col < boardWithAndHeight; col++) {
                if (board[row][col] == -1 && !visited[row][col] && possibleSolutionCandidates.get(row).get(col).size() < minPossibleSolutionCandidates) {

                    cellWithLeastPosibleSolutiomCandidates.setRow(row);
                    cellWithLeastPosibleSolutiomCandidates.setCol(col);

                    minPossibleSolutionCandidates = possibleSolutionCandidates.get(row).get(col).size();
                }
            }
        }

        return cellWithLeastPosibleSolutiomCandidates;
    }

    public boolean trySolveTheBoardAfterGuessMade (HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean theGuessDidntImmediatelyResultInAnErronousBoard;

        ExactTechniques exactTechniques = new ExactTechniques(board, possibleSolutionCandidates);

        HashMap<Integer, ArrayList<CellIndex>> trivialImpossibleSolutionCandidates = exactTechniques.findTrivialImpossibleSolutionCandidates();
        GeneralHelpers.removeSolutiomCandidates(possibleSolutionCandidates, trivialImpossibleSolutionCandidates);
        GeneralHelpers.addAll(solutionCandidatesRemoved, trivialImpossibleSolutionCandidates);

        theGuessDidntImmediatelyResultInAnErronousBoard = findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells(solutionCandidatesRemoved, solutionsFilled);
        //  && !isTheBoardErronous();

        if (theGuessDidntImmediatelyResultInAnErronousBoard) {

            exactTechniques.setBoard(board);
            exactTechniques.setPossibleSolutionCandidates(possibleSolutionCandidates);

            HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell = exactTechniques.findSolutionsWhichCanOnlyBeFilledInOneCell();

            while (!solutionsWhichCanOnlyBeFilledInOneCell.isEmpty()) {

                theGuessDidntImmediatelyResultInAnErronousBoard = fillInSolutionCandidatesWhichCanOnlyBeFilledInOneCell(solutionsWhichCanOnlyBeFilledInOneCell, solutionCandidatesRemoved, solutionsFilled);
                // && !isTheBoardErronous();

                if (!theGuessDidntImmediatelyResultInAnErronousBoard)
                    break;

                theGuessDidntImmediatelyResultInAnErronousBoard = findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (solutionCandidatesRemoved, solutionsFilled);
                // && !isTheBoardErronous();


                if (!theGuessDidntImmediatelyResultInAnErronousBoard)
                    break;

                exactTechniques.setBoard(board);
                exactTechniques.setPossibleSolutionCandidates(possibleSolutionCandidates);

                solutionsWhichCanOnlyBeFilledInOneCell = exactTechniques.findSolutionsWhichCanOnlyBeFilledInOneCell();
            }
        }

        return theGuessDidntImmediatelyResultInAnErronousBoard;
    }


    public boolean fillInSolutionCandidatesWhichCanOnlyBeFilledInOneCell (HashMap<Integer, ArrayList<CellIndex>> solutionsWhichCanOnlyBeFilledInOneCell, HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean isTheBoardErronous = false;

        for (int solution : solutionsWhichCanOnlyBeFilledInOneCell.keySet()) {

            ArrayList<CellIndex> cellIndicesForThisSolution = solutionsWhichCanOnlyBeFilledInOneCell.get(solution);
            for (CellIndex cellIndex : cellIndicesForThisSolution) {

                if (cellIndex.getRow() == -1 || cellIndex.getCol() == -1) {
                    isTheBoardErronous = true;
                    break;
                }

                board[cellIndex.getRow()][cellIndex.getCol()] = solution;

                possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

                ArrayList<CellIndex> cellsRelatedToThisCell = GeneralHelpers.findAllCellsRelatedToThisCell(cellIndex.getRow(), cellIndex.getCol());
                GeneralHelpers.removeSolutionCandidates(solution, cellsRelatedToThisCell, possibleSolutionCandidates);

                if (!solutionCandidatesRemoved.containsKey(solution))
                    solutionCandidatesRemoved.put(solution, new ArrayList<>());
                solutionCandidatesRemoved.get(solution).add(cellIndex);
                solutionCandidatesRemoved.get(solution).addAll(cellsRelatedToThisCell);
            }

            if (isTheBoardErronous)
                break;

            if (!solutionsFilled.containsKey(solution))
                solutionsFilled.put(solution, new ArrayList<>());
            solutionsFilled.get(solution).addAll(cellIndicesForThisSolution);
        }

        return !isTheBoardErronous;
    }

    public boolean findAndFillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean isTheBoardErroneous = false;

        ExactTechniques exactTechniques = new ExactTechniques(board, possibleSolutionCandidates);

        ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution = exactTechniques.findAllCellsWhichHaveOnlyOnePossibleSolution();

        while (!cellIndicesWithOnlyOnePossibleSolution.isEmpty()) {

            isTheBoardErroneous = !fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (cellIndicesWithOnlyOnePossibleSolution, solutionCandidatesRemoved, solutionsFilled);
            if (isTheBoardErroneous)
                break;

            exactTechniques.setBoard(board);
            exactTechniques.setPossibleSolutionCandidates(possibleSolutionCandidates);

            // some possible solutions could have been removed from various unfilled cells, which
            // may leave some cells with only one solution, so that we can fill them in in the next loop
            cellIndicesWithOnlyOnePossibleSolution = exactTechniques.findAllCellsWhichHaveOnlyOnePossibleSolution();
        }

        return !isTheBoardErroneous;

    }

    public boolean fillInCellsWhichHaveOnlyOnePossibleSolutiomAndRemoveTheseSolutiomValuesFromAllRelationsOfTheseCells (ArrayList<CellIndex> cellIndicesWithOnlyOnePossibleSolution, HashMap<Integer, ArrayList<CellIndex>> solutionCandidatesRemoved, HashMap<Integer, ArrayList<CellIndex>> solutionsFilled) {

        boolean isTheBoardErroneous = false;

        for (CellIndex cellIndex : cellIndicesWithOnlyOnePossibleSolution) {

            if (possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).isEmpty()) {
                isTheBoardErroneous = true;
                break;
            }

            // this cell only has one possible solution, so we set the cell
            // with the first number in its possible solutions list (since it's the only solution)

            int thisCellsSolution = possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).iterator().next();

            board[cellIndex.getRow()][cellIndex.getCol()] = thisCellsSolution;

            if (!solutionsFilled.containsKey(thisCellsSolution))
                solutionsFilled.put(thisCellsSolution, new ArrayList<>());
            solutionsFilled.get(thisCellsSolution).add(cellIndex);

            // clear this cells possible solutions list, it's no longer needed
            possibleSolutionCandidates.get(cellIndex.getRow()).get(cellIndex.getCol()).clear();

            // find cells related to this cell
            ArrayList<CellIndex> cellsRelatedToThisCell = GeneralHelpers.findAllCellsRelatedToThisCell(cellIndex.getRow(), cellIndex.getCol());

            // remove this cells solution as a solution candidate to all related cells
            GeneralHelpers.removeSolutionCandidates(thisCellsSolution, cellsRelatedToThisCell, possibleSolutionCandidates);

            if (!solutionCandidatesRemoved.containsKey(thisCellsSolution))
                solutionCandidatesRemoved.put(thisCellsSolution, new ArrayList<>());
            solutionCandidatesRemoved.get(thisCellsSolution).add(cellIndex);
            solutionCandidatesRemoved.get(thisCellsSolution).addAll(cellsRelatedToThisCell);

        }

        return !isTheBoardErroneous;
    }
}
