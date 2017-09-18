package contest.sponsored.codingame.subclasses;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum Action {
	GO_UP("C", Direction.UP), // decrease me.coordY ==> go up
	GO_DOWN("D", Direction.DOWN), // increase me.coordY ==> go down
	GO_LEFT("E", Direction.LEFT), // decrease me.coordX ==> go left
	GO_RIGHT("A", Direction.RIGHT), // increase me.coordX ==> go right
	WAIT("B", Direction.WAIT);

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

	private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
    private static final Map<Direction, Action> reverseMap = new HashMap<Direction, Action>();
    static {
    	reverseMap.put(Direction.UP, Action.GO_UP);
    	reverseMap.put(Direction.DOWN, Action.GO_DOWN);
    	reverseMap.put(Direction.RIGHT, Action.GO_RIGHT);
    	reverseMap.put(Direction.LEFT, Action.GO_LEFT);
    	reverseMap.put(Direction.WAIT, Action.WAIT);
    }

	public static Action random() {
		return VALUES.get(RANDOM.nextInt(SIZE));
	}
	
	public static Action valueOf(Direction direction) {
		return reverseMap.get(direction);
	}
}
