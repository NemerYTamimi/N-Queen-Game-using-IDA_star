/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1;


import static com.sun.javafx.scene.control.skin.Utils.getResource;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author nemertamimi
 */
public class Project1 {

    /**
     * @param args the command line arguments
     */
    static Node iniNode;
    public static Node goal;
    public Project1(){
        
    }
    public static void main(String[] args) {
        drowMain();
        
    }

    private static void drow() {
        GameBoard gb = new GameBoard(goal.n,false);
        JFrame frame = new JFrame("N Queen Game");
        JComponent JC = gb.getGui();
        JPanel main = new JPanel();
        main.add(JC);
        JC.setPreferredSize(new Dimension(500, 500));
        System.out.println(JC.getPreferredSize());
        main.setSize(JC.getPreferredSize().width + 40, JC.getPreferredSize().height);
        frame.add(BorderLayout.CENTER,main);

        for (int i = 0; i < goal.n; i++) {
            for (int j = 0; j < goal.n; j++) {
                if (goal.matrix[i][j] == 1) { 
                    if(GameBoard.icon1==null)
                        gb.c1squares[j][i].setBackground(Color.BLACK);
                    else
                        gb.c1squares[j][i].setIcon(GameBoard.icon1);
                }
            }
        }

        frame.setLocationByPlatform(true);
        frame.setMinimumSize(frame.getSize());
        frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(main.getPreferredSize().width, main.getPreferredSize().height));
        frame.setMinimumSize(new Dimension(550, 550));
        frame.setLocation(330,100);
        frame.pack();
        frame.setVisible(true);

        main.setLayout(new BorderLayout(40,40) );

    }

    private static void drowMain() {
        MainFrame frame =new MainFrame();
        frame.setVisible(true);
        frame.setLocation(400,100);
        
        
    }
    public static void solve(int [] initArray){
        IDA.ths.clear();
        
        iniNode = new Node(initArray, null, initArray.length);
        System.out.println(iniNode.toString());
        IDA ida = new IDA(iniNode, initArray.length);
        ida.run();


        drow();
    }
    public static void details(){
        String Details ="";
        if(goal.g==0)
            Details+="the initial state is goal \n\n-------------------------------\n"+iniNode.printMatrix()+"------------------------------\n\n";
        else
        {
            Details+="the initial state is \n\n-------------------------------\n"+iniNode.printMatrix()+"------------------------------\n\n";
            Details+="the Goal state is \n\n-------------------------------\n"+goal.printMatrix()+"------------------------------\n\n";

        }
        Details+="\nOperators: next states generated \nby moving one queen up or down in \nthe column also number of steps \nthat the queen move considered\n as a unit in the cost.\n\n";
        for (int i=0;i<IDA.ths.size();i++)
            Details+="Cutoff["+i+"]= "+IDA.ths.get(i)+"\n";
        if(goal.g!=0)
        {
            Details+="Total Path Cost from initial to goal="+goal.g+"\n";
            Details+="\nThat means the solution can be done by \n\tmoving the "+goal.n+" queens "+goal.g+" moves.";
            
        }

        DetailsFrame detailsFrame=new DetailsFrame(Details);
        detailsFrame.setVisible(true);
        detailsFrame.setLocation(300,100);
    }
    
}
