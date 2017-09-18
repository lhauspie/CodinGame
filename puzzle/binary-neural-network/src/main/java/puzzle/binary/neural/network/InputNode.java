package puzzle.binary.neural.network;

import java.math.BigDecimal;

public class InputNode extends Node {
    private BigDecimal input;

    public InputNode(String id) {
        super(id);
    }

    public void setInput(BigDecimal input) {
        this.input = input;
    }

    @Override
    protected BigDecimal calculateOutput() {
        return input;
    }
}
