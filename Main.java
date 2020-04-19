import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    /** utility objects **/
    private static final Scanner input = new Scanner(System.in);
    private static final Random RANDOM = new Random();

    /** the number of words to find for the wordsearch **/
    private static final int wordsToFind = 15;
    /** the board **/
    private static Board board;
    /** a list of all of the coordinates at which a letter of a found word is **/
    private static ArrayList<String> foundPositions = new ArrayList<>();

    public static void main(String[] args) {
        ArrayList<String> words = getWords();

        board = BoardMaker.makeWordsearch(words);
        String[][] fullBoard = cloneArray(board.getBoard());

        ArrayList<Word> solution = Solver.solve(board, board.getPlacedWords());
        populateFoundPositions(solution);

        Board finalBoard = new Board(fullBoard);
        finalBoard.updatePlacedWords();
        System.out.println(finalBoard.boardToString());
        System.out.println("Here are the words to find:\n" + finalBoard.getPlacedWords());
        System.out.println("\nPress enter to see the solution.");
        String temp = input.nextLine();
        System.out.println("Solution:\n" + finalBoard.getSolution());
    }

    /** gets the topic of the wordsearch from the user **/
    private static ArrayList<String> getWords() {
        ArrayList<String> output;
        while (true) {
            System.out.println("Enter topic of wordsearch:");
            String topic = input.nextLine().strip().replaceAll(" ", "+");
            output = getWordsFromURL(topic);

            if ((output.size() == 0) || someNonLetter(output)) {
                System.err.println("Invalid input: " + topic + "\n");
            } else {
                return output;
            }
        }
    }

    /** returns true if any character in a list of strings contains a non-alphabetic character and false otherwise **/
    public static boolean someNonLetter(ArrayList<String> input) {
        for (String str : input) {
            for (int i=0; i<str.length(); i++) {
                if (str.charAt(i) == ' ') {
                    continue;
                }
                if (!Character.isLetter(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** populates foundPositions **/
    private static void populateFoundPositions(ArrayList<Word> solution) {
        int[] currentXTrail, currentYTrail;
        for (Word word : solution) {
            currentXTrail = WordAdder.getXTrail(word);
            currentYTrail = WordAdder.getYTrail(word);

            for (int i=0; i<currentXTrail.length; i++) {
                foundPositions.add(currentXTrail[i] + " " + currentYTrail[i]);
            }
        }
    }

    /** returns a list of words relating to a certain topic **/
    private static ArrayList<String> getWordsFromURL(String topic) {
        String data = webpageToStr("https://api.datamuse.com/words?ml=" + topic + "&max=" + wordsToFind);
        return parseInput(data);
    }

    /** returns a list of words from the given website data **/
    private static ArrayList<String> parseInput(String data) {
        String dataRemaining = data;
        int startOfWord;
        int endOfWord;
        ArrayList<String> output = new ArrayList<>();

        while (dataRemaining.contains("\"word\"")) {
            startOfWord = dataRemaining.indexOf("\"word\"") + 8;
            endOfWord = dataRemaining.indexOf("\",\"", startOfWord);
            output.add(dataRemaining.substring(startOfWord, endOfWord));
            dataRemaining = dataRemaining.substring(endOfWord);
        }

        return output;
    }

    /** returns the contents of a webpage as a string **/
    private static String webpageToStr(String urlStr) {
        try {
            //Instantiating the URL class
            URL url = new URL(urlStr);
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext()) {
                sb.append(sc.next() + " ");
            }
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
            //Removing the HTML tags
            result = result.replaceAll("<[^>]*>", "");
            result = result.substring(0, result.length() - 1);
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to retrieve data.");
        }
    }

    /** returns a deep copy of a 2D array **/
    public static String[][] cloneArray(String[][] src) {
        int length = src.length;
        String[][] target = new String[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
}