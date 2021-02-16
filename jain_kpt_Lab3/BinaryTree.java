

import java.util.*;

/**
 * @author Archit Jain
 * Lab #3
 * Knowlegde Proces Technology
 * a node in a binary search tree
 */
class BTNode {
    BTNode left, right;
    String term;
    ArrayList<Integer> docLists;

    /**
     * Create a tree node using a term and a document list
     *
     * @param term    the term in the node
     * @param docList the ids of the documents that contain the term
     */
    public BTNode(String term, ArrayList<Integer> docList) {
        this.term = term;
        this.docLists = docList;
    }

}

/**
 * Binary search tree structure to store the term dictionary
 */
public class BinaryTree {
    BinaryTree() {
    }

    /**
     * insert a node to a subtree
     *
     * @param node  root node of a subtree
     * @param iNode the node to be inserted into the subtree
     */
    public void add(BTNode node, BTNode iNode) {
        //TO BE COMPLETED
        BTNode point = node;
        BTNode curr = null;

        while (point != null) {
            curr = point;
            if (iNode.term.compareTo(point.term) < 0) {
                point = point.left;
            } else {
                point = point.right;
            }
        }

        if (curr == null) {
            curr = node;
        } else if (iNode.term.compareTo(curr.term) < 0) {
            curr.left = iNode;
        } else {
            curr.right = iNode;
        }
    }

    /**
     * Search a term in a subtree
     *
     * @param n   root node of a subtree
     * @param key a query term
     * @return tree nodes with term that match the query term or null if no match
     */
    public BTNode search(BTNode n, String key) {
        BTNode  curr = n;
        while (curr != null) {
            if (curr.term.equals(key)) {
                return curr;
            } else {
                if (curr.term.compareTo(key) > 0) {
                    curr = curr.left;
                } else {
                    curr = curr.right;
                }
            }
        }
        return null;
    }

    /**
     * Do a wildcard search in a subtree
     *
     * @param n   the root node of a subtree
     * @param key a wild card term, e.g., ho (terms like home will be returned)
     * @return tree nodes that match the wild card
     */
    public ArrayList<BTNode> wildCardSearch(BTNode n, String key, ArrayList<BTNode> tree) {
        //TO BE COMPLETED
        if (n == null) {
            return tree;
        }
        if (n.term.startsWith(key)) {
            wildCardSearch(n.left, key, tree);
            if (n.term.startsWith(key)) {
                tree.add(n);
            }
            wildCardSearch(n.right, key, tree);
            return tree;
        } else {
            if (n.term.compareTo(key) < 0) {
                wildCardSearch(n.right, key, tree);
            } else {
                wildCardSearch(n.left, key, tree);
            }
            return tree;
        }
    }

    /**
     * Print the inverted index based on the increasing order of the terms in a subtree
     *
     * @param node the root node of the subtree
     */
    public void printInOrder(BTNode node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.term + " " + node.docLists);
        printInOrder(node.right);
        //TO BE COMPLETED
    }
	/*public static void printBinary(BTNode root, int level){
		if(root==null)
			return;
		printBinary(root.right, level+1);
		if(level!=0){
			for(int i=0;i<level-1;i++)
				System.out.print("|\t");
			System.out.println("|-------"+root.term);
		}
		else
			System.out.println(root.term);
		printBinary(root.left, level+1);
	}*/
}

