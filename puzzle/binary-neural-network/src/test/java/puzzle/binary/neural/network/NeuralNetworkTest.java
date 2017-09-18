package puzzle.binary.neural.network;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class NeuralNetworkTest {

    private static final BigDecimal LEARNING_RATE = BigDecimal.valueOf(0.60);

    @Test
    public void copycat() {
        NeuralNetwork nn = new NeuralNetwork(1, 1);
        for (int i = 0; i < 7; i++) {
            learn(nn, "0", "0");
            learn(nn, "1", "1");
        }

        Assertions.assertThat(play(nn, "0")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1")).isEqualTo("1");
    }

    @Test
    public void opposite() {
        NeuralNetwork nn = new NeuralNetwork(1, 1);
        for (int i = 0; i < 9; i++) {
            learn(nn, "0", "1");
            learn(nn, "1", "0");
        }

        Assertions.assertThat(play(nn, "0")).isEqualTo("1");
        Assertions.assertThat(play(nn, "1")).isEqualTo("0");
    }

    @Test
    public void or() {
        NeuralNetwork nn = new NeuralNetwork(2, 1);
        for (int i = 0; i < 40; i++) {
            learn(nn, "00", "0");
            learn(nn, "01", "1");
            learn(nn, "10", "1");
            learn(nn, "11", "1");
        }

        Assertions.assertThat(play(nn, "00")).isEqualTo("0");
        Assertions.assertThat(play(nn, "01")).isEqualTo("1");
        Assertions.assertThat(play(nn, "10")).isEqualTo("1");
        Assertions.assertThat(play(nn, "11")).isEqualTo("1");
    }

    @Test
    public void and() {
        NeuralNetwork nn = new NeuralNetwork(2, 1);
        for (int i = 0; i < 30; i++) {
            learn(nn, "00", "0");
            learn(nn, "01", "0");
            learn(nn, "10", "0");
            learn(nn, "11", "1");
        }

        Assertions.assertThat(play(nn, "00")).isEqualTo("0");
        Assertions.assertThat(play(nn, "01")).isEqualTo("0");
        Assertions.assertThat(play(nn, "10")).isEqualTo("0");
        Assertions.assertThat(play(nn, "11")).isEqualTo("1");
    }

    @Test
    public void reverseBits() {
        NeuralNetwork nn = new NeuralNetwork(3, 3);

        for (int i = 0; i < 7; i++) {
            learn(nn, "000", "000");
            learn(nn, "001", "100");
            learn(nn, "010", "010");
            learn(nn, "011", "110");
            learn(nn, "100", "001");
            learn(nn, "101", "101");
            learn(nn, "110", "011");
            learn(nn, "111", "111");
        }

        Assertions.assertThat(play(nn, "000")).isEqualTo("000");
        Assertions.assertThat(play(nn, "001")).isEqualTo("100");
        Assertions.assertThat(play(nn, "010")).isEqualTo("010");
        Assertions.assertThat(play(nn, "011")).isEqualTo("110");
        Assertions.assertThat(play(nn, "100")).isEqualTo("001");
        Assertions.assertThat(play(nn, "101")).isEqualTo("101");
        Assertions.assertThat(play(nn, "110")).isEqualTo("011");
        Assertions.assertThat(play(nn, "111")).isEqualTo("111");
    }

    @Test
    public void isOdd() {
        NeuralNetwork nn = new NeuralNetwork(6, 1);
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 64; j++) {
                nn.learn(intToBinaryDoubleArray(j, 6), intToBinaryDoubleArray(j % 2, 1), LEARNING_RATE);
            }
        }

        for (int j = 0; j < 64; j += 2) {
            Assertions.assertThat(listToString(nn.play(intToBinaryDoubleArray(j, 6)))).isEqualTo("0");
        }
        for (int j = 1; j < 64; j += 2) {
            Assertions.assertThat(listToString(nn.play(intToBinaryDoubleArray(j, 6)))).isEqualTo("1");
        }
    }

    @Test
    public void oneHiddenLayer() {
        NeuralNetwork nn = new NeuralNetwork(1, 1, 1);
        for (int i = 0; i < 250; i++) {
            learn(nn, "0", "0");
            learn(nn, "1", "1");
        }

        Assertions.assertThat(play(nn, "0")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1")).isEqualTo("1");
    }

    @Test
    public void xor() {
        NeuralNetwork nn = new NeuralNetwork(2, 2, 1);
        for (int i = 0; i < 274; i++) {
            learn(nn, "00", "0");
            learn(nn, "01", "1");
            learn(nn, "10", "1");
            learn(nn, "11", "0");
        }

        Assertions.assertThat(play(nn, "00")).isEqualTo("0");
        Assertions.assertThat(play(nn, "01")).isEqualTo("1");
        Assertions.assertThat(play(nn, "10")).isEqualTo("1");
        Assertions.assertThat(play(nn, "11")).isEqualTo("0");
    }

    @Test
    public void average() {
        NeuralNetwork nn = new NeuralNetwork(16, 4, 1);
        for (int i = 0; i < 100; i++) {
            learn(nn, "0011001001110000", "0");
            learn(nn, "0100000111011111", "1");
            learn(nn, "1000010111001100", "0");
            learn(nn, "1001010110011010", "1");
            learn(nn, "0011100011110011", "1");
            learn(nn, "1010110001110100", "1");
            learn(nn, "0110000011010100", "0");
            learn(nn, "0010001111010101", "1");
            learn(nn, "0001111010001100", "0");
            learn(nn, "1101001000100001", "0");
            learn(nn, "0010111101001000", "0");
            learn(nn, "0010011110110101", "1");
            learn(nn, "0001010110110110", "1");
            learn(nn, "0110010001011010", "0");
            learn(nn, "1110010111011010", "1");
            learn(nn, "0110010011000011", "0");
            learn(nn, "0011110110111000", "1");
            learn(nn, "1001000011001010", "0");
            learn(nn, "0111101001101110", "1");
            learn(nn, "1110011110011110", "1");
            learn(nn, "1001011011101011", "1");
            learn(nn, "0010011001000000", "0");
            learn(nn, "0101101000010000", "0");
            learn(nn, "0100010010101101", "0");
            learn(nn, "0111001010011000", "0");
            learn(nn, "1100111000001010", "0");
            learn(nn, "1101100111001010", "1");
            learn(nn, "1010111111000111", "1");
            learn(nn, "1100000100101101", "0");
            learn(nn, "0111010000000001", "0");
            learn(nn, "1000001001110001", "0");
            learn(nn, "1010110011111110", "1");
            learn(nn, "1100001101110000", "0");
            learn(nn, "1100010001000000", "0");
            learn(nn, "1001100111010111", "1");
            learn(nn, "0110001000100101", "0");
            learn(nn, "1110011101001000", "1");
            learn(nn, "1110110100011100", "1");
            learn(nn, "1000111111100001", "1");
            learn(nn, "0100000000011011", "0");
            learn(nn, "1000110001010000", "0");
            learn(nn, "0011101000001001", "0");
            learn(nn, "0110101111011011", "1");
            learn(nn, "1011000011101101", "1");
            learn(nn, "0011000000000010", "0");
            learn(nn, "0100100101101010", "0");
            learn(nn, "0010000011100011", "0");
            learn(nn, "0011011001100111", "1");
            learn(nn, "0000101101101100", "0");
            learn(nn, "0111111110011110", "1");
            learn(nn, "1110011010000100", "0");
            learn(nn, "0101001000100010", "0");
            learn(nn, "1000010011100101", "0");
            learn(nn, "0101110111011100", "1");
            learn(nn, "1010000001100101", "0");
            learn(nn, "0011011010110101", "1");
            learn(nn, "0101110110100010", "1");
            learn(nn, "1011111011101010", "1");
            learn(nn, "1100001111010101", "1");
            learn(nn, "1000111110111111", "1");
            learn(nn, "0010010110101101", "1");
            learn(nn, "1110011111000010", "1");
            learn(nn, "0111011111011110", "1");
            learn(nn, "0011011000000011", "0");
            learn(nn, "0000011001100011", "0");
            learn(nn, "0101001000011011", "0");
            learn(nn, "0100101100101101", "1");
            learn(nn, "1101110001110011", "1");
            learn(nn, "0011010100110101", "1");
            learn(nn, "0001101101110011", "1");
            learn(nn, "0110011000011011", "1");
            learn(nn, "0100010010100110", "0");
            learn(nn, "1000101110000110", "0");
            learn(nn, "1101100010000101", "0");
            learn(nn, "1010010101100111", "1");
            learn(nn, "0101001100110100", "0");
            learn(nn, "1001010111101111", "1");
            learn(nn, "1111101101100111", "1");
            learn(nn, "0100111101101010", "1");
            learn(nn, "0001000101001011", "0");
        }

        Assertions.assertThat(play(nn, "1000001010101010")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0000111000101101")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1101000001101011")).isEqualTo("1");
        Assertions.assertThat(play(nn, "1100111000110010")).isEqualTo("1");
    }

    @Test
    public void twoHiddenLayers() {
        NeuralNetwork nn = new NeuralNetwork(4, 2, 2, 1);
        for (int i = 0; i < 2430; i++) {
            learn(nn, "0000", "1");
            learn(nn, "0001", "0");
            learn(nn, "0010", "0");
            learn(nn, "0011", "0");
            learn(nn, "0100", "0");
            learn(nn, "0101", "1");
            learn(nn, "0110", "0");
            learn(nn, "0111", "0");
            learn(nn, "1000", "0");
            learn(nn, "1001", "0");
            learn(nn, "1010", "1");
            learn(nn, "1011", "0");
            learn(nn, "1100", "0");
            learn(nn, "1101", "0");
            learn(nn, "1110", "0");
            learn(nn, "1111", "1");
        }

        Assertions.assertThat(play(nn, "0000")).isEqualTo("1");
        Assertions.assertThat(play(nn, "0001")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0010")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0011")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0100")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0101")).isEqualTo("1");
        Assertions.assertThat(play(nn, "0110")).isEqualTo("0");
        Assertions.assertThat(play(nn, "0111")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1000")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1001")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1010")).isEqualTo("1");
        Assertions.assertThat(play(nn, "1011")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1100")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1101")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1110")).isEqualTo("0");
        Assertions.assertThat(play(nn, "1111")).isEqualTo("1");
    }

    private String listToString(List<BigDecimal> list) {
        return String.join("", list.stream().map(aBigDecimal -> aBigDecimal.doubleValue() > 0.5 ? "1" : "0").collect(Collectors.toList()));
    }

    private String toBinaryString(int i, int size) {
        return String.format("%0" + size + "d", Long.parseLong(Long.toBinaryString(i)));
    }

    private BigDecimal[] intToBinaryDoubleArray(int i, int size) {
        return binaryStringToBigDecimalArray(toBinaryString(i, size));
    }

    private BigDecimal[] binaryStringToBigDecimalArray(String binaryString) {
        List<BigDecimal> bigDecimals = binaryString
                .chars()
                .mapToObj(value -> '0' == value ? BigDecimal.ZERO : BigDecimal.ONE)
                .collect(Collectors.toList());

        BigDecimal[] result = new BigDecimal[binaryString.length()];
        for (int index = 0; index < binaryString.length(); index++) {
            result[index] = bigDecimals.get(index);
        }
        return result;
    }

    private void learn(NeuralNetwork nn, String inputs, String expectedOutputs) {
        nn.learn(binaryStringToBigDecimalArray(inputs), binaryStringToBigDecimalArray(expectedOutputs), LEARNING_RATE);
    }

    private String play(NeuralNetwork nn, String inputs) {
        return listToString(nn.play(binaryStringToBigDecimalArray(inputs)));
    }
}
