package hr.fer.zemris.ims.pacman.states.impl;

import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.ArrayList;
import java.util.Random;

import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class Frightened extends AbstractState {

    private final Random random = new Random();

    @Override
    public int makeAMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {

        return random.nextInt(moves.size());
    }
}
