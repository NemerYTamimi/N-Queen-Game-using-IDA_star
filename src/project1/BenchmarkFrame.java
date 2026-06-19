package project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Benchmark window shown next to the solution board after a solve. Displays the
 * measured cost of the IDA* search (wall-clock time, memory allocated, nodes
 * expanded/generated, iterations) together with the algorithm's theoretical
 * time and space complexity.
 *
 * Hand-written Swing (no NetBeans .form) so it can be edited freely.
 */
public class BenchmarkFrame extends JFrame {

    public BenchmarkFrame(int n,
                          long elapsedNanos,
                          long bytesAllocated,
                          long nodesExpanded,
                          long nodesGenerated,
                          int iterations,
                          int pathCost,
                          List<Integer> thresholds) {

        setTitle("Benchmark — IDA* Solver");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // ---- Header ----
        JLabel header = new JLabel("IDA*  Performance Benchmark");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(30, 30, 60));
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 4, 18));
        add(header, BorderLayout.NORTH);

        // ---- Measured metrics ----
        JPanel metrics = new JPanel(new GridBagLayout());
        metrics.setBackground(Color.WHITE);
        metrics.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        int row = 0;
        addRow(metrics, row++, "Board size",        n + " × " + n + "  (" + n + " queens)");
        addRow(metrics, row++, "Solve time",        formatTime(elapsedNanos));
        addRow(metrics, row++, "Memory allocated",  formatBytes(bytesAllocated));
        addRow(metrics, row++, "Nodes expanded",    String.format("%,d", nodesExpanded));
        addRow(metrics, row++, "Nodes generated",   String.format("%,d", nodesGenerated));
        addRow(metrics, row++, "IDA* iterations",   String.format("%,d", iterations));
        addRow(metrics, row++, "Solution path cost", pathCost + " move(s)");

        add(metrics, BorderLayout.CENTER);

        // ---- Complexity notes ----
        JTextArea notes = new JTextArea(complexityText(n, nodesGenerated, thresholds));
        notes.setEditable(false);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setFont(new Font("Monospaced", Font.PLAIN, 13));
        notes.setBackground(new Color(247, 247, 250));
        notes.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane sp = new JScrollPane(notes);
        sp.setBorder(BorderFactory.createTitledBorder("Time & Space Complexity"));
        sp.setPreferredSize(new Dimension(440, 200));
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(Color.WHITE);
        south.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        south.add(sp, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(470, 520));
        pack();
    }

    private static void addRow(JPanel panel, int row, String label, String value) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = row;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 14));
        l.setForeground(new Color(90, 90, 90));
        g.gridx = 0;
        g.weightx = 0;
        panel.add(l, g);

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 14));
        v.setForeground(new Color(20, 20, 40));
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 28, 4, 4);
        panel.add(v, g);
    }

    private static String formatTime(long nanos) {
        double ms = nanos / 1_000_000.0;
        if (ms < 1.0) {
            return String.format("%.3f ms  (%,d ns)", ms, nanos);
        }
        return String.format("%,.3f ms  (%,d ns)", ms, nanos);
    }

    private static String formatBytes(long bytes) {
        if (bytes < 0) return "n/a (allocation tracking unsupported)";
        String human;
        if (bytes >= 1024L * 1024L) {
            human = String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else if (bytes >= 1024L) {
            human = String.format("%.2f KB", bytes / 1024.0);
        } else {
            human = bytes + " B";
        }
        return String.format("%,d bytes  (~%s)", bytes, human);
    }

    private static String complexityText(int n, long nodesGenerated, List<Integer> thresholds) {
        long branching = (long) n * (n - 1);
        StringBuilder sb = new StringBuilder();

        sb.append("TIME — O(b^d), exponential in the solution depth d. ");
        sb.append("Branching factor b = n(n-1) = ").append(branching).append(". ");
        sb.append("Iterative deepening re-expands shallow nodes on every pass. ");
        sb.append("Measured work this run: ").append(String.format("%,d", nodesGenerated));
        sb.append(" nodes generated over ").append(thresholds.size()).append(" iteration(s).");

        sb.append("\n\n");

        sb.append("SPACE — O(d · n²), linear in depth. ");
        sb.append("In-place backtracking keeps one shared board; each recursion ");
        sb.append("frame holds only an O(n²) move buffer, so no per-node board ");
        sb.append("copies are retained on the search path.");

        sb.append("\n\n");

        sb.append("Cutoff thresholds: ");
        for (int i = 0; i < thresholds.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(thresholds.get(i));
        }
        return sb.toString();
    }
}
