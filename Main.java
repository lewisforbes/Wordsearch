import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    /** allows user to input data **/
    private static final Scanner input = new Scanner(System.in);

    /** the number of words to find for the wordsearch **/
    private static final int wordsToFind = 15;
    /** the board **/
    private static Board board;

    public static void main(String[] args) {
        System.out.println("- W O R D S E A R C H E S -");

        ArrayList<String> words = getWords();
        board = new Board(BoardMaker.makeWordsearch(words));

        String[][] fullBoard = Utils.clone2DArray(board.getBoard());
        ArrayList<Word> solution = Solver.solve(board, words);

        Board finalBoard = new Board(fullBoard);
        finalBoard.setPlacedWords(solution);
        finalBoard.setNamesOfWords(words);
        finalBoard.updateNamesOfWords();
        System.out.println(finalBoard.boardToString());
        userPlaying(finalBoard);
        System.out.println("\nThanks for playing!");
    }

    /** lets the user request to see a word's position or see the solution and end the game **/
    private static void userPlaying(Board finalBoard) {
        String seeSolution = "???";
        ArrayList<Word> matchedWords = new ArrayList<>();
        while (true) {
            System.out.println("Enter a word to see its position on the board.");
            System.out.println("Type '" + seeSolution + "' to see the solution and end the game.");
            String userInput = input.nextLine().strip().toUpperCase().replaceAll(" ", "");

            if (userInput.equalsIgnoreCase(seeSolution)) {
                System.out.println("\nHere is the solution:\n" + finalBoard.getSolution());
                return;
            }

            ArrayList<String> placedWordsNames = new ArrayList<>();
            for (Word placedWord : finalBoard.getPlacedWords()) {
                placedWordsNames.add(placedWord.getName());
            }

            if (placedWordsNames.contains(userInput)) {
                matchedWords.clear();
                for (int i=0; i<finalBoard.getPlacedWords().size(); i++) {
                    if (finalBoard.getPlacedWords().get(i).getName().equals(userInput)) {
                        matchedWords.add(finalBoard.getPlacedWords().get(i));
                    }
                }
                System.out.println(finalBoard.getSolutionOfWords(matchedWords));
                System.out.println("Press enter to see full board again.");
                String temp = input.nextLine();
                System.out.println(finalBoard.boardToString());
            } else {
                System.err.println("Could not find the word: " + userInput);
            }
        }
    }

    /** gets the topic of the wordsearch from the user **/
    private static ArrayList<String> getWords() {
        ArrayList<String> output;
        while (true) {
            System.out.println("Enter topic of wordsearch:");
            String topic = input.nextLine().strip().replaceAll(" ", "+");
            output = getWordsFromURL(topic);

            if ((output.size() == 0) || (!allLetters(output))) {
                System.err.println("Invalid input: " + topic + "\n");
            } else {
                return output;
            }
        }
    }

    /** returns true if every character in a list of strings is a letter or a space, and false otherwise **/
    public static boolean allLetters(ArrayList<String> input) {
        for (String str : input) {
            for (int i=0; i<str.length(); i++) {
                if (str.charAt(i) == ' ') {
                    continue;
                }
                if (!Character.isLetter(str.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
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
}