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

    public static void main(String[] args) {
        System.out.println("Enter topic of wordsearch:");
        String topic = input.nextLine().strip().replaceAll(" ", "+");

        ArrayList<String> words = getWords(topic);
        board = BoardMaker.makeWordsearch(words);
        System.out.println(board);
        System.out.println(board.deleteme());
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
}