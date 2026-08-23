# MCES Development Guide

## Project Structure

```
mces/
├── server/                      # MCES Server Daemon
│   ├── src/main/java/
│   │   └── dev/tacoagha/mces/
│   │       ├── MCESServer.java  # Main entry point
│   │       ├── api/             # API endpoints
│   │       ├── server/          # Server management
│   │       ├── process/         # Process management
│   │       ├── config/          # Configuration
│   │       └── util/            # Utilities
│   └── build.gradle
│
├── fabric-mod/                  # Fabric Client Mod
│   ├── src/main/java/
│   │   └── dev/tacoagha/mces/
│   │       ├── MCESMod.java     # Main mod class
│   │       ├── screen/          # UI screens
│   │       ├── provider/        # Provider management
│   │       ├── server/          # Server management
│   │       ├── api/             # HTTP client
│   │       ├── config/          # Cloth Config
│   │       └── util/            # Utilities
│   ├── src/main/resources/
│   │   ├── fabric.mod.json
│   │   └── assets/mces/         # Textures, models, etc.
│   └── build.gradle
│
└── docs/
    ├── API.md
    ├── CONFIGURATION.md
    └── DEVELOPMENT.md
```

## Building

### Prerequisites

- Java 21+
- Gradle 8.0+
- Git

### Build Commands

```bash
# Build everything
./gradlew build

# Build only server daemon
./gradlew :server:build

# Build only Fabric mod
./gradlew :fabric-mod:build

# Run Minecraft with the mod in development
./gradlew :fabric-mod:runClient

# Run the server daemon (if it has a main class configured)
./gradlew :server:run
```

## Code Organization

### Server Daemon (`/server`)

#### Core Components

- **MCESServer**: Main application class, starts HTTP server
- **ServerManager**: Manages server instances, lifecycle
- **ProcessManager**: Handles process creation, monitoring, cleanup
- **ConfigurationLoader**: Loads and manages server configuration
- **HealthChecker**: Monitors server health periodically

#### API Layer (`/api`)

- **ApiController**: Routes requests to appropriate handlers
- **ServerEndpoints**: `/api/v1/servers/*` endpoints
- **StatusEndpoints**: `/api/v1/*/status` endpoints
- **PluginEndpoints**: `/api/v1/*/plugins` endpoints

#### Models

- **ServerInstance**: Represents a Minecraft server instance
- **ServerConfig**: Server configuration (port, RAM, etc.)
- **ServerStatus**: Current server status (online, offline, crashed)
- **ServerProcess**: Manages server process lifecycle

### Fabric Mod (`/fabric-mod`)

#### Screens

- **ProviderListScreen**: Multiplayer-style provider list
- **ServerListScreen**: Multiplayer-style server list (per provider)
- **ServerCreationScreen**: Server creation wizard
- **ServerConfigScreen**: Server configuration editor
- **PluginManagementScreen**: Plugin management UI
- **RawConfigEditorScreen**: Raw config file editor

#### Provider System

- **ProviderManager**: Local provider storage and management
- **ProviderEntry**: Represents a provider configuration
- **HttpClient**: HTTP communication with MCES server

#### Configuration

- **MCESConfig**: Cloth Config screen builder
- **ConfigOptions**: All configurable options
- **ConfigSerializer**: Save/load provider and mod settings

## API Design Principles

1. **Versioning**: All endpoints use `/api/v1/`
2. **JSON Responses**: Consistent response format
3. **Async-Ready**: Architecture supports async operations without freezing client
4. **Error Handling**: Clear error codes and messages
5. **Future-Proof**: Authentication layer can be added without breaking existing endpoints

## Process Management

The MCES server handles Minecraft server processes with:

- **Start**: Launch Minecraft server JAR
- **Monitor**: Periodically check if process is running
- **Detect Crashes**: Monitor exit codes
- **Clean Shutdown**: Send stop commands
- **Force Shutdown**: Kill process if necessary
- **Log Collection**: Capture stdout/stderr
- **Port Management**: Track allocated ports, detect conflicts

## Error Handling

### Client-Side (Fabric Mod)

- Never block main thread with network calls
- Use async callbacks/futures for API requests
- Display error screens for API failures
- Retry logic with exponential backoff
- Cache last-known state

### Server-Side (MCES)

- Return consistent error responses
- Log all errors for debugging
- Detect and handle process crashes gracefully
- Validate all inputs
- Handle port conflicts and resource exhaustion

## Adding New Features

### Adding a New Server Endpoint

1. Create endpoint class in `server/src/main/java/.../api/`
2. Implement HTTP method handlers
3. Return JSON response via `ApiResponse`
4. Add validation and error handling
5. Update API.md documentation
6. Write unit tests

### Adding a New Mod Screen

1. Extend `Screen` class from Fabric API
2. Implement UI rendering and interaction
3. Handle HTTP calls asynchronously
4. Add configuration options to Cloth Config if needed
5. Update mod initialization to hook into menu/screens

## Testing

### Server Tests

```bash
./gradlew :server:test
```

### Mod Tests

```bash
./gradlew :fabric-mod:test
```

### Manual Testing

1. Start MCES server:
   ```bash
   java -jar server/build/libs/mces.jar
   ```

2. Run Fabric mod in development:
   ```bash
   ./gradlew :fabric-mod:runClient
   ```

3. Click "Easy Servers" button to test mod UI

## Debugging

### Server-Side

- Logs output to console and `mces-data/logs/`
- Set `DEBUG=true` environment variable for verbose logging
- Use breakpoints in IDE (IntelliJ, Eclipse, etc.)

### Mod-Side

- Enable Minecraft debug mode: `--debug`
- Log to chat with `/say` command
- Use `System.out.println()` for debugging
- Check latest log file: `.minecraft/logs/latest.log`

## Dependencies

### Server

- **Spring Boot** (or similar): HTTP server framework
- **Jackson**: JSON serialization
- **JUnit 5**: Testing
- **Java NIO**: Async I/O

### Fabric Mod

- **Fabric API**: Mod loading, events, networking
- **Cloth Config**: Configuration UI
- **Gradle Loom**: Fabric build tools
- **Minecraft Client**: Game interaction

## Git Workflow

1. Create feature branch: `git checkout -b feature/name`
2. Commit changes: `git commit -m "description"`
3. Push branch: `git push origin feature/name`
4. Create Pull Request on GitHub
5. Code review and merge

## Performance Considerations

1. **Async Networking**: Never block Minecraft thread on API calls
2. **Caching**: Cache provider/server lists locally
3. **Throttling**: Limit ping/refresh requests
4. **Memory**: Manage server list rendering efficiently
5. **Process Spawning**: Reuse Java process manager for efficiency
