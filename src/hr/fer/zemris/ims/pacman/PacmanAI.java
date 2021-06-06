package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanAgent;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.*;

import static hr.fer.zemris.ims.pacman.AIUtils.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public class PacmanAI extends AgentAI {

    public static final int TARGET_DURATION_TIMEOUT = 8;
    private static final PowerUpStatus powerUpStatus = PowerUpStatus.getInstance();
    private static final Map<Integer, List<Move>> history = new HashMap<>();
    private final HashSet<Location> points = new HashSet<>();
    private final HashSet<Location> powerUps = new HashSet<>(List.of(new Location(-8,-8),new Location(-8,8),new Location(8,-8),new Location(8,8)));
    private final HashSet<Location> ghosts = new HashSet<>();
    private final Random r = new Random();
    private Location myLocation = new Location(0, 0);
    private Location targetLocation = null;
    private int targetDuration = 0;
    private boolean targetPowerUp;

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        printStatus("STEPPPPPPP");

        targetDuration++;
        initializeHistoryMap(myInfo, history);
        List<Move> niceMoves = moves.stream().map(Move::from).collect(toList());
        powerUpStatus.setEnabled(myInfo.hasProperty(PacmanAgent.powerupPropertyName));
        ghosts.clear();

        myLocation = new Location((int) myInfo.getPosition().getX(), (int) myInfo.getPosition().getY());
        printStatus("MY LOCATION" + myLocation);
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
                        points.remove(myLocation.add(tempLocation));
                        powerUps.remove(myLocation.add(tempLocation));

                        if (info.getIdentifier().compareToIgnoreCase("Point") == 0)
                            points.add(myLocation.add(tempLocation));
                        if (info.getIdentifier().compareToIgnoreCase("Powerup") == 0)
                            powerUps.add(myLocation.add(tempLocation));
                        if (info.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
                            ghosts.add(myLocation.add(tempLocation));
                        }
                    }
                }
            }
        }
        points.remove(myLocation);
        powerUps.remove(myLocation);

        if (!ghosts.isEmpty()) {
            Location ghostTarget = ghosts.stream().min(comparing(myLocation::distanceTo)).orElseThrow();

            int ghostIndex = findClosest(niceMoves, ghostTarget.sub(myLocation));
            var closest = niceMoves.get(ghostIndex);
            if (powerUpStatus.isPowerUpEnabled() && Math.random() > 0.5) {
                return prepareReturn(myInfo, closest, moves, "Chase", history);
            }
            niceMoves.remove(closest);

            if (niceMoves.size() == 1) {
                return prepareReturn(myInfo, niceMoves.get(0), moves, "Run no option", history);
            }

            //todo remove 2nd worst if prev bad
            int ghostIndexNew = findClosest(niceMoves, ghostTarget.sub(myLocation));
            var closestNew = niceMoves.get(ghostIndexNew);
            if (myLocation.move(closest).distanceTo(ghostTarget) == myLocation.move(closestNew).distanceTo(ghostTarget)) {
                printStatus("new condition");
                niceMoves.remove(closestNew);
                if (niceMoves.size() == 1) {
                    return prepareReturn(myInfo, niceMoves.get(0), moves, "removed 2nd worst and had to do this", history);
                }
            }

            if (!powerUps.isEmpty()) {
                if (!targetPowerUp) {
                    targetDuration = 0;
                    targetPowerUp = true;
                    targetLocation = powerUps.stream().min(comparing(myLocation::distanceTo)).orElseThrow();
                    int powerUpIndex = findClosest(niceMoves, targetLocation.sub(myLocation));
                    return prepareReturn(myInfo, niceMoves.get(powerUpIndex), moves, "Chase powerUp", history);
                }
                return eat(true, moves, niceMoves, mySurroundings, myInfo);
            }
            return eat(true, moves, niceMoves, mySurroundings, myInfo);
        }
        return eat(false, moves, niceMoves, mySurroundings, myInfo);
    }

    private int eat(boolean chased, ArrayList<int[]> moves, List<Move> niceMoves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        if (!chased && !points.isEmpty()) {
            niceMoves = niceMoves.stream().filter(e -> !powerUps.contains(myLocation.move(e))).collect(toList());

            if (niceMoves.size() == 1) {
                return prepareReturn(myInfo, niceMoves.get(0), moves, "DO NOT EAT SUPER-COOKIE!", history);
            }
        }

        if ((!chased && targetPowerUp) || targetLocation == null || myLocation.equals(targetLocation)) {
//            printStatus((!chased && targetPowerUp) + " " + (targetLocation == null) + " " + myLocation.equals(targetLocation));
            Optional<Location> target = points.stream().min(comparing(myLocation::distanceTo));
            if (target.isPresent()) {
//                printStatus("CLOSEST" + target.get());
                int index = findClosest(niceMoves, target.get().sub(myLocation));
                targetLocation = target.get();
                targetDuration = 0;
                targetPowerUp = false;
                return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest point", history);
            }
            target = powerUps.stream().min(comparing(myLocation::distanceTo));
            if (target.isPresent()) {
//                printStatus("CLOSEST" + target.get());
                int index = findClosest(niceMoves, target.get().sub(myLocation));
                targetLocation = target.get();
                targetDuration = 0;
                targetPowerUp = false;
                return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest powerup", history);
            }
            targetLocation = null;
            targetPowerUp = false;
            targetDuration = 0;
            if (niceMoves.size() > 1) {
                removeLastFromHistory(niceMoves, myInfo, history);
            }
            Move move = random(niceMoves);
            return prepareReturn(myInfo, move, moves, "Random " + chased, history);
        }

        if (targetDuration >= TARGET_DURATION_TIMEOUT && !targetPowerUp) {
            if (!targetPowerUp) {
                points.remove(targetLocation);  //todo debateable
            }
            if (chased) {
                Optional<Location> target = powerUps.stream()
                        .filter(e -> !targetLocation.equals(e))
                        .min(comparing(myLocation::distanceTo));
                if (target.isPresent()) {
                    targetLocation = target.get();
                    targetDuration = 0;
                    targetPowerUp = true;
                    var index = findClosest(niceMoves, targetLocation.sub(myLocation));
                    return prepareReturn(myInfo, niceMoves.get(index), moves, "Yummy new closest powerUp", history);
                }
                return findAnotherPoint(niceMoves, moves, myInfo, "Chased but no powerup, gonna eat cookie :(");
            }
            return findAnotherPoint(niceMoves, moves, myInfo, "Gonna eat cookie :D");
        }

        //is something better
//        if (!targetPowerUp) {
//            Optional<Location> target = points.stream().min(comparing(myLocation::distanceTo));
//            if (target.isPresent() && !targetLocation.equals(target.get())) {
//                targetLocation = target.get();
//                targetDuration = 0;
//                targetPowerUp = false;
//                var index = findClosest(niceMoves, targetLocation.sub(myLocation));
//                return prepareReturn(myInfo, niceMoves.get(index), moves, "New masterplan cookie!" + targetLocation, history);
//            }
//        }
//        if (targetPowerUp) {
//            Optional<Location> target = powerUps.stream().min(comparing(myLocation::distanceTo));
//            if (target.isPresent() && !targetLocation.equals(target.get())) {
//                targetLocation = target.get();
//                targetDuration = 0;
//                targetPowerUp = false;
//                var index = findClosest(niceMoves, targetLocation.sub(myLocation));
//                return prepareReturn(myInfo, niceMoves.get(index), moves, "New masterplan super-cookie!" + targetLocation, history);
//            }
//        }

        var index = findClosest(niceMoves, targetLocation.sub(myLocation));
        return prepareReturn(myInfo, niceMoves.get(index), moves, "Execute the master plan! Target: " + targetLocation, history);
    }

    public int prepareReturn(WorldEntity.WorldEntityInfo myInfo, Move theMove, ArrayList<int[]> moves, String message, Map<Integer, List<Move>> history) {
        printStatus("ID:" + myInfo.getID() + " " + message + " " + theMove);
        history.get(myInfo.getID()).add(theMove);
        return findIndex(moves, theMove);
    }

    private int findAnotherPoint(List<Move> niceMoves, ArrayList<int[]> moves, WorldEntityInfo myInfo, String message) {
        Optional<Location> target = points.stream()
                .filter(e -> !targetLocation.equals(e))
                .min(comparing(myLocation::distanceTo));
        if (target.isPresent()) {
            targetLocation = target.get();
            targetDuration = 0;
            targetPowerUp = false;
            var index = findClosest(niceMoves, targetLocation.sub(myLocation));
            return prepareReturn(myInfo, niceMoves.get(index), moves, message, history);
        }

        targetLocation = null;
        targetDuration = 0;
        targetPowerUp = false;
        if (niceMoves.size() > 1) {
            removeLastFromHistory(niceMoves, myInfo, history);
        }
        Move move = random(niceMoves);
        return prepareReturn(myInfo, move, moves, "Random chased ", history);
    }
}
