import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");

class Solution {

    public final Comparator<Sheep>  X_POS_COMPARATOR    = Comparator.comparingInt(Sheep::getX);
    public final Comparator<Sheep>  Y_POS_COMPARATOR    = Comparator.comparingInt(Sheep::getY);
    private static double           MIN                 = Double.MAX_VALUE;
    private static int              MAX_POSSIBLE_SPAN   = Integer.MAX_VALUE;


    public int solution(int[] X, int[] Y) {

        final List<Sheep> sheepList = createSheepList(X, Y);
        sheepList.sort(X_POS_COMPARATOR);
        final double v = calculateShortestDistanceBetweenIn(sheepList, 0, sheepList.size());

        final int maxSpan = v == 0 ? 0 : MAX_POSSIBLE_SPAN;

        clearCaches();

        return maxSpan;
    }

    private double calculateShortestDistanceBetweenIn(final List<Sheep> sheepList, int from, int to) {

        final List<Sheep> sheepListToCheck = sheepList.subList(from, to);

        if (sheepListToCheck.size() <= 3) {
            return calculateShortestBruteForce(sheepListToCheck);
        }

        int middlePointX = (from + to) / 2;

        double dl = calculateShortestDistanceBetweenIn(sheepList, from, middlePointX);
        double dr = calculateShortestDistanceBetweenIn(sheepList, middlePointX, to);

        double d = Math.min(dl, dr);

        if (MAX_POSSIBLE_SPAN == 0) {
            return 0;
        }

        final List<Sheep> strip = new ArrayList<>();
        final int middleSheep_X = sheepList.get(middlePointX).getX();

        for (int i = 0; i < sheepListToCheck.size(); i++) {
            final Sheep sheep = sheepListToCheck.get(i);
            if (Math.abs(sheep.getX() - middleSheep_X) < d) {
                strip.add(sheep);
            }
        }

        final double min = Math.min(d, calculateShortestInStrip(strip, d));
        return min;
    }

    private double calculateShortestBruteForce(final List<Sheep> sheepList) {

        double shortest = Double.MAX_VALUE;

        for (int i = 0; i < sheepList.size(); i++) {
            for (int j = i + 1; j < sheepList.size(); j++) {

                final Sheep sheep1 = sheepList.get(i);
                final Sheep sheep2 = sheepList.get(j);

                double distance = calculateDistance(sheep1, sheep2);

                if (distance <= shortest) {
                    shortest = distance;
                    updateGlobalMinimum(sheep1, sheep2, shortest);
                }
            }
        }
        return shortest;
    }

    private double calculateShortestInStrip(final List<Sheep> strip, final double d) {

        double min = d;

        strip.sort(Y_POS_COMPARATOR);

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).getY() - strip.get(i).getY()) < d; j++) {
                final double distance = calculateDistance(strip.get(i), strip.get(j));

                updateGlobalMinimum(strip.get(i), strip.get(j), distance);

                if (distance <= min) {
                    min = distance;
                }
            }
        }

        return min;
    }

    private void updateGlobalMinimum(final Sheep sheep1, final Sheep sheep2, final double min) {

        final int delta_x = Math.abs(sheep1.getX() - sheep2.getX());
        final int delta_y = Math.abs(sheep1.getY() - sheep2.getY());
        final int maxSpan = delta_x > delta_y ? delta_x / 2 : delta_y / 2;

        if (maxSpan < MAX_POSSIBLE_SPAN) {
            MAX_POSSIBLE_SPAN = maxSpan;
        }
    }

    private double calculateDistance(final Sheep sheep1, final Sheep sheep2) {
        return calculateDistance(sheep1.getX(), sheep1.getY(), sheep2.getX(), sheep2.getY());
    }

    private double calculateDistance(final int x1, final int y1, final int x2, final int y2) {
        final double distance;
        if (x1 == x2) {
            distance = Math.abs(y1 - y2);
        } else if (y1 == y2) {
            distance = Math.abs(x1 - x2);
        } else {
            distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        }
        return distance;
    }

    private List<Sheep> createSheepList(int[] xs, int[] ys) {

        final List<Sheep> sheepList = new ArrayList<>();
        for (int i = 0; i < xs.length; i++) {
            final Sheep sheep = new Sheep(xs[i], ys[i]);
            sheepList.add(sheep);
        }
        return sheepList;
    }

    private void clearCaches() {
        MIN = Double.MAX_VALUE;
        MAX_POSSIBLE_SPAN = Integer.MAX_VALUE;
    }

    private class Sheep {

        private int x;
        private int y;

        public Sheep(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}