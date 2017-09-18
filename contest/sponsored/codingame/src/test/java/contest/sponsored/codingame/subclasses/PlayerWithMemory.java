package contest.sponsored.codingame.subclasses;

import java.util.ArrayList;
import java.util.List;

public class PlayerWithMemory extends PlayerPawn {
	private List<Direction> directions = new ArrayList<Direction>();
	
	public PlayerWithMemory() {
		super();
	}

	public PlayerWithMemory(int x, int y) {
		super(x, y);
	}

	protected PlayerWithMemory(PlayerWithMemory pawn) {
		super(pawn);
		this.directions = new ArrayList<Direction>(pawn.getDirections());
	}

    public List<Direction> getDirections() {
    	return directions;
    }

    @Override
    public void setDirection(Direction direction) {
    	super.setDirection(direction);
    	directions.add(direction);
    }
    
	@Override
	public PlayerWithMemory clone() {
		PlayerWithMemory clonedPawn = new PlayerWithMemory(this);
    	return clonedPawn;
    }
}
