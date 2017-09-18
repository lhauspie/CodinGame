package contest.sponsored.codingame.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class GeneticPlayer {

    //   X 0 1 2
    // Y 
    // 0   X # X
    // 2   _ M _
    // 3   X # X
    //
    
    // Données d'initialisation du jeu :
    //    Ligne 1 : hauteur du plateau
    //    Ligne 2 : largeur du plateau
    //    Ligne 3 : le nombre de pions sur le plateau (1 player et n others)
    
    // Données d'un tour de jeu
    //    Ligne 1 : contenu de la cellule du plateau se trouvant en haut du player (# pour un mur / _ pour du vide)
    //    Ligne 2 : contenu de la cellule du plateau se trouvant à droite du player (# pour un mur / _ pour du vide)
    //    Ligne 3 : contenu de la cellule du plateau se trouvant en bas du player (# pour un mur / _ pour du vide)
    //    Ligne 4 : contenu de la cellule du plateau se trouvant à gauche du player (# pour un mur / _ pour du vide)
    //    Ligne 5.n : position en X du pion n, n étant le numéro du pion
    //    Ligne 6.n : position en Y du pion n, n étant le numéro du pion

    // Complément :
    //    le player est le dernier pion de la liste 5/6
    //    les autres pions 5/6 sont des adverseres a éviter
    //    le jeu est fini quand un other touche le player
    //    le plateau est un tore
    
    // Les Actions :
    //    "D" : déplacer le pion player vers le bas (player.y++)
    //    "C" : déplacer le pion player vers le haut (player.y--)
    //    "A" : déplacer le pion player vers la droite (player.x++)
    //    "E" : déplacer le pion player vers la gauche (player.x--)
    //    "B" : Pas encore resolu cette action ==> WAIT ?
    
    
    // Conception :
    //    Je suis un joueur, mon but est d'éviter les adversaires
    //    Je connais la position des adversaires
    //    Les adversaires et moi-meme seront modélisés sous forme de Pion (Pawn)
    //   Un Pion étant un couple (X,Y) de coordonnées (Coord)
    
    // Algo :
    //    Choisir X mouvements
    //    Pour chaque mouvement :
    //      - appliquer le mouvement
    //      - simuler le déplacement des others
    //    Calculer le score en se basant sur :
    //      - la distance parcouru
    //      - nbVisit de la case
    //      - distance du other le plus proche
	
	// AMELIORATION A APPORTER :
	//    Poursuivre le déplacement des others
	//      - en metant à jour les nouvelles coordonnées d'un other, on peut en deduire sa direction (nouvelle position - ancienne position = direction)
	//      - il suffit donc d'appliquer la derniere direction connue
	//    Pondérer le score avec la visite de nouvelles cases du board
	//      - si la case a deja été visité, on marquera forcément moins de points que sur un case deja visitée
	//      - on pourrait décrémenter le score de nbVisites * 100 sur chaque déplacement, ca forcerait l'IA a privilégier les chemins inconnus
	//    Prendre en compte la distance entre le playre et le other le plus proche
	//      - plus on est proche d'un other plus le player a de chance de le rencontrer
	//    Se rendre compte d'une situation de blocage avant qu'elle n'arrive
	//      - il arrive parfois que le player se trouve dans un couloir cernés par 2 others
	//      - il faudrait réussir à anticiper cette situation de mort certaine
	//    Privilégier le coté avec le moins de others
	//      - si 3 others au dessus de player et 1 other en dessous, il faudrait anticiper un echappée par le bas
    
    private static class PlayerDeadException extends Exception {
		private static final long serialVersionUID = 2213941083249856583L;
    }
    
    private static enum Action {
        GO_UP("C", Direction.UP), // decrease me.coordY ==> go up
        GO_DOWN("D", Direction.DOWN), // increase me.coordY ==> go down
        GO_LEFT("E", Direction.LEFT), // decrease me.coordX ==> go left
        GO_RIGHT("A", Direction.RIGHT); // increase me.coordX ==> go right
        //WAIT("B");
        
        private String value;
        private Direction direction;
        
        private Action(String value, Direction direction) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public Direction getDirection() {
            return direction;
        }
        
        private static final List<Action> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();
        
        public static Action random()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }
    
    private static class Direction extends Coord {
        public static final Direction UP = new Direction(0, -1);
        public static final Direction DOWN = new Direction(0, 1);
        public static final Direction LEFT  = new Direction(-1, 0);
        public static final Direction RIGHT  = new Direction(1, 0);
        public static final Direction WAIT  = new Direction(0, 0);
        
        private Direction(int x, int y) {
            super(x, y);
        }
    }
    
    private static class Coord {
        private int x;
        private int y;
        
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * CLONE
         */
        public Coord(Coord coord) {
            this(coord.x, coord.y);
        }

        public void update(int x, int y) {
        	this.x = x;
        	this.y = y;
        }

        @Override
		public String toString() {
            return "X:" + x + "|Y:" + y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
    
    private static class Pawn extends Coord {
    	Direction direction = Direction.WAIT;
        public Pawn() {
            super(0, 0);
        }
        public Pawn(int x, int y) {
            super(x, y);
        }
        public Pawn(Pawn pawn) {
            super(pawn);
        }
        @Override
		public void update(int x, int y) {
        	this.direction = new Direction(x - this.getX(), y - this.getY());
        	super.update(x, y);
        }
        public Direction getDirection() {
        	return direction;
        }
    }
    
    private static class Board {
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
    
    private static class Game {
        private Board board;
        private Pawn player;
        private Pawn[] others;
        private int nbOthers;
        private int[][] visited;
        
        public Game(Board board, int nbOthers) {
            this.board = board;
            this.nbOthers = nbOthers;
            this.player = new Pawn();
            this.others = new Pawn[nbOthers];
            for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
                this.others[otherIdx] = new Pawn();
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
            this.player = new Pawn(game.player);
            this.others = new Pawn[game.others.length];
            for (int otherIdx = 0; otherIdx < game.others.length; otherIdx++) {
                this.others[otherIdx] = new Pawn(game.others[otherIdx]);
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
        
        public void updateOtherPosition(int otherIdx, int x, int y) {
            if (others[otherIdx] == null) {
                others[otherIdx] = new Pawn(x, y);
            } else {
                others[otherIdx].update(x, y);
            }
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
            board.setContent(board.toreX(player.getX()+1), board.toreY(player.getY()), second); // at right
            board.setContent(board.toreX(player.getX()-1), board.toreY(player.getY()), fourth); // at left
            board.setContent(board.toreX(player.getX()), board.toreY(player.getY()+1), third); // at down
            board.setContent(board.toreX(player.getX()), board.toreY(player.getY()-1), first); // at up
            board.setContent(board.toreX(player.getX()), board.toreY(player.getY()), Board.EMPTY); // at middle
        }
        
        public void play() throws PlayerDeadException {
            // rien à faire car les others ne bougent pas pour l'instant
            // on fera quelque chose ici quand on prédira le comportement des others
            // quelque soit le mouvement, si un other peux atteindre le player en un coup, le player est concidéré comme dead
            for (Pawn other : others) {
                if (other.getX() + Direction.UP.getX() == player.getX() && other.getY() + Direction.UP.getY() == player.getY()
                        || other.getX() + Direction.DOWN.getX() == player.getX() && other.getY() + Direction.DOWN.getY() == player.getY()
                        || other.getX() + Direction.RIGHT.getX() == player.getX() && other.getY() + Direction.RIGHT.getY() == player.getY()
                        || other.getX() + Direction.LEFT.getX() == player.getX() && other.getY() + Direction.LEFT.getY() == player.getY()) {
                    throw new PlayerDeadException();
                }
                
				other.update(board.toreX(other.getX() + other.getDirection().getX()), board.toreY(other.getY() + other.getDirection().getY()));
            }
        }
        
        public void apply(Action action) throws PlayerDeadException {
            // bouger le player en fonction de l'action;
            // ici on estime que :
            //    - si une case est un mur alors elle est infranchissable
            //    - si une case est vide ou non découverte alors elle est franchissable
            //    - si on rencontre un other, on leve une exception car le mouvement n'est pas viable
            Direction direction = action.getDirection();
            int playerDestinationX = board.toreX(player.getX() + direction.getX());
            int playerDestinationY = board.toreY(player.getY() + direction.getY());
            
            // si on rencontre un other, on leve une exception car le mouvement n'est pas viable
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
            contentWithPlayers[player.getY()][player.getX()] = PLAYER;
            for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
                Coord other = others[otherIdx];
                contentWithPlayers[other.getY()][other.getX()] = OTHER[otherIdx];
            }
            // affichage du board avec le player et les others
            for (int y = 0; y < board.getHeight(); y++) {
                System.err.println(String.join("", contentWithPlayers[y]));
            }
        }
        
        public long score() {
        	// si on tombe sur une cellule deja visitée, on a un moins bon score que si cette cellule n'a jamais été visitée
        	return -visited[player.getY()][player.getX()] * 10;
        }
        
		public Board getBoard() {
			return board;
		}
    }
    
    private static class Solution implements Comparable<Solution> {
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
        
        private long evaluate(Game game) {
            // ici on dispose du jeu de départ et du jeu d'arrivée
            // ainsi on pourra calculer la distance parcouru par le player par exemple
            int distanceParcourue = distance(originalGame.getPlayer(), game.getPlayer(), game.getBoard());
            return distanceParcourue * 10000;
        }
        
        private long evaluateOneStep(Game game) {
            return game.score();
        }
        
        @Override
		public int compareTo(Solution comparedSolution) {
    		return comparedSolution.getScore().compareTo(this.getScore());
    	}
    	
        private int distance(Coord a, Coord b, Board board) {
			// distance sans passer par les bords
			// return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);

        	// distances sans passer par les bords
			int innerDistanceX = Math.abs(a.x - b.x); 
			int innerDistanceY = Math.abs(a.y - b.y);

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
            int actionIdxToMutate = random(0, actions.size() - 1);
			Pawn ghostPlayer = apply(mutatedSolution, originalGame.getPlayer(), originalGame.getBoard(), actionIdxToMutate);
			Action relevantAction = choseRelevantAction(ghostPlayer, originalGame.getBoard());
            mutatedActions.set(actionIdxToMutate, relevantAction); // mutation aléatoire
            return mutatedSolution;
        }
        
        public void removeFirstActionAndAddRandomLastOne() {
            score = null; // reset the score because the solution is going to change
            actions.remove(0);
            actions.add(Action.random());
        }
    }
    
    
    private static final String PLAYER = "M";
    private static final String[] OTHER = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    
    private static Game game = null;
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int height = in.nextInt(); // = MAX_Y + 1
        int width = in.nextInt(); // = MAX_X + 1
        int nbPlayers = in.nextInt(); // = nbOthers + 1
        
        Board board = new Board(height, width);
        
        game = new Game(board, nbPlayers-1);
        
        ArtificialIntelligence artificialIntelligence = new ArtificialIntelligence(game, board, 10);
        
        // game loop
        while (true) {
            String firstInput = in.next();
            String secondInput = in.next();
            String thirdInput = in.next();
            String fourthInput = in.next();
            
            for (int i = 0; i < nbPlayers; i++) {
                int coordX = in.nextInt();
                int coordY = in.nextInt();
                if (i < nbPlayers - 1) {
                    game.updateOtherPosition(i, coordX, coordY);
                } else {
                    game.updatePlayerPosition(coordX, coordY);
                }
            }
            
            // on appel updateContent apres les updatePosition car la position des 4 inputString sont fonction de la position du player
            game.updateContent(firstInput, secondInput, thirdInput, fourthInput);
            game.render();

            // ici, on a le game réel le plus à jour possible
            // le board est à jour
            // la position du player est à jour
            // la position des others sont à jour
            // on va pouvoir commencer à s'amuser à faire de la génétique :D
            System.out.println(artificialIntelligence.getNextAction().getValue());
        }
    }
    
    private static int random(int min, int max) {
        return (int) (Math.random() * (max-min+1)) + min;
    }
    
    private static class ArtificialIntelligence {
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
            Pawn ghostPlayer = new Pawn(game.getPlayer()); // clone le player du game
            
            List<Solution> solutions = new ArrayList<Solution>(nbSolutionsToGenerate);
            for (int solutionIdx = 0; solutionIdx < nbSolutionsToGenerate; solutionIdx++) {
                List<Action> actions = new ArrayList<Action>(nbActionsPerSolution);
                for (int actionIdx = 0; actionIdx < nbActionsPerSolution; actionIdx++) {
                    Action action = choseRelevantAction(ghostPlayer, board);
                    actions.add(action);
                    applyActionOnPawnAccordingToTheBoard(ghostPlayer, action, board);
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
            	lastBestSolution.removeFirstActionAndAddRandomLastOne();
            	solutions.add(lastBestSolution);
            }
            
            long start = System.currentTimeMillis();
            long duration = 0;
            int nbMutations = 0;
            while (duration < maxDuration) {
                int solutionIdxToMutate = random(0, nbSolutions - 1);
                solutions.add(solutions.get(solutionIdxToMutate).mutate());
                Collections.sort(solutions);
                solutions.remove(solutions.size() - 1); // supprimer la derniere solution (c'est la moins performante)
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

    // Permet de calculer la position finale du player du game après application d'une partie de la Solution
    private static Pawn apply(Solution solution, Pawn pawn, Board board, int nbFirstActionsToApply) {
    	Pawn ghostPlayer = new Pawn(game.getPlayer()); // clone le player du game
    	for (int actionIdx = 0; actionIdx < nbFirstActionsToApply; actionIdx++) {
    		applyActionOnPawnAccordingToTheBoard(ghostPlayer, solution.getActions().get(actionIdx), board);
    	}
    	return ghostPlayer;
    }

    // Bouge le pawn dans la direction correspondante à l'action et en prenant en compte le board
    // si le pawn sort du board, on le remets de l'autre coté du board (Tore)
    private static void applyActionOnPawnAccordingToTheBoard(Pawn pawn, Action action, Board board) {
    	Direction direction = action.getDirection();
    	int destinationX = board.toreX(pawn.getX() + direction.getX());
    	int destinationY = board.toreY(pawn.getY() + direction.getY());
    	pawn.update(destinationX, destinationY);
    }
    
    // permet de trouver une action utile en fonction des coordonnées du pawn
    // une action utile est une action qui une fois appliquée ne débouche pas sur un Board.WALL
    private static Action choseRelevantAction(Pawn pawn, Board board) {
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
}