package project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Iterative Deepening A* solver for N-Queens.
 *
 * threshold starts at h(root) and increases to the minimum f-value that
 * exceeded the previous threshold — the standard IDA* schedule.
 */
public class IDA {

    private final Node initState;

    // Threshold used at each iteration; cleared by Project1.solve() before each run.
    static final List<Integer> thresholds = new ArrayList<>();

    IDA(Node initState) {
        this.initState = initState;
    }

    /**
     * Returns the goal node, or null if no solution exists.
     */
    Node run() {
        if (initState.h == 0) {
            return initState;
        }

        int threshold = initState.h;
        while (true) {
            thresholds.add(threshold);
            System.out.println("Threshold = " + threshold);

            int[] next = {Integer.MAX_VALUE};
            Node found = search(initState, threshold, next);

            if (found != null) {
                System.out.println("Solution found at g=" + found.g);
                return found;
            }
            if (next[0] == Integer.MAX_VALUE) {
                return null;   // exhausted search space — no solution
            }
            threshold = next[0];
        }
    }

    /**
     * DFS bounded by threshold. Populates next[0] with the minimum f that
     * exceeded the threshold (the next threshold candidate).
     */
    private Node search(Node node, int threshold, int[] next) {
        int f = node.f;
        if (f > threshold) {
            if (f < next[0]) next[0] = f;
            return null;
        }
        if (node.h == 0) {
            return node;
        }

        List<Node> children = node.nextStates();
        Collections.sort(children);          // best-first within the depth bound

        for (Node child : children) {
            Node result = search(child, threshold, next);
            if (result != null) return result;
        }
        return null;
    }
}
