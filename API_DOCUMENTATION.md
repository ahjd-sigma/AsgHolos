# AsgHolos API Documentation

The AsgHolos plugin provides a comprehensive API for external plugins to interact with the hologram system. This API allows you to create, delete, and manage holograms programmatically while respecting the plugin's limits and configurations.

## Getting Started

### 1. Add AsgHolos as a Dependency

In your `plugin.yml`, add AsgHolos as a dependency:

```yaml
depend: [AsgHolos]
# or as a soft dependency:
soft-depend: [AsgHolos]
```

### 2. Get the API Instance

```java
import ahjd.asgHolos.AsgHolos;
import ahjd.asgHolos.api.AsgHolosAPI;

public class YourPlugin extends JavaPlugin {
    private AsgHolosAPI asgHolosAPI;
    
    @Override
    public void onEnable() {
        // Get the AsgHolos plugin instance
        AsgHolos asgHolosPlugin = (AsgHolos) Bukkit.getPluginManager().getPlugin("AsgHolos");
        
        if (asgHolosPlugin != null) {
            // Get the API instance
            this.asgHolosAPI = asgHolosPlugin.getAPI();
        }
    }
}
```

## API Methods

### Hologram Creation

#### `createHologram(HologramData data)`
Creates a hologram with the specified data.

```java
HologramData hologramData = new HologramData(
    location,                    // Location
    "Hello World!",             // Text
    "My Hologram",              // Name (optional)
    false,                       // Persistent (false = temporary)
    true,                        // Shadowed
    false,                       // See-through
    Display.Billboard.CENTER,    // Billboard type
    0.0f,                        // Yaw
    0.0f,                        // Pitch
    1.0f,                        // Scale
    Display.TextDisplay.TextAlignment.CENTER, // Text alignment
    255,                         // Text opacity (0-255)
    0,                           // Background color (ARGB)
    50.0f,                       // View distance
    null                         // UUID (auto-generated)
);

HologramData created = asgHolosAPI.createHologram(hologramData);
```

#### `createHologram(HologramData data, Player creator)`
Creates a hologram with a specific creator (for event tracking).

```java
HologramData created = asgHolosAPI.createHologram(hologramData, player);
```

### Hologram Deletion

#### `deleteHologram(HologramData data)`
Deletes the specified hologram.

```java
boolean deleted = asgHolosAPI.deleteHologram(hologramData);
```

#### `deleteHologram(HologramData data, Player deleter)`
Deletes a hologram with a specific deleter (for event tracking).

```java
boolean deleted = asgHolosAPI.deleteHologram(hologramData, player);
```

### Hologram Retrieval

#### `getAllHolograms()`
Returns a list of all holograms (both temporary and persistent).

```java
List<HologramData> allHolograms = asgHolosAPI.getAllHolograms();
```

#### `getTempHolograms()`
Returns a list of all temporary holograms.

```java
List<HologramData> tempHolograms = asgHolosAPI.getTempHolograms();
```

#### `getPersistentHolograms()`
Returns a list of all persistent holograms.

```java
List<HologramData> persistentHolograms = asgHolosAPI.getPersistentHolograms();
```

#### `getHologramByUUID(UUID uuid)`
Retrieve a specific hologram by its UUID.

```java
HologramData hologram = asgHolosAPI.getHologramByUUID(uuid);
```

### Hologram Counts and Limits

#### Count Methods
```java
int totalCount = asgHolosAPI.getTotalHologramCount();
int tempCount = asgHolosAPI.getTempHologramCount();
int persistentCount = asgHolosAPI.getPersistentHologramCount();
```

#### Limit Methods
```java
Integer maxTemp = asgHolosAPI.getMaxTempHolograms();        // null if unlimited
Integer maxPersistent = asgHolosAPI.getMaxPersistentHolograms(); // null if unlimited
```

#### Limit Checking
```java
boolean canCreateTemp = asgHolosAPI.canCreateTempHologram();
boolean canCreatePersistent = asgHolosAPI.canCreatePersistentHologram();
```

### Utility Methods

#### `reloadConfig()`
Reloads the AsgHolos configuration and refreshes the hologram cache to ensure accurate counting.

```java
asgHolosAPI.reloadConfig();
```

This method will:
1. Reload the configuration values from config.yml
2. Reset and repopulate the hologram cache
3. Update all hologram counters to ensure accuracy

It's recommended to call this method after making changes to the configuration file or when you suspect the hologram counts might be inaccurate.

#### `getVersion()`
Returns the plugin version.

```java
String version = asgHolosAPI.getVersion();
```

## Events

The API provides two events that external plugins can listen to:

### HologramCreateEvent

Fired when a hologram is about to be created. This event can be cancelled.

```java
@EventHandler
public void onHologramCreate(HologramCreateEvent event) {
    Player creator = event.getCreator();           // Can be null for API calls
    HologramData hologram = event.getHologramData();
    CreationSource source = event.getSource();     // PLAYER_COMMAND, PLAYER_GUI, API_CALL
    
    // Cancel creation if needed
    if (hologram.text().contains("banned")) {
        event.setCancelled(true);
    }
    
    // Modify hologram data before creation
    HologramData modified = new HologramData(
        hologram.location(),
        "[Modified] " + hologram.text(),
        hologram.name(),
        hologram.persistent(),
        hologram.shadowed(),
        hologram.seeThrough(),
        hologram.billboard(),
        hologram.yaw(),
        hologram.pitch(),
        hologram.scale(),
        hologram.textAlignment(),
        hologram.textOpacity(),
        hologram.backgroundColor(),
        hologram.viewDistance(),
        hologram.entityUUID()
    );
    event.setHologramData(modified);
}
```

### HologramDeleteEvent

Fired when a hologram is about to be deleted. This event can be cancelled.

```java
@EventHandler
public void onHologramDelete(HologramDeleteEvent event) {
    Player deleter = event.getDeleter();           // Can be null for API calls
    HologramData hologram = event.getHologramData();
    DeletionSource source = event.getSource();     // PLAYER_COMMAND, PLAYER_GUI, API_CALL, etc.
    
    // Prevent deletion of important holograms
    if (hologram.name() != null && hologram.name().contains("important")) {
        event.setCancelled(true);
    }
}
```

### Event Sources

#### CreationSource
- `PLAYER_COMMAND` - Created via `/holo create` command
- `PLAYER_GUI` - Created via the creation GUI
- `API_CALL` - Created via external API call

#### DeletionSource
- `PLAYER_COMMAND` - Deleted via command
- `PLAYER_GUI` - Deleted via the list GUI
- `API_CALL` - Deleted via external API call
- `MAINTENANCE_TASK` - Deleted by maintenance task
- `PLUGIN_DISABLE` - Deleted when plugin disables
- `WORLD_UNLOAD` - Deleted when world unloads

## Best Practices

### 1. Check Plugin Availability

Always check if AsgHolos is available before using the API:

```java
AsgHolos asgHolosPlugin = (AsgHolos) Bukkit.getPluginManager().getPlugin("AsgHolos");
if (asgHolosPlugin == null) {
    getLogger().warning("AsgHolos not found!");
    return;
}
```

### 2. Respect Limits

Check limits before creating holograms:

```java
if (!asgHolosAPI.canCreateTempHologram()) {
    player.sendMessage("Cannot create hologram: Temporary limit reached!");
    return;
}
```

### 3. Handle Null Returns

API methods may return null if operations fail:

```java
HologramData created = asgHolosAPI.createHologram(data);
if (created == null) {
    // Handle creation failure
    getLogger().warning("Failed to create hologram!");
}
```

### 4. Use Events Wisely

Event listeners should be lightweight and avoid blocking operations:

```java
@EventHandler(priority = EventPriority.HIGH)
public void onHologramCreate(HologramCreateEvent event) {
    // Quick validation only
    if (isInvalidLocation(event.getHologramData().location())) {
        event.setCancelled(true);
    }
}
```

## Example Integration

See `examples/ExampleAPIUsage.java` for a complete example of how to integrate with the AsgHolos API.

## Support

For API support and questions, please refer to the main AsgHolos plugin documentation or create an issue in the plugin's repository.