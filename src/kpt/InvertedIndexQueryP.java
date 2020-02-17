package kpt;

import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * LAB 01: Knowlegde Process Technology
 * Author Archit Jain
 * email: aj4907@rit.edu
 */
public class InvertedIndexQueryP {
    private static String[] stopWords;

    private String path = "";
    private String[] myDocs;               //input docs
    private ArrayList<String> termList;    //dictionary
    private ArrayList<ArrayList<Integer>> docLists;
    private ArrayList<Integer> docList;
    public static ArrayList<String> docnames; //all files names

    /**
     * Binary search for stop words
     * @param key contains string word
     * @return 1 if the word is stop word
     */
    public int searchStopWord(String key) {
        int lo = 0;
        int hi = stopWords.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int result = key.compareTo(stopWords[mid]);
            if (result < 0) hi = mid - 1;
            else if (result > 0) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    //function for tokenization stop words check and stemming of data
    /* Performs tokenization , stop word removal and stemming in single function
     * @param filedata: ArrayList of String containing all words in single document per index
     * @return AllTokens: Arraylist of Arraylist string containg all array string of all documents per index
     * */
    public ArrayList<ArrayList<String>> tokenization(ArrayList<String> filedata) {
        ArrayList<ArrayList<String>> allTokens = new ArrayList<ArrayList<String>>();
        ArrayList<String> newdata = new ArrayList<String>();
        for (String s : filedata) {
            String[] tokens = s.split("[ '.,?!&#:;$%+()\\-\\/*\"\']+");//tokenization
            ArrayList<String> stemms = new ArrayList<String>();
            for (String token : tokens) {
                if (searchStopWord(token) == -1) {
                    //Stemming
                    Stemmer st = new Stemmer();
                    st.add(token.toCharArray(), token.length());
                    st.stem();
                    stemms.add(st.toString());
                    st = new Stemmer();
                }
            }
            allTokens.add(stemms);
        }
        /* //uncomment to print all token after tokenization stopwords-removal, stemming
        for (ArrayList<String> arr : allTokens) {
            System.out.println(arr);
        }*/
        return allTokens;
    }

    /*Creates the inverted index and termlist(dictionary)
     * @param cleandata : contains ArrayList<ArrayList<String>> data after tokenization
     * @return String of doclists can be used to print
     * */
    public String invertedindex(ArrayList<ArrayList<String>> cleanData) throws FileNotFoundException {
        termList = new ArrayList<String>();
        docLists = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < cleanData.size(); i++) {
            String[] words = cleanData.get(i).toArray(new String[0]);
            //System.out.println("words of index 0: " + words[0]);
            for (String word : words) {
                if (!termList.contains(word)) {
                    termList.add(word);
                    docList = new ArrayList<Integer>();
                    docList.add(i + 1);//i+1 for index {1to5}
                    docLists.add(docList);
                } else {
                    int index = termList.indexOf(word);
                    docList = docLists.get(index);
                    if (!docList.contains(i + 1)) {
                        docList.add(i + 1);
                        docLists.set(index, docList);
                    }
                }
            }
        }
        String outputString = new String();
        for (int i = 0; i < termList.size(); i++) {
            outputString += String.format("%-15s", termList.get(i));
            docList = docLists.get(i);
            for (int j = 0; j < docList.size(); j++) {
                outputString += docList.get(j) + "\t";
            }
            outputString += "\n";
        }
        return outputString;
    }

    /* Search algorithm for single query search
    @param keyword: String for input search word
    @return docindex: returns document names where query is found
    * */
    public ArrayList<String> searchSingleWord(String keyWord) {
        System.out.println("Search Word:" + keyWord);
        keyWord = stemmerSingleWord(keyWord);
        int pos = 0;

        ArrayList<Integer> docIndex = new ArrayList<Integer>();
        if (termList.contains(keyWord)) {
            pos = termList.indexOf(keyWord);
            docIndex = docLists.get(pos);
        }
        System.out.println("Single:" + docLists.get(pos));
        return docnames(docIndex);
    }

    /* Search alogorithm for and query containg 2 terms
    @param String word1 word2: containing String search words
    * */
    public ArrayList<String> searchAnd(String word1, String word2) {
        System.out.println("Search Word and: (" + word1 + "), (" + word2 + ")");
        word1 = stemmerSingleWord(word1);
        word2 = stemmerSingleWord(word2);
        if (termList.contains(word1) && termList.contains(word2)) {
            int pos1 = termList.indexOf(word1);
            int pos2 = termList.indexOf(word2);
            ArrayList<Integer> docIndex1 = docLists.get(pos1);
            ArrayList<Integer> docIndex2 = docLists.get(pos2);
            //merge
            ArrayList<Integer> docIndex = new ArrayList<Integer>();
            docIndex1.size();
            for (int m = 0, n = 0; m < docIndex1.size() && n < docIndex2.size(); ) {
                if (docIndex1.get(m) == docIndex2.get(n)) {
                    docIndex.add(docIndex1.get(m));
                    m++;
                    n++;
                } else if (docIndex1.get(m) < docIndex2.get(n)) {
                    m++;
                } else {
                    n++;
                }
            }
            System.out.println("AND list: " + docIndex);
            return docnames(docIndex);
        } else return null;
    }

    /* Function to return documents names for input inverted matrix
     *@ param docindex : ArrayList<Integer>  Position list
     * @return resultfiles: contains names for all document at the input index
     * */
    public ArrayList<String> docnames(ArrayList<Integer> docIndex) {
        ArrayList<String> resultfiles = new ArrayList<String>();
        for (int k = 1; k <= docIndex.size(); k++) {
            resultfiles.add(docnames.get(k - 1));
        }
        System.out.println("Result Files: " + resultfiles);
        return resultfiles;
    }

    /*Search algorithm for two word query for OR operation
    @param String word1 String word 2 for query
    * */
    public ArrayList<String> searchOr(String word1, String word2) {
        word1 = stemmerSingleWord(word1);
        word2 = stemmerSingleWord(word2);
        System.out.println("Search words: (" + word1 + "), (" + word2 + ")");
        if (termList.contains(word1) && !termList.contains(word2)) {
            System.out.println("or:1 " + docLists.get(termList.indexOf(word1)));
            return docnames(docLists.get(termList.indexOf(word1)));
        } else if (!termList.contains(word1) && termList.contains(word2)) {
            System.out.println("or:2 " + docLists.get(termList.indexOf(word2)));
            return docnames(docLists.get(termList.indexOf(word2)));
        } else if (termList.contains(word1) && termList.contains(word2)) {
            int pos1 = termList.indexOf(word1);
            int pos2 = termList.indexOf(word2);
            ArrayList<Integer> docIndex1 = docLists.get(pos1);
            ArrayList<Integer> docIndex2 = docLists.get(pos2);
            //merge
            ArrayList<Integer> docIndex = new ArrayList<Integer>();
            docIndex1.size();
            int m, n;
            for (m = 0, n = 0; m < docIndex1.size() && n < docIndex2.size(); ) {
                if (docIndex1.get(m) == docIndex2.get(n)) {
                    docIndex.add(docIndex1.get(m));
                    m++;
                    n++;
                } else if (docIndex1.get(m) < docIndex2.get(n)) {
                    docIndex.add(docIndex1.get(m));
                    m++;
                } else {
                    docIndex.add(docIndex2.get(n));
                    n++;
                }
            }
            //add the remaining terms to posting list
            while (m < docIndex1.size()) {
                if (!docIndex.contains(docIndex1.get(m))) {//check if already there
                    docIndex.add(docIndex1.get(m));//add only if not there
                    m++;
                }
            }
            while (n < docIndex2.size()) {
                if (!docIndex.contains(docIndex2.get(n))) {//check if already exist
                    docIndex.add(docIndex2.get(n));//add only if not there
                    n++;
                }
            }
            System.out.println("or: " + docIndex);
            return docnames(docIndex);
        } else return null;
    }
    /*porter's Stemmer function used for steming\
    * @param String word which needs to be stemmed
    *  */
    public ArrayList<String> stemmer(String word) {
        String[] stringArray = word.split(" ");
        ArrayList<String> stemms = new ArrayList<String>();
        for (String token : stringArray) {
            Stemmer st = new Stemmer();
            st.add(token.toCharArray(), token.length());
            st.stem();
            stemms.add(st.toString());
            st = new Stemmer();
        }

        return stemms;
    }
    /*stemmer function for the single word stemming process
    @param input String word
    @return stemmed String  st.toString();
    * */
    public String stemmerSingleWord(String word){
        Stemmer st = new Stemmer();
        st.add(word.toCharArray(), word.length());
        st.stem();

        return st.toString();
    }
    /*Posting list sort algorithm implementing bubble sort
    * The String word with smallest posting list is pushed at first index
    * @param ArrayList of Search terms and
    * @param their corresponding posting list's size
    * */
    public void sort(ArrayList<String> terms, ArrayList<Integer> termSize) {
        int i, j;
        for (i = 0; i < termSize.size() - 1; i++)//last term is already sorted
            for (j = 0; j < termSize.size() - i - 1; j++)
                if (termSize.get(j) > termSize.get(j + 1)) {
                    String temp = terms.get(j);//swap term name
                    terms.set(j, terms.get(j + 1));
                    terms.set(j + 1, temp);

                    int temp2 = termSize.get(j);//swap posting list size//maybe not needed
                    termSize.set(j, termSize.get(j + 1));
                    termSize.set(j + 1, temp2);
                }
    }
    /*Search algorithm for three or more query with AND operation
    * @param String query containing all the words of a search query
    * @return names of dcouments containing all the words
    * */
    public ArrayList<String> search_and_Three(String query) {
        ArrayList<String> queryArray = stemmer(query);
        ArrayList<String> searchterms = new ArrayList<String>();
        ArrayList<Integer> termsize = new ArrayList<Integer>();
        //find length
        for (String word : queryArray) {
            //check if all are there else return not found
            if (termList.contains(word)) {
                searchterms.add(word);
                termsize.add(docLists.get(termList.indexOf(word)).size());
            } else {
                System.out.println("Search query '" + query + "' Not Found!");
                return null;
            }
        }
        //sort by length anf word
        if (queryArray.size() >= 3) {
            sort(searchterms, termsize);//sorting
            System.out.println("Order of Merge"+searchterms);
            ArrayList<Integer> docIndex1 = docLists.get(termList.indexOf(searchterms.get(0)));//intialise with first

            for (int i = 0; i < searchterms.size() - 1; i++) {
                ArrayList<Integer> docIndex2 = docLists.get(termList.indexOf(searchterms.get(i + 1)));
                ArrayList<Integer> docIndex = new ArrayList<Integer>();
                for (int m = 0, n = 0; m < docIndex1.size() && n < docIndex2.size(); ) {
                    if (docIndex1.get(m) == docIndex2.get(n)) {
                        docIndex.add(docIndex1.get(m));
                        m++;
                        n++;
                    } else if (docIndex1.get(m) < docIndex2.get(n)) {
                        m++;
                    } else {
                        n++;
                    }
                }
                docIndex1 = docIndex; //make value equal to resultant
            }
            System.out.println("Three or more: " + docIndex1);
            return docnames(docIndex1);
        } else
            System.out.println("Not Found");
        return null;
    }
    /*Main function reads from stopword file and Lab01_data folder to get all the data

    * */
    public static void main(String[] args) throws IOException {
        ArrayList<String> data = new ArrayList<String>();
        InvertedIndexQueryP index = new InvertedIndexQueryP();
        //Read from stop words file and make them global
        File stopwordsfile = new File("stopwords.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(stopwordsfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String lines = new String();
        while (sc.hasNextLine()) {
            lines += sc.nextLine().toLowerCase() + " ";//added space to seperate out stop words
        }
        stopWords = lines.split("[ ']+");
        Arrays.sort(stopWords);//sort to binary search
        //Read from Lab1_Data folder
        File folder = new File("Lab1_Data/");
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> files;
        files = new ArrayList<String>();
        for (File file : listOfFiles) {//get list of all file names
            if (file.isFile()) {
                //System.out.println(file.getName());//Print all the file names
                files.add(file.getName());
            }
        }
        //for all fives files loop through
        for (String doc : files) {
            File input = new File("Lab1_Data/" + doc);
            Scanner read = null;
            try {
                read = new Scanner(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String text = "";
            while (read.hasNextLine()) {
                text += read.nextLine().toLowerCase();
            }
            data.add(text);
        }
        docnames = files;

        ArrayList<ArrayList<String>> cleandata = index.tokenization(data);
        String result = null;
        try {
            result = index.invertedindex(cleandata);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         //Uncomment this to print the complete posting list with term list
        System.out.println(result);

        //single word query
        System.out.println("Single Term Queries-------------------------");

        index.searchSingleWord("plot");
        index.searchSingleWord("teen");
        // two term AND
        System.out.println(" ");
        System.out.println("Two Term AND Queries-------------------------");
        index.searchAnd("church", "party");
        index.searchAnd("product", "year");
        //two term OR
        System.out.println(" ");
        System.out.println("Two Term OR Queries-------------------------");
        index.searchOr("plot", "teen");
        index.searchOr("product", "year");
        //Search AND for three or more
        System.out.println(" ");
        System.out.println("Three or more Term AND Queries-------------------------");
        index.search_and_Three("plot teen film ");
        index.search_and_Three("plot teen party ");

    }
}

