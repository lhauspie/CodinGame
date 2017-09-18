package puzzle.binary.neural.network;

import java.math.BigDecimal;

public class OutputNode extends Node {

    private BigDecimal delta;

    public OutputNode(String id) {
        super(id);
    }

    public void calculateDelta(BigDecimal expectedOutput) {
        // BACKPROPAGATION For output node k:
        //      δ[k] = o[k] * (1 - o[k]) * (o[k] - tk)
        delta = this.getOutput()
                .multiply(BigDecimal.ONE.subtract(this.getOutput()))
                .multiply(this.getOutput().subtract(expectedOutput));
    }

    public BigDecimal getDelta() {
        return delta;
    }
}
