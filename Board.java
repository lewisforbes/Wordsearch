import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A class to hold information about a board (wordsearch)
 */
public class Board {

    /** an object to generate random numbers **/
    private static final Random RANDOM = new Random();

    /** a 2D array representation of the board, where unfilled squares are null **/
    private String[][] board;
    /** a list of the positions and directions of all words on the board **/
    private ArrayList<Word> placedWords = new ArrayList<>();
    /** a list of the words on the board (for printing) **/
    private ArrayList<String> namesOfWords = new ArrayList<>();

    /** makes a new board object **/
    public Board(String[][] board) {
        this.board = board;
    }

    /** returns the board as a string array **/
    public String[][] getBoard() {
        return board;
    }

    /** returns the board's x dimension **/
    public int xSize() {
        return board.length;
    }

    /** returns the board's y dimension **/
    public int ySize() {
        return board[0].length;
    }



    /** returns the unsolved board as a string, with the words on the side **/
    public String boardToString() {
        if ((namesOfWords == null) || (namesOfWords.size() == 0)) {
            throw new IllegalArgumentException("There is nothing in namesOfWords");
        }

        int colsOfNofW = (int) Math.ceil((namesOfWords.size())/((double) ySize()-2));
        int padding = 5;
        int maxNMLength = 0;
        for (String name : namesOfWords) {
            if (name.length() > maxNMLength) {
                maxNMLength = name.length();
            }
        }

        int xSize = xSize();
        int ySize = ySize();
        String[][] board = getBoard();
        String output = "";

        boolean headingPrinted = false;
        int nameOfWordOn = 0;
        for (int y = (ySize - 1); y >= 0; y--) {
            for (int x = 0; x <= (xSize - 1); x++) {
                if (board[x][y] == null) {
                    output += getBlankChar() + " ";
                } else {
                    output += board[x][y] + " ";
                }
            }
            output += " ".repeat(padding);

            if (y==(ySize-1)) {
                if (!headingPrinted) {
                    output += "Words to Find:\n";
                }
                continue;
            }

            if (y==(ySize-2)) {
                if (!headingPrinted) {
                    output += "\n";
                    headingPrinted = true;
                }
                continue;
            }
            if (nameOfWordOn < namesOfWords.size()) {
                for (int i=0; i<colsOfNofW; i++) {
                    output += namesOfWords.get(nameOfWordOn);
                    output += "   " + " ".repeat(maxNMLength-namesOfWords.get(nameOfWordOn).length());
                    nameOfWordOn++;
                    if (!(nameOfWordOn<namesOfWords.size())) {
                        break;
                    }
                }
            }
            output += "\n";
        }
        return output;
    }

    /** returns the board's solution as a string **/
    public String getSolution() {
        int xSize = xSize();
        int ySize = ySize();
        String[][] board = getBoard();
        String output = "";

        for (int y = (ySize - 1); y >= 0; y--) {
            for (int x = 0; x <= (xSize - 1); x++) {
                if (board[x][y] == null) {
                    output += "-" + " ";
                } else {
                    output += board[x][y] + " ";
                }
            }
            output += "\n";
        }
        return output;
    }

    /** returns a board containing all blanks apart form the words given **/
    public String getSolutionOfWords(ArrayList<Word> words) {
        ArrayList<int[]> xTrails = new ArrayList<>();
        ArrayList<int[]> yTrails = new ArrayList<>();
        for (Word word : words) {
            xTrails.add(WordAdder.getXTrail(word));
            yTrails.add(WordAdder.getYTrail(word));
        }

        ArrayList<String[]> coordinates = new ArrayList<>();
        String[] currentCoordinates;
        for (int w=0; w<words.size(); w++) {
            currentCoordinates = new String[xTrails.get(w).length];
            for (int i = 0; i < currentCoordinates.length; i++) {
                currentCoordinates[i] = (xTrails.get(w)[i] + " " + yTrails.get(w)[i]);
            }
            coordinates.add(currentCoordinates);
        }
        String output = "";
        String currentPos;
        boolean match;

        for (int y = (ySize() - 1); y >= 0; y--) {
            for (int x = 0; x <= (xSize() - 1); x++) {
                currentPos = x + " " + y;
                match = false;
                for (String[] coords : coordinates) {
                    if (Utils.arrContainsStr(coords, currentPos)) {
                        output += words.get(coordinates.indexOf(coords)).getName().charAt(Utils.arrIndexOf(currentPos, coords)) + " ";
                        match = true;
                    }
                }
                if (!match) {
                    output += "-" + " ";
                }
            }
            output += "\n";
        }
        return output;
    }

    /** sets placedWords to a given value **/
    public void setPlacedWords(ArrayList<Word> placedWords) {
        this.placedWords = placedWords;
    }

    /** sets namesOfWords to a given value with all names of words capitalised **/
    public void setNamesOfWords(ArrayList<String> namesOfWords) {
        this.namesOfWords = Utils.sentenceCaseArrLst(namesOfWords);
    }

    /** updates namesOfWords to account for duplicates in placedWords **/
    public void updateNamesOfWords() {
        if ((placedWords.size() == 0) || (namesOfWords.size() == 0)) {
            throw new IllegalArgumentException("Function has been called too early");
        }
        ArrayList<String> namesOfPlacedWords = new ArrayList<>();
        for (Word word : placedWords) {
            namesOfPlacedWords.add(word.getName());
        }

        int currentNameCount;
        for (String name : namesOfWords) {
            currentNameCount = 0;
            for (Word word : placedWords) {
                if (word.getName().equalsIgnoreCase(name.replaceAll(" ", ""))) {
                    currentNameCount++;
                }
            }
            if (currentNameCount == 0) {
                throw new IllegalArgumentException(
                        "currentNameCount should have been at least 1\n\tplacedWords: " + placedWords +
                                "\n\tname: " + name + "\n\tnameOfWords: " + namesOfWords);
            }
            if (currentNameCount>1) {
                name = name + " (x" + currentNameCount + ")";
            }
        }
    }

    /** returns the placedWords list **/
    public ArrayList<Word> getPlacedWords() {
        return placedWords;
    }

    /** removes any rows or columns that are completely blank **/
    public void strip() {
        ArrayList<Integer> rowsToRemove = new ArrayList<>();
        ArrayList<Integer> colsToRemove = new ArrayList<>();

        for (int x=0; x<xSize(); x++) {
            if (nullCol(x)) {
                colsToRemove.add(x);
            }
        }

        for (int y=0; y<ySize(); y++) {
            if (nullRow(y)) {
                rowsToRemove.add(y);
            }
        }

        String[][] output =
                new String[xSize() - colsToRemove.size()][ySize() - rowsToRemove.size()];

        int xOn = -1;
        int yOn = -1;


        for (int y=0; y<ySize(); y++) {
            xOn = -1;
            if (!rowsToRemove.contains(y)) {
                yOn++;
                for (int x=0; x<xSize(); x++) {
                    if (!colsToRemove.contains(x)) {
                        xOn++;
                        output[xOn][yOn] = board[x][y];
                    }
                }
            }
        }
        board = output;
    }

    /** adds in blank rows or columns until the board is square **/
    public void mkSquare() {
        if (xSize() > ySize()) {
            verticalPad(xSize());
        }
        if (ySize() > xSize()) {
            horizontalPad(ySize());
        }
    }

    /** adds in blank rows in until the y dimension is the provided size **/
    public void verticalPad(int ySizeGoal) {
        if (ySizeGoal < ySize()) {
            throw new IllegalArgumentException("The ySizeGoal is smaller than the ySize");
        }

        int blankRowsBelow = RANDOM.nextInt(ySizeGoal - ySize());

        String[][] newBoard = new String[xSize()][ySizeGoal];

        for (int y=0; y<ySizeGoal; y++) {
            if (y>=blankRowsBelow) {
                for (int x=0; x<xSize(); x++) {
                    try {
                        newBoard[x][y] = board[x][y-blankRowsBelow];
                    } catch (ArrayIndexOutOfBoundsException e) { }
                }
            }
        }
        board = newBoard;
    }

    /** adds in blank columns in until the x dimension is the provided size **/
    public void horizontalPad(int xSizeGoal) {
        if (xSizeGoal < xSize()) {
            throw new IllegalArgumentException("The ySizeGoal is smaller than the ySize");
        }

        int blankColsLeft = RANDOM.nextInt(xSizeGoal - xSize());

        String[][] newBoard = new String[xSizeGoal][ySize()];

        for (int y=0; y<ySize(); y++) {
            for (int x=0; x<xSizeGoal; x++) {
                if (x>=blankColsLeft) {
                    try {
                        newBoard[x][y] = board[x-blankColsLeft][y];
                    } catch (ArrayIndexOutOfBoundsException e) { }
                }
            }
        }
        board = newBoard;
    }

    /** flips the board randomly, so the solution is the same but the positions are different **/
    public void shuffle() {
        switch (RANDOM.nextInt(4)) {
            case 0:
                return;

            case 1:
                flipX();
                return;

            case 2:
                flipY();
                return;

            case 3:
                flipX();
                flipY();
                return;
        }
        throw new IllegalArgumentException("Random number was not 0-3");
    }

    /** mirrors every x coordinate **/
    private void flipX() {
        String[][] output = new String[xSize()][ySize()];
        for (int y = 0; y < ySize(); y++) {
            for (int x = 0; x < xSize(); x++) {
                output[x][y] = board[xSize() - 1 - x][y];
            }
        }
        board = output;
    }

    /** mirrors every y coordinate **/
    private void flipY() {
        String[][] output = new String[xSize()][ySize()];
        for (int y=0; y<ySize(); y++) {
            for (int x=0; x<xSize(); x++) {
                output[x][y] = board[x][ySize()-1-y];
            }
        }
        board = output;
    }

    /** returns true is a row is completely null, false otherwise **/
    private boolean nullRow(int y) {
        for (int x=0; x<xSize(); x++) {
            if (board[x][y] != null) {
                return false;
            }
        }
        return true;
    }

    /** returns true if a column is completely null, false otherwise **/
    private boolean nullCol(int x) {
        for (int y=0; y<ySize(); y++) {
            if (board[x][y] != null) {
                return false;
            }
        }
        return true;
    }

    /** converts a letter to its equivalent in the font for letters that haven't been found **/
    private String toUnfoundFont(String letter) {
        int alphPos = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(letter);
        if (alphPos == -1) {
            return letter;
        }
        String[] newAlph = new String[] {"\uD83C\uDD50", "\uD83C\uDD51", "\uD83C\uDD52", "\uD83C\uDD53", "\uD83C\uDD54", "\uD83C\uDD55", "\uD83C\uDD56", "\uD83C\uDD57", "\uD83C\uDD58", "\uD83C\uDD59", "\uD83C\uDD5A", "\uD83C\uDD5B", "\uD83C\uDD5C", "\uD83C\uDD5D", "\uD83C\uDD5E", "\uD83C\uDD5F", "\uD83C\uDD60", "\uD83C\uDD61", "\uD83C\uDD62", "\uD83C\uDD63", "\uD83C\uDD64", "\uD83C\uDD65", "\uD83C\uDD66", "\uD83C\uDD67", "\uD83C\uDD68", "\uD83C\uDD69"};
        return "" + newAlph[alphPos];
    }

    /** converts a letter to its equivalent in the font for letters that have been found **/
    private String toFoundFont(String letter) {
        int alphPos = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(letter);
        if (alphPos == -1) {
            throw new IllegalArgumentException("alphPos = -1, letter = " + letter);
        }
        String newAlph = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓄⓃⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ";
        return "" + newAlph.charAt(alphPos);
    }

    /** returns a populated blank square on the board **/
    private String getBlankChar() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return "" + alphabet.charAt(RANDOM.nextInt(alphabet.length()));
    }
}