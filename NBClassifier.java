 /**
 * 612-195 Text Classification using a Naïve Bayes (Multinomial)Classifier
 * LAB#4 
 @author Archit Jain
   Knowledge Process Technology
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class NBClassifier {
    private ArrayList<String> trainingDocs = new ArrayList<String>();
    private ArrayList<String> trainingClasses = new ArrayList<String>();
    private ArrayList<String> testFilesData = new ArrayList<String>();
    private int posCount = 0, negCount = 0, positiveCounter = 0, negativeCounter = 0;
    private double priorPositiveProb = 0.0;
    private double priorNegativeProb = 0.0;
    private HashMap<String, HashMap<String, Double>> wordDict = new HashMap<String, HashMap<String, Double>>();

    /**
     * Build a Naive Bayes classifier using a training document set
     *
     * @param trainDataFolder the training document folder
     */
    public NBClassifier(String trainDataFolder) throws Exception {
        preprocess(trainDataFolder);
        //conditionalProbability();
    }

    /**
     * Classify a test doc
     *
     * @param doc test doc
     * @return class label
     */
    public int classify(String doc) {
		//ArrayList<String> tokenizedDoc = tokenizeTest("[\" ()_,?:;%&-]+" , doc);
		String[] terms = doc.split("[\" ()_,?:;%&-]+");
		ArrayList<String> instance = new ArrayList<String>();
		for (String term: terms){
			//if(stopWords.indexOf(term.trim()) == -1){
				instance.add(term.trim());
			//}
		}
		double posProb = 0.0;
		double negProb = 0.0;
		for (String term : instance){
			if(wordDict.containsKey(term)){
				HashMap<String, Double> content = wordDict.get(term);
				double positiveProb = content.get("positiveProb");
				double negativeProb = content.get("negativeProb");
				posProb+=Math.log10(positiveProb);
				negProb+=Math.log10(negativeProb);
			}
			else{
				posProb+=Math.log10((double)1/(positiveCounter + wordDict.size()));
				negProb+=Math.log10((double)1/(negativeCounter + wordDict.size()));
			}
		}
		posProb+=Math.log10(priorPositiveProb);
		negProb+=Math.log10(priorNegativeProb);

		return posProb > negProb ? 1 : 0;
    }

    /**
     * Load the training documents
     *
     * @param trainDataFolder
     */
    public void preprocess(String trainDataFolder) throws Exception {
        posCount = readDocs(trainDataFolder.concat("/pos"), "train");
        negCount = readDocs(trainDataFolder.concat("/neg"), "train");
        System.out.println("positive count" + posCount);
        System.out.println("the negative count" + negCount);

        priorPositiveProb = (double) posCount / (posCount + negCount);
        priorNegativeProb = (double) negCount / (posCount + negCount);

        //getStopWords("stopwords_stanford.txt");
        //this.tokenize("[\" ()_,?:;%&-]+");
        int pCount = 0, nCount = 0;
        for (int index = 0; index < trainingDocs.size(); index++) {
            String[] terms = trainingDocs.get(index).split("[\" ()_,?:;%&-]+");
            ArrayList<String> instance = new ArrayList<String>();
            for (String term : terms) {
                //Adding to TermList
                //  if (stopWords.indexOf(term.trim()) == -1) {
                instance.add(term.trim());
                //  }
            }
            addToDict(instance, index);
            if (trainingClasses.get(index).equals("pos")) {
                ++pCount;
                positiveCounter += instance.size();
            } else if (trainingClasses.get(index).equals("neg")) {
                ++nCount;
                negativeCounter += instance.size();
            }

        }

        Iterator it = wordDict.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, HashMap<String, Double>> pair = (Map.Entry) it.next();
            String term = pair.getKey();
            HashMap<String, Double> content = pair.getValue();
            double positiveProb = (double) ((content.get("positiveCount")) + 1) / ((positiveCounter) + wordDict.size());
            double negativeProb = (double) ((content.get("negativeCount")) + 1) / ((negativeCounter) + wordDict.size());
            //conditionalProbability(content.get("negativeCount"), negativeCounter);
            content.put("positiveProb", positiveProb);
            content.put("negativeProb", negativeProb);
            wordDict.put(term, content);
        }
    }

    private void addToDict(ArrayList<String> instance, int index) {
        String label = trainingClasses.get(index);
        for (String word : instance) {
            if (!word.isEmpty()) {
                if (wordDict.containsKey(word)) {
                    HashMap<String, Double> content = wordDict.get(word);
                    double positiveCount = content.get("positiveCount");
                    double negativeCount = content.get("negativeCount");
                    if (label.equals("pos")) {
                        positiveCount += 1;
                        content.put("positiveCount", positiveCount);
                    } else if (label.equals("neg")) {
                        negativeCount += 1;
                        content.put("negativeCount", negativeCount);
                    }
                    wordDict.put(word, content);
                } else {
                    HashMap<String, Double> content = new HashMap<String, Double>();
                    if (label.equals("pos")) {
                        content.put("positiveCount", 1.0);
                        content.put("negativeCount", 0.0);
                    } else if (label.equals("neg")) {
                        content.put("positiveCount", 0.0);
                        content.put("negativeCount", 1.0);
                    }
                    wordDict.put(word, content);
                }
            }
        }
    }

    public int readDocs(String path, String flag) throws Exception {
        int count = 0;
        File files = new File(path);
        //System.out.print();
        File[] listFiles = files.listFiles();
        for (File file : listFiles) {
            if (file.isFile()) {
                String fileData = new String(Files.readAllBytes(Paths.get(file.toString())));
                fileData = fileData.toLowerCase();
                if (flag.equals("train")) {
                    trainingDocs.add(fileData);
                    if (path.equals("data/train/pos")) {
                        trainingClasses.add("pos");
                    } else if (path.equals("data/train/neg")) {
                        trainingClasses.add("neg");
                    }
                } else if (flag.equals("test")) {
                    testFilesData.add(fileData);
                }
                ++count;
            }
        }
        return count;
    }

    /**
     * Classify a set of testing documents and report the accuracy
     *
     * @param testDataFolder fold that contains the testing documents
     * @return classification accuracy
     */
    public double classifyAll(String testDataFolder) throws Exception {

		int totalCount = readDocs(testDataFolder.concat("/pos"),"test");
		totalCount += readDocs(testDataFolder.concat("/neg"),"test");
		int index = 0, correctClassify = 0;
		for( String doc : testFilesData){
			int label = classify(doc);
			index+=1;
			if(index<100){
				if(label == 1){
					correctClassify+=1;
				}
			}
			if(index>100){
				if(label == 0){
					correctClassify+=1;
				}
			}
		}
		System.out.println("Correctly Classified Instances: "+correctClassify+" / "+totalCount);
		return (double)correctClassify/totalCount*100;
	}


    public static void main(String[] args) throws Exception {
       /* NBClassifier classifier = new NBClassifier("data/train");
        classifier.preprocess("data/train");
        System.out.print("main");
*/
		NBClassifier cl = new NBClassifier("data/train");

		System.out.println("-------------FOR CLASSIFY_ALL TEST DOCS FROM TEST FOLDER ---------------");
		System.out.println("Classification Accuracy - "+cl.classifyAll("data/test"));

		System.out.println("-------------FOR CLASSIFYING SOME RANDOM DOCS FROM TEST FOLDER (DOCS 0-199) ---------------");
		//int randomIndex = new Random().nextInt(cl.testFilesData.size());
		System.out.println("Doc Index : "+ 195 + " - " + (cl.classify(cl.testFilesData.get(195))==1 ? "Positive" : "Negative"));

		//randomIndex = new Random().nextInt(cl.testFilesData.size());
		System.out.println("Doc Index : "+ 71 + " - " + (cl.classify(cl.testFilesData.get(70))==1 ? "Positive" : "Negative"));

	}
}