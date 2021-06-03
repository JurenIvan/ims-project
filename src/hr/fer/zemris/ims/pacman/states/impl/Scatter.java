package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.domain.Move;
import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.List;

import static hr.fer.zemris.ims.pacman.domain.Move.*;
import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class Scatter extends AbstractState {

    @Override
    public int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        if (moves.size() == 1) return 0;

        int mapCorner = myInfo.getID() % 4;

        if (mapCorner == 0) {
            return findMove(moves, LEFT, UP, DOWN, RIGHT);
        } else if (mapCorner == 1) {
            return findMove(moves, RIGHT, UP, DOWN, LEFT);
        } else if (mapCorner == 2) {
            return findMove(moves, LEFT, DOWN, UP, RIGHT);
        } else {
            return findMove(moves, RIGHT, DOWN, UP, LEFT);
        }

    }

    private int findMove(List<Move> moves, Move first, Move second, Move third, Move forth) {
        if (moves.contains(first)) {
            return moves.indexOf(first);
        } else if (moves.contains(second)) {
            return moves.indexOf(second);
        } else if (moves.contains(third)) {
            return moves.indexOf(third);
        } else {
            return moves.indexOf(forth);
        }
    }
}
