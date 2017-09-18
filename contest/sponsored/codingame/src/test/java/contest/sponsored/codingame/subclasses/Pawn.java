package contest.sponsored.codingame.subclasses;

public abstract class Pawn extends Coord {
	private Direction currentDirection;
	
	public Pawn() {
        super(0, 0);
    }
    public Pawn(int x, int y) {
        super(x, y);
    }
    protected Pawn(Pawn pawn) {
        super(pawn);
    }
    
    public void setDirection(Direction direction) {
    	this.currentDirection = direction;
    }
    
    public Direction getCurrentDirection() {
    	return currentDirection == null ? Direction.WAIT : currentDirection;
    }
    
	abstract public boolean isPlayer();

	abstract public boolean isOther();
	
	@Override
	abstract public Pawn clone();
}
