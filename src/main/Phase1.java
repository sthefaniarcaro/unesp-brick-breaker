package brickbreaker;

public class Phase1 implements Phase {
    @Override
    public BrickManager createBricks(int panelWidth) {
        return new BrickManager(1, 8, panelWidth); // 1 row, 8 columns
    }
}