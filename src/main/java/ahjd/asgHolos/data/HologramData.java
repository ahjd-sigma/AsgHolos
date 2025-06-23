package ahjd.asgHolos.data;

import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;

import java.util.UUID;

public record HologramData(
        Location location,
        String text,
        boolean persistent,
        boolean shadowed,
        boolean seeThrough,
        Billboard billboard,
        UUID entityUUID
) {}