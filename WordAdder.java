import java.util.ArrayList;

public class WordAdder {
    private static ArrayList<Word> words;
    private static boolean haveIterated;
    private static String[][] board;
    private static int xSize;
    private static int ySize;
    private static ArrayList<Word> placedWords = new ArrayList<>();

    // USED FOR ADDING LONG WORDS //
    public static boolean canAddLongWord(Word word, Board givenBoard) {
        board = givenBoard.getBoard();

        if (canAdd(word)) {
            return true;
        } else {
            return false;
        }
    }

    public static Board addLongWordToBoard(Word word, Board givenBoard) {
        board = givenBoard.getBoard();

        addWordToBoard(word);
        return new Board(board);
    }

    public static String[][] tryAllPositions(ArrayList<Word> givenWords, int givenXSize, int givenYSize) {
        words = givenWords;
        xSize = givenXSize;
        ySize = givenYSize;
        haveIterated = false;
        board = new String[xSize][ySize];
        go();
        return board;
    }

    private static void go() {
        while (canIterateAnyPos()) {
            if (!haveIterated) {
                haveIterated = true;
            } else {
                iteratePositions();
            }

            board = new String[xSize][ySize];

            for (int i = 0; i < words.size(); i++) {
                try {
                    if (canAdd(words.get(i))) {
                        addWordToBoard(words.get(i));
                        if (i == (words.size() - 1)) {
                            return;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }
        board = null;
    }

    private static boolean canIterateAnyPos() {
        for (Word word : words) {
            if (word.canIteratePosition()) {
                return true;
            }
        }
        return false;
    }

    private static void iteratePositions() {
        int indexToIterate = -1;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).canIteratePosition()) {
                indexToIterate = i;
            }
        }

        if (indexToIterate == (-1)) {
            throw new IllegalArgumentException("No word's direction could be iterated.");
        }

        for (int i = indexToIterate; i < words.size(); i++) {
            words.get(i).iteratePosition();
        }
    }

    private static boolean canAdd(Word word) {
        int[] xTrail = getXTrail(word);
        int[] yTrail = getYTrail(word);
        String name = word.getName();

        int currentX;
        int currentY;
        for (int i = 0; i < name.length(); i++) {
            currentX = xTrail[i];
            currentY = yTrail[i];

            if (board[currentX][currentY] == null) {
                continue;
            }

            if (board[currentX][currentY].equals("" + name.charAt(i))) {
                continue;
            }

            return false;
        }

        return true;
    }

    private static void addWordToBoard(Word word) {
        int[] xTrail = getXTrail(word);
        int[] yTrail = getYTrail(word);
        String name = word.getName();

        for (int i = 0; i < name.length(); i++) {
            board[xTrail[i]][yTrail[i]] = "" + name.charAt(i);
            board[xTrail[i]][yTrail[i]] = "" + name.charAt(i);
        }
        if (placedWords.contains(word)) {
            placedWords.remove(word);
        }
        placedWords.add(word);
    }

    public static int[] getXTrail(Word word) {
        ArrayList<Direction> direction = word.getDirection();
        int xInit = word.getX();

        int[] xTrail = new int[word.getName().length()];

        int i = 0;
        if (direction.contains(Direction.LEFT)) {
            for (int x = xInit; i < xTrail.length; x--) {
                xTrail[i] = x;
                i++;
            }
        } else if (direction.contains(Direction.RIGHT)) {
            for (int x = xInit; i < xTrail.length; x++) {
                xTrail[i] = x;
                i++;
            }
        } else {
            while (i < xTrail.length) {
                xTrail[i] = xInit;
                i++;
            }
        }

        if (xTrail[0] == -1) {
            throw new IllegalArgumentException("got here");
        }

        return xTrail;
    }

    public static int[] getYTrail(Word word) {
        ArrayList<Direction> direction = word.getDirection();

        int yInit = word.getY();
        int[] yTrail = new int[word.getName().length()];

        int i = 0;
        if (direction.contains(Direction.DOWN)) {
            for (int y = yInit; i < yTrail.length; y--) {
                yTrail[i] = y;
                i++;
            }
        } else if (direction.contains(Direction.UP)) {
            for (int y = yInit; i < yTrail.length; y++) {
                yTrail[i] = y;
                i++;
            }
        } else {
            while (i < yTrail.length) {
                yTrail[i] = yInit;
                i++;
            }
        }

        return yTrail;
    }

    public static ArrayList<Word> getPlacedWords() {
        return placedWords;
    }
}