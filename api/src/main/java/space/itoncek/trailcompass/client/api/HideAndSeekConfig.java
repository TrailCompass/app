package space.itoncek.trailcompass.client.api;

import java.io.Serializable;

public final class HideAndSeekConfig implements Serializable {
    public String jwt_token;
    public String base_url;
    public String username;
    public String password_hash;

    public HideAndSeekConfig(String jwt_token,
                             String base_url,
                             String username,
                             String password_hash) {

        this.jwt_token = jwt_token;
        this.base_url = base_url;
        this.username = username;
        this.password_hash = password_hash;
    }

    public static HideAndSeekConfig generateEmptyConfig() {
        return new HideAndSeekConfig("","","","");
    }
}
