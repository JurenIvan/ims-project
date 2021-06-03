package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.AgentAI;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class GhostAI extends AgentAI {

    private int findClosest(List<Move> moves, Location location) {
        int index = 0;
        Move move = moves.get(0);
        float dist = Math.abs(location.getX() - move.getX()) + Math.abs(location.getY() - move.getY());
        for (int i = 1; i < moves.size(); i++) {
            move = moves.get(i);
            float currDist = Math.abs(location.getX() - move.getX()) + Math.abs(location.getY() - move.getY());
            if (currDist < dist) {
                dist = currDist;
                index = i;
            }
        }
        return index;
    }

    @Override
    public int decideMove(ArrayList<int[]> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
        if (!history.containsKey(myInfo.getID())) {
            List<Move> list = new ArrayList<>();
            list.add(Move.LEFT);
            history.put(myInfo.getID(), list);
        }

        if (moves.size() == 1) {
            printStatus("default return 0");
            return 0;
        }
        List<Move> niceMoves = moves.stream().map(Move::from).collect(toList());

        var pacmanMoveIndex = findPacman(niceMoves, mySurroundings, myInfo);
        if (pacmanMoveIndex != -1) {    // naden pacman
            if (powerUpStatus.isPowerUpEnabled()) {
                var oppositeMove = niceMoves.get(pacmanMoveIndex).opposite();
                if (niceMoves.contains(oppositeMove)) {
                    history.get(myInfo.getID()).add(oppositeMove);
                    printStatus("55 " + oppositeMove);
                    return niceMoves.indexOf(oppositeMove);
                } else {
                    //dont go back on prev location
                    var list = history.get(myInfo.getID());
                    niceMoves.remove(list.get(list.size() - 1));
                    var pickedMove = niceMoves.get((int) (Math.random() * niceMoves.size()));
                    history.get(myInfo.getID()).add(pickedMove);
                    printStatus("63 " + pickedMove);

                    return moves.indexOf(pickedMove.toArray());
                }
            }
        } else {
            //nadi duhove
            int ghostMoveIndex = findGhost(niceMoves, mySurroundings, myInfo);
            if (ghostMoveIndex != -1) {    //vidis ghosta
                if (powerUpStatus.isPowerUpEnabled()) { //ako ima powerup odi u smjeru duha
                    Move bestMove = niceMoves.get(ghostMoveIndex);
                    history.get(myInfo.getID()).add(bestMove);
                    printStatus("75 "+ bestMove);
                    return ghostMoveIndex;
                } else {
                    Move oppositeMove = niceMoves.get(ghostMoveIndex).opposite(); //ako nema powerup odi u suprotnom duha
                    if (niceMoves.contains(oppositeMove)) {
                        history.get(myInfo.getID()).add(oppositeMove);
                        printStatus("81 " + oppositeMove);
                        return niceMoves.indexOf(oppositeMove);
                    }//else fallthrough
                }
            }
        }
        // ne vidis ghosta
        //dont go back on prev location
        var list = history.get(myInfo.getID());
        niceMoves.remove(list.get(list.size() - 1));
        var pickedMove = niceMoves.get((int) (Math.random() * niceMoves.size()));
        history.get(myInfo.getID()).add(pickedMove);
        printStatus("laast " + moves.indexOf(pickedMove.toArray()) + " " + pickedMove + " " + moves.stream().map(Arrays::toString).collect(joining(" ")));

        for (int i = 0; i < moves.size(); i++) {
            int[] m = moves.get(i);
            if (m[0] == pickedMove.getX() && m[1] == pickedMove.getY()) {
                return i;
            }
        }
        return 0;
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
                                move = Move.from((int[]) metaHash.remove(id));
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


    private int findGhost(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntityInfo myInfo) {
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
                        if (el.getIdentifier().compareToIgnoreCase("Ghost") == 0) {
                            return findClosest(moves, new Location(i, j));
                        }
                    }
                }

            }

        }
        return -1;
    }
}
