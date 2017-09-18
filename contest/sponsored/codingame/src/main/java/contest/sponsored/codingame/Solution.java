package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.List;

public class Solution implements Comparable<Solution> {
	private List<Action> actions;
	private Game originalGame;
	private Long score = null;

	public Solution(List<Action> actions, Game game) {
		this.actions = actions;
		this.originalGame = game;
	}

	private Long score() {
		// cloner le jeu pour pouvoir en faire ce qu'on veux sans changer l'état
		// actuel
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

	private long evaluate(Game game) {
		int totalDistance = 0;
		int minDistance = Integer.MAX_VALUE;
		for (Pawn other : game.getOthers()) {
			int currentDistance = distance(other, game.getPlayer(), game.getBoard());
			totalDistance += currentDistance;
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
			}
		}
		// ici on dispose du jeu de départ et du jeu d'arrivée
		// ainsi on pourra calculer la distance parcouru par le player par
		// exemple
		// Mes priorités :
		// 		P1 : Parcourir le plus de chemin possible
		// 		P2 : Etre le plus eloigné possible du other le plus proche
		//		P3 : Emprunter le chemin le moins emprunté jusqu'à maintenant
		int distanceParcourue = distance(originalGame.getPlayer(), game.getPlayer(), game.getBoard());
		return distanceParcourue * 1000000 + minDistance * 105;
	}

	private long evaluateOneStep(Game game) {
		return game.score();
	}

	@Override
	public int compareTo(Solution comparedSolution) {
		return comparedSolution.getScore().compareTo(this.getScore());
	}

	private int distance(Point a, Point b, Board board) {
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

	public Solution mutate() {
		ArrayList<Action> mutatedActions = new ArrayList<Action>(actions);
		Solution mutatedSolution = new Solution(mutatedActions, originalGame);
		int actionIdxToMutate = Utils.random(0, actions.size() - 1);
		Action actionToMutate = mutatedActions.get(actionIdxToMutate);
		// mutation aléatoire mais differentes de l'initiale (cela evite d'avoir
		// une mutation non mutante)
		Action mutatedAction = Action.random();
		while (actionToMutate == mutatedAction) {
			mutatedAction = Action.random();
		}
		mutatedActions.set(actionIdxToMutate, mutatedAction);
		return mutatedSolution;
	}

	public void removeFirstActionAndAddRandomLastOne() {
		score = null; // reset the score because the solution is going to change
		actions.remove(0);
		actions.add(Action.random());
	}
}
