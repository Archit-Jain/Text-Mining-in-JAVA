

import java.util.*;

public class BTreeIndex {
    String[] myDocs;
    BinaryTree termList;
    BTNode root;
    /**
     * Construct binary search tree to store the term dictionary
     *
     * @param docs List of input strings
     */
    public BTreeIndex(String[] docs) {
        ArrayList<String> terms = new ArrayList<String>();
        myDocs = docs;
        for (String doc : docs) {
            String words[] = doc.split(" ");
            for (String word : words) {
                if (terms.indexOf(word) == -1) {
                    terms.add(word);
                }
            }
        }
        Collections.sort(terms);
        System.out.println("Terms:");
        System.out.println(terms);

        termList = new BinaryTree();

        root = balancedTree(terms, 0, terms.size() - 1);
        System.out.println("\n Terms in order:");
        termList.printInOrder(root);
        System.out.println("\n");
    }

    public BTNode balancedTree(ArrayList<String> terms, int start, int end) {

        if (start > end) {
            return null;
        }
        int mid = (start + end) / 2;
        ArrayList<Integer> docList = new ArrayList<Integer>();
        BTNode node = new BTNode(terms.get(mid), docList);
        int count = 0;
        for (String doc : myDocs) {
            if (doc.indexOf(terms.get(mid)) != -1) {
                node.docLists.add(count);
            }
            ++count;
        }

        termList.add(null, node);
        /*construct left subtree */
        BTNode left = balancedTree(terms, start, mid - 1);
        if (left != null) {
            termList.add(node, left);
        }
        /*construct right subtree */
        BTNode right = balancedTree(terms, mid + 1, end);
        if (right != null) {
            termList.add(node, right);
        }
        return node;
    }

    /**
     * Single keyword search
     *
     * @param query the query string
     * @return doclists that contain the term
     */
    public ArrayList<Integer> search(String query) {
        BTNode node = termList.search(root, query);
        if (node == null)
            return null;
        return node.docLists;
    }

    /**
     * conjunctive query search
     *
     * @param query the set of query terms
     * @return doclists that contain all the query terms
     */
    public ArrayList<Integer> search(String[] query) {
        ArrayList<Integer> list = search(query[0]);
        int termId = 1;
        while (termId < query.length) {
            ArrayList<Integer> list1 = search(query[termId]);
            list = merge(list, list1);
            termId++;
        }
        return list;
    }

    /**
     * @param wildcard the wildcard query, e.g., ho (so that home can be located)
     * @return a list of ids of documents that contain terms matching the wild card
     */
    public ArrayList<Integer> wildCardSearch(String wildcard) {
        ArrayList<Integer> terms = new ArrayList<Integer>();
        ArrayList<BTNode> tree = termList.wildCardSearch(root, wildcard, new ArrayList<BTNode>());
        if (tree.size() > 0) {
            BTNode start = tree.get(0);
            terms = start.docLists;
            if (tree.size() > 1) {
                for (BTNode node : tree) {
                    terms = wildMerge(terms, node.docLists);
                }
            }
        }
        return terms;
    }

    private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        ArrayList<Integer> mergeList = new ArrayList<Integer>();
        int id1 = 0, id2 = 0;
        if(l1!=null&&id1 < l1.size() && id2 < l2.size()) {
            while (id1 < l1.size() && id2 < l2.size()) {
                if (l1.get(id1).intValue() == l2.get(id2).intValue()) {
                    mergeList.add(l1.get(id1));
                    id1++;
                    id2++;
                } else if (l1.get(id1) < l2.get(id2))
                    id1++;
                else
                    id2++;
            }
            return mergeList;
        }else{
            //System.out.print("Not found");
            return null;
        }
    }
    /*
    * */
    public ArrayList<Integer> wildMerge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        ArrayList<Integer> mergeList = new ArrayList<Integer>();
        int m = l1.size();
        int n = l2.size();
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (l1.get(i) < l2.get(j))
                mergeList.add(l1.get(i++));
            else if (l2.get(j) < l1.get(i))
                mergeList.add(l2.get(j++));
            else {
                mergeList.add(l2.get(j++));
                i++;
            }
        }
        while (i < m)
            mergeList.add(l1.get(i++));
        while (j < n)
            mergeList.add(l2.get(j++));

        return mergeList;
    }


    /**
     * Test cases
     *
     * @param args commandline input
     */
    public static void main(String[] args) {
        String[] docs = {"text warehousing over big data",
                "dimensional data warehouse over big data",
                "nlp before text mining",
                "nlp before text classification"};
        //TO BE COMPLETED with testcases
        BTreeIndex bt = new BTreeIndex(docs);
        System.out.println("\n Single Term query:");
        System.out.println("text: " + bt.search("nlp"));
        System.out.println("data: " + bt.search("data"));
        System.out.println("before: " + bt.search("text"));
        System.out.println("\n");
        System.out.println("conjunctive query:");
        System.out.println("Archit big data: "+bt.search(new String[]{"Archit", "big", "data"}));
        System.out.println("nlp before text: "+bt.search(new String[]{"nlp", "before", "text"}));
        System.out.println("\n");

        System.out.println(" Wildcard search:");
        System.out.println("archit: "+bt.wildCardSearch("archit"));
        System.out.println("war: "+bt.wildCardSearch("war"));
        System.out.println("bi: "+bt.wildCardSearch("bi"));
        System.out.println("\n");
    }
}