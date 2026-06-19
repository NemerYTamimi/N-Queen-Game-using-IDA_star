package project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Project1 {

    static Node iniNode;
    public static Node goal;

    public static void main(String[] args) {
        drawMain();
    }

    public static void solve(int[] initArray) {
        IDA.thresholds.clear();

        iniNode = new Node(initArray, 0);
        System.out.println(iniNode);

        IDA ida = new IDA(iniNode);

        // Benchmark the search: wall-clock time and bytes allocated on this thread.
        long memBefore = allocatedBytes();
        long t0 = System.nanoTime();
        goal = ida.run();
        long elapsedNanos = System.nanoTime() - t0;
        long memAfter = allocatedBytes();
        long bytesAllocated =
            (memBefore < 0 || memAfter < 0) ? -1 : memAfter - memBefore;

        if (goal == null) {
            System.out.println("No solution found.");
            javax.swing.JOptionPane.showMessageDialog(null,
                "No solution found for this initial configuration.");
            return;
        }

        drawSolution();
        drawBenchmark(ida, elapsedNanos, bytesAllocated);
    }

    /**
     * Bytes allocated by the current thread so far, or -1 if the JVM does not
     * support per-thread allocation tracking.
     */
    private static long allocatedBytes() {
        try {
            java.lang.management.ThreadMXBean bean =
                java.lang.management.ManagementFactory.getThreadMXBean();
            if (bean instanceof com.sun.management.ThreadMXBean) {
                com.sun.management.ThreadMXBean sun = (com.sun.management.ThreadMXBean) bean;
                if (sun.isThreadAllocatedMemorySupported()) {
                    return sun.getThreadAllocatedBytes(Thread.currentThread().getId());
                }
            }
        } catch (Throwable ignore) {
            // fall through to unsupported
        }
        return -1;
    }

    private static void drawBenchmark(IDA ida, long elapsedNanos, long bytesAllocated) {
        BenchmarkFrame bf = new BenchmarkFrame(
            goal.n,
            elapsedNanos,
            bytesAllocated,
            ida.nodesExpanded,
            ida.nodesGenerated,
            IDA.thresholds.size(),
            goal.g,
            IDA.thresholds);
        bf.setLocation(880, 120);
        bf.setVisible(true);
    }

    public static void details() {
        if (goal == null) return;

        StringBuilder sb = new StringBuilder();
        if (goal.g == 0) {
            sb.append("The initial state is already a goal:\n\n")
              .append("-------------------------------\n")
              .append(iniNode.printMatrix())
              .append("-------------------------------\n\n");
        } else {
            sb.append("Initial state:\n\n")
              .append("-------------------------------\n")
              .append(iniNode.printMatrix())
              .append("-------------------------------\n\n")
              .append("Goal state:\n\n")
              .append("-------------------------------\n")
              .append(goal.printMatrix())
              .append("-------------------------------\n\n");
        }

        sb.append("Operators: move one queen up or down in its column.\n")
          .append("Each step counts as 1 unit of path cost.\n\n");

        for (int i = 0; i < IDA.thresholds.size(); i++) {
            sb.append("Cutoff[").append(i).append("] = ")
              .append(IDA.thresholds.get(i)).append('\n');
        }

        if (goal.g != 0) {
            sb.append("\nTotal path cost (moves): ").append(goal.g).append('\n')
              .append("The ").append(goal.n)
              .append(" queens can be placed in ").append(goal.g).append(" move(s).");
        }

        DetailsFrame detailsFrame = new DetailsFrame(sb.toString());
        detailsFrame.setVisible(true);
        detailsFrame.setLocation(300, 100);
    }

    private static void drawSolution() {
        int n = goal.n;
        int[][] matrix = goal.toMatrix();

        GameBoard gb = new GameBoard(n, false);
        JComponent jc = gb.getGui();
        jc.setPreferredSize(new Dimension(500, 500));

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (matrix[row][col] == 1) {
                    if (GameBoard.icon1 == null)
                        gb.c1squares[col][row].setBackground(Color.BLACK);
                    else
                        gb.c1squares[col][row].setIcon(GameBoard.icon1);
                }
            }
        }

        JPanel main = new JPanel();
        main.add(jc);

        JFrame frame = new JFrame("N Queen Solution");
        frame.add(BorderLayout.CENTER, main);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(550, 550));
        frame.setLocation(330, 100);
        frame.pack();
        frame.setVisible(true);
    }

    private static void drawMain() {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
        frame.setLocation(400, 100);
    }
}
