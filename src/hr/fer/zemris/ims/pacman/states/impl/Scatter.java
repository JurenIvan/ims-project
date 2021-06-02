package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;

public class Scatter extends AbstractState {

    @Override
    public int makeAMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo) {
        if (moves.size() == 1) return 0;

        int mapCorner = myInfo.getID() % 4;


    }
}
