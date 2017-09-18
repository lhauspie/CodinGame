package contest.sponsored.codingame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class DummyPlayer {

    //   X 0 1 2
    // Y 
    // 0   X # X
    // 2   _ O _
    // 3   X # X
    //
    
    // Données d'initialisation du jeu :
    // Ligne 1 : hauteur du plateau
    // Ligne 2 : largeur du plateau
    // Ligne 3 : le nombre de pions sur le plateau (1 player et n others)
    
    // Données d'un tour de jeu
    // Ligne 1 : contenu de la cellule du plateau se trouvant en haut du player (# pour un mur / _ pour du vide)
    // Ligne 2 : contenu de la cellule du plateau se trouvant à droite du player (# pour un mur / _ pour du vide)
    // Ligne 3 : contenu de la cellule du plateau se trouvant en bas du player (# pour un mur / _ pour du vide)
    // Ligne 4 : contenu de la cellule du plateau se trouvant à gauche du player (# pour un mur / _ pour du vide)
    // Ligne 5.n : position en X du pion n, n étant le numéro du pion
    // Ligne 6.n : position en Y du pion n, n étant le numéro du pion

    // Complément :
    // le player est le dernier pion de la liste 5/6
    // les autres pions 5/6 sont des adverseres a éviter
    // le jeu est fini quand un other touche le player
    // le plateau est un tore
    
    // Les Actions :
    // "D" : déplacer le pion player vers le bas (player.y++)
    // "C" : déplacer le pion player vers le haut (player.y--)
    // "A" : déplacer le pion player vers la droite (player.x++)
    // "E" : déplacer le pion player vers la gauche (player.x--)
    // "B" : Pas encore resolu cette action
    
    private static final String WALL = "#";
    private static final String EMPTY = "_";
    private static final String ME = "M";
    private static final String[] OTHER = {"0", "1", "2", "3", "4"};
    
    // firstInput = X:me.coordX | Y:me.coordY-1;
    // secondInput = X:me.coordX+1 | Y:me.coordY;
    // thirdInput = X:me.coordX | Y:me.coordY+1;
    // fourthInput = X:me.coordX-1 | Y:me.coordY;
    
    private static class Plateau {
        private int height;
        private int width;
        private int nbOthers;
        private String[][] content;
        private int[][] alreadyVisited;
        private Coord player;
        private Coord[] others;
        
        public Plateau(int height, int width, int nbOthers) {
            this.height = height;
            this.width = width;
            this.nbOthers = nbOthers;
            content = new String[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    content[y][x] = "X";
                }
            }
            others = new Coord[nbOthers];
        }
        
        public void updateContent(String first, String second, String third, String fourth) {
            content[getY(player.y)][getX(player.x+1)] = second; // at right
            content[getY(player.y)][getX(player.x-1)] = fourth; // at left
            content[getY(player.y+1)][getX(player.x)] = third; // at down
            content[getY(player.y-1)][getX(player.x)] = first; // at up
            content[getY(player.y)][getX(player.x)] = EMPTY; // at middle
        }
        
        public void updateOther(int otherIdx, Coord other) {
            others[otherIdx] = other;
            content[getY(other.y)][getX(other.x)] = EMPTY;
        }
        
        public void updatePlayer(Coord player) {
            this.player = player;
        }
        
        
        public void render() {
            String[][] contentWithPlayers = new String[height][width];
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    contentWithPlayers[y][x] = content[y][x];
                }
            }
            
            contentWithPlayers[player.y][player.x] = ME;
            for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
                Coord other = others[otherIdx];
                contentWithPlayers[other.y][other.x] = OTHER[otherIdx];
            }

            for (int y = 0; y < height; y++) {
                System.err.println("Debug messages... plateau : " + String.join("", contentWithPlayers[y]));
            }
        }
        
        public boolean playerCanGoUp() {
            return playerCanGoTo(new Coord(getX(player.x), getY(player.y-1)));
        }
        
        public boolean playerCanGoDown() {
            return playerCanGoTo(new Coord(getX(player.x), getY(player.y+1)));
        }
        
        public boolean playerCanGoRight() {
            return playerCanGoTo(new Coord(getX(player.x+1), getY(player.y)));
        }
        
        public boolean playerCanGoLeft() {
            return playerCanGoTo(new Coord(getX(player.x-1), getY(player.y)));
        }
        
        public boolean playerCanGoTo(Coord coord) {
            return content[getY(coord.y)][getX(coord.x)].equals(EMPTY);
        }
        
        private int getX(int x) {
            if (x < 0) {
				return x + width;
			}
            if (x >= width) {
				return x - width;
			}
            return x;
        }
        
        private int getY(int y) {
            if (y < 0) {
				return y + height;
			}
            if (y >= height) {
				return y - height;
			}
            return y;
        }
        
        public String chooseAnAction() {
            List<String> actions = new ArrayList<String>();
            if (this.playerCanGoUp()) {
                actions.add(GO_UP);
            }
            if (this.playerCanGoLeft()) {
                actions.add(GO_LEFT);
            }
            if (this.playerCanGoDown()) {
                actions.add(GO_DOWN);
            }
            if (this.playerCanGoRight()) {
                actions.add(GO_RIGHT);
            }
            
            // find the closest other of the player
            int distance = height + width;
            int nearestOtherIdx = -1;
            for (int otherIdx = 0; otherIdx < nbOthers; otherIdx++) {
                int nextPlayerDistanceUp = player.distance(others[otherIdx]);
                if (distance > nextPlayerDistanceUp) {
                    distance = nextPlayerDistanceUp;
                    nearestOtherIdx = otherIdx;
                }
            }

            String bestAction = "";
            if (distance == 1) {
                bestAction = ACTION_B;
            } else {
                // find the best move to do to be farer
                distance = 0;
                for (String action : actions) {
                    Coord nextMovePlayer;
                    switch (action) {
                        case GO_UP : nextMovePlayer = player.getCoordOfUp(); break;
                        case GO_DOWN : nextMovePlayer = player.getCoordOfDown(); break;
                        case GO_LEFT : nextMovePlayer = player.getCoordOfLeft(); break;
                        case GO_RIGHT : nextMovePlayer = player.getCoordOfRight(); break;
                        default : nextMovePlayer = player; break;
                    }
                    int nextMoveDistance = nextMovePlayer.distance(others[nearestOtherIdx]);
                    if (distance <= nextMoveDistance) {
                        distance = nextMoveDistance;
                        bestAction = action;
                    }
                }
            }
            return bestAction;
        }
    }
    
    
    private static final String GO_DOWN = "D"; // increase me.coordY ==> go down
    private static final String GO_UP = "C"; // decrease me.coordY ==> go up
    private static final String GO_RIGHT = "A"; // increase me.coordX ==> go right
    private static final String GO_LEFT = "E"; // decrease me.coordX ==> go left
    private static final String ACTION_B = "B"; // no effect for now
    
    
    private static class Coord {
        public int x;
        public int y;
        
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int distance(Coord other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
        
        @Override
		public String toString() {
            return "X:"+x+"|Y:"+y;
        }
        
        public boolean canGoUp(String first, String second, String third, String fourth) {
            return first.equals(EMPTY);
        }
        public boolean canGoDown(String first, String second, String third, String fourth) {
            return third.equals(EMPTY);
        }
        public boolean canGoLeft(String first, String second, String third, String fourth) {
            return fourth.equals(EMPTY);
        }
        public boolean canGoRight(String first, String second, String third, String fourth) {
            return second.equals(EMPTY);
        }
        
        public Coord getCoordOfUp() {
            return new Coord(this.x, this.y-1);
        }
        public Coord getCoordOfDown() {
            return new Coord(this.x, this.y+1);
        }
        public Coord getCoordOfLeft() {
            return new Coord(this.x-1, this.y);
        }
        public Coord getCoordOfRight() {
            return new Coord(this.x+1, this.y);
        }
    }
    
    private static String[][] plateau; // plateau[Y][X]
    
    private static Coord me;
    private static Coord[] others;
    private static String[] actions = {GO_UP, GO_DOWN, GO_RIGHT, GO_LEFT};
    
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int firstInitInput = in.nextInt();
        int secondInitInput = in.nextInt();
        int thirdInitInput = in.nextInt();
        
        
        int height = firstInitInput; // = MAX_Y + 1
        int width = secondInitInput; // = MAX_X + 1
        int nbPlayers = thirdInitInput; // = MAX_X + 1
        
        Plateau plateau = new Plateau(height, width, nbPlayers-1);

        // game loop
        while (true) {
            String firstInput = in.next();
            String secondInput = in.next();
            String thirdInput = in.next();
            String fourthInput = in.next();
            
            for (int i = 0; i < thirdInitInput; i++) {
                int fifthInput = in.nextInt();
                int sixthInput = in.nextInt();
                Coord coord = new Coord(fifthInput, sixthInput);
                if (i < thirdInitInput - 1) {
                    plateau.updateOther(i, coord);
                } else {
                    plateau.updatePlayer(coord);
                }
            }
            
            plateau.updateContent(firstInput, secondInput, thirdInput, fourthInput);
            plateau.render();
            System.out.println(plateau.chooseAnAction());
            
            // the game is finished if one of 'others' is near of me.
            // Action A does something only if the secondInput equals to '_'
            // Action E does something only if the fourthInput equals to '_'
            
        }
    }
}