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
2. **`Project1.solve(int[] initArray)`** — Called by the UI. Creates the initial `Node`, runs `IDA` (timing the search and recording bytes allocated on the calling thread), then opens **two** windows: a result `GameBoard` displaying the solution and a `BenchmarkFrame` reporting the measured cost. Holds the static global `Project1.goal` (the solved `Node`).
3. **`IDA`** — Runs the search. On completion, `Project1.goal` holds the goal node, `IDA.thresholds` holds the list of threshold values used, and `IDA.nodesExpanded` / `IDA.nodesGenerated` hold the search-work counters used by the benchmark.
4. **`Project1.details()`** — Called by the "Details" button; opens a `DetailsFrame` showing the initial/goal board states, cutoff thresholds, and total path cost.

### State Representation

Board state is encoded as `int[] stateArray` of length n, where `stateArray[col] = row` — one queen per column by design. The `Node.toMatrix()` method converts this to a 2D `int[n][n]` matrix for display and heuristic computation.

**`Node` cost functions:**
- `g` — path cost (depth from initial state, i.e., number of moves)
- `h` — heuristic: number of attacking pairs of queens across rows and both diagonals (an admissible lower bound), computed by `Node.computeH` via per-row/diagonal counts and the `C(k,2)` pairs formula
- `f = g + h`

`Node` is an immutable value holder used only for the initial and goal states. The search itself does **not** build a `Node` per state — see below.

### IDA* Algorithm (`IDA.java`)

The search is an in-place backtracking DFS to minimize allocation. `IDA` holds a single mutable board (`queens[col] = row`) plus incrementally-maintained row/diagonal conflict-count arrays (`rowCnt`, `d1`, `d2`), allocated once in `run()` and shared across all recursion.

- `threshold` starts at `h(root)` (`initState.h`).
- `search(g, h, threshold, next)` is bounded DFS: if `f = g + h > threshold` it records the value in `next[0]` (min f above threshold) and prunes; if `h == 0` it returns the goal as a freshly-cloned `Node` (the only allocation on success).
- Children ("move one queen within its column", n×(n-1) per frame) are enumerated with O(1) incremental heuristic updates and packed into a primitive `long[]` keyed by child `h`, then `Arrays.sort`ed for best-first order — no child `Node` objects or board clones. Each move is applied to the shared arrays, recursed, then undone.
- After a failed iteration, `threshold` is raised to `next[0]`; if `next[0]` is still `Integer.MAX_VALUE`, the space is exhausted and `run()` returns `null` (no solution).
- Each threshold used is appended to the static `IDA.thresholds` list for display in the details panel.

### Global Mutable State

`Project1.goal` and `IDA.thresholds` are static fields. `IDA.thresholds` is explicitly cleared at the start of each `Project1.solve()` call. This means only one solve session is valid at a time.

### UI Components

- **`GameBoard`** — Renders an n×n grid of `JButton`s (`c1squares[col][row]`). When `f=true` (initialization mode), buttons are clickable to toggle queen placement (highlighted in `Color.lightGray`). When `f=false` (display mode), the solved queen positions are set programmatically by the caller.
- **`MainFrame.form` / `DetailsFrame.form`** — NetBeans GUI Builder `.form` files. The corresponding `initComponents()` methods in the `.java` files are auto-generated; do not manually edit those sections (marked with `//GEN-BEGIN` / `//GEN-END` comments).
- **`BenchmarkFrame`** — Hand-written Swing window (no `.form`) opened next to the solution board after each solve. Shows the measured solve time, bytes allocated, nodes expanded/generated, IDA* iterations, and path cost, plus the algorithm's time/space complexity. Since it is hand-coded, edit it freely.

### Image Assets

All images are loaded as classpath resources from `/Images/`:
- `Background.jpg` — main frame background
- `Black_Queen.png` — queen piece icon
- `icons8_*.png` — toolbar button icons
