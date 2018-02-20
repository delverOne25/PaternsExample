/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patterns;

import java.awt.Graphics;
import java.util.*;
import javax.swing.*;
import java.awt.Color;
import javax.swing.text.Utilities;




/**
 *
 * @author danii
 */
public class TreeLightWeight {
     static int CANVAS_SIZE=500;
      static int TREES_TO_DRAW=2;
      static int TREE_TYPE=2;
    
    public  static  void main(String[]args){
       
        Forest forest=new Forest();       
        forest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        forest.setSize(CANVAS_SIZE,CANVAS_SIZE);
        for(int i=0; i<Math.floor(TREES_TO_DRAW/TREE_TYPE);i++){
            forest.plaintTree(random(0,CANVAS_SIZE), random(0, CANVAS_SIZE), "Summer Oak", Color.GREEN, "Oak testure stub");
            forest.plaintTree(random(0, CANVAS_SIZE), random(0,CANVAS_SIZE),  "Authr oak", Color.ORANGE,"Authumn Oak textute sstub");
        }
        
        forest.setSize(CANVAS_SIZE,CANVAS_SIZE);
        forest.setVisible(true);
        
        System.out.println(TREES_TO_DRAW+" trees drawn");
        System.out.println("---------------------------");
        System.out.println("Memory usage: ");
        System.out.println("Tree size (8 bytes) * "+TREES_TO_DRAW);
        System.out.println("+ TreeTypes size (~30 bytes) * " + TREE_TYPE + "");
        System.out.println("---------------------------");
        System.out.println("Total: "+((TREES_TO_DRAW*8+TREE_TYPE*30/1024))+
                " Mb (instread of "+((TREES_TO_DRAW*38)/1024/1024)+" MB");
        
        
    }
    public static int random(int min, int max){
        return min+(int)(Math.random()*((max-min)+1))
    ;}
    
}

class Tree{
    private int x;
    private int y;
    private TreeType type;
    public Tree(int x, int y, TreeType type){
        this.x=x;
        this.y=y;
        this.type=type;
    }
    public void draw(Graphics g){
        type.draw(g,x,y);
    }
}

// Легковес имеющий общее состояние(внутренея состояние) для многих обьектов
class TreeType{
    private String name;
    private Color color;
    private String otherTreeData;
    
    public TreeType(String name, Color color, String otherTreeData){
        this.name=name;
        this.color=color;
        this.otherTreeData=otherTreeData;
    }
    public void draw(Graphics g, int x, int y){
        g.setColor(Color.BLACK);
        g.fillRect(x-1,y,3,5);
        g.setColor(color);
        g.fillOval(x-5,y-10,10,10);
    }
}
//Фабрика деревьев
class TreeFactory{
    static Map<String, TreeType>treeTypes = new HashMap<>();
    public static TreeType getTreeType(String name, Color color, String otherTreeData){
        TreeType result = treeTypes.get(name);
        if(result==null){
            result =new TreeType(name, color, otherTreeData);
            treeTypes.put(name, result);
        }
        return result;
    }
}

class Forest extends JFrame{
    private List<Tree> trees = new ArrayList<>();
    public void plaintTree(int x, int y, String name,Color  color,String othherData){
        TreeType type=TreeFactory.getTreeType(name,color,othherData);
        Tree tree =new Tree(x,y,type);
        trees.add(tree);
    }
    @Override
    public void paint(Graphics graphics){
        for(Tree tree : trees)
            tree.draw(graphics);
    }
}





















