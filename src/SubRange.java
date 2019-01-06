public class SubRange {

    private int startRow = -1, startCol = -1, endRow = -1, endCol = -1;

    SubRange() {}
    SubRange (int startR, int endR, int startC, int endC) {
        startRow = startR;
        endRow = endR;
        startCol = startC;
        endCol = endC;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndCol() {
        return endCol;
    }
}
