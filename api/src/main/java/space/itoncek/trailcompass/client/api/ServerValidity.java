package space.itoncek.trailcompass.client.api;

public enum ServerValidity {
    NOT_FOUND("❔"),
    INCOMPATIBLE_VERSION("‼"),
    DEVELOPMENT_VERSION("🛠⚠"),
    OK("✅");

    public final String icon;

    ServerValidity(String icon) {
        this.icon = icon;
    }
}
