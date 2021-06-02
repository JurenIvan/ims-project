package hr.fer.zemris.ims.pacman.states;

import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;

public interface State {

    int makeAMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo);
}
