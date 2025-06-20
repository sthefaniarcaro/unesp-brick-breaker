package main;

public class Phase2 implements Phase {
    @Override
    public BrickManager createBricks(int panelWidth) {
        return new BrickManager(2, 8, panelWidth); // 2 rows, 8 columns
    }
}