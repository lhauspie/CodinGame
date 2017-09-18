package contest.sponsored.codingame;

public class Pawn extends Point {
	Direction direction = Direction.WAIT;

	public Pawn() {
		super(0, 0);
	}

	public Pawn(int x, int y) {
		super(x, y);
	}

	public Pawn(Pawn pawn) {
		super(pawn);
	}

	@Override
	public void update(int x, int y) {
		this.direction = Direction.valueOf(x - this.getX(), y - this.getY());
		super.update(x, y);
	}

	public Direction getDirection() {
		return direction;
	}
}
