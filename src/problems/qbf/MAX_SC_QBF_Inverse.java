package problems.qbf;

import solutions.Solution;
import java.io.IOException;

public class MAX_SC_QBF_Inverse extends MAX_SC_QBF {

    public MAX_SC_QBF_Inverse(String filename) throws IOException {
        super(filename);
    }

    @Override
    public Double evaluate(Solution<Integer> sol) {
        Double v = super.evaluate(sol);
        return -v;
    }

    @Override
    public Double evaluateInsertionCost(Integer elem, Solution<Integer> sol) {
        Double d = super.evaluateInsertionCost(elem, sol);
        return -d;
    }

    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {
        Double d = super.evaluateRemovalCost(elem, sol);
        return -d;
    }

    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {
        Double d = super.evaluateExchangeCost(elemIn, elemOut, sol);
        return -d;
    }
}
