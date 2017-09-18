package contest.sponsored.codingame.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class NewGenerationOfGeneticPlayer {

    //   X 0 1 2
    // Y 
    // 0   X # X
    // 2   _ M _
    // 3   X # X
    //
    
    // Données d'initialisation du jeu :
    //    Ligne 1 : hauteur du plateau
    //    Ligne 2 : largeur du plateau
    //    Ligne 3 : le nombre de pions sur le plateau (1 player et n others)
    
    // Données d'un tour de jeu
    //    Ligne 1 : contenu de la cellule du plateau se trouvant en haut du player (# pour un mur / _ pour du vide)
    //    Ligne 2 : contenu de la cellule du plateau se trouvant à droite du player (# pour un mur / _ pour du vide)
    //    Ligne 3 : contenu de la cellule du plateau se trouvant en bas du player (# pour un mur / _ pour du vide)
    //    Ligne 4 : contenu de la cellule du plateau se trouvant à gauche du player (# pour un mur / _ pour du vide)
    //    Ligne 5.n : position en X du pion n, n étant le numéro du pion
    //    Ligne 6.n : position en Y du pion n, n étant le numéro du pion

    // Complément :
    //    le player est le dernier pion de la liste 5/6
    //    les autres pions 5/6 sont des adverseres a éviter
    //    le jeu est fini quand un other touche le player
    //    le plateau est un tore
    
    // Les Actions :
    //    "D" : déplacer le pion player vers le bas (player.y++)
    //    "C" : déplacer le pion player vers le haut (player.y--)
    //    "A" : déplacer le pion player vers la droite (player.x++)
    //    "E" : déplacer le pion player vers la gauche (player.x--)
    //    "B" : Pas encore resolu cette action ==> WAIT ?
    
    
    // Conception :
    //    Je suis un joueur, mon but est d'éviter les adversaires
    //    Je connais la position des adversaires
    //    Les adversaires et moi-meme seront modélisés sous forme de Pion (Pawn)
    //   Un Pion étant un couple (X,Y) de coordonnées (Coord)
    
    // Algo :
    //    Choisir X mouvements
    //    Pour chaque mouvement :
    //      - appliquer le mouvement
    //      - simuler le déplacement des others
    //    Calculer le score en se basant sur :
    //      - la distance parcouru
    //      - nbVisit de la case
    //      - distance du other le plus proche
	
	// AMELIORATION A APPORTER :
	//    Poursuivre le déplacement des others
	//      - en metant à jour les nouvelles coordonnées d'un other, on peut en deduire sa direction (nouvelle position - ancienne position = direction)
	//      - il suffit donc d'appliquer la derniere direction connue
	//      - on peux aussi calculer les probailité de mouvements en fonction de la direction courante et la position du player
	//    Pondérer le score avec la visite de nouvelles cases du board
	//      - si la case a deja été visité, on marquera forcément moins de points que sur un case deja visitée
	//      - on pourrait décrémenter le score de nbVisites * 100 sur chaque déplacement, ca forcerait l'IA a privilégier les chemins inconnus
	//    Prendre en compte la distance entre le player et le other le plus proche
	//      - plus on est proche d'un other plus le player a de chance de le rencontrer
	//    Se rendre compte d'une situation de blocage avant qu'elle n'arrive
	//      - il arrive parfois que le player se trouve dans un couloir cernés par 2 others
	//      - il faudrait réussir à anticiper cette situation de mort certaine
	//    Privilégier le coté avec le moins de others
	//      - si 3 others au dessus de player et 1 other en dessous, il faudrait anticiper un echappée par le bas
    
	private static class Game {
	    private Board board;
	    private PlayerPawn player;
	    private List<Pawn> others;
	    private int[][] visited;
	    
	    public Game(Board board, int nbOthers) {
	        this.board = board;
	        this.player = new PlayerPawn();
	        this.others = new ArrayList<Pawn>();
	        for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
	            this.others.add(new OtherPawn());
	        }
	        visited = new int[board.getHeight()][board.getWidth()];
	        for (int coordY = 0; coordY < board.getHeight(); coordY++) {
	        	for (int coordX = 0; coordX < board.getWidth(); coordX++) {
	        		visited[coordY][coordX] = 0;
	        	}
	        }
	    }
	    
	    protected Game(Game game) {
	        this.board = game.board.clone();
	        this.player = game.player.clone();
	        this.others = new ArrayList<Pawn>();
	        for (Pawn other : game.others) {
	        	this.others.add(other.clone());
	        }
	        for (int otherIdx = 0; otherIdx < game.others.size(); otherIdx++) {
	        }
	        visited = new int[board.getHeight()][board.getWidth()];
	        for (int coordY = 0; coordY < board.getHeight(); coordY++) {
	        	for (int coordX = 0; coordX < board.getWidth(); coordX++) {
	        		visited[coordY][coordX] = game.visited[coordY][coordX];
	        	}
	        }
	    }
	    
	    @Override
		public Game clone() {
	    	Game clonedGame = new Game(this);
	    	return clonedGame;
	    }
	    
	    public void updateOtherPosition(int otherIdx, int x, int y) {
	    	Pawn theOther = others.get(otherIdx);
	        // calculer la direction du other (gestion du Tore)
			int directionX = x - theOther.getX();
	    	if (directionX > 1) {
	    		directionX = directionX - board.getWidth();
	    	}
	    	if (directionX < -1) {
	    		directionX = directionX + board.getWidth();
	    	}
	    	
	    	int directionY = y - theOther.getY();
	    	if (directionY > 1) {
	    		directionY = directionY - board.getHeight();
	    	}
	    	if (directionY < -1) {
	    		directionY = directionY + board.getHeight();
	    	}

	    	theOther.setDirection(Direction.valueOf(directionX, directionY));
	    	theOther.update(x, y);
	        board.setContent(x, y, Board.EMPTY);
	    }
	    
	    public void updatePlayerPosition(int x, int y) {
	        if (player == null) {
	            player = new PlayerPawn(x, y);
	        } else {
	            player.update(x, y);
	        }
	        board.setContent(x, y, Board.EMPTY);
	        visited[y][x]++;
	    }
	    
	    public void updateContent(String first, String second, String third, String fourth) {
	        board.setContent(board.toreX(player.getX()+1), board.toreY(player.getY()), second); // at right
	        board.setContent(board.toreX(player.getX()-1), board.toreY(player.getY()), fourth); // at left
	        board.setContent(board.toreX(player.getX()), board.toreY(player.getY()+1), third); // at down
	        board.setContent(board.toreX(player.getX()), board.toreY(player.getY()-1), first); // at up
	        board.setContent(board.toreX(player.getX()), board.toreY(player.getY()), Board.EMPTY); // at middle
	    }
	    
	    public void play() throws PlayerDeadException {
	        // quelque soit le mouvement, si un other peux atteindre le player en un coup, le player est concidéré comme dead
	        for (Pawn other : others) {
	            if (other.getX() + Direction.UP.getX() == player.getX() && other.getY() + Direction.UP.getY() == player.getY()
	                    || other.getX() + Direction.DOWN.getX() == player.getX() && other.getY() + Direction.DOWN.getY() == player.getY()
	                    || other.getX() + Direction.RIGHT.getX() == player.getX() && other.getY() + Direction.RIGHT.getY() == player.getY()
	                    || other.getX() + Direction.LEFT.getX() == player.getX() && other.getY() + Direction.LEFT.getY() == player.getY()) {
	                throw new PlayerDeadException();
	            }
	            
				other.update(board.toreX(other.getX() + other.getCurrentDirection().getX()), board.toreY(other.getY() + other.getCurrentDirection().getY()));
	        }
	    }
	    
	    public void apply(Action action) throws PlayerDeadException {
	        // bouger le player en fonction de l'action;
	        // ici on estime que :
	        //    - si une case est un mur alors elle est infranchissable
	        //    - si une case est vide ou non découverte alors elle est franchissable
	        //    - si on rencontre un other, on leve une exception car le mouvement n'est pas viable
	        Direction direction = action.getDirection();
	        int playerDestinationX = board.toreX(player.getX() + direction.getX());
	        int playerDestinationY = board.toreY(player.getY() + direction.getY());
	        
	        // si on rencontre un other, on leve une exception car le mouvement n'est pas viable
	        for (Pawn other : others) {
	            if (other.getX() == playerDestinationX && other.getY() == playerDestinationY) {
	                throw new PlayerDeadException();
	            }
	        }
	        
	        if (Board.WALL.equals(board.getContent(playerDestinationX, playerDestinationY))) {
	            // on ne bouge pas le jouer
	        } else {
	            updatePlayerPosition(playerDestinationX, playerDestinationY);
	        }
	    }
	    
	    public void render() {
	    	// initialisation du board
	        String[][] contentWithPlayers = new String[board.getHeight()][board.getWidth()];
	        for (int y = 0; y < board.getHeight(); y++) {
	            for (int x = 0; x < board.getWidth(); x++) {
	                contentWithPlayers[y][x] = board.getContent(x, y);
	            }
	        }
	    	// placement du player et des others
	        contentWithPlayers[player.getY()][player.getX()] = Constants.PLAYER;
	        for (Pawn other : others) {
	        	contentWithPlayers[other.getY()][other.getX()] = Constants.OTHER;
	        }
	        // affichage du board avec le player et les others
	        for (int y = 0; y < board.getHeight(); y++) {
	            System.err.println(String.join("", contentWithPlayers[y]));
	        }
	    }
	    
	    public long score() {
	    	long score = 0;
	    	
	    	// les bonus
	    	// plus on est loin des others, mieux on est
			score += Utils.minimumMoveToGoToTarget(board, others, player) * 100;
			
			// les malus
			// si on tombe sur une cellule deja visitée, on a un moins bon score que si cette cellule n'a jamais été visitée
			score -= visited[player.getY()][player.getX()] * 10;
	    	Utils.minimumMoveToGoToTarget(board, others, player);
	    	
	    	return score;
	    }
	    
		public Board getBoard() {
			return board;
		}
	    
	    public Pawn getPlayer() {
	        return player;
	    }
	    
	    public List<Pawn> getOthers() {
			return others;
		}
}
	
	private static class PlayerDeadException extends Exception {
		private static final long serialVersionUID = 2213941083249856583L;
	}

    private static Game game = null;
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int height = in.nextInt(); // = MAX_Y + 1
        int width = in.nextInt(); // = MAX_X + 1
        int nbPlayers = in.nextInt(); // = nbOthers + 1
        
        Board board = new Board(height, width);
        
        game = new Game(board, nbPlayers-1);
        
        ArtificialIntelligence artificialIntelligence = new ArtificialIntelligence(game, board, 10);
        
        // game loop
        while (true) {
            String firstInput = in.next();
            String secondInput = in.next();
            String thirdInput = in.next();
            String fourthInput = in.next();
            
            for (int i = 0; i < nbPlayers; i++) {
                int coordX = in.nextInt();
                int coordY = in.nextInt();
                if (i < nbPlayers - 1) {
                    game.updateOtherPosition(i, coordX, coordY);
                } else {
                    game.updatePlayerPosition(coordX, coordY);
                }
            }
            
            // on appel updateContent apres les updatePosition car la position des 4 inputString sont fonction de la position du player
            game.updateContent(firstInput, secondInput, thirdInput, fourthInput);
            game.render();

            // ici, on a le game réel le plus à jour possible
            // le board est à jour
            // la position du player est à jour
            // la position des others sont à jour
            // on va pouvoir commencer à s'amuser à faire de la génétique :D
            System.out.println(artificialIntelligence.getNextAction().getValue());
        }
    }
    
    
    // FIXME : ne pas modifier le code ci-dessous
    // FIXME : il faut plutot faire un copier coller de toutes les sous-classes


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
	
//		private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final List<Action> VALUES = new ArrayList<Action>();
		static {
			VALUES.add(GO_UP);
			VALUES.add(GO_DOWN);
			VALUES.add(GO_RIGHT);
			VALUES.add(GO_LEFT);
		}
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





	private static class ArtificialIntelligence {
		private Game game;
		private Board board;
		private long maxDuration;
		private List<Solution> lastBestSolutions = new ArrayList<Solution>();
		
		public ArtificialIntelligence(Game game, Board board, long maxDuration) {
			this.game = game;
			this.board = board;
			this.maxDuration = maxDuration;
		}
		
	    // Genere une population de solutions à tester
	    private List<Solution> generatePopulation(int nbSolutionsToGenerate, int nbActionsPerSolution) {
	        Pawn ghostPlayer = game.getPlayer().clone(); // clone le player du game
	        
	        List<Solution> solutions = new ArrayList<Solution>(nbSolutionsToGenerate);
	        for (int solutionIdx = 0; solutionIdx < nbSolutionsToGenerate; solutionIdx++) {
	            List<Action> actions = new ArrayList<Action>(nbActionsPerSolution);
	            for (int actionIdx = 0; actionIdx < nbActionsPerSolution; actionIdx++) {
	                Action action = Utils.choseRandomRelevantAction(ghostPlayer, board);
	                actions.add(action);
	                Utils.apply(ghostPlayer, action, board);
	            }
	            solutions.add(new Solution(actions, game));
	        }
	        return solutions;
	    }

	    public Action getNextAction() {
	        int nbSolutions = 5;
	        int nbActionsPerSolution = 3;
	        int nbBestSolutions = 1;
	        
	        List<Solution> solutions = generatePopulation(nbSolutions - lastBestSolutions.size(), nbActionsPerSolution);
	        for (Solution lastBestSolution : lastBestSolutions) {
	        	lastBestSolution.removeFirstActionAndAddRandomRelevantLastOne();
	        	solutions.add(lastBestSolution);
	        }
	        
	        long start = System.currentTimeMillis();
	        long duration = 0;
	        int nbMutations = 0;
	        while (duration < maxDuration) {
	        	// on ne veux faire muter que les meilleurs pour voir si on peux encore les améliorer
	            int solutionIdxToMutate = Utils.random(0, Math.min(3, nbSolutions - 1));
	            solutions.add(solutions.get(solutionIdxToMutate).mutate());
	            // sort permet de trier les solutions en fonction de leur score
	            // si le score n'est pas deja calculer alors la methode de calcul du score sera appeler pendant le trie
	            Collections.sort(solutions);
	            // supprimer la derniere solution (c'est la moins performante)
	            solutions.remove(solutions.size() - 1);
	            nbMutations++;
	            duration = System.currentTimeMillis() - start;
	        }
	        
	        
	        // on sauvegarde les 3 meilleurs solutions pour les challenger
	        lastBestSolutions.clear();
	        System.err.println("best solutions after "+ nbMutations +" mutations : ");
	        for (int solutionIdx = 0; solutionIdx < nbBestSolutions; solutionIdx++) {
	        	Solution aSolution = solutions.get(solutionIdx);
	        	System.err.println("Action = " + aSolution.getActions() + " : Score = " + aSolution.getScore());
	        	lastBestSolutions.add(solutions.get(solutionIdx));
	        }
	        
	        return solutions.get(0).getFirstAction();
	    }
}


	private static class Solution implements Comparable<Solution> {
	    private List<Action> actions;
	    private Game originalGame;
	    private Long score = null;
	    
	    public Solution(List<Action> actions, Game game) {
	        this.actions = actions;
	        this.originalGame = game;
	    }
	    
	    private Long score() {
	        // cloner le jeu pour pouvoir en faire ce qu'on veux sans changer l'état actuel
	        Game clonedGame = new Game(originalGame);
	        try {
	            score = 0L;
	            for (Action action : actions) {
	                clonedGame.apply(action);
	                clonedGame.play();
	                score += evaluateOneStep(clonedGame);
	            }
	            score += evaluate(clonedGame);
	        } catch (PlayerDeadException pde) {
	            score = Long.MIN_VALUE;
	        }
	        
	        return score;
	    }
	    
	    public Long getScore() {
	        // si le score n'a pas encore été calculé alors on le calcul
	        return score == null ? score() : score;
	    }
	    
	    /**
	     * Evulation le game est un mix entre :
	     * - distance parcouru
	     * - nombre minimum de mouvements des others avant une rencontre avec le player
	     *      - sous-cas #1 : le player reste immobile pendant le calcul
	     *      - sous-cas #2 : le player se déplace en meme temps que les others
	     * 
	     * @param game
	     * @return
	     */
	    private long evaluate(Game game) {
	        // ici on dispose du jeu de départ et du jeu d'arrivée
	        // ainsi on pourra calculer la distance parcouru par le player par exemple
	    	
	    	// int distanceParcourue = flySmallestdistanceWithToreBoard(originalGame.getPlayer(), game.getPlayer(), game.getBoard());
	    	// FIXME : cette evaluation est completement débile dans la cas ou le pawn n'a pas d'autres choix que de faire peux de distance a vol d'oiseau :
	    	// Cas suivant : M fait une distance 2 a vol d'oiseau mais a fait une distance de 10 en comptant les murs
	    	// M n'aurai pas pu faire mieux dans ce cas
	    	// # # # # # # #
	    	// # . . . . . #
	    	// # # # # # . #
	    	// # M . . . . #
	    	// # # # # # # #
	    	// il faut plutot calcule la distance reel parcouru grace à Utils.minimumMoveToGoToTarget(originalGame.getBoard(), originalGame.getPlayer(), game.getPlayer());

	    	// plus on est loin des others, mieux on est
    		score += Utils.minimumMoveToGoToTarget(originalGame.getBoard(), originalGame.getOthers(), game.getPlayer()) * 100;

	    	// plus on est loin de son point d'origine, mieux on est
    		score += Utils.minimumMoveToGoToTarget(originalGame.getBoard(), originalGame.getPlayer(), game.getPlayer()) * 10000;
    		
	        return score;
	    }
	    
	    private long evaluateOneStep(Game game) {
	        return game.score();
	    }
	    
	    @Override
		public int compareTo(Solution comparedSolution) {
			return comparedSolution.getScore().compareTo(this.getScore());
		}
		
	    private int flySmallestdistanceWithToreBoard(Coord a, Coord b, Board board) {
			// distance sans passer par les bords
			// return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);

	    	// distances sans passer par les bords
			int innerDistanceX = Math.abs(a.getX() - b.getX()); 
			int innerDistanceY = Math.abs(a.getY() - b.getY());

			// distance en passant par les bords
			int outerDistanceX = Math.abs(innerDistanceX - board.getWidth());
			int outerDistanceY = Math.abs(innerDistanceY - board.getHeight());

			int closestX = outerDistanceX;
			if (innerDistanceX < outerDistanceX) {
				closestX = innerDistanceX;
			}

			int closestY = outerDistanceY;
			if (innerDistanceY < outerDistanceY) {
				closestY = innerDistanceY;
			}

			return closestX + closestY;
	    }
	    
	    public List<Action> getActions() {
	        return actions;
	    }
	    
	    public Action getFirstAction() {
	        return actions.get(0);
	    }
	    
	    // AMELIORATION : ne pas prendre une action au hazard > introduire une amplitude (de 100% à 0%) qui décroit au fur et a mesure des mutations
	    // AMELIORATION : l'amplitude serait l'emplacement dans la chaine d'actions
	    // AMELIORATION : la position dans la chaine d'action serait fonction de de l'amplitude
	    // AMELIORATION :    > (size - (size * amplitude)) exemple : 10 - 10*1 = 0 / 10 - 10*0.05 = 10
	    // AMELIORATION : Faire muter toute la chaine d'actions à partir d'une action donnée
//	    public Solution mutate(float amplitude) {
//	    	ArrayList<Action> mutatedActions = new ArrayList<Action>(actions);
//	    	Solution mutatedSolution = new Solution(mutatedActions, originalGame);
//	    	int maxIdx = actions.size() - 1;
//	    	int minIdx = maxIdx - (int)(maxIdx * amplitude);
//	    	int actionIdxToMutate = Utils.random(minIdx, maxIdx);
//	    	// ici on applique les X premieres actions de la solution pour faire arriver le player a la position sur le board qui correspond à l'action à faire muter
//	    	// on peut donc faire un choix pertinant en fonction de l'etat du board au coordonnées calculées (ne pas choisir un mur)
//	    	Pawn ghostPlayer = apply(mutatedSolution, originalGame, actionIdxToMutate);
//	    	for (; actionIdxToMutate < actions.size(); actionIdxToMutate++) {
//	    		// mutation aléatoire mais avec un minimum d'intelligence
//	    		Action relevantAction = Utils.choseRandomRelevantAction(ghostPlayer, originalGame.getBoard());
//	    		Utils.apply(ghostPlayer, relevantAction, originalGame.getBoard());
//	    		mutatedActions.set(actionIdxToMutate, relevantAction);
//	    	}
//	    	return mutatedSolution;
//	    }
	    public Solution mutate() {
	        ArrayList<Action> mutatedActions = new ArrayList<Action>(actions);
	        Solution mutatedSolution = new Solution(mutatedActions, originalGame);
	        int actionIdxToMutate = Utils.random(0, actions.size() - 1);
	        mutatedActions.set(actionIdxToMutate, Action.random());
	        return mutatedSolution;
	    }
	    
	    // supprimer la premiere action
	    // applique la solution en resultant au player du originalGame
	    // choisi l'action une action pertinante au hazard
	    public void removeFirstActionAndAddRandomRelevantLastOne() {
	        score = null; // reset the score because the solution is going to change
	        actions.remove(0);
	        Pawn ghostPlayer = apply(this, originalGame);
	        actions.add(Utils.choseRandomRelevantAction(ghostPlayer, originalGame.getBoard()));
	    }

	    // Permet de calculer la position finale du player du game après application de toute la Solution
	    private static Pawn apply(Solution solution, Game game) {
	    	return apply(solution, game, solution.getActions().size() - 1);
	    }
	    
	    // Permet de calculer la position finale du player du game après application d'une partie de la Solution
	    private static Pawn apply(Solution solution, Game game, int nbFirstActionsToApply) {
	    	Pawn ghostPlayer = game.getPlayer().clone(); // clone le pawn
	        for (int actionIdx = 0; actionIdx <= nbFirstActionsToApply; actionIdx++) {
	    		Utils.apply(ghostPlayer, solution.getActions().get(actionIdx), game.getBoard());
	    	}
	    	return ghostPlayer;
	    }
	}
}