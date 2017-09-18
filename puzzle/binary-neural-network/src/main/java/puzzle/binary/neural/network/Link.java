package puzzle.binary.neural.network;

import java.math.BigDecimal;

public class Link {
    private Node source;
    private Node target;
    private BigDecimal weight;
    private static final BigDecimal MINUS_ONE = BigDecimal.ONE.negate();


    public Link(Node source, Node target, BigDecimal weight) {
        System.out.println("w[" + source.getId() + ", " + target.getId() + "] = " + weight);
        this.source = source;
        this.target = target;
        this.weight = weight;
        BigDecimal.valueOf(-1);
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void calculateAndApplyDelta(BigDecimal learningRate) {
        // BACKPROPAGATION For link, w[j, k]:
        //      ∆w = −η * δ[k] * o[j]
        BigDecimal delta = learningRate
                .multiply(target.getDelta())
                .multiply(source.getOutput())
                .negate();
        weight = delta.add(weight);
    }

    @Override
    public String toString() {
        return "w[" + source.getId() + "," + target.getId() + "] = " + weight;
    }
}
