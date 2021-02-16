import java.util.*;
/**
 * 612 PBE06 Binary Search Tree
 * Prof Kang
 */
 
class Node {
   Node left;
   Node right;
   int value;
   public Node(int value) {
      this.value =value; 
   }
}

public class BSTa {
   private Node root;
   
   public void insert(Node node, int value) {
      //To be Completed
      if(value < node.value) 
         if(node.left!=null) insert (node.left,value);
         else{
         node.left = new Node(value);
         System.out.println("INserted "+value+"to the left node "+node.value);
      }else if (value > node.value){
         if(node.right!=null) insert (node.right,value);
         else{
         node.right = new Node(value);
         System.out.println("INserted "+value+"to the right node "+node.value);
      }
   }
   }
   
   
   public void printInOrder(Node node) {
      if(node != null) {
         printInOrder(node.left);
         System.out.print(node.value + " ");
         printInOrder(node.right);
      }
   }
   public Node search(Node n, int key) {
      //To be completed
      if(n==null) return null;
      if(n.value == key) return n;
      else if(n.value>key) return search (n.left,key);
      else return search(n.right, key);
     // return null;
   }
   
   public static void main(String[] args) {
         BSTa bst = new BSTa();
      Node rootnode = new Node(35);
      bst.insert(rootnode, 20);
      bst.insert(rootnode, 85);
      bst.insert(rootnode, 30);
      bst.insert(rootnode, 45);
      
      System.out.println("traversed");
      bst.printInOrder(rootnode);
      
      Node n = bst.search(rootnode, 45);
      //Node n = bst.search(rootnode, 100);
      if(n != null) System.out.println("\n" + n.value);
      else System.out.println("\n no match");
   
   }
}