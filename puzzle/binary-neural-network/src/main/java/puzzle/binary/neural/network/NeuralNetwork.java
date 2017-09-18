package puzzle.binary.neural.network;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class NeuralNetwork {
    private Layer<InputNode> inputLayer = new Layer<>();
    private List<Layer<HiddenNode>> hiddenLayers = new ArrayList<>();
    private Layer<OutputNode> outputLayer = new Layer<>();

    private BiasNode biasNode = new BiasNode("B");

    private LinearCongruentialGenerator lcg = new LinearCongruentialGenerator();

    private List<Link> allLinks = new ArrayList<>();

    public void learn(BigDecimal[] inputs, BigDecimal[] outputs, BigDecimal learningRate) {
        setInputs(inputs);
        getOutputs();
        backPropagate(learningRate, outputs);
    }

    public List<BigDecimal> play(BigDecimal... inputs) {
        setInputs(inputs);
        List<BigDecimal> outputs = getOutputs();
        System.out.println("inputs[" + Arrays.toString(inputs) + "] get outputs[" + outputs + "]");
        return outputs;
    }

    private void setInputs(BigDecimal... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            ((InputNode)inputLayer.getNodes().get(i)).setInput(inputs[i]);
        }
    }

    private List<BigDecimal> getOutputs() {
        return outputLayer.getNodes().stream().map(outputNode -> outputNode.getNormalizedOutput()).collect(Collectors.toList());
    }

    private void initInputNodes(int nbInputNodes) {
        for (int i = 0; i < nbInputNodes; i++) {
            inputLayer.addNode(new InputNode("I" + (i+1)));
        }
    }

    private void initOutputNodes(int nbOutputNodes) {
        for (int i = 0; i < nbOutputNodes; i++) {
            outputLayer.addNode(new OutputNode("O" + (i+1)));
        }
    }

    private void initLinks() {
        Layer<? extends Node> previousLayer = inputLayer;
        for (Layer<HiddenNode> layer : hiddenLayers) {
            for (HiddenNode node : layer.getNodes()) {
                linkNodeToPreviousLayer(node, previousLayer);
            }
            previousLayer = layer;
        }

        for (Node node : outputLayer.getNodes()) {
            linkNodeToPreviousLayer(node, previousLayer);
        }
    }

    private void linkNodeToPreviousLayer(Node node, Layer<? extends Node> previousLayer) {
        for (Node previousLayerNode : previousLayer.getNodes()) {
            Link link = new Link(previousLayerNode, node, lcg.next());
            node.addLinkToPreviousLayer(link);
            previousLayerNode.addLinkToNextLayer(link);
            allLinks.add(link);
        }
        Link link = new Link(biasNode, node, lcg.next());
//        Link link = new Link(new BiasNode(), node, lcg.next());
        node.addLinkToPreviousLayer(link);
        allLinks.add(link);
    }

    public NeuralNetwork(int... nbNodes) {
        if (nbNodes.length < 2) {
            throw new IllegalArgumentException("At least nbInputNodes and nbOuputNodes need to be specified !");
        }

        initInputNodes(nbNodes[0]);
        for (int i = 1; i < nbNodes.length - 1; i++) {
            int nbHiddenNodes = nbNodes[i];
            if (nbHiddenNodes > 0) {
                Layer hiddenLayer = new Layer();
                for (int j = 0; j < nbHiddenNodes; j++) {
                    hiddenLayer.addNode(new HiddenNode("H" + i + ":" + (j + 1) ));
                }
                hiddenLayers.add(hiddenLayer);
            }
        }
        initOutputNodes(nbNodes[nbNodes.length - 1]);

        initLinks();
    }

    private void backPropagate(BigDecimal learningRate, BigDecimal... expectedOutputs) {
        // calculate the delta for all output nodes
        for (int i = 0; i < outputLayer.getNodes().size(); i++) {
            OutputNode node = outputLayer.getNodes().get(i);
            node.calculateDelta(expectedOutputs[i]);
        }


//        ListIterator<Link> linksIterator = allLinks.listIterator(allLinks.size());
//        while(linksIterator.hasPrevious()) {
//            linksIterator.previous().calculateAndApplyDelta(learningRate);
//        }
        for (Link link : allLinks) {
            link.calculateAndApplyDelta(learningRate);
        }
    }

    @Override
    public String toString() {
        String result = "";

        result += biasNode + "\n";

        for (Node node : inputLayer.getNodes()) {
            result += node + "\n";
        }

        for (Layer<HiddenNode> hiddenLayer : hiddenLayers) {
            for (HiddenNode node : hiddenLayer.getNodes()) {
                result += node + "\n";
            }
        }

        for (Node node : outputLayer.getNodes()) {
            result += node + "\n";
        }

        for (Link link : allLinks) {
            result += link + "\n";
        }

        return result;
    }
}
