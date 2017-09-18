package contest.sponsored.codingame;

public class Direction extends Point {
    public static final Direction UP = new Direction(0, -1);
    public static final Direction DOWN = new Direction(0, 1);
    public static final Direction LEFT  = new Direction(-1, 0);
    public static final Direction RIGHT  = new Direction(1, 0);
    public static final Direction WAIT  = new Direction(0, 0);
    
    private Direction(int x, int y) {
        super(x, y);
    }

	public static Direction valueOf(int x, int y) {
		Direction direction = null;
		if (x == 0 && y == -1) {
			direction = UP;
		}
		if (x == 0 && y == 1) {
			direction = DOWN;
		}
		if (x == 1 && y == 0) {
			direction = RIGHT;
		}
		if (x == -1 && y == 0) {
			direction = LEFT;
		}
		if (x == 0 && y == 0) {
			direction = WAIT;
		}
		return direction;
	}
}
