package hr.fer.zemris.ims.pacman;

import com.jme3.math.Vector3f;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static java.lang.Float.MAX_VALUE;

public class PacmanAI extends AgentAI {

    private static final PowerUpStatus powerUpStatus = PowerUpStatus.getInstance();

    private final HashSet<Location> points = new HashSet<>();
    private final Random r = new Random();
    private Location myLocation = new Location(0, 0);
    private Location targetLocation = myLocation;
    private int targetDuration = 0;

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo) {
        int radiusX = mySurroundings.getDimensionX() / 2;
        int radiusY = mySurroundings.getDimensionY() / 2;

        powerUpStatus.setEnabled(myInfo.hasProperty(PacmanAgent.powerupPropertyName));

        Vector3f pos = myInfo.getPosition();
        printStatus("Location x: " + pos.x + " y: " + pos.y);

        double ghostDistance = Double.MAX_VALUE;
        Location ghostLocation = null;
        for (int i = -radiusX; i <= radiusX; i++) {
            for (int j = -radiusY; j <= radiusY; j++) {
                if (i == 0 && j == 0) continue;
                Location tempLocation = new Location(myLocation.getX() + i, myLocation.getY() + j);
                ArrayList<WorldEntity.WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
                if (neighPosInfos != null) {
                    for (WorldEntity.WorldEntityInfo info : neighPosInfos) {
                        if (info.getIdentifier().compareToIgnoreCase("Pacman") == 0) {
                            //Ignore myself
                        } else if (info.getIdentifier().compareToIgnoreCase("Wall") == 0) {
                            //Its a wall, who cares!
                        } else if (info.getIdentifier().compareToIgnoreCase("Point") == 0 ||
                                info.getIdentifier().compareToIgnoreCase("Powerup") == 0) {
                            //Remember where it is!
                            double currPointDistance = myLocation.distanceTo(tempLocation);
                            points.add(tempLocation);
                        } else if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
                            //Remember him!
                            double currGhostDistance = myLocation.distanceTo(tempLocation);
                            if (currGhostDistance < ghostDistance) {
                                ghostDistance = currGhostDistance;
                                ghostLocation = tempLocation;
                            }
                        } else {
                            printStatus("I dont know what " + info.getIdentifier() + " is!");
                        }
                    }
                }
            }
        }

        //move toward the point
        //pick next if arrived
//        double targetDistance = MAX_VALUE;
        if (targetLocation == myLocation) {
            targetLocation = points.iterator().next();
//            targetDistance = myLocation.distanceTo(targetLocation);
            targetDuration = 0;
        }

        targetDuration++;

        //sticking with target too long -> got stuck
        //dont get stuck
        if (targetDuration > 10) {
            ArrayList<Location> pointList = new ArrayList<>(points);
            int choice = r.nextInt(pointList.size());

            targetLocation = pointList.get(choice);
//            targetDistance = myLocation.distanceTo(targetLocation);
            targetDuration = 0;
        }

        //select move
        double currMinPDistance = MAX_VALUE;
        Location nextLocation = myLocation;
        int moveIndex = 0;

        for (int i = moves.size() - 1; i >= 0; i--) {
            int[] move = moves.get(i);
            Location moveLocation = new Location(myLocation.getX() + move[0], myLocation.getY() + move[1]);
            double newPDistance = moveLocation.distanceTo(targetLocation);
            double newGDistance = (ghostDistance < MAX_VALUE) ? moveLocation.distanceTo(ghostLocation) : MAX_VALUE;
            if (newPDistance <= currMinPDistance && newGDistance > 1) {
                //that way
                currMinPDistance = newPDistance;
                nextLocation = moveLocation;
                moveIndex = i;
            }
        }

        points.remove(myLocation);
        myLocation = nextLocation;
        points.remove(myLocation);

        return moveIndex;
    }
}
