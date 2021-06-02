package hr.fer.zemris.ims.pacman.states;

import hr.fer.zemris.ims.pacman.Location;
import hr.fer.zemris.ims.pacman.domain.Move;

import java.util.List;

public abstract class AbstractState implements State {

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
