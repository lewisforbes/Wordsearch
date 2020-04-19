import java.util.ArrayList;

public class Word {

    private String name;

    private int xSize;
    private int ySize;

    public String position;
    private int xMin;
    private int xMax;
    private int yMin;
    private int yMax;

    public ArrayList<ArrayList<Direction>> directionIteration;
    private ArrayList<Direction> direction;
    private int directionItersMade = 0;

    public Word(String name, int xSize, int ySize) {
        this.name  = name.toUpperCase().replaceAll(" ", "");
        this.xSize = xSize;
        this.ySize = ySize;
        this.directionIteration = Direction.getRandomIteration();
        this.direction = directionIteration.get(0);
        resetPos();
    }


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

    public boolean canIteratePosition() {
        if ((getX()+1<=xMax) || (getY()+1<=yMax))  {
            return true;
        } else {
            return false;
        }
    }

    public String getPosition() {
        return position;
    }

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

    public boolean canIterateDirection() {
        if ((directionIteration.indexOf(direction)+1) == directionIteration.size()) {
            return false;
        }

        return true;
    }

    public int getDirectionItersMade() {
        return directionItersMade;
    }

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

    public String getName() {
        return name;
    }

    public int getX() {
        try {
            return Integer.parseInt("" + position.split(" ")[0]);
        } catch (NullPointerException e) {
            throw new NullPointerException("Position: " + position);
        }
        }

    public int getY() {
        return Integer.parseInt("" + position.split(" ")[1]);
    }

    public ArrayList<Direction> getDirection() {
        if (direction == null) {
            return directionIteration.get(0);
        } else {
            return direction;
        }
    }

    public void mkDirectionBasic() {
        directionIteration = Direction.getBasicIteration();
    }

    @Override
    public String toString() {
        return name;
    }

    public void setDirection(ArrayList<Direction> direction) {
        this.direction = direction;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
