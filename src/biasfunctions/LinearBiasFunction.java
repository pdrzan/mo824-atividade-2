package biasfunctions;

import problems.Evaluator;
import solutions.Solution;
import metaheuristics.grasp.AbstractGRASP;
import java.util.ArrayList;
import java.util.Random;

public class LinearBiasFunction implements BiasFunction<Integer> {
    static Random rng = new Random(0);

    @Override
    public int selectCandidate(ArrayList<Integer> RCL, Evaluator objFunction, Solution sol) {
        int n = RCL.size();
        if (n <= 1) return 0;

        // Calculate ranks
        double[] ranks = new double[n];
        for (int i = 0; i < n; i++) ranks[i] = objFunction.evaluateInsertionCost(RCL.get(i), sol);

        // Order positions by ranks
        ArrayList<Integer> pos = new ArrayList<>(n);
        for (int i = 0; i < n; i++) pos.add(i);
        pos.sort((a, b) -> Double.compare(ranks[a], ranks[b]));

        // apply linear bias w[i] = 1/rank and sum the results
        double[] w = new double[n];
        double sum = 0.0;
        for (int rank = 1; rank <= n; rank++) {
            int i = pos.get(rank - 1);
            w[i] = 1.0 / rank;
            sum += w[i];
        }

        // pi function
        double r = rng.nextDouble() * sum, acc = 0.0;
        for (int i = 0; i < n; i++) {
            acc += w[i];
            if (r <= acc) return i;
        }

        return n - 1;
    }

    public String toString() {
        return "Linear Bias Function";
    }
}