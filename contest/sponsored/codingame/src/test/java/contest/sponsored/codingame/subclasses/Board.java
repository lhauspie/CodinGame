package contest.sponsored.codingame.subclasses;

public class Board {
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
