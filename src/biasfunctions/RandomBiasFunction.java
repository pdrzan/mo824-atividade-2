package biasfunctions;

import java.util.ArrayList;
import java.util.Random;

public class RandomBiasFunction implements BiasFunction<Integer> {
    static Random rng = new Random(0);

    public int selectCandidate(ArrayList<Integer> RCL) {
        return rng.nextInt(RCL.size());
    }
}
