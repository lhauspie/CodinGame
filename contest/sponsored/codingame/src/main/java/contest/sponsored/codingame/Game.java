package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private Board board;
	private Pawn player;
	private List<Pawn> others;
	private int nbOthers;
	private int[][] visited;

	public Game(Board board, int nbOthers) {
		this.board = board;
		this.nbOthers = nbOthers;
		this.player = new Pawn();
		this.others = new ArrayList<Pawn>(nbOthers);
		for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
			this.others.add(new Pawn());
		}
		visited = new int[board.getHeight()][board.getWidth()];
		for (int coordY = 0; coordY < board.getHeight(); coordY++) {
			for (int coordX = 0; coordX < board.getWidth(); coordX++) {
				visited[coordY][coordX] = 0;
			}
		}
	}

	/**
	 * CLONE
	 */
	public Game(Game game) {
		this.board = new Board(game.board);
		this.nbOthers = game.nbOthers;
		this.player = new Pawn(game.player);
		this.others = new ArrayList<Pawn>(nbOthers);
		for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
			this.others.add(new Pawn(game.others.get(otherIdx)));
		}
		visited = new int[board.getHeight()][board.getWidth()];
		for (int coordY = 0; coordY < board.getHeight(); coordY++) {
			for (int coordX = 0; coordX < board.getWidth(); coordX++) {
				visited[coordY][coordX] = game.visited[coordY][coordX];
			}
		}
	}

	public Pawn getPlayer() {
		return player;
	}

	public Board getBoard() {
		return board;
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

    	theOther.update(x, y);
        board.setContent(x, y, Board.EMPTY);
	}

	public void updatePlayerPosition(int x, int y) {
		if (player == null) {
			player = new Pawn(x, y);
		} else {
			player.update(x, y);
		}
		board.setContent(x, y, Board.EMPTY);
		visited[y][x]++;
	}

	public void updateContent(String first, String second, String third, String fourth) {
		board.setContent(board.toreX(player.getX() + 1), board.toreY(player.getY()), second); // at right
		board.setContent(board.toreX(player.getX() - 1), board.toreY(player.getY()), fourth); // at left
		board.setContent(board.toreX(player.getX()), board.toreY(player.getY() + 1), third); // at down
		board.setContent(board.toreX(player.getX()), board.toreY(player.getY() - 1), first); // at up
		board.setContent(board.toreX(player.getX()), board.toreY(player.getY()), Board.EMPTY); // at middle
	}

	public void play() throws PlayerDeadException {
		// rien � faire car les others ne bougent pas pour l'instant
		// on fera quelque chose ici quand on pr�dira le comportement des others
		// quelque soit le mouvement, si un other peux atteindre le player en un
		// coup, le player est concid�r� comme dead
		for (Pawn other : others) {
			if (other.getX() + Direction.UP.getX() == player.getX() && other.getY() + Direction.UP.getY() == player.getY()
					|| other.getX() + Direction.DOWN.getX() == player.getX() && other.getY() + Direction.DOWN.getY() == player.getY()
					|| other.getX() + Direction.RIGHT.getX() == player.getX() && other.getY() + Direction.RIGHT.getY() == player.getY()
					|| other.getX() + Direction.LEFT.getX() == player.getX() && other.getY() + Direction.LEFT.getY() == player.getY()) {
				throw new PlayerDeadException();
			}

			other.update(board.toreX(other.getX() + other.getDirection().getX()),
					board.toreY(other.getY() + other.getDirection().getY()));
		}
	}

	public void apply(Action action) throws PlayerDeadException {
		// bouger le player en fonction de l'action;
		// ici on estime que :
		// - si une case est un mur alors elle est infranchissable
		// - si une case est vide ou non d�couverte alors elle est franchissable
		// - si on rencontre un other, on leve une exception car le mouvement
		// n'est pas viable
		Direction direction = action.getDirection();
		int playerDestinationX = board.toreX(player.getX() + direction.getX());
		int playerDestinationY = board.toreY(player.getY() + direction.getY());

		// si on rencontre un other, on leve une exception car le mouvement
		// n'est pas viable
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
		for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
			Point other = others.get(otherIdx);
			contentWithPlayers[other.getY()][other.getX()] = Constants.OTHER;
		}
		// affichage du board avec le player et les others
		for (int y = 0; y < board.getHeight(); y++) {
			System.err.println(String.join("", contentWithPlayers[y]));
		}
	}

	public long score() {
		// si on tombe sur une cellule deja visit�e, on a un moins bon score que
		// si cette cellule n'a jamais �t� visit�e
		return -visited[player.getY()][player.getX()] * 100;
	}

	public List<Pawn> getOthers() {
		return others;
	}
}
