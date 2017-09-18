package puzzle.binary.neural.network;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * BigDecimal special functions.
 * <a href="http://arxiv.org/abs/0908.3030">A Java Math.BigDecimal Implementation of Core Mathematical Functions</a>
 *
 * @author Richard J. Mathar
 * @see <a href="http://apfloat.org/">apfloat</a>
 * @see <a href="http://dfp.sourceforge.net/">dfp</a>
 * @see <a href="http://jscience.org/">JScience</a>
 * @since 2009-05-22
 */
public class BigDecimalMath {

    /**
     * A suggestion for the maximum numter of terms in the Taylor expansion of the exponential.
     */
    static private int TAYLOR_NTERM = 8;

    /**
     * The exponential function.
     *
     * @param x the argument.
     * @return exp(x).
     * The precision of the result is implicitly defined by the precision in the argument.
     * In particular this means that "Invalid Operation" errors are thrown if catastrophic
     * cancellation of digits causes the result to have no valid digits left.
     * @author Richard J. Mathar
     * @since 2009-05-29
     */
    static public BigDecimal exp(BigDecimal x) {
                /* To calculate the value if x is negative, use exp(-x) = 1/exp(x)
                */
        if (x.compareTo(BigDecimal.ZERO) < 0) {
            final BigDecimal invx = exp(x.negate());
                        /* Relative error in inverse of invx is the same as the relative errror in invx.
                        * This is used to define the precision of the result.
                        */
            MathContext mc = new MathContext(invx.precision());
            return BigDecimal.ONE.divide(invx, mc);
        } else if (x.compareTo(BigDecimal.ZERO) == 0) {
                        /* recover the valid number of digits from x.ulp(), if x hits the
                        * zero. The x.precision() is 1 then, and does not provide this information.
                        */
            return scalePrec(BigDecimal.ONE, -(int) (Math.log10(x.ulp().doubleValue())));
        } else {
                        /* Push the number in the Taylor expansion down to a small
                        * value where TAYLOR_NTERM terms will do. If x<1, the n-th term is of the order
                        * x^n/n!, and equal to both the absolute and relative error of the result
                        * since the result is close to 1. The x.ulp() sets the relative and absolute error
                        * of the result, as estimated from the first Taylor term.
                        * We want x^TAYLOR_NTERM/TAYLOR_NTERM! < x.ulp, which is guaranteed if
                        * x^TAYLOR_NTERM < TAYLOR_NTERM*(TAYLOR_NTERM-1)*...*x.ulp.
                        */
            final double xDbl = x.doubleValue();
            final double xUlpDbl = x.ulp().doubleValue();
            if (Math.pow(xDbl, TAYLOR_NTERM) < TAYLOR_NTERM * (TAYLOR_NTERM - 1.0) * (TAYLOR_NTERM - 2.0) * xUlpDbl) {
                                /* Add TAYLOR_NTERM terms of the Taylor expansion (Euler's sum formula)
                                */
                BigDecimal resul = BigDecimal.ONE;

                                /* x^i */
                BigDecimal xpowi = BigDecimal.ONE;

                                /* i factorial */
                BigInteger ifac = BigInteger.ONE;

                                /* TAYLOR_NTERM terms to be added means we move x.ulp() to the right
                                * for each power of 10 in TAYLOR_NTERM, so the addition won't add noise beyond
                                * what's already in x.
                                */
                MathContext mcTay = new MathContext(err2prec(1., xUlpDbl / TAYLOR_NTERM));
                for (int i = 1; i <= TAYLOR_NTERM; i++) {
                    ifac = ifac.multiply(new BigInteger("" + i));
                    xpowi = xpowi.multiply(x);
                    final BigDecimal c = xpowi.divide(new BigDecimal(ifac), mcTay);
                    resul = resul.add(c);
                    if (Math.abs(xpowi.doubleValue()) < i && Math.abs(c.doubleValue()) < 0.5 * xUlpDbl)
                        break;
                }
                                /* exp(x+deltax) = exp(x)(1+deltax) if deltax is <<1. So the relative error
                                * in the result equals the absolute error in the argument.
                                */
                MathContext mc = new MathContext(err2prec(xUlpDbl / 2.));
                return resul.round(mc);
            } else {
                                /* Compute exp(x) = (exp(0.1*x))^10. Division by 10 does not lead
                                * to loss of accuracy.
                                */
                int exSc = (int) (1.0 - Math.log10(TAYLOR_NTERM * (TAYLOR_NTERM - 1.0) * (TAYLOR_NTERM - 2.0) * xUlpDbl
                        / Math.pow(xDbl, TAYLOR_NTERM)) / (TAYLOR_NTERM - 1.0));
                BigDecimal xby10 = x.scaleByPowerOfTen(-exSc);
                BigDecimal expxby10 = exp(xby10);

                                /* Final powering by 10 means that the relative error of the result
                                * is 10 times the relative error of the base (First order binomial expansion).
                                * This looses one digit.
                                */
                MathContext mc = new MathContext(expxby10.precision() - exSc);
                                /* Rescaling the powers of 10 is done in chunks of a maximum of 8 to avoid an invalid operation
                                * response by the BigDecimal.pow library or integer overflow.
                                */
                while (exSc > 0) {
                    int exsub = Math.min(8, exSc);
                    exSc -= exsub;
                    MathContext mctmp = new MathContext(expxby10.precision() - exsub + 2);
                    int pex = 1;
                    while (exsub-- > 0)
                        pex *= 10;
                    expxby10 = expxby10.pow(pex, mctmp);
                }
                return expxby10.round(mc);
            }
        }
    } /* BigDecimalMath.exp */


    /**
     * Append decimal zeros to the value. This returns a value which appears to have
     * a higher precision than the input.
     *
     * @param x The input value
     * @param d The (positive) value of zeros to be added as least significant digits.
     * @return The same value as the input but with increased (pseudo) precision.
     */
    static public BigDecimal scalePrec(final BigDecimal x, int d) {
        return x.setScale(d + x.scale());
    }

    /**
     * Convert an absolute error to a precision.
     *
     * @param x    The value of the variable
     *             The value returned depends only on the absolute value, not on the sign.
     * @param xerr The absolute error in the variable
     *             The value returned depends only on the absolute value, not on the sign.
     * @return The number of valid digits in x.
     * Derived from the representation x+- xerr, as if the error was represented
     * in a "half width" (half of the error bar) form.
     * The value is rounded down, and on the pessimistic side for that reason.
     * @since 2009-05-30
     */
    static public int err2prec(double x, double xerr) {
                /* Example: an error of xerr=+-0.5 at x=100 represents 100+-0.5 with
                * a precision = 3 (digits).
                */
        return 1 + (int) (Math.log10(Math.abs(0.5 * x / xerr)));
    }

    /**
     * Convert a relative error to a precision.
     *
     * @param xerr The relative error in the variable.
     *             The value returned depends only on the absolute value, not on the sign.
     * @return The number of valid digits in x.
     * The value is rounded down, and on the pessimistic side for that reason.
     * @since 2009-08-05
     */
    static public int err2prec(double xerr) {
                /* Example: an error of xerr=+-0.5 a precision of 1 (digit), an error of
                * +-0.05 a precision of 2 (digits)
                */
        return 1 + (int) (Math.log10(Math.abs(0.5 / xerr)));
    }

} /* BigDecimalMath */