import java.awt.EventQueue;
import javax.swing.UIManager;

public class SudokuRun implements Runnable {

    public void run() {

        SudokuImplementation sudokuImplementation = new MySudokuSolver();
        SudokuView sudokuView = new SudokuView();
        sudokuView.setSudokuImplementation(sudokuImplementation);
        sudokuView.setVisible(true);
    }

    public static void main(String args[]) {
        tryToSetSystemLookAndFeel();
        EventQueue.invokeLater(new SudokuRun());
    }

    private static void tryToSetSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Couldn't set LAF");
        }
    }
}
