# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java Swing desktop application that solves the N-Queens problem using the IDA* (Iterative Deepening A*) algorithm. Built as a NetBeans Ant project targeting Java 8. There is no test suite.

## Build & Run Commands

This is a NetBeans Ant project. Use `ant` from the project root:

```bash
# Compile
ant compile

# Build JAR
ant jar

# Run the application
ant run

# Clean build artifacts
ant clean

# Clean and rebuild
ant clean && ant jar
```

The compiled JAR is output to `dist/project1_1.jar`. The main class is `project1.Project1`.

You can also run the compiled JAR directly (requires JavaFX on the classpath):
```bash
java -jar dist/project1_1.jar
```

## Architecture

### Core Data Flow

1. **`MainFrame`** — Swing GUI entry point. User enters board size (minimum 4), clicks "Initialize Board" to interactively place queens on a `GameBoard`, then clicks "Solve".
2. **`Project1.solve(int[] initArray)`** — Called by the UI. Creates the initial `Node`, runs `IDA`, then opens a result `GameBoard` window displaying the solution. Holds the static global `Project1.goal` (the solved `Node`).
3. **`IDA`** — Runs the search. On completion, `Project1.goal` holds the goal node and `IDA.ths` holds the list of threshold values used.
4. **`Project1.details()`** — Called by the "Details" button; opens a `DetailsFrame` showing the initial/goal board states, cutoff thresholds, and total path cost.

### State Representation

Board state is encoded as `int[] stateArray` of length n, where `stateArray[col] = row` — one queen per column by design. The `Node.toMatrix()` method converts this to a 2D `int[n][n]` matrix for display and heuristic computation.

**`Node` cost functions:**
- `g` — path cost (depth from initial state, i.e., number of moves)
- `h` — heuristic: for each queen, count conflicts in the same row and on both diagonals (sums row conflicts + right-to-left diagonal conflicts + left-to-right diagonal conflicts)
- `f = g + h`

**`Node.nextStates()`** generates all children by moving each queen to every other row in its column (n×(n-1) children total), each as a new `Node`.

### IDA* Algorithm (`IDA.java`)

- `threshold` is initialized to the minimum `h` among the initial node's children.
- Each call to `ida(node)` explores children with `f ≤ threshold`; tracks the minimum f-value seen above the threshold in `lastMin`.
- If no solution is found in an iteration, `threshold` is updated to `lastMin` and search restarts.
- If `lastMin == threshold` after an iteration, no solution exists (search terminates).
- Each threshold used is appended to the static `IDA.ths` vector for display in the details panel.

### Global Mutable State

`Project1.goal` and `IDA.ths` are static fields. `IDA.ths` is explicitly cleared at the start of each `Project1.solve()` call. This means only one solve session is valid at a time.

### UI Components

- **`GameBoard`** — Renders an n×n grid of `JButton`s (`c1squares[col][row]`). When `f=true` (initialization mode), buttons are clickable to toggle queen placement (highlighted in `Color.lightGray`). When `f=false` (display mode), the solved queen positions are set programmatically by the caller.
- **`MainFrame.form` / `DetailsFrame.form`** — NetBeans GUI Builder `.form` files. The corresponding `initComponents()` methods in the `.java` files are auto-generated; do not manually edit those sections (marked with `//GEN-BEGIN` / `//GEN-END` comments).

### Image Assets

All images are loaded as classpath resources from `/Images/`:
- `Background.jpg` — main frame background
- `Black_Queen.png` — queen piece icon
- `icons8_*.png` — toolbar button icons
