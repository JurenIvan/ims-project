package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hr.fer.zemris.ims.pacman.AIUtils.*;
import static java.util.stream.Collectors.toList;

public class GhostAI extends AgentAI {

    public static final double COOKIE_GREEDINESS = 0;
    private static final PowerUpStatus powerUpStatus = PowerUpStatus.getInstance();
    private static final Map<Integer, List<Move>> history = new HashMap<>();

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        initializeHistoryMap(myInfo, history);
        List<Move> niceMoves = moves.stream().map(Move::from).collect(toList());

        if (niceMoves.size() == 1) {
            return prepareReturn(myInfo, niceMoves.get(0), moves, "Default!", history);
        }

        var pacmanMoveIndex = findPacman(niceMoves, mySurroundings, myInfo);
        if (pacmanMoveIndex != -1) {    // naden pacman
            if (powerUpStatus.isPowerUpEnabled()) {
                var towardPacman = niceMoves.get(pacmanMoveIndex);
                var oppositeMove = towardPacman.opposite();
                if (niceMoves.contains(oppositeMove)) {
                    return prepareReturn(myInfo, oppositeMove, moves, "Run, opposite!", history);
                }
                niceMoves.remove(towardPacman);
                var picked = random(niceMoves);   //add random
                return prepareReturn(myInfo, picked, moves, "Run, wherever!", history);
            }
            var towardPacman = niceMoves.get(pacmanMoveIndex);
            return prepareReturn(myInfo, towardPacman, moves, "Chase!", history);
        }

        //nema pacmana
        removeLastFromHistory(niceMoves, myInfo, history);
        if (niceMoves.size() == 1) {
            return prepareReturn(myInfo, niceMoves.get(0), moves, "Default non back!", history);
        }

        int ghostMoveIndex = find(niceMoves, mySurroundings, "Ghost");
        if (ghostMoveIndex != -1) {    //vidis ghosta
            Move towardGhost = niceMoves.get(ghostMoveIndex);
            Move oppositeMove = towardGhost.opposite();
            if (niceMoves.contains(oppositeMove)) {
                return prepareReturn(myInfo, oppositeMove, moves, "Opposite to ghost!", history);
            }
            niceMoves.remove(towardGhost);
            var chosen = random(niceMoves);
            return prepareReturn(myInfo, chosen, moves, "Ghost anything!", history);
        }
        if (Math.random() < COOKIE_GREEDINESS) {
            int cookieMoveIndex = find(niceMoves, mySurroundings, "Point");
            if (cookieMoveIndex != -1) {
                return prepareReturn(myInfo, niceMoves.get(cookieMoveIndex), moves, "Cookie fever!", history);
            }
        }

        var pickedMove = random(niceMoves);
        return prepareReturn(myInfo, pickedMove, moves, "Last default!", history);
    }


    private int findPacman(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        Move move = null;

        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX() / 2; i++) {
            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                //find pacman
                ArrayList<WorldEntityInfo> elements = mySurroundings.getWorldInfoAt(i, j);
                HashMap<Integer, Object> metaHash = mySurroundings.getWorldMetadataAt(i, j);
                if (elements != null && metaHash != null) {
                    for (WorldEntityInfo el : elements) {
                        if (el.getIdentifier().compareToIgnoreCase("Pacman") == 0) {
                            int index = findClosest(moves, new Location(i, j));
                            metaHash.clear();
                            metaHash.put(myInfo.getID(), moves.get(index));
                            return index;
                        }
                    }
                    //Check if someone else found him
                    if (!metaHash.isEmpty()) {
                        for (Integer id : metaHash.keySet()) {
                            if (id != myInfo.getID()) {
                                move = (Move) metaHash.remove(id);
                                //printStatus(myInfo.getID()+": Found pacman trail left by ghost: "+id+"!");
                            }
                        }
                    }
                }
            }
        }
        if (move != null) {
            for (int i = 0; i < moves.size(); i++) {
                Move m = moves.get(i);
                if (m.equals(move)) {
                    return i;
                }
            }
        }
        return -1;
    }


    private int find(List<Move> moves, PacmanVisibleWorld mySurroundings, String lookFor) {
        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX() / 2; i++) {
            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                //find ghost
                ArrayList<WorldEntityInfo> elements = mySurroundings.getWorldInfoAt(i, j);
                HashMap<Integer, Object> metaHash = mySurroundings.getWorldMetadataAt(i, j);
                if (elements != null && metaHash != null) {
                    for (WorldEntityInfo el : elements) {
                        if (el.getIdentifier().compareToIgnoreCase(lookFor) == 0) {
                            return findClosest(moves, new Location(i, j));
                        }
                    }
                }
            }
        }
        return -1;
    }
}