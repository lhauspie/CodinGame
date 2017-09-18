package contest.sponsored.teads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class Solution {

	// Cette variable me sert de base de données
	// je peux donc faire un getNodeById et un getAllNodes
    private static Map<Integer, Node> nodes = new HashMap<Integer, Node>();

    private static class Node {
        private int id;
        private List<Node> children = new ArrayList<Node>();
        
        public Node(int id) {
            this.id = id;
        }
        
        public void addChild(Node node) {
        	children.add(node);
        	node.children.add(this);
        }
        
        public int getId() {
            return id;
        }
        public List<Node> getChildren() {
            return children;
        }
        // une feuille est un node qui n'a qu'un link
        public boolean isLeaf() {
        	return children.size() == 1;// || parents.isEmpty();
        }

		@Override
		// method utile pour les HashSet utilisés pour l'algorithm
		public int hashCode() {
			return id;
		}
    }
    
    
    public static void main(String args[]) {
    	// on construit la base de données à l'aide des entrée
        Scanner in = new Scanner(System.in);
        int n = in.nextInt(); // the number of adjacency relations
        for (int i = 0; i < n; i++) {
            int xi = in.nextInt(); // the ID of a node which is adjacent to yi
            int yi = in.nextInt(); // the ID of a node which is adjacent to xi
            Node xNode = getNode(xi);
            xNode.addChild(getNode(yi));
    		System.err.println("Debug messages... input : " + xi + " " + yi);
        }
        
        // base de données initialisée

        // on commence l'algorithme par les feuilles de l'arbdre
        Set<Node> leafs = new HashSet<Node>();
        for (Node node : nodes.values()) {
        	if (node.isLeaf()) {
        		System.err.println("Debug messages... node with id [" + node.getId() + "] is a leaf !");
        		leafs.add(node);
        	}
        }
        
        
        // The minimal amount of steps required to completely propagate the advertisement
        // on fait appel à la methode recursive de calcul
        System.out.println(getMinimumTreeHeightByStartingByLeafs(leafs) - 1);
    }
    
    // méthode recursive de calcul de profondeur minimum d'un arbre bidirectionnel
    // on commence toujours par les feuilles de l'arbre (def : une feuille est un node avec un seul child)
    // pseudo code : 
    // 		- condition d'arret : plus de nodes à parcourir pour le calcul => on renvoit 0
    //      - pour chaque feuille passée en parametre : 
    //			- si plus d'un chemin à suivre alors on garde le noeud en cours pour faire le calcul du tour suivant (synchronisation des childs)
    //			- sinon on ajoute le child a la liste à passer en parametre à l'appel récursif suivant
    //			- on casse le lien inverse du lien qu'on vient de suivre pour eviter de revenir en arriere pendant la suite du calcul
    // ATTENTION :
    // 		il se peut que 2 chemins se croisent et implique une incrémentation inutile de la hauteur de l'arbre
    // 		on detecte donc un croisement en constant que la destionation du lien (le Child) ne contient aucun child
    private static int getMinimumTreeHeightByStartingByLeafs(Set<Node> nodes) {
        System.err.println("Debug messages... getMinimumTreeHeightByStartingByLeafs !");
    	if (nodes == null || nodes.size() == 0) {
    		return 0;
    	}
    	
        // il faut réussir à detecter un croisement
    	boolean croisement = Boolean.FALSE;
    	Set<Node> nextStepNodes = new HashSet<Node>();
        for (Node parentNode : nodes) {
            System.err.println("Debug messages... parentNode : " + parentNode.getId());
            // ce test permet une sorte de synchronisation
            // si il y a plus d'une relation à suivre alors on attends que les autres soient arrivées pour continuer le chemin
            // on reste donc sur le node parent
            // exemple : sur 0 avec (0,1) / (0,2) / (0,3) / (2,4) / (3,5)
            // 1, 4 et 5 sont des feuilles : on execute un tour de boucle
            //    -> 0 est atteint via le chemin (1,0) mais pas via (2,0) ou (3,0)
            //    -> du point de vue de 0 il reste donc 2 chemins à parcourrir, on va attendre que tous les enfants soient arrivés avant de faire un mouvement
            //    -> 0 a plus d'un chemin donc je le garde tel que
            // 0, 2 et 3 sont en course pour le tour suivant : on execute un tour de boucle
            //    -> 0 est maitenant atteint par tous ses enfants (1, 2 et 3)
            if (parentNode.getChildren().size() > 1) {
            	nextStepNodes.add(parentNode);
            } else {
	        	for (Node childNode : parentNode.getChildren()) {
	        		// il y a un croisement si j'arrive dans une situation avec un node sans enfant (suppression du link dans les 2 sens)
	        		if (childNode.getChildren().size() == 0) {
	        			croisement = Boolean.TRUE;
	        		}
	        		nextStepNodes.add(childNode);
	        		breakLink(childNode, parentNode);
	        	}
            }
        }
        
        // si il y a eut un croisement, alors on ne doit pas incrémenter
        return (croisement ? 0 : 1) + getMinimumTreeHeightByStartingByLeafs(nextStepNodes);
    }
    
    private static Node getNode(int id) {
        Node node = nodes.get(id);
        if (node == null) {
            node = new Node(id);
            nodes.put(id, node);
        }
        return node;
    }
    
    // casser le lien directionnel srcNode vers targetNode
    private static void breakLink(Node srcNode, Node targetNode) {
    	srcNode.getChildren().remove(targetNode);
    }
}