/**
 * 
 */
package metaheuristics.grasp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import biasfunctions.BiasFunction;
import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic GRASP (Greedy Randomized Adaptive Search
 * Procedure). It consider a minimization problem.
 * 
 * @author ccavellucci, fusberti
 * @param <E>
 *            Generic type of the element which composes the solution.
 */
public abstract class AbstractGRASP<E> {

	/**
	 * flag that indicates whether the code should print more information on
	 * screen
	 */
	public static boolean verbose = true;

	/**
	 * a random number generator
	 */
	static Random rng = new Random(0);

	/**
	 * the objective function being optimized
	 */
	public Evaluator<E> ObjFunction;

	/**
	 * the GRASP greediness-randomness parameter
	 */
	protected Double alpha;

	/**
	 * the best (incumbent) solution cost
	 */
	protected Double bestCost;

	/**
	 * the current solution cost
	 */
	protected Double cost;

	/**
	 * the best solution
	 */
	protected Solution<E> bestSol;

	/**
	 * the current solution
	 */
	protected Solution<E> sol;

	/**
	 * time in secconds to run GRASP
	 */
	protected Integer seconds;

	/**
	 * the Candidate List of elements to enter the solution.
	 */
	protected ArrayList<E> CL;

	/**
	 * the Restricted Candidate List of elements to enter the solution.
	 */
	protected ArrayList<E> RCL;

    /**
     * Bias function to select that select the elements from RCL
     * that will enter the solution.
     */
    protected BiasFunction<E> biasFunction;

    /**
     * When true adopts the first-improving strategy. When false
     * adopts the best improving strategy.
     */
    protected boolean isFirstImproving;

	/**
	 * Creates the Candidate List, which is an ArrayList of candidate elements
	 * that can enter a solution.
	 * 
	 * @return The Candidate List.
	 */
	public abstract ArrayList<E> makeCL();

	/**
	 * Creates the Restricted Candidate List, which is an ArrayList of the best
	 * candidate elements that can enter a solution. The best candidates are
	 * defined through a quality threshold, delimited by the GRASP
	 * {@link #alpha} greedyness-randomness parameter.
	 * 
	 * @return The Restricted Candidate List.
	 */
	public abstract ArrayList<E> makeRCL();

	/**
	 * Updates the Candidate List according to the current solution
	 * {@link #sol}. In other words, this method is responsible for
	 * updating which elements are still viable to take part into the solution.
	 */
	public abstract void updateCL();

	/**
	 * Creates a new solution which is empty, i.e., does not contain any
	 * element.
	 * 
	 * @return An empty solution.
	 */
	public abstract Solution<E> createEmptySol();

	/**
	 * The GRASP local search phase is responsible for repeatedly applying a
	 * neighborhood operation while the solution is getting improved, i.e.,
	 * until a local optimum is attained.
	 * 
	 * @return An local optimum solution.
	 */
	public abstract Solution<E> localSearch();

	/**
	 * Constructor for the AbstractGRASP class.
	 * 
	 * @param objFunction
	 *            The objective function being minimized.
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param seconds
	 *            The number of seconds which the GRASP will be executed.
	 */
	public AbstractGRASP(Evaluator<E> objFunction, Double alpha, Integer seconds, BiasFunction<E> biasFunction, boolean isFirstImproving) {
		this.ObjFunction = objFunction;
		this.alpha = alpha;
		this.seconds = seconds;
        this.biasFunction = biasFunction;
        this.isFirstImproving = isFirstImproving;
	}
	
	/**
	 * The GRASP constructive heuristic, which is responsible for building a
	 * feasible solution by selecting in a greedy-random fashion, candidate
	 * elements to enter the solution.
	 *
     * @param numberOfRandomIterations
     *      The number of iterations that randomness will be applied
     *      (Random plus greedy).After numberOfRandomIterations iterations,
     *      only a greedy strategy will be used.
	 * @return A feasible solution to the problem being minimized.
	 */
	public Solution<E> constructiveHeuristic(int numberOfRandomIterations) {

		CL = makeCL();
		RCL = makeRCL();
		sol = createEmptySol();
		cost = Double.POSITIVE_INFINITY;
        int currentInteration = 0;

		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria()) {

			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			cost = ObjFunction.evaluate(sol);
			updateCL();

            if (CL.isEmpty()) break;

            E bestCandidate = CL.get(0);

			/*
			 * Explore all candidate elements to enter the solution, saving the
			 * highest and lowest cost variation achieved by the candidates.
			 */
			for (E c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
				if (deltaCost < minCost) {
                    minCost = deltaCost;
                    bestCandidate = c;
                }
				if (deltaCost > maxCost)
					maxCost = deltaCost;
			}

            if (Double.isInfinite(minCost) || Double.isInfinite(maxCost)) break;

			/*
			 * Among all candidates, insert into the RCL those with the highest
			 * performance using parameter alpha as threshold.
			 */
			for (E c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
				if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
					RCL.add(c);
				}
			}

            RCL.sort(Comparator.comparingDouble(c -> ObjFunction.evaluateInsertionCost(c, sol)));

            E inCand = bestCandidate;

            if (RCL.isEmpty()) break;

            if (currentInteration < numberOfRandomIterations) {
                /* Choose a candidate randomly from the RCL */
                int indexCandidateToEnterSolution = biasFunction.selectCandidate(RCL, ObjFunction, sol);
                inCand = RCL.get(indexCandidateToEnterSolution);
            }

			CL.remove(inCand);
			sol.add(inCand);
			ObjFunction.evaluate(sol);
			RCL.clear();

            currentInteration++;
		}

		return sol;
	}

	/**
	 * The GRASP mainframe. It consists of a loop, in which each iteration goes
	 * through the constructive heuristic and local search. The best solution is
	 * returned as result.
	 *
     * @param numberOfRandomIterations
     *      Number of iterations in construction phase that randomness will
     *      be applied (Random plus greedy)
	 * @return The best feasible solution obtained throughout all iterations.
	 */
	public Solution<E> solve(int numberOfRandomIterations) {
        numberOfRandomIterations = Math.max(numberOfRandomIterations, 0);

		bestSol = createEmptySol();
        bestSol.cost = Double.POSITIVE_INFINITY;

        long startTime = System.currentTimeMillis();
        long endTime = startTime;

		for (int i = 0; endTime - startTime < seconds * 1000; i++) {
			constructiveHeuristic(numberOfRandomIterations);
			localSearch();
			
//			System.out.println("iter: " + i + " -> " + sol.cost);
			if (bestSol.cost > sol.cost) {
				bestSol = new Solution<E>(sol);
				if (verbose)
					System.out.println("(Iter. " + i + "|" + numberOfRandomIterations + ") BestSol = " + bestSol);
			}

            endTime = System.currentTimeMillis();
		}

		return bestSol;
	}

	/**
	 * A standard stopping criteria for the constructive heuristic is to repeat
	 * until the current solution improves by inserting a new candidate
	 * element.
	 * 
	 * @return true if the criteria is met.
	 */
	public Boolean constructiveStopCriteria() {
		return (cost >= sol.cost) ? false : true;
	}

}
