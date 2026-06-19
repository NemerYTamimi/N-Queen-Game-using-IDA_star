package project1;

import java.util.ArrayList;
import java.util.List;

/**
 * Board state: queens[col] = row (one queen per column guaranteed).
 * Heuristic: number of attacking pairs across rows and both diagonals.
 * Child heuristics are computed with O(1) incremental updates from the parent.
 */
public class Node implements Comparable<Node> {

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

    // Package-private: used by nextStates() for O(1) child heuristic.
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

    List<Node> nextStates() {
        // Build conflict-count arrays for this state once — O(n).
        int[] rowCnt = new int[n];
        int[] d1    = new int[2 * n];
        int[] d2    = new int[2 * n];
        for (int c = 0; c < n; c++) {
            int r = queens[c];
            rowCnt[r]++;
            d1[r + c]++;
            d2[r - c + n]++;
        }

        List<Node> children = new ArrayList<>(n * (n - 1));
        int childG = g + 1;

        for (int col = 0; col < n; col++) {
            int oldRow = queens[col];

            // Pairs that involve the queen at (col, oldRow).
            int removedPairs = (rowCnt[oldRow] - 1)
                             + (d1[oldRow + col] - 1)
                             + (d2[oldRow - col + n] - 1);
            int hBase = h - removedPairs;

            // Temporarily remove this queen from the tracking arrays.
            rowCnt[oldRow]--;
            d1[oldRow + col]--;
            d2[oldRow - col + n]--;

            for (int newRow = 0; newRow < n; newRow++) {
                if (newRow == oldRow) continue;

                // Pairs added by placing queen at (col, newRow).
                int addedPairs = rowCnt[newRow]
                               + d1[newRow + col]
                               + d2[newRow - col + n];

                int[] childQueens = queens.clone();
                childQueens[col] = newRow;
                children.add(new Node(childQueens, childG, hBase + addedPairs));
            }

            // Restore.
            rowCnt[oldRow]++;
            d1[oldRow + col]++;
            d2[oldRow - col + n]++;
        }

        return children;
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
    public int compareTo(Node o) {
        return Integer.compare(this.f, o.f);
    }

    @Override
    public String toString() {
        return "State{ g=" + g + ", h=" + h + ", f=" + f + ", n=" + n + "}\n" + printMatrix();
    }
}
