public class CellIndex {

    private int row = -1, col = -1;

    CellIndex(int r, int c) {
        row = r;
        col = c;
    }

    CellIndex() {}

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
