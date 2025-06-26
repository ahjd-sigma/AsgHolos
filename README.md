# AsgHolos - Advanced Hologram Plugin for Spigot 1.21.5

A powerful and feature-rich hologram plugin that allows you to create and manage text display holograms with extensive customization options.

## ⚠️ Important Warning
**THIS PLUGIN DOES NOT WORK WITH ANY OTHER PLUGIN THAT HANDLES/SPAWNS TEXTDISPLAY ENTITIES - IT WILL DELETE THEM**

## Features

### Core Functionality
- **Easy Hologram Creation**: Create holograms with a user-friendly GUI interface
- **Persistent Storage**: Holograms are automatically saved and restored on server restart
- **Temporary Holograms**: Option to create non-persistent holograms that don't save
- **Automatic Maintenance**: Built-in maintenance system to ensure hologram integrity

### Text Customization
- **Rich Text Support**: Full color code support with `&` codes and hex colors `{#hex}`
- **Text Shadows**: Toggle shadow effects behind text for better visibility
- **Text Alignment**: Choose from LEFT, CENTER, or RIGHT text alignment
- **Text Opacity**: Adjust text transparency from 0-255 (0-100%)

### Visual Properties
- **Scale Control**: Resize holograms from 0.1x to 5.0x scale
- **Billboard Modes**: Support for all billboard rotation types (CENTER, FIXED, VERTICAL, HORIZONTAL)
- **See Through**: Toggle visibility through blocks
- **Background**: Customizable background color and opacity

### Positioning & Rotation
- **Yaw/Pitch Control**: Precise rotation control with directional indicators (North, South, East, West)
- **Fixed Positioning**: Support for FIXED billboard mode with custom rotations
- **Location-based Spawning**: Holograms spawn 2 blocks above player location

### Advanced Settings
- **View Distance**: Configurable render distance from 16 to 512 blocks
- **Entity Management**: Automatic UUID tracking and entity cache management
- **Batch Processing**: Efficient hologram spawning in batches during maintenance

## Commands
- `/holo` - Main command to access hologram functionality

## GUI Controls

### Creation GUI
- **Text Input**: Click to enter custom text via chat
- **Shadowed Text**: Toggle shadow effects
- **Persistence**: Toggle between persistent and temporary holograms
- **Billboard Mode**: Cycle through billboard rotation types
- **See Through**: Toggle visibility through blocks
- **Scale**: Left click +0.1, Right click -0.1
- **Yaw/Pitch**: Left click adjusts yaw, Right click adjusts pitch
- **Text Alignment**: Cycle through LEFT, CENTER, RIGHT
- **Text Opacity**: Left click +10, Right click -10
- **Background**: Left click +10 opacity, Right click -10 opacity
- **View Distance**: Left click +16 blocks, Right click -16 blocks

## Technical Details

### Data Storage
- Holograms are saved in `holograms.yml` in the plugin data folder
- Automatic backup and restoration on server restart
- UUID-based entity tracking for reliable management

### Performance
- Entity caching system for O(1) hologram lookups
- Batch processing during maintenance cycles
- Automatic cleanup of invalid entities
- 5-minute maintenance intervals

### Compatibility
- Designed for Spigot 1.21.5
- Uses modern TextDisplay entities
- Full support for all Display entity properties

## Installation
1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart the server
4. Use `/holo` to start creating holograms

## Support
This plugin is currently in active development. Report issues and suggestions to the development team.

---
*Note: This plugin manages TextDisplay entities exclusively and will conflict with other plugins that use the same entity type.*