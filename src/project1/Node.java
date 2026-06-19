package project1;

/**
 * Board state: queens[col] = row (one queen per column guaranteed).
 * Heuristic: number of attacking pairs across rows and both diagonals.
 *
 * Nodes are immutable value holders for the initial and goal states only;
 * the IDA* search itself runs over a single mutable board (see IDA) and
 * never allocates intermediate Nodes.
 */
public class Node {

    final int[] queens;
    final int g;
    final int h;
    final int f;
    final int n;

    Node(int[] queens, int g) {
        this.queens = queens;
        this.n = queens.length;
        this.g = g;
        this.h = computeH(queens);
        this.f = g + h;
    }

    // Package-private: used by IDA to build the goal node with a known heuristic.
    Node(int[] queens, int g, int h) {
        this.queens = queens;
        this.n = queens.length;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }

    static int computeH(int[] queens) {
        int n = queens.length;
        int[] rowCnt = new int[n];
        int[] d1    = new int[2 * n];   // r + c
        int[] d2    = new int[2 * n];   // r - c + n
        for (int c = 0; c < n; c++) {
            int r = queens[c];
            rowCnt[r]++;
            d1[r + c]++;
            d2[r - c + n]++;
        }
        return pairs(rowCnt, d1, d2, n);
    }

    private static int pairs(int[] rowCnt, int[] d1, int[] d2, int n) {
        int total = 0;
        for (int i = 0; i < n; i++) {
            int k = rowCnt[i];
            if (k > 1) total += k * (k - 1) >> 1;
        }
        for (int i = 0; i < 2 * n; i++) {
            int k = d1[i];
            if (k > 1) total += k * (k - 1) >> 1;
            k = d2[i];
            if (k > 1) total += k * (k - 1) >> 1;
        }
        return total;
    }

    int[][] toMatrix() {
        int[][] m = new int[n][n];
        for (int c = 0; c < n; c++) {
            m[queens[c]][c] = 1;
        }
        return m;
    }

    String printMatrix() {
        int[][] m = toMatrix();
        StringBuilder sb = new StringBuilder();
        for (int[] row : m) {
            for (int cell : row) {
                sb.append(' ').append(cell).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "State{ g=" + g + ", h=" + h + ", f=" + f + ", n=" + n + "}\n" + printMatrix();
    }
}
