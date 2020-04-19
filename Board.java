import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Board {

    private static final Random RANDOM = new Random();

    private String[][] board;
    private ArrayList<Word> placedWords = new ArrayList<>();

    public Board(String[][] board) {
        this.board = board;
    }

    public String[][] getBoard() {
        return board;
    }

    public int xSize() {
        return board.length;
    }

    public int ySize() {
        return board[0].length;
    }

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

    private void flipX() {
        String[][] output = new String[xSize()][ySize()];
        for (int y = 0; y < ySize(); y++) {
            for (int x = 0; x < xSize(); x++) {
                output[x][y] = board[xSize() - 1 - x][y];
            }
        }
        board = output;
    }

    private void flipY() {
        String[][] output = new String[xSize()][ySize()];
        for (int y=0; y<ySize(); y++) {
            for (int x=0; x<xSize(); x++) {
                output[x][y] = board[x][ySize()-1-y];
            }
        }
        board = output;
    }

    public void mkSquare() {
        if (xSize() > ySize()) {
            verticalPad(xSize());
        }
        if (ySize() > xSize()) {
            horizontalPad(ySize());
        }
    }

    private boolean nullRow(int y) {
        for (int x=0; x<xSize(); x++) {
            if (board[x][y] != null) {
                return false;
            }
        }
        return true;
    }

    private boolean nullCol(int x) {
        for (int y=0; y<ySize(); y++) {
            if (board[x][y] != null) {
                return false;
            }
        }
        return true;
    }

    public void updatePlacedWords() {
        placedWords = WordAdder.getPlacedWords();
        Collections.shuffle(placedWords);
    }

    public String boardToString() {
        int xSize = xSize();
        int ySize = ySize();
        String[][] board = getBoard();
        String output = "";

        for (int y = (ySize - 1); y >= 0; y--) {
            for (int x = 0; x <= (xSize - 1); x++) {
                if (board[x][y] == null) {
                    output += getBlankChar() + " ";
                } else {
                    output += board[x][y] + " ";
                }
            }
            output += "\n";
        }
        return output;
    }

    public String boardToStringWithSolved(ArrayList<String> positions) {
        String[][] originalBoard = board;
        String output;
        int currentX, currentY;

        for (String pos : positions) {
            currentX = Integer.parseInt(pos.split( " ")[0]);
            currentY = Integer.parseInt(pos.split( " ")[1]);

            board[currentX][currentY] = board[currentX][currentY].toLowerCase();
        }

        output = boardToString();
        board = originalBoard;
        return output;
    }

    public ArrayList<Word> getPlacedWords() {
        return placedWords;
    }

    private String getBlankChar() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return "" + alphabet.charAt(RANDOM.nextInt(alphabet.length()));
    }

    @Override
    public Board clone() {
        Board board = null;
        try {
            board = (Board) super.clone();
        } catch (CloneNotSupportedException e) {
            board = new Board(this.board.clone());
        }
        board.placedWords = (ArrayList<Word>) this.placedWords.clone();
        return board;
    }
}
