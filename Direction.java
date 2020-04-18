import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    private static final Random RANDOM = new Random();


    public static ArrayList<ArrayList<Direction>> getRandomIteration() {
        ArrayList<ArrayList<Direction>> output = new ArrayList<>();

        ArrayList<Direction> horizontal = new ArrayList<>(Arrays.asList(LEFT, RIGHT));
        ArrayList<Direction> vertical = new ArrayList<>(Arrays.asList(UP, DOWN));
        Collections.shuffle(horizontal);
        Collections.shuffle(vertical);
        output.add(new ArrayList<>(Arrays.asList(horizontal.get(0))));
        output.add(new ArrayList<>(Arrays.asList(vertical.get(0))));


        if (RANDOM.nextInt(2) == 0) {
            output.add(new ArrayList<>(Arrays.asList(LEFT, UP)));
        } else {
            output.add(new ArrayList<>(Arrays.asList(RIGHT, DOWN)));
        }

        if (RANDOM.nextInt(2) == 0) {
            output.add(new ArrayList<>(Arrays.asList(LEFT, DOWN)));
        } else {
            output.add(new ArrayList<>(Arrays.asList(RIGHT, UP)));
        }
        Collections.shuffle(output);

        return output;
    }

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

    public static ArrayList<ArrayList<Direction>> getBasicIteration() {
        ArrayList<ArrayList<Direction>> output = new ArrayList<>();

        output.add(new ArrayList<>(Arrays.asList(UP)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT)));

        return output;
    }


    //unused
    private static ArrayList<Direction> getRndDirection() {

        int r = RANDOM.nextInt(2);
        ArrayList<Direction> output = new ArrayList<>();

        if (r==0) {
            int r2 = RANDOM.nextInt(2);
            if (r2==0) {
                output.add(RIGHT);
            } else {
                output.add(RIGHT);
            }

            r2 = RANDOM.nextInt(2);
            if (r2==0) {
                output.add(UP);
            } else {
                output.add(DOWN);
            }
        } else {
            r = RANDOM.nextInt(4);

            switch(r) {
                case 0:
                    output.add(UP);
                    break;

                case 1:
                    output.add(DOWN);
                    break;

                case 2:
                    output.add(RIGHT);
                    break;

                case 3:
                    output.add(RIGHT);
                    break;
            }
        }

        return output;
    }

    public static ArrayList<ArrayList<Direction>> getRandomIterationOLD() {
        ArrayList<ArrayList<Direction>> output = new ArrayList<>();

        for (Direction d : Direction.values()) {
            output.add(new ArrayList<>(Arrays.asList(d)));
        }

        output.add(new ArrayList<>(Arrays.asList(LEFT, UP)));
        output.add(new ArrayList<>(Arrays.asList(LEFT, DOWN)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT, UP)));
        output.add(new ArrayList<>(Arrays.asList(RIGHT, DOWN)));

        Collections.shuffle(output);

        return output;
    }
}
