package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.Location;
import hr.fer.zemris.ims.pacman.domain.Move;
import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static hr.fer.zemris.ims.pacman.domain.Move.*;
import static java.util.function.Function.identity;
import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class Chase extends AbstractState {

    private static final Map<Predicate<Integer>, Function<Location, Location>> TARGET_FUN_MAP = Map.of(
            i -> i % 4 == 0, identity(),
            i -> i % 4 == 1, location -> location.move(UP, UP, LEFT, LEFT),
            i -> i % 4 == 2, location -> location.move(LEFT, LEFT),
            i -> i % 4 == 3, location -> location.move(RIGHT, RIGHT));

    @Override
    public int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        Location pacLocation = findPacman(mySurroundings);
        return 0;
    }
}
