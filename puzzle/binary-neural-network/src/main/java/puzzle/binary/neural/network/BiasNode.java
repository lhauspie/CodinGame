package puzzle.binary.neural.network;

import java.math.BigDecimal;

public class BiasNode extends Node {

    public BiasNode(String id) {
        super(id);
    }

    @Override
    protected BigDecimal calculateOutput() {
        return BigDecimal.valueOf(1);
    }
}
