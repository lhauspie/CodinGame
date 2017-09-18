package puzzle.binary.neural.network;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int nbInputs = in.nextInt();
        int nbOutputs = in.nextInt();
        int nbHiddenLayers = in.nextInt();
        int nbTestInputs = in.nextInt();
        int nbTrainingExamples = in.nextInt();
        int nbTrainingIterations = in.nextInt();

        System.err.println("1 : " + nbInputs + " " + nbOutputs + " " + nbHiddenLayers + " " + nbTestInputs + " " + nbTrainingExamples + " " + nbTrainingIterations);

        // init layers description : number of nodes per layer including input an output layers
        in.nextLine();
        int[] layerNodesNumber = new int[nbHiddenLayers + 2];
        layerNodesNumber[0] = nbInputs;
        for (int i = 0; i < nbHiddenLayers; i++) {
            int nodes = in.nextInt();
            layerNodesNumber[i + 1] = nodes;
        }
        layerNodesNumber[nbHiddenLayers + 1] = nbOutputs;

        in.nextLine();
        List<BigDecimal[]> testInputs = new ArrayList<>();
        for (int i = 0; i < nbTestInputs; i++) {
            String testInputsStr = in.nextLine();
            BigDecimal[] testInputsArray = new BigDecimal[nbInputs];
            for (int j = 0; j < nbInputs; j++) {
                testInputsArray[j] = '0' == testInputsStr.charAt(j) ? BigDecimal.ZERO : BigDecimal.ONE;
            }
            testInputs.add(testInputsArray);
            System.err.println("2 : testInputs" + Arrays.toString(testInputsArray));
        }

        List<BigDecimal[]> trainingInputs = new ArrayList<>();
        List<BigDecimal[]> trainingOutputs = new ArrayList<>();
        for (int i = 0; i < nbTrainingExamples; i++) {
            String trainingInputsStr = in.next();
            String expectedOutputsStr = in.next();

            BigDecimal[] trainingInputsArray = new BigDecimal[nbInputs];
            for (int j = 0; j < nbInputs; j++) {
                trainingInputsArray[j] = '0' == trainingInputsStr.charAt(j) ? BigDecimal.ZERO : BigDecimal.ONE;
            }
            trainingInputs.add(trainingInputsArray);

            BigDecimal[] trainingOutputsArray = new BigDecimal[nbOutputs];
            for (int j = 0; j < nbOutputs; j++) {
                trainingOutputsArray[j] = '0' == expectedOutputsStr.charAt(j) ? BigDecimal.ZERO : BigDecimal.ONE;
            }
            trainingOutputs.add(trainingOutputsArray);

            System.err.println("3 : trainingInputs" + Arrays.toString(trainingInputsArray) + " - trainingOutputs" + Arrays.toString(trainingOutputsArray));
        }


        System.err.println("neural network configuration is : " + Arrays.toString(layerNodesNumber));
        NeuralNetwork nn = new NeuralNetwork(layerNodesNumber);

        for (int i = 0; i < nbTrainingIterations; i++) {
            for (int j = 0; j < trainingInputs.size(); j++) {
                System.err.println("learn : inputs" + Arrays.toString(trainingInputs.get(j)) + ", ouputs" + Arrays.toString(trainingOutputs.get(j)));
                nn.learn(trainingInputs.get(j), trainingOutputs.get(j), BigDecimal.valueOf(0.5));
            }
        }

        for (int i = 0; i < nbTestInputs; i++) {
            BigDecimal[] inputsArray = testInputs.get(i);
            System.err.println(i + " : inputsArray" + Arrays.toString(inputsArray));
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            List<String> outputsList = new ArrayList<>();
            for (BigDecimal output : nn.play(inputsArray)) {
                outputsList.add(output.doubleValue() > 0.5 ? "1" : "0");
            }

            System.out.println(String.join("", outputsList));
        }
    }
}
