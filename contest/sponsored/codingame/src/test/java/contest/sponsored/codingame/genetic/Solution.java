package contest.sponsored.codingame.genetic;

import java.util.ArrayList;
import java.util.List;

import contest.sponsored.codingame.subclasses.Action;
import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.Coord;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.PlayerDeadException;
import contest.sponsored.codingame.subclasses.Utils;

public class Solution implements Comparable<Solution> {
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
//    public Solution mutate(float amplitude) {
//    	ArrayList<Action> mutatedActions = new ArrayList<Action>(actions);
//    	Solution mutatedSolution = new Solution(mutatedActions, originalGame);
//    	int maxIdx = actions.size() - 1;
//    	int minIdx = maxIdx - (int)(maxIdx * amplitude);
//    	int actionIdxToMutate = Utils.random(minIdx, maxIdx);
//    	// ici on applique les X premieres actions de la solution pour faire arriver le player a la position sur le board qui correspond à l'action à faire muter
//    	// on peut donc faire un choix pertinant en fonction de l'etat du board au coordonnées calculées (ne pas choisir un mur)
//    	Pawn ghostPlayer = apply(mutatedSolution, originalGame, actionIdxToMutate);
//    	for (; actionIdxToMutate < actions.size(); actionIdxToMutate++) {
//    		// mutation aléatoire mais avec un minimum d'intelligence
//    		Action relevantAction = Utils.choseRandomRelevantAction(ghostPlayer, originalGame.getBoard());
//    		Utils.apply(ghostPlayer, relevantAction, originalGame.getBoard());
//    		mutatedActions.set(actionIdxToMutate, relevantAction);
//    	}
//    	return mutatedSolution;
//    }
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
