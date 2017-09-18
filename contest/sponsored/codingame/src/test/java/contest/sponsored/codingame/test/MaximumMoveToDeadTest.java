package contest.sponsored.codingame.test;

import java.util.ArrayList;
import java.util.List;

import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.Constants;
import contest.sponsored.codingame.subclasses.Direction;
import contest.sponsored.codingame.subclasses.OtherPawn;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.PlayerWithMemory;

public class MaximumMoveToDeadTest {

	// width : 28
	// height : 35
	private static String[] initial = {
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"X____________XX____________X",
			"X_XXXX_XXXXX_XX_XXXXX_XXXX_X",
			"X_XXXX_XXXXX_XX_XXXXX_XXXX_X",
			"X_XXXX_XXXXX_XX_XXXXX_XXXX_X",
			"X____________XX_______XXXX_X",
			"XXXXXX_XX_XXXXXXXX_XXXXXXX_X",
			"XXXXXX_XX_XXXXXXXX_XXXXXXX_X",
			"XXXXXX_XX____XX____XX______X",
			"XXXXXX_XXXXX_XX_XXXXX_XXXXXX",
			"XXXXXX_XXXXX_XX_XXXXX_XXXXXX",
			"XXXXXX_XXXXX____XXXXX_XXXXXX",
			"XXXXXX_XXXXXXX_XXXXXX_XXXXXX",
			"XXXXXX_XXXX______XXXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"XXXXXX_XXXX___XXCXXXX_XXXXXX",
			"XXXXXX_XXXXXX_XXXXXXX_XXXXXX",
			"XXXXXX_XXXXXX____CXXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"X______XXXXXXXXXXXXXX______X",
			"X_XXXXXXXXXXXXXXXXXXXXXXXX_X",
			"X_XXXXXXXXXXX##_#####_XXXX_X",
			"X___XXXXXXXX__________#X___X",
			"XXX_XXXXXXXXX#####_##_#X_XXX",
			"XXXCXXXXXXXXXXX###_##_##C##X",
			"XXXXXXXXXXXXXX#____##______#",
			"XXXXXXXXXXXXXX#_###XX#####_#",
			"XXXXXXXXXXXXXX#_##########_#",
			"XXXXXXXXXXXXX_M____________#",
			"XXXXXXXXXXXXXX#############X",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX"};
	
//    private static enum Action {
//        GO_UP("C"), // decrease me.coordY ==> go up
//        GO_DOWN("D"), // increase me.coordY ==> go down
//        GO_LEFT("E"), // decrease me.coordX ==> go left
//        GO_RIGHT("A"); // increase me.coordX ==> go right
//        //WAIT("B");
//        
//        private String value;
//        
//        private Action(String value) {
//            this.value = value;
//        }
//        
//        public String getValue() {
//            return value;
//        }
//        
//        private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
//        private static final int SIZE = VALUES.size();
//        private static final Random RANDOM = new Random();
//        
//        public static Action random()  {
//            return VALUES.get(RANDOM.nextInt(SIZE));
//        }
//    }
    
//	private static enum Direction {
//		UP(new Coord(0, -1)),
//		DOWN(new Coord(0, 1)),
//		LEFT(new Coord(-1, 0)),
//		RIGHT(new Coord(1, 0));
//		
//		Coord coord;
//		private Direction(Coord coord) {
//			this.coord = coord;
//		}
//		
//		public Coord getCoord() {
//			return coord;
//		}
//	}
    
//	private static Map<Direction, Action> directionToAction = new HashMap<Direction, Action>();
//	static {
//		directionToAction.put(Direction.UP, Action.GO_UP);
//		directionToAction.put(Direction.DOWN, Action.GO_DOWN);
//		directionToAction.put(Direction.RIGHT, Action.GO_RIGHT);
//		directionToAction.put(Direction.LEFT, Action.GO_LEFT);
//	}
//    private static class Direction extends Coord {
//        public static final Direction UP = new Direction(0, -1);
//        public static final Direction DOWN = new Direction(0, 1);
//        public static final Direction LEFT  = new Direction(-1, 0);
//        public static final Direction RIGHT  = new Direction(1, 0);
//        
//        private Direction(int x, int y) {
//            super(x, y);
//        }
//    }

//    private static class Board {
//        private int height;
//        private int width;
//        private String[][] content;
//        
//        public static final String UNKNOWN = "X";
//        public static final String WALL = "#";
//        public static final String EMPTY = "_";
//        
//        public Board(int height, int width) {
//            this.height = height;
//            this.width = width;
//            content = new String[height][width];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    content[y][x] = UNKNOWN;
//                }
//            }
//        }
//        
//        /**
//         * CLONE
//         */
//        public Board(Board board) {
//            this.height = board.height;
//            this.width = board.width;
//            this.content = new String[height][width];
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    this.content[y][x] = board.content[y][x];
//                }
//            }
//        }
//        
//        public int getHeight() {
//            return height;
//        }
//        
//        public int getWidth() {
//            return width;
//        }
//        
//        public String getContent(int x, int y) {
//            return content[y][x];
//        }
//        
//        public String setContent(int x, int y, String content) {
//            return this.content[y][x] = content;
//        }
//
//        public String setContent(Coord coord, String content) {
//            return this.content[coord.y][coord.x] = content;
//        }
//
//        public int toreX(int x) {
//        	if (x >= width) {
//        		return width - 1;
//        	}
//        	if (x < 0) {
//        		return 0;
//        	}
////            if (x < 0) {
////				return x + width;
////			}
////            if (x >= width) {
////				return x - width;
////			}
//            return x;
//        }
//        
//        public int toreY(int y) {
//        	if (y >= height) {
//        		return height - 1;
//        	}
//        	if (y < 0) {
//        		return 0;
//        	}
////            if (y < 0) {
////				return y + height;
////			}
////            if (y >= height) {
////				return y - height;
////			}
//            return y;
//        }
//        
//        public void render() {
//            // affichage du board avec le player et les others
//            for (int y = 0; y < height; y++) {
//                System.out.println(String.join("", content[y]));
//            }
//        }
//    }
//    
//    private static class Coord {
//        private int x;
//        private int y;
//        
//        public Coord(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        /**
//         * CLONE
//         */
//        public Coord(Coord coord) {
//            this(coord.x, coord.y);
//        }
//
//        @Override
//		public String toString() {
//            return "X:" + x + "|Y:" + y;
//        }
//        
//        public void update(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//        
//        public int getX() {
//            return x;
//        }
//        
//        public int getY() {
//            return y;
//        }
//    }
//    
//    private static class Pawn extends Coord {
//        List<Direction> directions = new ArrayList<Direction>();
//    	public Pawn() {
//            super(0, 0);
//        }
//        public Pawn(int x, int y) {
//            super(x, y);
//        }
//        public Pawn(Pawn pawn) {
//            super(pawn);
//            this.directions = new ArrayList<Direction>(pawn.directions);
//        }
//        public void addDirection(Direction direction) {
//        	directions.add(direction);
//        }
//        public List<Direction> getDirections() {
//        	return directions;
//        }
//    }
//    
//    private static final String EMPTY = "_";
//    private static final String OTHER = "C";
//    private static final String PLAYER = "M";
    
    public static void main(String... args) {
    	Board board = new Board(initial.length, initial[0].length());
    	List<Pawn> others = new ArrayList<Pawn>();
    	List<Pawn> players = new ArrayList<Pawn>();
    	
    	for (int y = 0; y < initial.length; y++) {
    		String initialLine = initial[y];
        	for (int x = 0; x < initialLine.length(); x++) {
        		String boardCell = String.valueOf(initialLine.charAt(x));
        		if (Constants.OTHER.equals(boardCell)) {
        			others.add(new OtherPawn(x, y));
        		} else if (Constants.PLAYER.equals(boardCell)) {
        			players.add(new PlayerWithMemory(x, y));
        		}
        		board.setContent(x, y, boardCell);
        	}
    	}
    	
    	board.render();
    
    	Pawn theBestPlayer = null;
    	while (theBestPlayer == null) {
    		// un tour
    		// on fait bouger les others
	    	List<Pawn> newOthers = new ArrayList<Pawn>();
	    	for (Pawn other : others) {
	    		moveIfYouCan(other, Direction.UP, board, Constants.OTHER, newOthers);
	    		moveIfYouCan(other, Direction.DOWN, board, Constants.OTHER, newOthers);
	    		moveIfYouCan(other, Direction.RIGHT, board, Constants.OTHER, newOthers);
	    		moveIfYouCan(other, Direction.LEFT, board, Constants.OTHER, newOthers);
	    	}
	    	others = newOthers;
	
	    	// on fait bouger les players
	    	List<Pawn> newPlayers = new ArrayList<Pawn>();
	    	for (Pawn player : players) {
	    		moveIfYouCan(player, Direction.UP, board, Constants.PLAYER, newPlayers);
	    		moveIfYouCan(player, Direction.DOWN, board, Constants.PLAYER, newPlayers);
	    		moveIfYouCan(player, Direction.RIGHT, board, Constants.PLAYER, newPlayers);
	    		moveIfYouCan(player, Direction.LEFT, board, Constants.PLAYER, newPlayers);
	    	}
	    	
	    	if (newPlayers.size() == 0) {
	    		// match null entre plusieurs players car on passe de 2 players au moins à 0 directement
	    		// on va donc piocher dans le tour précédent pour trouver le meilleur player
		    	System.out.println("no best player found so we pick up one in the previous turn from " + players.size());
	    		theBestPlayer = players.get(1);
	    	} else if (newPlayers.size() == 1) {
	    		// on a trouvé le meilleur Player
		    	System.out.println("only one best player found");
	    		theBestPlayer = newPlayers.get(0);
	    	} else {
	    		players = newPlayers;
	    	}

	    	if (theBestPlayer != null) {
	    		board.setContent(theBestPlayer, "B");
	    	}
	    	System.out.println("=================================================================");
	    	board.render();
    	}
    	
    	System.out.println("Best player found with this path : " + ((PlayerWithMemory)theBestPlayer).getDirections());
    }
    
    private static void moveIfYouCan(Pawn pawn, Direction direction, Board board, String symbolOnBoard, List<Pawn> newPawns) {
		if (canMoveTo(pawn, direction, board)) {
			Pawn newPawn = pawn.clone();
			moveTo(newPawn, direction, board);
			board.setContent(newPawn, symbolOnBoard);
			newPawns.add(newPawn);
		}
	}

	private static boolean canMoveTo(Pawn pawn, Direction direction, Board board) {
    	int destinactionY = board.toreY(pawn.getY() + direction.getY());
		int destinationX = board.toreX(pawn.getX() + direction.getX());
		String destinationContent = board.getContent(destinationX, destinactionY);
		return Board.EMPTY.equals(destinationContent) || Board.UNKNOWN.equals(destinationContent);
    }

    private static void moveTo(Pawn pawn, Direction direction, Board board) {
    	int destinactionY = board.toreY(pawn.getY() + direction.getY());
		int destinationX = board.toreX(pawn.getX() + direction.getX());
		pawn.update(destinationX, destinactionY);
		pawn.setDirection(direction);
    }
}
