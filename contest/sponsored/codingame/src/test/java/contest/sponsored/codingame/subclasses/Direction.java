package contest.sponsored.codingame.subclasses;

public enum Direction {
	UP(0, -1),  // decrease me.coordY ==> go up
	DOWN(0, 1), // increase me.coordY ==> go down
	RIGHT(1, 0), // decrease me.coordX ==> go left
	LEFT(-1, 0), // increase me.coordX ==> go right
	WAIT(0, 0);

	private Coord coord;

	private Direction(int x, int y) {
		this.coord = new Coord(x, y);
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

	public int getX() {
		return coord.getX();
	}

	public int getY() {
		return coord.getY();
	}
}
