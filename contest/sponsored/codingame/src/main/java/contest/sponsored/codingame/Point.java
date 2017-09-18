package contest.sponsored.codingame;

public class Point {
	private int x;
	private int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * CLONE
	 */
	public Point(Point coord) {
		this(coord.x, coord.y);
	}

	public void update(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "X:" + x + "|Y:" + y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
