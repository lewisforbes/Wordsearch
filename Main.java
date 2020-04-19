import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final Scanner input = new Scanner(System.in);
    private static final Random RANDOM = new Random();

    private static final int wordsToFind = 15;
    private static Board board;
    private static ArrayList<String> foundPositions = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Enter topic of wordsearch:");
        //String topic = input.nextLine().strip().replaceAll(" ", "+");
        String topic = "dog"; // TODO delete
        System.out.println(topic + "\n");
        ArrayList<String> words = getWords(topic);

        board = BoardMaker.makeWordsearch(words);
        System.out.println(board.boardToString());
        String[][] fullBoard = cloneArray(board.getBoard());
        ArrayList<Word> solution = Solver.solve(board.clone(), board.getPlacedWords());
        System.out.println(solution);

        int[] currentXTrail, currentYTrail;
        for (Word word : solution) {
            currentXTrail = WordAdder.getXTrail(word);
            currentYTrail = WordAdder.getYTrail(word);

            if (currentXTrail.length != currentYTrail.length) {
                throw new IllegalArgumentException("xTrail length: " + currentXTrail.length + ", yTrail length: " + currentYTrail.length);
            }

            for (int i=0; i<currentXTrail.length; i++) {
                foundPositions.add(currentXTrail[i] + " " + currentYTrail[i]);
            }
        }

        System.out.println(new Board(fullBoard).boardToStringWithSolved(foundPositions));
    }

    private static ArrayList<String> getWords(String topic) {
        String data = webpageToStr("https://api.datamuse.com/words?ml=" + topic + "&max=" + wordsToFind);
        return parseInput(data);
    }

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

    public static String[][] cloneArray(String[][] src) {
        int length = src.length;
        String[][] target = new String[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
}