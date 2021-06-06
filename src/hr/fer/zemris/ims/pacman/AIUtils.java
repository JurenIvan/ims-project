package hr.fer.zemris.ims.pacman;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.WorldEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIUtils {

    public static int findClosest(List<Move> moves, Location location) {
        int index = 0;
        Move move = moves.get(0);
        double dist = (location.getX() - move.getX()) * (location.getX() - move.getX()) + (location.getY() - move.getY()) * (location.getY() - move.getY());
        for (int i = 1; i < moves.size(); i++) {
            move = moves.get(i);
            double currDist = (location.getX() - move.getX()) * (location.getX() - move.getX()) + (location.getY() - move.getY()) * (location.getY() - move.getY());
            if (Math.abs(currDist - dist) < 0.01 && Math.random() < 0.5) {
                dist = currDist;
                index = i;
            } else if (currDist < dist) {
                dist = currDist;
                index = i;
            }
        }
        return index;
    }

    public static int prepareReturn(WorldEntity.WorldEntityInfo myInfo, Move theMove, ArrayList<int[]> moves, String message, Map<Integer, List<Move>> history) {
        // printStatus("ID:" + myInfo.getID() + " " + message + " " + theMove);
        history.get(myInfo.getID()).add(theMove);
        return findIndex(moves, theMove);
    }

    public static Move random(List<Move> niceMoves) {
        return niceMoves.get((int) (Math.random() * 1007) % niceMoves.size());
    }

    public static void removeLastFromHistory(List<Move> niceMoves, WorldEntity.WorldEntityInfo myInfo, Map<Integer, List<Move>> history) {
        var list = history.get(myInfo.getID());
        if (list.size() > 0) {
            niceMoves.remove(list.get(list.size() - 1).opposite());
        }
    }

    public static void initializeHistoryMap(WorldEntity.WorldEntityInfo myInfo, Map<Integer, List<Move>> history) {
        if (!history.containsKey(myInfo.getID())) {
            List<Move> list = new ArrayList<>();
            history.put(myInfo.getID(), list);
        }
    }

    public static int findIndex(ArrayList<int[]> moves, Move pickedMove) {
        for (int i = 0; i < moves.size(); i++) {
            int[] m = moves.get(i);
            if (m[0] == pickedMove.getX() && m[1] == pickedMove.getY()) {
                return i;
            }
        }
        return -1;
    }
}
