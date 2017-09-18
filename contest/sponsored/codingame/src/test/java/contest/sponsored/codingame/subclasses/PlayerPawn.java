package contest.sponsored.codingame.subclasses;

public class PlayerPawn extends Pawn {

	public PlayerPawn() {
        super(0, 0);
    }
    public PlayerPawn(int x, int y) {
        super(x, y);
    }
    protected PlayerPawn(Pawn pawn) {
        super(pawn);
    }
    
    @Override
	public boolean isPlayer() {
    	return Boolean.TRUE;
	}

	@Override
	public boolean isOther() {
		return Boolean.FALSE;
	}

	@Override
	public PlayerPawn clone() {
		return new PlayerPawn(this);
    }
}
