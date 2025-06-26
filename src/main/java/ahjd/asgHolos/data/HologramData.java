package ahjd.asgHolos.data;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;

public record HologramData(Location location, String text, String name, boolean persistent, boolean shadowed, boolean seeThrough, Billboard billboard, float yaw, float pitch, float scale, TextAlignment textAlignment, int textOpacity, int backgroundColor, int viewDistance, UUID entityUUID) {
    public HologramData(Location location, String text, String name, boolean persistent, boolean shadowed, boolean seeThrough, Billboard billboard, float yaw, float pitch, float scale, TextAlignment textAlignment, int textOpacity, int backgroundColor, int viewDistance, UUID entityUUID) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        } else if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        } else {
            this.location = location;
            this.text = text;
            this.name = name;
            this.persistent = persistent;
            this.shadowed = shadowed;
            this.seeThrough = seeThrough;
            this.billboard = billboard;
            this.yaw = yaw;
            this.pitch = pitch;
            this.scale = scale;
            this.textAlignment = textAlignment;
            this.textOpacity = textOpacity;
            this.backgroundColor = backgroundColor;
            this.viewDistance = viewDistance;
            this.entityUUID = entityUUID;
        }
    }

    public int getBackgroundOpacity() {
        return this.backgroundColor >> 24 & 255;
    }

    public String displayName() {
        return this.name != null ? this.name : this.text;
    }

    public Location location() {
        return this.location;
    }

    public String text() {
        return this.text;
    }

    public String name() {
        return this.name;
    }

    public boolean persistent() {
        return this.persistent;
    }

    public boolean shadowed() {
        return this.shadowed;
    }

    public boolean seeThrough() {
        return this.seeThrough;
    }

    public Billboard billboard() {
        return this.billboard;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public float scale() {
        return this.scale;
    }

    public TextAlignment textAlignment() {
        return this.textAlignment;
    }

    public int textOpacity() {
        return this.textOpacity;
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public int viewDistance() {
        return this.viewDistance;
    }

    public UUID entityUUID() {
        return this.entityUUID;
    }
}