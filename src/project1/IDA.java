/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Vector;

/**
 *
 * @author 11700
 */
public class IDA {

    Node init_state;
    int threshold;
    int lastMin;
    Vector<Node> path;
    int n;
    Node goal;
    static Vector <String> ths=new Vector<String>();
    public IDA(Node inNode, int n) {
        this.init_state = inNode;
        this.n = n;
        path=new Vector<Node>();
        

    }

    public void run() {
        if (init_state.f == 0) {
            System.out.println("initial is goal " + init_state.toString());
            Project1.goal=init_state;
            
            return;
        }
        Vector<Node> firstchilds = init_state.nextStates();
        Collections.sort(firstchilds);
//                for(int i=0;i<firstchilds.size();i++)
//                    System.out.println(firstchilds.get(i).printMatrix());
        Node min = firstchilds.get(0);
        threshold = min.h;        
        while (true) {
            lastMin=firstchilds.get(0).h;
            Node path1 = ida(init_state);
            if (path1 != null) {
                path.add(path1);
                ths.add(String.valueOf(threshold));
                System.out.println("Threshold= " + threshold);
                for (int i = 0; i < path.size(); i++) {
                    System.out.println(path.get(i).toString());
                    Project1.goal=path.get(i);
                return;
                }
            } else {
                if(lastMin==threshold)
                    return;
                System.out.println("old Threshold= "+threshold);
                System.out.println("new Threshold= "+lastMin);
                ths.add(String.valueOf(threshold));
                threshold=lastMin;
            }
        }

    }

    public Node ida(Node node) {
        Vector<Node> childs = node.nextStates();
        //System.out.println(System.nanoTime());
        Collections.sort(childs);
       // System.out.println(System.nanoTime());
        Node path1=null;
        Node min=childs.get(0);
        if(lastMin<min.f)
            lastMin=min.f;
        for(int i=0;i<childs.size();i++)
        {
            if(childs.get(i).h==0)
            {
                goal=childs.get(i);
                return childs.get(i);
            }
            if(childs.get(i).f<=threshold)
                path1=ida(childs.get(i));
            if(path1!=null)
                break;
        }
        if(path1!=null)
        {            
            
            path.add(path1);
            return node;
        }
       
        return null;
    }

    private Node minimum(Vector<Node> firstchilds) {
        Node min = firstchilds.get(0);
        for (int i = 1; i < firstchilds.size(); i++) {
            if (firstchilds.get(i).f < min.f) {
                min = firstchilds.get(i);
            }
        }
        return min;
    }

}
