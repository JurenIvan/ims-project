package hr.fer.zemris.ims.pacman.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Move {

    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int x;
    private final int y;

    public static Move from(int x, int y) {
        if (UP.x == x && UP.y == y) return UP;
        if (DOWN.x == x && DOWN.y == y) return DOWN;
        if (LEFT.x == x && LEFT.y == y) return LEFT;
        return RIGHT;
    }

    public static Move from(int[] move) {
        return from(move[0], move[1]);
    }
}
