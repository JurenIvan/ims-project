package hr.fer.zemris.ims.pacman;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static java.lang.Math.sqrt;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Location {

    private final int x;
    private final int y;

    public double distanceTo(Location other) {
        int distanceX = other.getX() - x;
        int distanceY = other.getY() - y;

        return sqrt(distanceX * distanceX + distanceY + distanceY);
    }
}
