import java.util.ArrayList;

public class Word {

    /** the word **/
    private String name;

    /** the dimensions of the board the word's on **/
    private int xSize;
    private int ySize;

    /** the position of the first letter of the word on the board, in the form '<x> <y>' **/
    public String position;

    /** the constraints of a word's position on the board considering its length and direction **/
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

    /** the order in which to iterate a word's direction **/
    private ArrayList<ArrayList<Direction>> directionIteration;
    /** the direction of the word on the board **/
    private ArrayList<Direction> direction;
    /** the number of direction iterations made **/
    private int directionItersMade = 0;


    /** make a new word object **/
    public Word(String name, int xSize, int ySize) {
        this.name  = name.toUpperCase().replaceAll(" ", "");
        this.xSize = xSize;
        this.ySize = ySize;
        this.directionIteration = Direction.getRandomIteration();
        this.direction = directionIteration.get(0);
        resetPos();
    }

    /** returns a word's position **/
    public String getPosition() {
        return position;
    }

    /** returns the name if the word **/
    public String getName() {
        return name;
    }

    /** returns a word's direction **/
    public ArrayList<Direction> getDirection() {
        if (direction == null) {
            return directionIteration.get(0);
        } else {
            return direction;
        }
    }

    /** returns the x coordinate of the first letter of the word **/
    public int getX() {
        try {
            return Integer.parseInt("" + position.split(" ")[0]);
        } catch (NullPointerException e) {
            throw new NullPointerException("Position: " + position);
        }
    }

    /** returns the y coordinate of the first letter of the word **/
    public int getY() {
        return Integer.parseInt("" + position.split(" ")[1]);
    }

    /** returns the number of direction iterations made **/
    public int getDirectionItersMade() {
        return directionItersMade;
    }

    /** sets the direction iteration to the basic iteration **/
    public void mkDirectionBasic() {
        directionIteration = Direction.getBasicIteration();
    }

    /** sets the direction to the given one **/
    public void setDirection(ArrayList<Direction> direction) {
        this.direction = direction;
    }

    /** sets the position to the given one **/
    public void setPosition(String position) {
        this.position = position;
    }

    /** resets a word's position and position constraints **/
    public void resetPos() {
        int xMin = 0;
        int xMax = xSize-1;
        int yMin = 0;
        int yMax = ySize-1;

        if (direction.contains(Direction.UP)) {
            yMax = (ySize-1) - (name.length()-1);
        }

        if (direction.contains(Direction.DOWN)) {
            yMin = name.length()-1;
        }

        if (direction.contains(Direction.RIGHT)) {
            xMax = (xSize-1) - (name.length()-1);
        }

        if (direction.contains(Direction.LEFT)) {
            xMin = name.length()-1;
        }

        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        position = xMin + " " + yMin;
    }

    /** returns true if a word can be iterated up or right on the board, false otherwise **/
    public boolean canIteratePosition() {
        if ((getX()+1<=xMax) || (getY()+1<=yMax))  {
            return true;
        } else {
            return false;
        }
    }

    /** iterates a word's position. If unable to, it resets it **/
    public void iteratePosition() {
        int currentX = getX();
        int currentY = getY();

        if (currentX+1<=xMax) {
            position = (currentX+1) + " " + currentY;
            return;
        }

        if (currentY+1<=yMax) {
            position = xMin + " " + (currentY+1);
            return;
        }

        position = xMin + " " + yMin;
    }

    /** returns true if a word's direction can be changed to the next one on the directionIteration list, false otherwise **/
    public boolean canIterateDirection() {
        if ((directionIteration.indexOf(direction)+1) == directionIteration.size()) {
            return false;
        }

        return true;
    }

    /** changes direction to next one on directionIteration list or resets it to the first one if no more **/
    public void iterateDirection() {
        directionItersMade++;
        int directionIndex = directionIteration.indexOf(direction);
        directionIndex++;

        if (directionIndex == directionIteration.size()) {
            direction = directionIteration.get(0);
            resetPos();
            return;
        }

        direction = directionIteration.get(directionIndex);
    }

    /** allows a word to be printed **/
    @Override
    public String toString() {
        return name.toUpperCase();
    }
}
