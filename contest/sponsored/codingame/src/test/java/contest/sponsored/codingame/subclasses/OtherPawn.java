package contest.sponsored.codingame.subclasses;

public class OtherPawn extends Pawn {

	public OtherPawn() {
        super(0, 0);
    }
    public OtherPawn(int x, int y) {
        super(x, y);
    }
    protected OtherPawn(Pawn pawn) {
        super(pawn);
    }
    
    @Override
	public boolean isPlayer() {
		return Boolean.FALSE;
	}

	@Override
	public boolean isOther() {
		return Boolean.TRUE;
	}

	@Override
	public OtherPawn clone() {
		return new OtherPawn(this);
    }
}
