package puzzle.binary.neural.network;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class Node {
    protected List<Link> linksToPreviousLayer = new ArrayList<>();
    protected List<Link> linksToNextLayer = new ArrayList<>();

    private String id;
    private BigDecimal output;

    public Node(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getUnNormalizedOutput() {
        return linksToPreviousLayer.stream()
                .map(link -> link.getWeight().multiply(link.getSource().getNormalizedOutput()))
                .reduce(BigDecimal::add).get();
    }

    public BigDecimal getNormalizedOutput() {
        output = calculateOutput();
        return output;
    }

    protected BigDecimal calculateOutput() {
        return BigDecimal.ONE.divide(BigDecimal.ONE.add(BigDecimalMath.exp(getUnNormalizedOutput().negate())), MathContext.DECIMAL32);
    }

    public void addLinkToPreviousLayer(Link link) {
        linksToPreviousLayer.add(link);
    }

    public void addLinkToNextLayer(Link link) {
        linksToNextLayer.add(link);
    }

    public BigDecimal getOutput() {
        return output;
    }

    public BigDecimal getDelta() {
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return id + "=>" + getOutput();
    }
}
