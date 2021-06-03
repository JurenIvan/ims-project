package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.Location;
import hr.fer.zemris.ims.pacman.domain.Move;
import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static hr.fer.zemris.ims.pacman.domain.Move.*;
import static java.util.function.Function.identity;
import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class Chase extends AbstractState {

    private static final Map<Integer, Function<Location, Location>> TARGET_FUN_MAP = Map.of(
            0, identity(),
            1, target -> target.move(UP, UP, LEFT, LEFT),
            2, target -> target.move(LEFT, DOWN, DOWN),
            3, target -> target.move(UP, UP, RIGHT, RIGHT));

    @Override
    public int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        Location pacLocation = findPacman(mySurroundings);
        if (pacLocation == null) {
            return 0;
        }

        var myPos = new Location((int) myInfo.getPosition().x, (int) myInfo.getPosition().x);
        var target = TARGET_FUN_MAP.get(myInfo.getID() % 4).apply(pacLocation);

        return findClosest(moves, target, myPos);
    }
}
