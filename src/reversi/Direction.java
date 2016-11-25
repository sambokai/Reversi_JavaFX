package reversi;

/**
 * Created by sambokai on 25.11.16.
 */
final class Direction {

    //delta x und delta y
    private int dx, dy;

    public Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
