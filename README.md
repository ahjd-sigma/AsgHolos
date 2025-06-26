# AsgHolos - Advanced Hologram Plugin

AsgHolos is a powerful Minecraft plugin for creating and managing holograms with a comprehensive API for external plugin integration.

⚠️ **WARNING**: This plugin does not work with any other plugin that handles/spawns TextDisplay entities - it will delete them.

## Features

### Core Features
- **Interactive GUI**: Easy-to-use graphical interface for hologram creation and management
- **Persistent & Temporary Holograms**: Support for both persistent (saved) and temporary holograms
- **Flexible Configuration**: Customizable limits and settings
- **Performance Optimized**: Efficient entity management and caching
- **Maintenance System**: Automatic hologram cleanup and restoration

### API Features
- **Comprehensive API**: Full programmatic access to hologram functionality
- **Event System**: Listen to hologram creation and deletion events
- **Event Modification**: Modify hologram data during creation
- **Event Cancellation**: Cancel hologram operations through events
- **Multiple Creation Sources**: Track how holograms are created (GUI, Command, API, Maintenance)
- **Multiple Deletion Sources**: Track how holograms are deleted (GUI, Command, API, Maintenance)

## Installation

1. Download the latest release from the releases page
2. Place the `AsgHolos.jar` file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/AsgHolos/config.yml`

## Commands

- `/hologram` - Show help message
- `/hologram create` - Open hologram creation GUI
- `/hologram list` - Open hologram management GUI
- `/hologram limit` - Show current hologram limits and usage
- `/hologram help` - Show help message

## Permissions

- `asgholos.use` - Basic plugin usage
- `asgholos.create` - Create holograms
- `asgholos.delete` - Delete holograms
- `asgholos.admin` - Administrative access

## API Usage

### Getting Started

1. Add AsgHolos as a dependency in your `plugin.yml`:
```yaml
depend: [AsgHolos]
```

2. Get the API instance in your plugin:
```java
import ahjd.asgHolos.AsgHolos;
import ahjd.asgHolos.api.AsgHolosAPI;

public class YourPlugin extends JavaPlugin {
    private AsgHolosAPI asgHolosAPI;
    
    @Override
    public void onEnable() {
        // Get the API instance
        asgHolosAPI = AsgHolos.getInstance().getAPI();
    }
}
```

### Creating Holograms

```java
import ahjd.asgHolos.data.HologramData;
import org.bukkit.Location;

// Create hologram data
HologramData hologramData = new HologramData(
    location,           // Location
    "Hello World!",     // Text
    "my-hologram",      // Name
    true,               // Persistent
    false,              // Shadowed
    false,              // See through
    null,               // Billboard
    0.0f,               // Yaw
    0.0f,               // Pitch
    1.0f,               // Scale
    null,               // Text alignment
    255,                // Text opacity
    0,                  // Background color
    64.0f,              // View distance
    null                // Entity UUID
);

// Create the hologram
HologramData createdHologram = asgHolosAPI.createHologram(hologramData);

// Create with player context
HologramData createdHologram = asgHolosAPI.createHologram(hologramData, player);
```

### Event Handling

```java
import ahjd.asgHolos.api.events.HologramCreateEvent;
import ahjd.asgHolos.api.events.HologramDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HologramEventListener implements Listener {
    
    @EventHandler
    public void onHologramCreate(HologramCreateEvent event) {
        // Get event information
        HologramData data = event.getHologramData();
        Player creator = event.getCreator(); // May be null
        HologramCreateEvent.CreationSource source = event.getSource();
        
        // Cancel creation
        if (data.name().contains("banned")) {
            event.setCancelled(true);
            return;
        }
        
        // Modify hologram data
        HologramData modifiedData = new HologramData(
            data.location(),
            "[Modified] " + data.text(), // Add prefix
            data.name(),
            data.persistent(),
            data.shadowed(),
            data.seeThrough(),
            data.billboard(),
            data.yaw(),
            data.pitch(),
            data.scale(),
            data.textAlignment(),
            data.textOpacity(),
            data.backgroundColor(),
            data.viewDistance(),
            data.entityUUID()
        );
        event.setHologramData(modifiedData);
    }
    
    @EventHandler
    public void onHologramDelete(HologramDeleteEvent event) {
        // Cancel deletion of protected holograms
        if (event.getHologramData().name().contains("protected")) {
            event.setCancelled(true);
            if (event.getDeleter() != null) {
                event.getDeleter().sendMessage("This hologram is protected!");
            }
        }
    }
}
```

## Event Sources

### Creation Sources
- `PLAYER_COMMAND` - Created via command
- `PLAYER_GUI` - Created via GUI
- `API_CALL` - Created via API
- `MAINTENANCE_TASK` - Created during maintenance/restoration

### Deletion Sources
- `PLAYER_COMMAND` - Deleted via command
- `PLAYER_GUI` - Deleted via GUI
- `API_CALL` - Deleted via API
- `MAINTENANCE_TASK` - Deleted during maintenance/cleanup

## Best Practices

1. **Always check for null**: API methods may return null if operations fail
2. **Handle events properly**: Register your event listeners in your plugin's onEnable method
3. **Use appropriate sources**: When creating/deleting holograms via API, the source will be automatically set to API_CALL
4. **Respect limits**: Check hologram limits before creating new holograms
5. **Clean up**: Remove holograms created by your plugin when your plugin is disabled

## Documentation

For detailed API documentation, see `API_DOCUMENTATION.md`.
For usage examples, see `examples/ExampleAPIUsage.java`.

## Support

For support, bug reports, or feature requests, please visit our GitHub repository or contact the development team.

## License

This project is licensed under the MIT License - see the LICENSE file for details.