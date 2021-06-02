package hr.fer.zemris.ims.pacman.states.resolvers;

import hr.fer.zemris.ims.pacman.states.State;
import hr.fer.zemris.ims.pacman.states.impl.Chase;
import hr.fer.zemris.ims.pacman.states.impl.Frightened;
import hr.fer.zemris.ims.pacman.states.impl.Scatter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

public class StateGhostResolver {

    // needed to change states
    private static final LocalDateTime START = now();

    public static final State chaseState = new Chase();
    public static final State frightenedState = new Frightened();
    public static final State scatterState = new Scatter();

    public static final int FRIGHTENED_LENGTH = 10;
    public static final int TOTAL_CYCLE_TIME = 27;
    public static final int SCATTER_TIME = 7;
    private final Map<Integer, State> stateMap = new HashMap<>();
    private LocalDateTime frightenedStartTime = now().minusDays(1);

    public void setFrightenedTime() {
        this.frightenedStartTime = now();
    }

    public State resolve(int id) {
        if (!stateMap.containsKey(id)) {
            stateMap.put(id, scatterState);
        }

        if (frightenedStartTime.until(now(), SECONDS) < FRIGHTENED_LENGTH) {
            stateMap.put(id, frightenedState);
            return frightenedState;
        }

        if (START.until(now(), SECONDS) % TOTAL_CYCLE_TIME > SCATTER_TIME) {
            stateMap.put(id, chaseState);
            return chaseState;
        } else {
            stateMap.put(id, scatterState);
            return scatterState;
        }
    }
}
