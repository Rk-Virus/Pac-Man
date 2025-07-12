import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int width = columnCount * tileSize;
        int height = rowCount * tileSize;

        JFrame frame = new JFrame("PacMan");
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Additional application logic can be added here
        PacMan pacMan = new PacMan();
        frame.add(pacMan);
        frame.pack();
        frame.requestFocus();
        frame.setVisible(true);
    }
}
