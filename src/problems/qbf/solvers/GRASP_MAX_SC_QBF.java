package problems.qbf.solvers;

import biasfunctions.BiasFunction;
import biasfunctions.RandomBiasFunction;
import biasfunctions.LinearBiasFunction;
import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.MAX_SC_QBF_Inverse;
import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class GRASP_MAX_SC_QBF extends AbstractGRASP<Integer> {

	/**
	 * Constructor for the GRASP_MAX_SC_QBF class.
	 *
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public GRASP_MAX_SC_QBF(Double alpha, Integer iterations, BiasFunction biasFunction, String filename, boolean isFirstImproving) throws IOException {
		super(new MAX_SC_QBF_Inverse(filename), alpha, iterations, biasFunction, isFirstImproving);
        System.out.println("Using the " + biasFunction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	@Override
	public ArrayList<Integer> makeCL() {

		ArrayList<Integer> _CL = new ArrayList<Integer>();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			Integer cand = i;
			_CL.add(cand);
		}

		return _CL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
	 */
	@Override
	public ArrayList<Integer> makeRCL() {

		ArrayList<Integer> _RCL = new ArrayList<Integer>();

		return _RCL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {

        // Shuffles to make first-improvement more efficiently
        if (isFirstImproving) {
            Collections.shuffle(CL);
        }

	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution and it attributes a
	 * zero cost, since it is known that a QBF solution with all variables set
	 * to zero has also zero cost.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();
		sol.cost = 0.0;
		return sol;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The local search operator developed for the QBF objective function is
	 * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
	 */
	@Override
	public Solution<Integer> localSearch() {

		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();
				
			// Evaluate insertions
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;

                    if (isFirstImproving && minDeltaCost < -Double.MIN_VALUE) break;
				}
			}

            if (!isFirstImproving || minDeltaCost >= -Double.MIN_VALUE) {
                // Evaluate removals
                for (Integer candOut : sol) {
                    double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = null;
                        bestCandOut = candOut;

                        if (isFirstImproving && minDeltaCost < -Double.MIN_VALUE) break;
                    }
                }
            }


            if (!isFirstImproving || minDeltaCost >= -Double.MIN_VALUE) {
                // Evaluate exchanges
                for (Integer candIn : CL) {
                    for (Integer candOut : sol) {
                        double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                        if (deltaCost < minDeltaCost) {
                            minDeltaCost = deltaCost;
                            bestCandIn = candIn;
                            bestCandOut = candOut;

                            if (isFirstImproving && minDeltaCost < -Double.MIN_VALUE) break;
                        }
                    }
                }
            }

			// Implement the move, if it reduces the solution cost.
			if (minDeltaCost < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					sol.remove(bestCandOut);
					CL.add(bestCandOut);
				}
				if (bestCandIn != null) {
					sol.add(bestCandIn);
					CL.remove(bestCandIn);
				}
				ObjFunction.evaluate(sol);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);

		return sol;
	}

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

//        String instanceFilePath = "instances/max_sc_qbf/max_sc_qbf-n_100-k_5.txt";
        String instanceFilePath = "instances/max_sc_qbf_artur/instance_6.txt";

        Double alpha = 0.05;
        int iterations = 1000;
        int numberOfRandomIterations = 500;

		long startTime = System.currentTimeMillis();

		GRASP_MAX_SC_QBF grasp = new GRASP_MAX_SC_QBF(alpha, iterations, new LinearBiasFunction(), instanceFilePath, false);
//        GRASP_MAX_SC_QBF grasp = new GRASP_MAX_SC_QBF(alpha, iterations, new RandomBiasFunction(), instanceFilePath, true);
		Solution<Integer> bestSol = grasp.solve(numberOfRandomIterations);

		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;

        System.out.println("maxVal = " + bestSol);
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}

}
