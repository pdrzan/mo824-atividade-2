package biasfunctions;

import problems.Evaluator;
import solutions.Solution;

import java.util.ArrayList;
import java.util.Random;

public class RandomBiasFunction implements BiasFunction<Integer> {
    static Random rng = new Random(0);

    public int selectCandidate(ArrayList<Integer> RCL, Evaluator objFunction, Solution sol) {
        return rng.nextInt(RCL.size());
    }

    public String toString() {
        return "Random Bias Function";
    }
}
