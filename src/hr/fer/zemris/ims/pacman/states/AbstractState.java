package hr.fer.zemris.ims.pacman.states;

import hr.fer.zemris.ims.pacman.Location;
import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.PacmanVisibleWorld;

import java.util.List;
import java.util.Map;

import static mmaracic.gameaiframework.WorldEntity.WorldEntityInfo;

public abstract class AbstractState implements State {

    public static final String PACMAN_CONSTANT = "Pacman";
    public static Location lastPacmanLocation = null;

    public static int findClosest(List<Move> moves, Location target, Location curr) {
        int index = 0;
        Move move = moves.get(0);
        double dist = curr.move(move).distanceTo(target);
        for (int i = 1; i < moves.size(); i++) {
            move = moves.get(i);
            double currDist = curr.move(move).distanceTo(target);
            if (currDist < dist) {
                dist = currDist;
                index = i;
            }
        }
        return index;
    }

    public static Location findPacman(PacmanVisibleWorld mySurroundings) {
        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX() / 2; i++) {
            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
                if (i == 0 && j == 0) continue;
                List<WorldEntityInfo> elements = mySurroundings.getWorldInfoAt(i, j);
                Map<Integer, Object> metaHash = mySurroundings.getWorldMetadataAt(i, j);
                if (elements != null && metaHash != null) {
                    for (WorldEntityInfo el : elements) {
                        if (el.getIdentifier().compareToIgnoreCase(PACMAN_CONSTANT) == 0) {
                            return new Location(i, j);
                        }
                    }
                }
            }
        }
        return null;
    }


//
//        int[] move = null;
//
//        for (int i = -mySurroundings.getDimensionX() / 2; i <= mySurroundings.getDimensionX() / 2; i++) {
//            for (int j = -mySurroundings.getDimensionY() / 2; j <= mySurroundings.getDimensionY() / 2; j++) {
//                if (i == 0 && j == 0) continue;
//
//                //find pacman
//                ArrayList<WorldEntity.WorldEntityInfo> elements = mySurroundings.getWorldInfoAt(i, j);
//                HashMap<Integer, Object> metaHash = mySurroundings.getWorldMetadataAt(i, j);
//                if (elements != null && metaHash != null) {
//                    for (WorldEntity.WorldEntityInfo el : elements) {
//                        if (el.getIdentifier().compareToIgnoreCase("Pacman") == 0) {
//                            int index = findClosest(moves, new int[]{i, j});
//                            metaHash.clear();
//                            metaHash.put(myInfo.getID(), moves.get(index));
//                            return index;
//                        }
//                    }
//                    //Check if someone else found him
//                    if (!metaHash.isEmpty()) {
//                        for (Integer id : metaHash.keySet()) {
//                            if (id != myInfo.getID()) {
//                                move = (int[]) metaHash.remove(id);
//                                //printStatus(myInfo.getID()+": Found pacman trail left by ghost: "+id+"!");
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //Go where metadata pointed
//        if (move != null) {
//            for (int i = 0; i < moves.size(); i++) {
//                int[] m = moves.get(i);
//                if (m[0] == move[0] && m[1] == move[1]) {
//                    return i;
//                }
//            }
//        }
//        //Go random
//        int choice = r.nextInt(moves.size());
//        return choice;

}
