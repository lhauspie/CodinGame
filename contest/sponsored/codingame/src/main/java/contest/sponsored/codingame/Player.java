package contest.sponsored.codingame;

import java.util.Scanner;

public class Player {

	public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int height = in.nextInt(); // = MAX_Y + 1
        int width = in.nextInt(); // = MAX_X + 1
        int nbPlayers = in.nextInt(); // = nbOthers + 1
        
        Board board = new Board(height, width);
        Game game = new Game(board, nbPlayers-1);
        
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

            // ici, on a le game r�el le plus � jour possible
            // le board est � jour
            // la position du player est � jour
            // la position des others sont � jour
            // on va pouvoir commencer � s'amuser � faire de la g�n�tique :D
            System.out.println(artificialIntelligence.getNextAction().getValue());
        }
    }
}