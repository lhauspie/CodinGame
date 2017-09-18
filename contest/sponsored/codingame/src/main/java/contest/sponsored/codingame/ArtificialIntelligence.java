package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtificialIntelligence {
	private Game game;
	private Board board;
	private long maxDuration;
	private List<Solution> lastBestSolutions = new ArrayList<>();

	public ArtificialIntelligence(Game game, Board board, long maxDuration) {
		this.game = game;
		this.board = board;
		this.maxDuration = maxDuration;
	}

	// Genere une population de solutions à tester
	private List<Solution> generatePopulation(int nbSolutionsToGenerate, int nbActionsPerSolution) {
		Pawn ghostPlayer = new Pawn(game.getPlayer()); // clone le player du
														// game

		List<Solution> solutions = new ArrayList<Solution>(nbSolutionsToGenerate);
		for (int solutionIdx = 0; solutionIdx < nbSolutionsToGenerate; solutionIdx++) {
			List<Action> actions = new ArrayList<Action>(nbActionsPerSolution);
			for (int actionIdx = 0; actionIdx < nbActionsPerSolution; actionIdx++) {
				Action action = choseRelevantAction(ghostPlayer);
				actions.add(action);
				applyActionOnPawnAccordingToTheBoard(ghostPlayer, action);
			}
			solutions.add(new Solution(actions, game));
		}
		return solutions;
	}

	// permet de trouver une action utile en fonction des coordonnées du pawn
	// une action utile est une action qui une fois appliquée ne débouche pas
	// sur un Board.WALL
	private Action choseRelevantAction(Pawn pawn) {
		while (Boolean.TRUE) {
			Action action = Action.random();
			Direction direction = action.getDirection();
			int watchOutX = board.toreX(pawn.getX() + direction.getX());
			int watchOutY = board.toreY(pawn.getY() + direction.getY());
			if (!Board.WALL.equals(board.getContent(watchOutX, watchOutY))) {
				return action;
			}
		}
		return null;
	}

	// Bouge le pawn dans la direction correspondante à l'action et en prenant
	// en compte le board
	// si le pawn sort du board, on le remets de l'autre coté du board (Tore)
	private void applyActionOnPawnAccordingToTheBoard(Pawn pawn, Action action) {
		Direction direction = action.getDirection();
		int destinationX = board.toreX(pawn.getX() + direction.getX());
		int destinationY = board.toreY(pawn.getY() + direction.getY());
		pawn.update(destinationX, destinationY);
	}

	public Action getNextAction() {
		int nbSolutions = 10;
		int nbActionsPerSolution = 5;
		int nbBestSolutions = 3;

		List<Solution> solutions = generatePopulation(nbSolutions, nbActionsPerSolution);
		for (Solution lastBestSolution : lastBestSolutions) {
			lastBestSolution.removeFirstActionAndAddRandomLastOne();
			solutions.add(lastBestSolution);
		}

		long start = System.currentTimeMillis();
		long duration = 0;
		int nbMutations = 0;
		while (duration < maxDuration) {
			int solutionIdxToMutate = Utils.random(0, nbSolutions - 1);
			solutions.add(solutions.get(solutionIdxToMutate).mutate());
			Collections.sort(solutions);
			solutions.remove(solutions.size() - 1); // supprimer la derniere
													// solution (c'est la moins
													// performante)
			nbMutations++;
			duration = System.currentTimeMillis() - start;
		}

		// on sauvegarde les 3 meilleurs solutions pour les challenger
		lastBestSolutions.clear();
		System.err.println("best solutions after " + nbMutations + " mutations : ");
		for (int solutionIdx = 0; solutionIdx < nbBestSolutions; solutionIdx++) {
			Solution aSolution = solutions.get(solutionIdx);
			System.err.println("Action = " + aSolution.getActions() + " : Score = " + aSolution.getScore());
			lastBestSolutions.add(solutions.get(solutionIdx));
		}

		return solutions.get(0).getFirstAction();
	}

	// Permet de calculer la position finale du player du game après application
	// d'une partie de la Solution
	private Pawn apply(Solution solution, int nbFirstActionsToApply) {
		Pawn ghostPlayer = new Pawn(game.getPlayer()); // clone le player du
														// game
		for (Action action : solution.getActions()) {
			applyActionOnPawnAccordingToTheBoard(ghostPlayer, action);
		}
		return ghostPlayer;
	}
}
