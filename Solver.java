import java.util.ArrayList;

public class Solver {

    private static ArrayList<Word> foundWords = new ArrayList<>();
    private static String[][] board;
    private static int xSize;
    private static int ySize;

    private static final int errorInt = -1;

    public static ArrayList<Word> solve(Board givenBoard, ArrayList<Word> words) {
        board = givenBoard.getBoard().clone();
        xSize = givenBoard.xSize();
        ySize = givenBoard.ySize();
        replaceNulls("!");

        ArrayList<String> wordsToFind = new ArrayList<>();
        for (Word word : words) {
            wordsToFind.add(word.getName());
        }
        findWords(wordsToFind);
        if (foundWords.size() < wordsToFind.size()) {
            throw new IllegalArgumentException(
                    "wordsToFind.size(): " + wordsToFind.size() + " - foundWords.size(): " + foundWords.size()
                            + "\nfoundWords: " + foundWords);
        }
        return foundWords;
    }

    private static void replaceNulls(String toReplaceWith) {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                if (board[x][y] == null) {
                    board[x][y] = toReplaceWith;
                }
            }
        }
    }

    private static void findWords(ArrayList<String> wordsToFind) {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (String word : wordsToFind) {
                    if (board[x][y].equals("" + word.charAt(0))) {
                        firstLetterMatches(word, x, y);
                    }
                }
            }
        }
    }

    private static void firstLetterMatches(String word, int x, int y) {
        int currentX, currentY;
        Word foundWord;

        for (ArrayList<Direction> directionFull : Direction.getDefaultIteration()) {
            currentX = x;
            currentY = y;
            for (int c=1; c<word.length(); c++) {
                for (Direction direction : directionFull) {
                    currentX = iterateX(direction, currentX);
                    currentY = iterateY(direction, currentY);
                }
                if (((currentX==errorInt) || (currentY==errorInt)) ||
                        (!doesLetterMatch(word.charAt(c), currentX, currentY))) {
                    break;
                }

                if (c==(word.length()-1)) {
                    foundWord = new Word(word, xSize, ySize);
                    foundWord.setDirection(directionFull);
                    foundWord.setPosition(x + " " + y);
                    foundWords.add(foundWord);
                }
            }
        }
    }

    private static boolean doesLetterMatch(char letter, int x, int y) {
        return board[x][y].equals("" + letter);
    }

    private static int iterateX(Direction d, int init) {
        int output = init;
        switch (d) {
            case RIGHT:
                output++;
                break;

            case LEFT:
                output--;
                break;
        }

        if (output < 0) {
            return errorInt;
        }

        if (output>=xSize) {
            return errorInt;
        }
        return output;
    }

    private static int iterateY(Direction d, int init) {
        int output = init;
        switch (d) {
            case UP:
                output++;
                break;

            case DOWN:
                output--;
                break;
        }

        if (output < 0) {
            return errorInt;
        }

        if (output>=ySize) {
            return errorInt;
        }
        return output;
        }
}