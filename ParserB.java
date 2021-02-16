import java.io.*;
import java.util.*;
/**
 * ISTE-612 LBE03 Text processing
 * Prof Kang
 */

public class ParserB {
   private String[] myDocs;
      
   String[] stopWords = {"a","is","in","so","of", "at", "the","to","and","it","as",
         "be","are","on","into","if","it's"};
         
   public ParserB(String fileName) {
     
   }
   
   public ParserB() {
     Arrays.sort(stopWords);     //sort the stop words
     
     String sw = new String();
     for(int i=0;i<stopWords.length;i++) {
      sw += stopWords[i] + " ";
     }
     System.out.println("Stop words: " + sw);
   }
   
   //Binary search for a stop word
   public int searchStopWord(String key) {
      int lo=0;
      int hi = stopWords.length-1;
      
      while(lo<=hi) {
         int mid = lo +(hi-lo)/2;
         int result = key.compareTo(stopWords[mid]);
         if(result<0) hi = mid-1;
         else if(result > 0) lo = mid+1;
         else return mid;
      }
      return -1;
   }
   
   //Tokenization
   public ArrayList<String> parseB(File fileName) throws IOException {
      String[] tokens = null;
      ArrayList<String> pureTokens = new ArrayList<String>();
      ArrayList<String> stemms = new ArrayList<String>();
      
      Scanner scan = new Scanner(fileName);
      String allLines = new String();
      
      //Case folding
      while(scan.hasNextLine()) {
         allLines += scan.nextLine().toLowerCase();
      }
      
      tokens = allLines.split("[ '.,?!:;$%+()\\-\\*]+");
      
      //remove stop words
      for(String token:tokens) {
         if(searchStopWord(token) == -1) {
            pureTokens.add(token);
         }
      }
      
      //stemming
      Stemmer st = new Stemmer();
      for(String token:pureTokens) {
         st.add(token.toCharArray(), token.length());
         st.stem();
         stemms.add(st.toString());
         st = new Stemmer();
      }
      return stemms;
   }
   
   public static void main(String[] args) {
      ParserB p = new ParserB();
      
      System.out.println("Stop word: " + p.searchStopWord("are"));
      System.out.println("Stop word: " + p.searchStopWord("be"));
      System.out.println("Stop word: " + p.searchStopWord("ss"));
      
      //Stemming
      Stemmer st = new Stemmer();
      String stTest = "replacement";
      st.add(stTest.toCharArray(), stTest.length());
      st.stem();
      System.out.println("Stemmed: " + st.toString());
  
      try {
         File file = new File("stemTest.txt");
         
         ArrayList<String> stemmed = p.parseB(file);
         for(String stm:stemmed) {
            System.out.println(stm);
         }
         
      }
      catch (IOException ioe) {
         ioe.printStackTrace();
      }    
      

   }
}