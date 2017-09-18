package puzzle.binary.neural.network;

import java.math.BigDecimal;

public class HiddenNode extends Node {

    public HiddenNode(String id) {
        super(id);
    }

    public BigDecimal getDelta() {
        // BACKPROPAGATION For hidden node j (sum for all nodes k in the next layer to the right):
        //      δ[j] = o[j]
        //              * (1 - o[j])
        //              * (δ[k1] * w[j, k1] + ... + δ[kn]*w[j, kn])
        return this.getOutput()
                .multiply(BigDecimal.ONE.subtract(this.getOutput()))
                .multiply(this.linksToNextLayer.stream().map(link -> link.getTarget().getDelta().multiply(link.getWeight())).reduce(BigDecimal::add).get());
    }
}
