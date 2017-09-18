package contest.sponsored.codingame.subclasses;

public class DirectionWithNextStep {
	private Direction head;
	private Direction right;
	private Direction left;
	private Direction back;
	
	public DirectionWithNextStep(Direction direction) {
		if (direction.equals(Direction.UP)) {
			head = Direction.UP;
			back = Direction.DOWN;
			left = Direction.LEFT;
			right = Direction.RIGHT;
		} else if (direction.equals(Direction.DOWN)) {
			head = Direction.DOWN;
			back = Direction.UP;
			left = Direction.RIGHT;
			right = Direction.LEFT;
		} else if (direction.equals(Direction.RIGHT)) {
			head = Direction.RIGHT;
			back = Direction.LEFT;
			left = Direction.UP;
			right = Direction.DOWN;
		} else if (direction.equals(Direction.LEFT)) {
			head = Direction.LEFT;
			back = Direction.RIGHT;
			left = Direction.DOWN;
			right = Direction.UP;
		} else {
			// si le pawn est en train d'attendre alors on estime qu'il n'ira null par
			head = Direction.WAIT;
			back = Direction.WAIT;
			left = Direction.WAIT;
			right = Direction.WAIT;
		}
	}
	
	public DirectionWithNextStep(int x, int y) {
		this(Direction.valueOf(x, y));
	}
	public Direction getHead() {
		return head;
	}
	public Direction getRight() {
		return right;
	}
	public Direction getLeft() {
		return left;
	}
	public Direction getBack() {
		return back;
	}
}
