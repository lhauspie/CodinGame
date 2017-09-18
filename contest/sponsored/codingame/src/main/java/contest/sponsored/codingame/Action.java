package contest.sponsored.codingame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Action {
    GO_UP("C", Direction.UP), // decrease me.coordY ==> go up
    GO_DOWN("D", Direction.DOWN), // increase me.coordY ==> go down
    GO_LEFT("E", Direction.LEFT), // decrease me.coordX ==> go left
    GO_RIGHT("A", Direction.RIGHT); // increase me.coordX ==> go right
    //WAIT("B", Direction.WAIT);
	
	private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	
	public static Action random()  {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
    
    private String value;
    private Direction direction;
    
    private Action(String value, Direction direction) {
        this.value = value;
        this.direction = direction;
    }
    
    public String getValue() {
        return value;
    }
    
    public Direction getDirection() {
		return direction;
	}
}
