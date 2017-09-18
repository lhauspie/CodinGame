package contest.sponsored.codingame.subclasses;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    
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
//	    	clonedBoard.render();
//	    	System.err.println("==============================================================");
    	}
    	return nbMoves;
    }
}
