import java.util.*;

/**
 * A static class which makes a wordsearch board from a given list of words
 */
public class BoardMaker {

    /** allows random numbers to be generated **/
    private static final Random RANDOM = new Random();

    /** a list of words. Has multiple uses throughout the class **/
    private static ArrayList<Word> words = new ArrayList<>();

    /** true if the a direction iteration has been made, false otherwise **/
    private static boolean haveIterated = false;

    /** the shortest length of a long word **/
    private static final int longWordLength = 11;
    /** a list of the long words for this board **/
    private static ArrayList<String> longWords = new ArrayList<>();

    /** the number of short words to include per board segment **/
    private static final int shortWordsPerGroup = 4;
    /** a list of groups of short words for each segment **/
    private static ArrayList<String[]> shortWords = new ArrayList<>();
    /** a list of segments to be combined to a megaboard **/
    private static ArrayList<Board> boards = new ArrayList<>();
    /** the result of combined segments **/
    private static Board megaBoard;

    /** makes a wordsearch from a given list of words **/
    public static String[][] makeWordsearch(ArrayList<String> givenAllWordsStrs) {
        ArrayList<String> allWordsStrs = Utils.cloneStrArrLst(givenAllWordsStrs);
        separateWords(allWordsStrs);
        populateBoards();

        for (Board board : boards) {
            board.strip();
            board.shuffle();
        }

        if (boards.size() > 20) {
            throw new IllegalArgumentException("Too many words have been used.");
        }
        megaBoard = joinBoardSegment(boards);
        addLongWords();

        if (!words.isEmpty()) {
            completeBoard();
        }

        megaBoard.shuffle();
        megaBoard.strip();
        return megaBoard.getBoard();
    }

    /** looks for places to include long words inside of otherwise complete wordsearch **/
    private static void addLongWords() {
        words.clear();
        for (String word : longWords) {
            words.add(new Word(word, megaBoard.xSize(), megaBoard.ySize()));
        }

        for (Word word : words) {
            word.mkDirectionBasic();
        }

        ArrayList<Word> wordsToRemove = new ArrayList<>();
        for (int i = 0; i < Direction.getBasicIteration().size(); i++) {

            for (Word word : words) {
                word.resetPos();
                while (word.canIteratePosition()) {
                    word.iteratePosition();
                    try {
                        if (WordAdder.canAddLongWord(word, megaBoard)) {
                            megaBoard = WordAdder.addLongWordToBoard(word, megaBoard);
                            wordsToRemove.add(word);
                            break;
                        }
                    } catch (Exception e) {
                    }

                }
            }
        }

        for (Word word : wordsToRemove) {
            words.remove(word);
        }

        for (Word word : words) {
            word.iterateDirection();
        }

        wordsToRemove.clear();
    }

    /** adds remaining long words to edge of board **/
    private static void completeBoard() {
        for (Word longWord : words) {
            int r = RANDOM.nextInt(2);
            if (megaBoard.xSize() > megaBoard.ySize()) {
                if (r==0) {
                    megaBoard = verticalJoin(megaBoard, wordToHBoard(longWord.getName()));
                } else {
                    megaBoard = verticalJoin(wordToHBoard(longWord.getName()), megaBoard);
                }
            } else {
                if (r==1) {
                    megaBoard = horizontalJoin(megaBoard, wordToVBoard(longWord.getName()));
                } else {
                    megaBoard  = horizontalJoin(wordToVBoard(longWord.getName()), megaBoard);
                }
            }

        }
    }

    /** joins together given board segments **/
    private static Board joinBoardSegment(ArrayList<Board> boardSegments) {
        Board output = null;
        switch (boardSegments.size()) {
            case 0:
                return new Board(new String[1][1]);

            case 1:
                output = boardSegments.get(0);
                break;

            case 2:
                output = join2Boards(boardSegments.get(0), boardSegments.get(1));
                break;

            case 3:
                output = join3Boards(boardSegments);
                break;

            case 4:
                output = join4Boards(boardSegments);
                break;

            default:
                ArrayList<Board> superSegments = new ArrayList<>();
                try {
                    superSegments.add(joinBoardSegment(new ArrayList<>(boardSegments.subList(0, 4))));
                    superSegments.add(joinBoardSegment(new ArrayList<>(boardSegments.subList(4, 8))));
                    superSegments.add(joinBoardSegment(new ArrayList<>(boardSegments.subList(8, 12))));
                    superSegments.add(joinBoardSegment(new ArrayList<>(boardSegments.subList(12, 16))));
                } catch (Exception e) {}

                output = joinBoardSegment(superSegments);
                }

        output.strip();
        return output;
    }

    /** joins together the specified number of given board segments **/
    private static Board join2Boards(Board board1, Board board2) {
        Board output;
        if ((board1.xSize()+board2.xSize()) > (board1.xSize()+board2.xSize())) {
            output = verticalJoin(board1, board2);
        } else {
            output = horizontalJoin(board1, board2);
        }
        output.mkSquare();
        return output;
    }

    private static Board join3Boards(ArrayList<Board> boards) {
        if (boards.size() != 3) {
            throw new IllegalArgumentException("join3Boards used with list of size: " + boards.size());
        }

        int bigXValue = 0;
        int bigXIndex = -1;
        int bigYValue = 0;
        int bigYIndex = -1;

        for (int i = 0; i < 3; i++) {
            if (boards.get(i).xSize() > bigXValue) {
                bigXValue = boards.get(i).xSize();
                bigXIndex = i;
            }
            if (boards.get(i).ySize() > bigYValue) {
                bigYValue = boards.get(i).ySize();
                bigYIndex = i;
            }
        }

        int totalX = 0;
        int totalY = 0;

        for (int i = 0; i < 3; i++) {
            if (i != bigXIndex) {
                totalX += boards.get(i).xSize();
            }
            if (i != bigYIndex) {
                totalY += boards.get(i).ySize();
            }
        }

        ArrayList<Board> toJoin = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if ((totalX < totalY) && (i != bigXIndex)) {
                toJoin.add(boards.get(i));
            }
            if ((totalX >= totalY) && (i != bigYIndex)) {
                toJoin.add(boards.get(i));
            }
        }

        if (toJoin.size() != 2) {
            throw new IllegalArgumentException(
                    "toJoin should have size 2 but it has size: " + toJoin.size());
        }

        Board output;
        if (longWords.size() != 0) {
            if (totalX < totalY) {
                output = horizontalJoin(toJoin.get(0), wordToVBoard(longWords.get(0)));
                longWords.remove(0);
                output = horizontalJoin(output, toJoin.get(1));
                return verticalJoin(output, boards.get(bigXIndex));
            } else {
                output = verticalJoin(toJoin.get(0), wordToHBoard(longWords.get(0)));
                longWords.remove(0);
                output = verticalJoin(output, toJoin.get(1));
                return horizontalJoin(output, boards.get(bigYIndex));
            }
        } else {
            if (totalX < totalY) {
                output = horizontalJoin(toJoin.get(0), toJoin.get(1));
                return verticalJoin(output, boards.get(bigXIndex));
            } else {
                output = verticalJoin(toJoin.get(0), toJoin.get(1));
                return horizontalJoin(output, boards.get(bigYIndex));
            }

        }
    }

    private static Board join4Boards(ArrayList<Board> boards) { //TODO improve
        Board temp = join2Boards(boards.get(0), boards.get(1));
        if (temp.xSize()>temp.ySize()) {
            return verticalJoin(temp, (horizontalJoin(boards.get(2), boards.get(3))));
        } else {
            return horizontalJoin(temp, verticalJoin(boards.get(2), boards.get(3)));
        }
    }
    /****/

    /** converts a word to a vertical board and returns the board **/
    private static Board wordToVBoard(String givenStr) {
        String word = givenStr.replaceAll(" ", "").toUpperCase();
        String[][] board = new String[1][word.length()];
        if (RANDOM.nextInt(2) == 0) {
            for (int y=0; y<word.length(); y++) {
                board[0][y] = "" + word.charAt(y);
            }
        } else {
            for (int y=0; y<word.length(); y++) {
                board[0][y] = "" + word.charAt(word.length()-1-y);
            }
        }
        return new Board(board);
    }

    /** converts a word to a horizontal board and returns the board **/
    private static Board wordToHBoard(String givenStr) {
        String word = givenStr.replaceAll(" ", "").toUpperCase();
        String[][] board = new String[word.length()][1];
        if (RANDOM.nextInt(2) == 0) {
            for (int x=0; x<word.length(); x++) {
                board[x][0] = "" + word.charAt(x);
            }
        } else {
            for (int x=0; x<word.length(); x++) {
                board[x][0] = "" + word.charAt(word.length()-1-x);
            }
        }
        return new Board(board);
    }

    /** joins two boards together horizontally **/
    private static Board horizontalJoin(Board left, Board right) {
        if (left.ySize() < right.ySize()) {
            left.verticalPad(right.ySize());
        }
        if (right.ySize() < left.ySize()) {
            right.verticalPad(left.ySize());
        }

        String[][] output = new String[left.xSize() + right.xSize()][left.ySize()];

        for (int y=0; y<left.ySize(); y++) {
            for (int x=0; x<left.xSize(); x++) {
                output[x][y] = left.getBoard()[x][y];
            }

            for (int x=0; x<right.xSize(); x++) {
                output[x + left.xSize()][y] = right.getBoard()[x][y];
            }
        }
        return new Board(output);
    }

    /** joins to boards together vertically **/
    private static Board verticalJoin(Board top, Board bottom) {
        if (top.xSize() < bottom.xSize()) {
            top.horizontalPad(bottom.xSize());
        }
        if (bottom.xSize() < top.xSize()) {
            bottom.horizontalPad(top.xSize());
        }

        String[][] output = new String[top.xSize()][top.ySize() + bottom.ySize()];

        for (int y=0; y<bottom.ySize(); y++) {
            for (int x=0; x<bottom.xSize(); x++) {
                output[x][y] = bottom.getBoard()[x][y];
            }
        }
        for (int y=0; y<top.ySize(); y++) {
            for (int x=0; x<bottom.xSize(); x++) {
                output[x][y + bottom.ySize()] = top.getBoard()[x][y];
            }
        }
        return new Board(output);
    }

    /** separates words into long words and group of short words **/
    private static void separateWords(ArrayList<String> allWordsStrs) {
        for (String word : allWordsStrs) {
            if (word.length() >= longWordLength) {
                longWords.add(word);
            }
        }

        for (String longWord : longWords) {
            allWordsStrs.remove(longWord);
        }

        Collections.shuffle(allWordsStrs);

        String[] shortWordsGroup = new String[shortWordsPerGroup];
        for (int i = 0; i < allWordsStrs.size(); i++) {
            shortWordsGroup[i % shortWordsPerGroup] = allWordsStrs.get(i);

            if ((i % shortWordsPerGroup) == (shortWordsPerGroup - 1)) {
                shortWords.add(shortWordsGroup);
                shortWordsGroup = new String[shortWordsPerGroup];
            }
        }

        if (shortWordsGroup[shortWordsGroup.length - 1] == null) {
            shortWords.add(removeNulls(shortWordsGroup));
        }
    }

    /** makes a board for every group of short words and adds it it to the boards list **/
    private static void populateBoards() {
        int dim, totalLetters, xSize, ySize;
        for (String[] group : shortWords) {
            dim = 0;
            totalLetters = 0;


            for (String word : group) {
                totalLetters += word.length();
                if (word.length() > dim) {
                    dim = word.length();
                }
            }

            xSize = dim;
            ySize = dim;

            while ((xSize * ySize) < totalLetters) {
                xSize++;
                ySize++;
            }

            while (true) {

                try {
                    if (group.length == 0) {
                        break;
                    }
                    boards.add(new Board(mkBoard(group, xSize, ySize)));
                    break;
                } catch (Exception e) {}

                if (xSize > ySize) {
                    ySize++;
                } else {
                    xSize++;
                }
            }
        }

    }

    /** makes a board of given dimension from an array of words **/
    private static String[][] mkBoard(String[] wordsStrs, int xSize, int ySize) {
        Arrays.sort(wordsStrs, Comparator.comparing(String::length));
        words.clear();
        for (String wordStr : wordsStrs) {
            words.add(new Word(wordStr, xSize, ySize));

        }
        Collections.reverse(words);

        String[][] result;

        while (words.get(0).canIterateDirection()) {
            if (!haveIterated) {
                haveIterated = true;
            } else {
                iterateDirections();
            }

            for (Word word : words) {
                word.resetPos();
            }

            result = WordAdder.tryAllPositions(words, xSize, ySize);

            if (result != null) {
                return result;
            }
        }
        throw new IllegalArgumentException("No board was created for this dimension.");
    }

    /** iterates the directions of the words **/
    private static void iterateDirections() {
        int indexToIterate = -1;
        for (int i=0; i<words.size(); i++) {
            if (words.get(i).canIterateDirection()) {
                indexToIterate = i;
            }
        }

        if (indexToIterate == (-1)) {
            throw new IllegalArgumentException("No word's direction could be iterated.");
        }

        for (int i=indexToIterate; i<words.size(); i++) {
            words.get(i).iterateDirection();
        }
    }

    /** removes null elements from a string array **/
    private static String[] removeNulls(String[] strArr) {
        ArrayList<String> tempArrList = new ArrayList<>();

        for (String str : strArr) {
            if (str != null) {
                tempArrList.add(str);
            }
        }

        String[] output = new String[tempArrList.size()];
        for (int i=0; i<output.length; i++) {
            output[i] = tempArrList.get(i);
        }
        return output;
    }
}