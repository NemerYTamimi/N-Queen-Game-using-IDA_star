/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1;

import java.util.ArrayList;
import java.util.Vector;

public class Node implements Comparable<Node> {

    int[] stateArray;
    int g;
    int h;
    int f;
    Node parent;
    int[][] matrix;
    int n ;

    public Node(int state[], Node parent, int n) {
        this.stateArray = state;
        this.n=n;
        this.parent = parent;
        this.f = calc_cost();
       
    }

    public int[] getStateArray() {
        return stateArray;
    }

    public int getPathCost() {
        return g;
    }

    public int getHeuristic() {
        return h;
    }

    public int getTotalCost() {
        return f;
    }

    private int calc_cost() {
        this.g = calc_pathCost();
        this.h = calc_heuristic();
        return calc_totalCost();
    }

    private int calc_pathCost() {
        if (this.parent != null) {
            return (this.parent.getPathCost() + 1);
        }
        return 0;
    }

    private int calc_heuristic() {
        this.matrix = toMatrix();

        int total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1) {
                    total += calc_conflict(matrix, i, j);
                }
            }
        }
        return total;
    }

    private int calc_totalCost() {
        return (this.g + this.h);
    }

    int calc_conflict(int board[][], int row, int col) {

        int total_conflict = 0;
        // in row
        int sumRow = -1;
        for (int i = 0; i < n; i++) {
            if (board[row][i] == 1) {
                sumRow++;
            }
        }
        // in R to L diagonal 
        int nodRToL = row + col;
        int sumRtoL = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 1) {
                    if (!(i == row && j == col)) {
                        if (nodRToL == (i + j)) {
                            sumRtoL++;
                        }
                    }
                }
            }
        }

        int nodLtoR = (n-row-1) + (col);
        int sumLtoR = 0;
        for (int i = 0; i<n; i++) {
            for (int j = 0; j <n; j++) {
                if (board[i][j] == 1) {
                    if (!((n-i-1) == (n-row-1) && j == col)) {
                        if (nodLtoR == ((n-i-1) + j)) {
                            sumLtoR++;
                        }
                    }
                }
            }
        }
        total_conflict =sumLtoR+sumRow + sumRtoL;

        return total_conflict;
    }

    public int[][] toMatrix() {

        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = 0;
            }
        }
        for (int i = 0; i < n; i++) {
            matrix[this.stateArray[i]][i] = 1;
        }
        return matrix;
    }

    @Override
    public String toString() {
        return "State{ pathCost=" + g + ", heuristic=" + h + ", totalCost=" + f + ", n=" + n + '}' + '\n' + printMatrix();

    }

    public String printMatrix() {
        String str = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                str = str + " " + matrix[i][j];
                str = str + " ";
            }
            str += '\n';
        }
        return str;
    }

    public Vector<Node> nextStates() {
        
        Node ini_state = new Node(stateArray, this, n);
        Vector<Node> childs = new Vector<Node>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (j != stateArray[i]) {
                    int[] newArray = new int[n];
                    for (int z = 0; z < n; z++) {
                        newArray[z] = stateArray[z];
                    }
                    newArray[i] = j;
                    Node newChild = new Node(newArray, ini_state, n);
                    childs.add(newChild);
                }
            }
        }
        return childs;
    }

    
    public boolean equals(Node node) {
        if (node.h == this.h) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Node node) {
        if(node==null)
            return 0;
        if (node.f > this.f) {
            return -1;
        }
        if (node.f < this.f) {
            return 1;
        }
        return 0;
    }

}
