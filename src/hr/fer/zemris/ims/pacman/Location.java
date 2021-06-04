package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static java.lang.Integer.compare;
import static java.lang.Math.sqrt;
import static java.util.Arrays.stream;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class Location implements Comparable<Location> {

    private final int x;
    private final int y;

    public double distanceTo(Location other) {
        int distanceX = other.getX() - x;
        int distanceY = other.getY() - y;

        return sqrt(distanceX * distanceX + distanceY * distanceY);
    }

    public Location move(Move move) {
        return new Location(x + move.getX(), y + move.getY());
    }

    public Location move(Move... moves) {
        return new Location(x + stream(moves).mapToInt(Move::getX).sum(), y + stream(moves).mapToInt(Move::getY).sum());
    }

    public Location add(Location location) {
        return new Location(x + location.getX(), y + location.getY());
    }

    public Location sub(Location location) {
        return new Location(x - location.getX(), y - location.getY());
    }

    @Override
    public int compareTo(Location o) {
        return x == o.x ? compare(y, o.y) : compare(x, o.x);
    }
}
