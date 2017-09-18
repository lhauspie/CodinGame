package puzzle.binary.neural.network;

import java.util.ArrayList;
import java.util.List;

public class Layer<T extends Node> {
    private List<T> nodes = new ArrayList<>();

    public void addNode(T node) {
        nodes.add(node);
    }

    public List<T> getNodes() {
        return nodes;
    }
}
