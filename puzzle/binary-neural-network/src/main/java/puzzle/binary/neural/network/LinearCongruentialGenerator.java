package puzzle.binary.neural.network;

import java.math.BigDecimal;
import java.math.MathContext;

public class LinearCongruentialGenerator {

    // The generator is defined by the recurrence relation:
    //      Xn+1 = (a * Xn + c) mod m
    // where X is the sequence of pseudorandom values, and
    //      m,  0 < m       : the "modulus"
    //      a,  0 < a < m   : the "multiplier"
    //      c,  0 <= c < m  : the "increment"
    //      X0, 0 <= X0 < m : the "seed" or "start value"


    // A PASSER EN BIG DECIMAL

    private BigDecimal X0 = BigDecimal.valueOf(1103527590);
    private BigDecimal c = BigDecimal.valueOf(12345);
    private BigDecimal a = X0.subtract(c);
    private BigDecimal m = BigDecimal.valueOf(Integer.MAX_VALUE);

    private BigDecimal current = X0;

    public BigDecimal next() {
        BigDecimal result = current;
        current = (a.multiply(current).add(c)).remainder(m);
        return result.divide(m, MathContext.DECIMAL32);
    }
}
