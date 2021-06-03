package hr.fer.zemris.ims.pacman;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class PowerUpStatus {

    private static final PowerUpStatus instance = new PowerUpStatus();

    public static PowerUpStatus getInstance() {
        return instance;
    }

    private boolean enabled = false;

    public boolean isPowerUpEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
