package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.domain.Move;
import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;
import java.util.List;

public class Chase extends AbstractState {

    @Override
    public int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo) {
        return 0;
    }
}
