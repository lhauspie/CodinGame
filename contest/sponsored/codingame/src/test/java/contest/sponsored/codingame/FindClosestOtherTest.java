package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.List;

import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.OtherPawn;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.PlayerPawn;
import contest.sponsored.codingame.subclasses.Utils;

public class FindClosestOtherTest {

	// this board results to 15
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
			"XXXXXX_XXXX___XXXXXXX_XXXXXX",
			"XXXXXX_XXXXXX_XXXXXXX_XXXXXX",
			"XXXXXX_XXXXXX_____XXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"X______XXXXXXXXXXXXXX______X",
			"X_XXXXXXXXXXXXXXXXXXXXXXXX_X",
			"X_XXXXXXXXXXX##_#####_XXXX_X",
			"X___XXXXXXXX__________#X___X",
			"XXX_XXXXXXXXX#####_##_#X_XXX",
			"XXXCXXXXXXXXXXX###_##_##_##X",
			"XXXXXXXXXXXXXX#____##______#",
			"XXXXXXXXXXXXXX#_###XX#####_#",
			"XXXXXXXXXXXXXX#_##########_#",
			"XXXXXXXXXXXXX_____________M#",
			"XXXXXXXXXXXXXX#############X",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX"};

	
    private static final String OTHER = "C";
    private static final String PLAYER = "M";
    
    public static void main(String... args) {
    	Board board = new Board(initial.length, initial[0].length());
    	List<Pawn> others = new ArrayList<Pawn>();
    	Pawn player = new PlayerPawn();
    	
    	for (int y = 0; y < initial.length; y++) {
    		String initialLine = initial[y];
        	for (int x = 0; x < initialLine.length(); x++) {
        		String boardCell = String.valueOf(initialLine.charAt(x));
        		if (OTHER.equals(boardCell)) {
        			others.add(new OtherPawn(x, y));
        		} else if (PLAYER.equals(boardCell)) {
        			player = new PlayerPawn(x, y);
        		}
        		board.setContent(x, y, boardCell);
        	}
    	}
    	
    	board.render();
    
    	System.out.println("minimun move to dead : " + Utils.minimumMoveToGoToTarget(board, others, player));
    }
}
