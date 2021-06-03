package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.*;

import static hr.fer.zemris.ims.pacman.AIUtils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class PacmanAI extends AgentAI {

    public static final int TARGET_DURATION_TIMEOUT = 10;
    private static final PowerUpStatus powerUpStatus = PowerUpStatus.getInstance();
    private static final Map<Integer, List<Move>> history = new HashMap<>();
    private final HashSet<Location> points = new HashSet<>();
    private final HashSet<Location> powerUps = new HashSet<>();
    private final HashSet<Location> ghosts = new HashSet<>();
    private final Random r = new Random();
    private Location myLocation = new Location(0, 0);
    private Location targetLocation = myLocation;
    private int targetDuration = 0;
    private boolean targetPowerUp;

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        targetDuration++;
        initializeHistoryMap(myInfo, history);
        List<Move> niceMoves = moves.stream().map(Move::from).collect(toList());
        powerUpStatus.setEnabled(myInfo.hasProperty(PacmanAgent.powerupPropertyName));
        ghosts.clear();

        Location myAbsoluteLocation = new Location((int) myInfo.getPosition().getX(), (int) myInfo.getPosition().getY());
        printStatus("Location x: " + myAbsoluteLocation);

        if (niceMoves.size() == 1) {
            return prepareReturn(myInfo, niceMoves.get(0), moves, "Default!", history);
        }

        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX(); i++) {
            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
                if (i == 0 && j == 0) continue;
                Location tempLocation = new Location(i, j);
                List<WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
                if (neighPosInfos != null) {
                    for (WorldEntityInfo info : neighPosInfos) {
                        if (info.getIdentifier().compareToIgnoreCase("Point") == 0) {
                            points.add(myAbsoluteLocation.add(tempLocation));
                        } else if (info.getIdentifier().compareToIgnoreCase("Powerup") == 0) {
                            powerUps.add(myAbsoluteLocation.add(tempLocation));
                        } else if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
                            ghosts.add(myAbsoluteLocation.add(tempLocation));
                        }
                    }
                }
            }
        }

        if (!ghosts.isEmpty()) {
            Location target = ghosts.stream().min(comparing(myAbsoluteLocation::distanceTo)).orElseThrow();
            int index = findClosest(niceMoves, target);
            if (powerUpStatus.isPowerUpEnabled()) {
                return prepareReturn(myInfo, niceMoves.get(index), moves, "Chase", history);
            }
            niceMoves.remove(ghostIndex);
            if (niceMoves.size() == 1) {
                return prepareReturn(myInfo, niceMoves.get(0), moves, "Run no option", history);
            }

            if(!powerUps.isEmpty()){

            }
        }


        return eat(false, moves, niceMoves, mySurroundings, myInfo);
    }
}
