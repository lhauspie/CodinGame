package contest.sponsored.codingame.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import contest.sponsored.codingame.subclasses.Action;
import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.Constants;
import contest.sponsored.codingame.subclasses.Coord;
import contest.sponsored.codingame.subclasses.Direction;
import contest.sponsored.codingame.subclasses.DirectionWithNextStep;
import contest.sponsored.codingame.subclasses.OtherPawn;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.PlayerPawn;
import contest.sponsored.codingame.subclasses.Utils;

public class ProbabilityToMoveTest {

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
			"XXXXXX_XXXX_C_XXXXXXX_XXXXXX",
			"XXXXXX_XXXXXX_XXXXXXX_XXXXXX",
			"XXXXXX_XXXXXX_____XXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"XXXXXX_XXXXXXXXXXXXXX_XXXXXX",
			"X______XXXXXXXXXXXXXX______X",
			"X_XXXXXXXXXXXXXXXXXXXXXXXX_X",
			"X_XXXXXXXXXXX##_#####_XXXX_X",
			"X___XXXXXXXX__________#X___X",
			"XXX_XXXXXXXXX#####_##_#__XXX",
			"XXX_XXXXXXXXXXX###_##_##_##X",
			"XXXXXXXXXXXXXX#____##______#",
			"XXXXXXXXXXXXXX#_###XX#####_#",
			"XXXXXXXXXXXXXX#_##########_#",
			"XXXXXX_XXXXXX__M___________#",
			"XXXXXXXXXXXXXX#############X",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			"XXXXXXXXXXXXXXXXXXXXXXXXXXXX"};
    
    public static void main(String... args) {
    	Board board = new Board(initial.length, initial[0].length());
    	List<Pawn> others = new ArrayList<Pawn>();
    	Pawn player = new PlayerPawn();
    	
    	for (int y = 0; y < initial.length; y++) {
    		String initialLine = initial[y];
        	for (int x = 0; x < initialLine.length(); x++) {
        		String boardCell = String.valueOf(initialLine.charAt(x));
				if (Constants.OTHER.equals(boardCell)) {
					Pawn other = new OtherPawn(x, y);
        			others.add(other);
        			other.setDirection(Direction.UP);
        		} else if (Constants.PLAYER.equals(boardCell)) {
        			player = new PlayerPawn(x, y);
        		} else {
        			board.setContent(x, y, boardCell);
        		}
        	}
    	}
    	
    	board.render();
    
    	for (Pawn other : others) {
	    	Map<Direction, Float> probabilitiesToMove = estimate(board, other, player);
	    	System.err.println("estimation starts for " + other + " ===============================================");
	    	probabilitiesToMove.forEach((key, value) -> System.err.println(key + " : " + value));
	    	System.err.println("estimation ends for " + other + " =================================================");
    	}
    }

    private static Map<Direction, Float> estimate(Board board, Pawn other, Coord target) {
    	Map<Direction, Float> estimations = buildStartEstimation(other);
    	
    	// on rend la probabilité de mouvement nul si le mouvement ne peux pas être fait (Mur ou Other)
    	estimations.forEach((direction, probability) -> {
    		if (!Utils.canDo(other, Action.valueOf(direction), board)) {
    			estimations.put(direction, 0.00f);
    		}
    	});
    	
    	// on sait qu'un other va privéligé sa direction actuelle
    	estimations.put(other.getCurrentDirection(), estimations.get(other.getCurrentDirection()) * 1.20f);
    	
    	// on sait qu'un other va très rarement revenir en arrière
    	DirectionWithNextStep directionWithNextStep = new DirectionWithNextStep(other.getCurrentDirection());
    	estimations.put(directionWithNextStep.getBack(), estimations.get(directionWithNextStep.getBack()) * 0.50f);
    	
    	// on préviligie les mouvements se rapprochant de la target
    	int minDistance = Integer.MAX_VALUE;
    	Direction theDirectionWithMinDistance = null;
    	for (Entry<Direction, Float> estimation : estimations.entrySet()) {
    		Direction direction = estimation.getKey();
			// si la proba est nule, il est inutile de calculer quoi que ce soit
    		if (estimation.getValue() != 0.00f) {
				Pawn ghostOther = new OtherPawn(board.toreX(other.getX() + estimation.getKey().getX()), board.toreY(other.getY() + estimation.getKey().getY()));
				int currentDistance = Utils.minimumMoveToGoToTarget(board, ghostOther, target);
				if (minDistance > currentDistance) {
					minDistance = currentDistance;
					theDirectionWithMinDistance = direction;
				}
			}
		}
    	estimations.put(theDirectionWithMinDistance, estimations.get(theDirectionWithMinDistance) * 1.50f);
    	
    	return estimations;
    };
    

    private static Map<Direction, Float> buildStartEstimation(Pawn other) {
    	Map<Direction, Float> startSituation = new HashMap<>();
    	DirectionWithNextStep direction = new DirectionWithNextStep(other.getCurrentDirection());
		startSituation.put(direction.getHead(), 1.00f);
    	startSituation.put(direction.getLeft(), 1.00f);
    	startSituation.put(direction.getRight(), 1.00f);
    	startSituation.put(direction.getBack(), 1.00f);
    	return startSituation;
    }
}
