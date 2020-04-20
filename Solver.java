import java.util.ArrayList;

public class Solver {

    private static ArrayList<Word> foundWords = new ArrayList<>();
    private static String[][] board;
    private static int xSize;
    private static int ySize;

    private static final int errorInt = -1;

    public static ArrayList<Word> solve(Board givenBoard, ArrayList<String> givenWordsToFind) {
        board = Utils.clone2DArray(givenBoard.getBoard());
        xSize = givenBoard.xSize();
        ySize = givenBoard.ySize();
        replaceNulls(".");
        ArrayList<String> wordsToFind = Utils.cloneStrArrLst(givenWordsToFind);
        wordsToFind = Utils.removeSpacesFromArrLst(wordsToFind);
        wordsToFind = Utils.capitaliseArrLst(wordsToFind);
        findWords(wordsToFind);
        if (foundWords.size() < wordsToFind.size()) {
            throw new IllegalArgumentException(
                    "wordsToFind.size(): " + wordsToFind.size() + " - foundWords.size(): " + foundWords.size()
                            + "\nfoundWords: " + foundWords + "\nwordsToFind: " + wordsToFind);
        }
        return removeDuplicates(foundWords);
    }

    /** removes words from list that are contained in other ones **/
    private static ArrayList<Word> removeDuplicates(ArrayList<Word> originalList) {
        ArrayList<Integer> duplicatesIndexes = new ArrayList<>();
        ArrayList<String> originalNames = new ArrayList<>();
        for (Word word : originalList) {
            originalNames.add(word.getName());
        }

        for (int i=0; i<originalNames.size(); i++) {
            if (originalNames.subList(0, i).contains(originalNames.get(i))) {
                duplicatesIndexes.add(i);
            } else if ((i+1) != originalNames.size()) {
                if (originalNames.subList((i + 1), (originalList.size())).contains(originalNames.get(i))) {
                    duplicatesIndexes.add(i);
                }
            }
        }

        ArrayList<Word> duplicates = new ArrayList<>();
        for (int i : duplicatesIndexes) {
            duplicates.add(originalList.get(i));
        }

        boolean substring;
        ArrayList<Word> wordsToRemove = new ArrayList<>();
        for (Word word : duplicates) {
            substring = false;
            for (String wordName : originalNames) {
                if (wordName.contains(word.getName())) {
                    substring = true;
                }
            }
            if (substring) {
                wordsToRemove.add(word);
            }
        }
        for (Word word : wordsToRemove) {
            duplicates.remove(word);
        }

        ArrayList<Word> output = new ArrayList<>();
        for (Word word : originalList) {
            if (!duplicates.contains(word)) {
                output.add(word);
            }
        }
        return output;
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