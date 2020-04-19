import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The directions modifiers a word can have
 */
public enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    /** returns a random iteration of all possible directions **/
    public static ArrayList<ArrayList<Direction>> getRandomIteration() {
        ArrayList<ArrayList<Direction>> output = getDefaultIteration();
        Collections.shuffle(output);
        return output;
    }

    /** returns the default (ordered) direction iteration **/
    public static ArrayList<ArrayList<Direction>> getDefaultIteration() {
        ArrayList<ArrayList<Direction>> output = new ArrayList<>();

        output.add(new ArrayList<>(Arrays.asList(UP)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT, UP)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT, DOWN)));
        output.add(new ArrayList<>(Arrays.asList(DOWN)));
        output.add(new ArrayList<>(Arrays.asList(LEFT, DOWN)));
        output.add(new ArrayList<>(Arrays.asList(LEFT)));
        output.add(new ArrayList<>(Arrays.asList(LEFT, UP)));

        return output;
    }

    /** returns the basic iteration of directions **/
    public static ArrayList<ArrayList<Direction>> getBasicIteration() {
        ArrayList<ArrayList<Direction>> output = new ArrayList<>();

        output.add(new ArrayList<>(Arrays.asList(UP)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT)));

        return output;
    }
}
