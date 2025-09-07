package biasfunctions;

import problems.Evaluator;
import solutions.Solution;
import java.util.ArrayList;

public interface BiasFunction<E> {

    public int selectCandidate(ArrayList<E> RCL, Evaluator objFunction, Solution currentSolution);
}
