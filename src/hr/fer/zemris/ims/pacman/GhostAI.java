package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.states.resolvers.StateGhostResolver;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;

public class GhostAI extends AgentAI {

    private static final StateGhostResolver stateResolver = new StateGhostResolver();

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo) {
        return stateResolver.resolve(myInfo.getID()).makeAMove(moves, mySurroundings, myInfo);
    }
}
