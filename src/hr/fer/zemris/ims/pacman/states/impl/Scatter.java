package hr.fer.zemris.ims.pacman.states.impl;

import com.jme3.math.Vector3f;
import hr.fer.zemris.ims.pacman.Location;
import hr.fer.zemris.ims.pacman.domain.Move;
import hr.fer.zemris.ims.pacman.states.AbstractState;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.List;

import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class Scatter extends AbstractState {

    @Override
    public int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        if (moves.size() == 1) return 0;

        int mapCorner = myInfo.getID() % 4;
        int targetX, targetY;

        if (mapCorner == 0) {
            targetX = -mySurroundings.getDimensionX() / 2;
            targetY = mySurroundings.getDimensionY() / 2;
        } else if (mapCorner == 1) {
            targetX = mySurroundings.getDimensionX() / 2;
            targetY = mySurroundings.getDimensionY() / 2;
        } else if (mapCorner == 2) {
            targetX = -mySurroundings.getDimensionX() / 2;
            targetY = -mySurroundings.getDimensionY() / 2;
        } else {
            targetX = mySurroundings.getDimensionX() / 2;
            targetY = -mySurroundings.getDimensionY() / 2;
        }

        Vector3f myPosition = myInfo.getPosition();
        return findClosest(moves, new Location(targetX, targetY), new Location((int) myPosition.x, (int) myPosition.y));
    }
}
