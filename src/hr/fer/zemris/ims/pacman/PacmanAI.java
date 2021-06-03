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

        myLocation = new Location((int) myInfo.getPosition().getX(), (int) myInfo.getPosition().getY());
        printStatus("Location x: " + myLocation);

        if (niceMoves.size() == 1) {
            return prepareReturn(myInfo, niceMoves.get(0), moves, "Default!", history);
        }

        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX() / 2; i++) {
            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
                if (i == 0 && j == 0) continue;
                Location tempLocation = new Location(i, j);
                List<WorldEntityInfo> neighPosInfos = mySurroundings.getWorldInfoAt(i, j);
                if (neighPosInfos != null) {
                    for (WorldEntityInfo info : neighPosInfos) {
                        if (info.getIdentifier().compareToIgnoreCase("Point") == 0) {
                            points.add(myLocation.add(tempLocation));
                        } else if (info.getIdentifier().compareToIgnoreCase("Powerup") == 0) {
                            powerUps.add(myLocation.add(tempLocation));
                        } else if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
                            ghosts.add(myLocation.add(tempLocation));
                        }
                    }
                }
            }
        }

        if (!ghosts.isEmpty()) {
            Location ghostTarget = ghosts.stream().min(comparing(myLocation::distanceTo)).orElseThrow();
            int ghostIndex = findClosest(niceMoves, ghostTarget);
            if (powerUpStatus.isPowerUpEnabled()) {
                return prepareReturn(myInfo, niceMoves.get(ghostIndex), moves, "Chase", history);
            }
            niceMoves.remove(ghostIndex);
            if (niceMoves.size() == 1) {
                return prepareReturn(myInfo, niceMoves.get(0), moves, "Run no option", history);
            }

            if (!powerUps.isEmpty()) {
                if (!targetPowerUp) {
                    targetDuration = 0;
                    targetPowerUp = true;
                    Location powerUpTarget = powerUps.stream().min(comparing(myLocation::distanceTo)).orElseThrow();
                    int powerUpIndex = findClosest(niceMoves, powerUpTarget);
                    return prepareReturn(myInfo, niceMoves.get(powerUpIndex), moves, "Chase powerUp", history);
                }
                return eat(true, moves, niceMoves, mySurroundings, myInfo);
            }
        }


        return eat(false, moves, niceMoves, mySurroundings, myInfo);
    }

    private int eat(boolean chased, ArrayList<int[]> moves, List<Move> niceMoves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        if ((!chased && targetPowerUp) || myLocation.equals(targetLocation)) {
            Location target = points.stream().min(comparing(myLocation::distanceTo)).orElseThrow();
            int index = findClosest(niceMoves, target);
            targetLocation = target;
            targetDuration = 0;
            targetPowerUp = false;
            return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest point", history);
        }

        if (targetDuration >= TARGET_DURATION_TIMEOUT) {
            if (chased) {
                Optional<Location> target = powerUps.stream()
                        .filter(e -> !targetLocation.equals(e))
                        .min(comparing(myLocation::distanceTo));
                if (target.isPresent()) {
                    targetLocation = target.get();
                    targetDuration = 0;
                    targetPowerUp = true;
                    var index = findClosest(niceMoves, targetLocation);
                    return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest powerUp", history);
                }
                target = points.stream().min(comparing(myLocation::distanceTo));
                if (target.isPresent()) {
                    targetLocation = target.get();
                    targetDuration = 0;
                    targetPowerUp = false;
                    var index = findClosest(niceMoves, targetLocation);
                    return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest point", history);
                }
            }
        }
        Move move = random(niceMoves);
        return prepareReturn(myInfo, move, moves, "Random", history);
    }
}
