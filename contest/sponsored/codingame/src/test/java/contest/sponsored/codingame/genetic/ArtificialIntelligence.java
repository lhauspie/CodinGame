package contest.sponsored.codingame.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import contest.sponsored.codingame.subclasses.Action;
import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.Utils;

public class ArtificialIntelligence {
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
        int nbSolutions = 10;
        int nbActionsPerSolution = 5;
        int nbBestSolutions = 3;
        
        List<Solution> solutions = generatePopulation(nbSolutions, nbActionsPerSolution);
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
