package biasfunctions;

import java.util.ArrayList;

public interface BiasFunction<E> {
    public int selectCandidate(ArrayList<E> RCL);
}
