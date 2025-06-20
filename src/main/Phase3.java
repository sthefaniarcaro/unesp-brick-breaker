package brickbreaker;

public class Phase3 implements Phase {
    @Override
    public BrickManager createBricks(int panelWidth) {
        return new BrickManager(3, 8, panelWidth); // 3 rows, 8 columns
    }
}