package contest.sponsored.codingame;

public class Board {
	private int height;
	private int width;
	private String[][] content;

	public static final String UNKNOWN = "X";
	public static final String WALL = "#";
	public static final String EMPTY = "_";

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
	public Board(Board board) {
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

	public int toreX(int x) {
		if (x < 0) {
			return x + width;
		}
		if (x >= width) {
			return x - width;
		}
		return x;
	}

	public int toreY(int y) {
		if (y < 0) {
			return y + height;
		}
		if (y >= height) {
			return y - height;
		}
		return y;
	}
}
