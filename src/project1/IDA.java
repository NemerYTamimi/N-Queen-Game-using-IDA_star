package project1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Iterative Deepening A* solver for N-Queens.
 *
 * threshold starts at h(root) and increases to the minimum f-value that
 * exceeded the previous threshold — the standard IDA* schedule.
 *
 * The search runs as an in-place backtracking DFS over a single mutable
 * board plus incrementally-maintained row/diagonal conflict counts. No
 * child Node objects or board copies are allocated while searching; each
 * frame allocates only one primitive long[] of candidate moves (sorted
 * best-first), so peak memory is O(depth * n^2) primitives rather than
 * O(depth * n^2) Node objects each holding a cloned int[n].
 */
public class IDA {

    private final Node initState;

    // Threshold used at each iteration; cleared by Project1.solve() before each run.
    static final List<Integer> thresholds = new ArrayList<>();

    // Mutable working state, allocated once and shared across all recursion.
    private int[] queens;   // queens[col] = row
    private int[] rowCnt;   // queens per row
    private int[] d1;       // queens per "/" diagonal, indexed r + c
    private int[] d2;       // queens per "\" diagonal, indexed r - c + n
    private int n;

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

        n = initState.n;
        queens = initState.queens.clone();
        rowCnt = new int[n];
        d1 = new int[2 * n];
        d2 = new int[2 * n];
        for (int c = 0; c < n; c++) {
            int r = queens[c];
            rowCnt[r]++;
            d1[r + c]++;
            d2[r - c + n]++;
        }

        int threshold = initState.h;
        while (true) {
            thresholds.add(threshold);
            System.out.println("Threshold = " + threshold);

            int[] next = {Integer.MAX_VALUE};
            Node found = search(0, initState.h, threshold, next);

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
     * Backtracking DFS bounded by threshold, operating on the shared mutable
     * board. g and h are carried in as primitives (no Node per frame). Populates
     * next[0] with the minimum f that exceeded the threshold.
     */
    private Node search(int g, int h, int threshold, int[] next) {
        int f = g + h;
        if (f > threshold) {
            if (f < next[0]) next[0] = f;
            return null;
        }
        if (h == 0) {
            return new Node(queens.clone(), g, 0);   // single allocation, only on success
        }

        // Enumerate every "move one queen within its column" child, computing its
        // heuristic by O(1) incremental update. Pack (childH, move) into a long so
        // the candidates sort best-first without boxing or building Node objects.
        // childG = g + 1 is constant across siblings, so ordering by childH == by f.
        long[] moves = new long[n * (n - 1)];
        int m = 0;
        for (int col = 0; col < n; col++) {
            int oldRow = queens[col];
            int removed = (rowCnt[oldRow] - 1)
                        + (d1[oldRow + col] - 1)
                        + (d2[oldRow - col + n] - 1);
            int hBase = h - removed;

            rowCnt[oldRow]--; d1[oldRow + col]--; d2[oldRow - col + n]--;
            for (int newRow = 0; newRow < n; newRow++) {
                if (newRow == oldRow) continue;
                int added = rowCnt[newRow] + d1[newRow + col] + d2[newRow - col + n];
                int childH = hBase + added;
                moves[m++] = ((long) childH << 32) | (col * n + newRow);
            }
            rowCnt[oldRow]++; d1[oldRow + col]++; d2[oldRow - col + n]++;
        }
        Arrays.sort(moves);   // ascending childH, i.e. best-first within the depth bound

        int childG = g + 1;
        for (long move : moves) {
            int childH = (int) (move >>> 32);
            int code   = (int) move;
            int col    = code / n;
            int newRow = code % n;
            int oldRow = queens[col];

            // apply
            queens[col] = newRow;
            rowCnt[oldRow]--; d1[oldRow + col]--; d2[oldRow - col + n]--;
            rowCnt[newRow]++; d1[newRow + col]++; d2[newRow - col + n]++;

            Node result = search(childG, childH, threshold, next);

            // undo
            queens[col] = oldRow;
            rowCnt[newRow]--; d1[newRow + col]--; d2[newRow - col + n]--;
            rowCnt[oldRow]++; d1[oldRow + col]++; d2[oldRow - col + n]++;

            if (result != null) return result;
        }
        return null;
    }
}
