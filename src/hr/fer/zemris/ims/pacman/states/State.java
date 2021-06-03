package hr.fer.zemris.ims.pacman.states;

import hr.fer.zemris.ims.pacman.domain.Move;
import mmaracic.gameaiframework.PacmanVisibleWorld;
import mmaracic.gameaiframework.WorldEntity;

import java.util.List;

public interface State {

    int makeAMove(List<Move> moves, PacmanVisibleWorld mySurroundings, WorldEntity.WorldEntityInfo myInfo);
}
