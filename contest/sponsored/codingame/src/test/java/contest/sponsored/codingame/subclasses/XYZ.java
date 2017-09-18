package contest.sponsored.codingame.subclasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class XYZ {
	
	private static enum Action {
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
	
	private static class Board {
		public static final String UNKNOWN = "X";
		public static final String WALL = "#";
		public static final String EMPTY = "_";

		private int height;
		private int width;
		private String[][] content;

		public Board(int height, int width) {
			this.height = height;
			this.width = width;
			content = new String[height][width];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					content[y][x] = UNKNOWN;
				}
			}
		}

		/**
		 * CLONE
		 */
		protected Board(Board board) {
			this.height = board.height;
			this.width = board.width;
			this.content = new String[height][width];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					this.content[y][x] = board.content[y][x];
				}
			}
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}

		public String getContent(int x, int y) {
			return content[y][x];
		}

		public String setContent(int x, int y, String content) {
			return this.content[y][x] = content;
		}

		public String setContent(Coord coord, String content) {
			return this.content[coord.getY()][coord.getX()] = content;
		}

		public int toreX(int x) {
			if (x >= width) {
				return 0;
			}
			if (x < 0) {
				return width - 1;
			}
			// if (x < 0) {
			// return x + width;
			// }
			// if (x >= width) {
			// return x - width;
			// }
			return x;
		}

		public int toreY(int y) {
			if (y >= height) {
				return 0;
			}
			if (y < 0) {
				return height - 1;
			}
			// if (y < 0) {
			// return y + height;
			// }
			// if (y >= height) {
			// return y - height;
			// }
			return y;
		}

		public void render() {
			// affichage du board avec le player et les others
			for (int y = 0; y < height; y++) {
				System.err.println(String.join("", content[y]));
			}
		}
		
		@Override
		public Board clone() {
			Board clonedBoard = new Board(this);
			return clonedBoard;
		}
	}

	private static class Constants {
	    public static final String PLAYER = "M";
	    public static final String OTHER = "C";
	}

	private static class Coord {
		private int x;
		private int y;

		public Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * CLONE
		 */
		public Coord(Coord coord) {
			this(coord.x, coord.y);
		}

		@Override
		public String toString() {
			return "X:" + x + "|Y:" + y;
		}

		public void update(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			Coord other = (Coord) obj;
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			return true;
		}
	}

	private static enum Direction {
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

	private static class DirectionWithNextStep {
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

	private static class OtherPawn extends Pawn {

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

	private static abstract class Pawn extends Coord {
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

	private static class PlayerDeadException extends Exception {
		private static final long serialVersionUID = 2213941083249856583L;
	}

	private static class PlayerPawn extends Pawn {

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

	private static class PlayerWithMemory extends PlayerPawn {
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

	private static class Utils {
	    
	    public static int random(int min, int max) {
	        return (int) (Math.random() * (max - min + 1)) + min;
	    }
	    
		public static boolean canDo(Pawn pawn, Action action, Board board) {
	    	int destinactionY = board.toreY(pawn.getY() + action.getDirection().getY());
			int destinationX = board.toreX(pawn.getX() + action.getDirection().getX());
			String destinationContent = board.getContent(destinationX, destinactionY);
			if (pawn.isPlayer()) {
				// un player ne peut franchir que les cellule vide ou inconnue
				return Board.EMPTY.equals(destinationContent) || Board.UNKNOWN.equals(destinationContent);
			} else {
				// un other ne peut franchir que les cellule vide ou inconnue mais les players en plus
				return Board.EMPTY.equals(destinationContent) || Board.UNKNOWN.equals(destinationContent) || Constants.PLAYER.equals(destinationContent);
			}
	    }

		/**
		 * be careful this method modify the pawn coords.
		 * @param pawn
		 * @param action
		 * @param board
		 */
	    public static void apply(Pawn pawn, Action action, Board board) {
	    	int destinactionY = board.toreY(pawn.getY() + action.getDirection().getY());
			int destinationX = board.toreX(pawn.getX() + action.getDirection().getX());
			pawn.update(destinationX, destinactionY);
			if (pawn instanceof PlayerWithMemory) {
				((PlayerWithMemory)pawn).setDirection(action.getDirection());
			}
	    }
	    
	    // permet de trouver une action utile en fonction des coordonnées du pawn
	    // une action utile est une action qui une fois appliquée ne débouche pas sur un Board.WALL
	    public static Action choseRandomRelevantAction(Pawn pawn, Board board) {
	    	while (Boolean.TRUE) {
	    		Action action = Action.random();
	            Direction direction = action.getDirection();
	            int watchOutX = board.toreX(pawn.getX() + direction.getX());
	            int watchOutY = board.toreY(pawn.getY() + direction.getY());
	            if (!Board.WALL.equals(board.getContent(watchOutX, watchOutY))) {
	            	return action;
	            }
	    	}
	    	return Action.WAIT;
	    }
	    
	    // nombre minimum de mouvements que doit effectuer un Pawn pour arriver à la target
	    public static int minimumMoveToGoToTarget(Board board, Pawn pawn, Coord target) {
	    	ArrayList<Pawn> pawns = new ArrayList<Pawn>();
	    	pawns.add(pawn);
			return minimumMoveToGoToTarget(board, pawns, target);
	    }
	    
	    // nombre minimum de mouvements que doit effectuer un des Pawns pour arriver à la target (tous Pawns confondus)
	    // la target ne bouge pas
	    public static int minimumMoveToGoToTarget(Board board, List<Pawn> pawns, Coord target) {
	    	Board clonedBoard = new Board(board);
	    	boolean targetReached = false;
	    	int nbMoves = 0;
	    	while (!targetReached) {
	    		// un tour
	    		// on fait bouger les pawns
		    	List<Pawn> newPawns = new ArrayList<Pawn>();
		    	for (Pawn pawn : pawns) {
		        	clonedBoard.setContent(pawn, Constants.OTHER);
		        	if (canDo(pawn, Action.GO_UP, clonedBoard)) {
		    			Pawn newPawn = pawn.clone();
		    			apply(newPawn, Action.GO_UP, clonedBoard);
		    			clonedBoard.setContent(newPawn, Constants.OTHER);
		    			newPawns.add(newPawn);
		    			if (newPawn.equals(target)) {
		    				targetReached = Boolean.TRUE;
		    			}
		    		}

		        	if (canDo(pawn, Action.GO_DOWN, clonedBoard)) {
		    			Pawn newPawn = pawn.clone();
		    			apply(newPawn, Action.GO_DOWN, clonedBoard);
		    			clonedBoard.setContent(newPawn, Constants.OTHER);
		    			newPawns.add(newPawn);
		    			if (newPawn.equals(target)) {
		    				targetReached = Boolean.TRUE;
		    			}
		    		}

		        	if (canDo(pawn, Action.GO_RIGHT, clonedBoard)) {
		        		Pawn newPawn = pawn.clone();
		        		apply(newPawn, Action.GO_RIGHT, clonedBoard);
		        		clonedBoard.setContent(newPawn, Constants.OTHER);
		        		newPawns.add(newPawn);
		        		if (newPawn.equals(target)) {
		        			targetReached = Boolean.TRUE;
		        		}
		        	}
		        	
		        	if (canDo(pawn, Action.GO_LEFT, clonedBoard)) {
		        		Pawn newPawn = pawn.clone();
		        		apply(newPawn, Action.GO_LEFT, clonedBoard);
		        		clonedBoard.setContent(newPawn, Constants.OTHER);
		        		newPawns.add(newPawn);
		        		if (newPawn.equals(target)) {
		        			targetReached = Boolean.TRUE;
		        		}
		        	}
		    	}
		    	pawns = newPawns;
		    	nbMoves++;
//		    	clonedBoard.render();
//		    	System.err.println("==============================================================");
	    	}
	    	return nbMoves;
	    }
	}
}
