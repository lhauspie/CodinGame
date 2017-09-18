package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import contest.sponsored.codingame.subclasses.Action;
import contest.sponsored.codingame.subclasses.Board;
import contest.sponsored.codingame.subclasses.Constants;
import contest.sponsored.codingame.subclasses.Direction;
import contest.sponsored.codingame.subclasses.OtherPawn;
import contest.sponsored.codingame.subclasses.Pawn;
import contest.sponsored.codingame.subclasses.PlayerWithMemory;
import contest.sponsored.codingame.subclasses.Utils;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class AllSolutionsPlayer {

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
	//    Choisir le nombre de tour à simuler
	//    Pour chaque tour :
	//      - simuler toutes les positions possible de chaque pawn (player et others inclus)
	//      - dans chaque pawn on trouvera l'ensemble des actions qui lui ont permis d'arriver a sa position final
	//      - on supprime les pawn initiaux
	//    une fois les tours fini, on va prendre le player le plus éloigner de tous les others
	//    piocher dans la memoire interne du pawn en question et en prendre la premiere action
   
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int height = in.nextInt(); // = MAX_Y + 1
        int width = in.nextInt(); // = MAX_X + 1
        int nbPlayers = in.nextInt(); // = nbOthers + 1
        
        Board initialBoard = new Board(height, width);
        PlayerWithMemory initialPlayer = null;
        Pawn[] initialOthers = new Pawn[nbPlayers - 1];
        
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
            		initialBoard.setContent(coordX, coordY, Board.EMPTY);
            		initialOthers[i] = new OtherPawn(coordX, coordY);
                } else {
            		initialBoard.setContent(coordX, coordY, Board.EMPTY);
            		initialPlayer = new PlayerWithMemory(coordX, coordY);
                }
            }
            
            initialBoard.setContent(initialBoard.toreX(initialPlayer.getX() + Direction.UP.getX()), initialBoard.toreY(initialPlayer.getY() + Direction.UP.getY()), firstInput);
            initialBoard.setContent(initialBoard.toreX(initialPlayer.getX() + Direction.DOWN.getX()), initialBoard.toreY(initialPlayer.getY() + Direction.DOWN.getY()), thirdInput);
            initialBoard.setContent(initialBoard.toreX(initialPlayer.getX() + Direction.RIGHT.getX()), initialBoard.toreY(initialPlayer.getY() + Direction.RIGHT.getY()), secondInput);
            initialBoard.setContent(initialBoard.toreX(initialPlayer.getX() + Direction.LEFT.getX()), initialBoard.toreY(initialPlayer.getY() + Direction.LEFT.getY()), fourthInput);

            // CLONE THE INITIAL STATE
            // cloner le board
            Board clonedBoard = initialBoard.clone();
            List<Pawn> clonedPlayers = new ArrayList<Pawn>();
            List<Pawn> clonedOthers = new ArrayList<Pawn>();
            
            // cloner et placer le player sur le board cloné
            clonedBoard.setContent(initialPlayer, Constants.PLAYER);
            clonedPlayers.add(initialPlayer.clone());
            
            // cloner et placer les others sur le board cloné
            for (Pawn other : initialOthers) {
            	clonedBoard.setContent(other, Constants.OTHER);
            	clonedOthers.add(other.clone());
            }
            
            clonedBoard.render();
            
            // ici, on a le board réel le plus à jour possible
            // le board est à jour
            // la position du player est à jour
            // la position des others sont à jour
            Pawn theBestPlayer = null;
        	while (theBestPlayer == null) {
        		// un tour
        		// on fait bouger les others
    	    	List<Pawn> newClonedOthers = new ArrayList<Pawn>();
    	    	for (Pawn other : clonedOthers) {
    	    		moveIfYouCan(other, Direction.UP, clonedBoard, Constants.OTHER, newClonedOthers);
    	    		moveIfYouCan(other, Direction.DOWN, clonedBoard, Constants.OTHER, newClonedOthers);
    	    		moveIfYouCan(other, Direction.RIGHT, clonedBoard, Constants.OTHER, newClonedOthers);
    	    		moveIfYouCan(other, Direction.LEFT, clonedBoard, Constants.OTHER, newClonedOthers);
    	    	}
    	    	clonedOthers = newClonedOthers;
    	
    	    	// on fait bouger les players
    	    	List<Pawn> newClonedPlayers = new ArrayList<Pawn>();
    	    	for (Pawn player : clonedPlayers) {
    	    		moveIfYouCan(player, Direction.UP, clonedBoard, Constants.PLAYER, newClonedPlayers);
    	    		moveIfYouCan(player, Direction.DOWN, clonedBoard, Constants.PLAYER, newClonedPlayers);
    	    		moveIfYouCan(player, Direction.RIGHT, clonedBoard, Constants.PLAYER, newClonedPlayers);
    	    		moveIfYouCan(player, Direction.LEFT, clonedBoard, Constants.PLAYER, newClonedPlayers);
    	    	}
    	    	
    	    	if (newClonedPlayers.size() == 0) {
    	    		// match null entre plusieurs players car on passe de 2 players au moins à 0 directement
    	    		// on va donc piocher dans le tour précédent pour trouver le meilleur player
    		    	System.err.println("no best player found so we pick up one in the previous turn from " + clonedPlayers.size());
    	    		theBestPlayer = clonedPlayers.get(0);
    	    	} else if (newClonedPlayers.size() == 1) {
    	    		// on a trouvé le meilleur Player
    		    	System.err.println("only one best player found");
    	    		theBestPlayer = newClonedPlayers.get(0);
    	    	} else {
    	    		clonedPlayers = newClonedPlayers;
    	    	}
        	}
            System.out.println(Action.valueOf(((PlayerWithMemory)theBestPlayer).getDirections().get(0)).getValue());
        }
    }
    
    private static void moveIfYouCan(Pawn pawn, Direction direction, Board board, String symbolOnBoard, List<Pawn> newPawns) {
    	if (Utils.canDo(pawn, Action.valueOf(direction), board)) {
			Pawn newPawn = pawn.clone();
			Utils.apply(newPawn, Action.valueOf(direction), board);
			board.setContent(newPawn, symbolOnBoard);
			newPawns.add(newPawn);
		}
	}
}