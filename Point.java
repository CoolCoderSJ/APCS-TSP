package apcs;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(other.getX() - x, 2) + Math.pow(other.getY() - y, 2));
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static void main(String[] args) {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(-1, 6);
        System.out.println(p1.distance(p2));
        System.out.println(p1);
        System.out.println(p2.getX());
        System.out.println(p1.getY());
    }
}
