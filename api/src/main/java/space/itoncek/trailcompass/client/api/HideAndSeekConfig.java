package space.itoncek.trailcompass.client.api;

import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class HideAndSeekConfig implements Serializable {
    public String jwt_token;
    public String base_url;
    public String username;
    public String passwordHash;

    public HideAndSeekConfig(String jwt_token,
                             String base_url,
                             String username,
                             String passwordHash) {

        this.jwt_token = jwt_token;
        this.base_url = base_url;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public static HideAndSeekConfig generateEmptyConfig() {
        return new HideAndSeekConfig("","","","");
    }
}
