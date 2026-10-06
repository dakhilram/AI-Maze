# Privacy and Security

AI Maze is intentionally designed as an offline educational Android application.

## Data handling

- No user accounts.
- No advertising.
- No analytics or tracking SDKs.
- No paid AI APIs or external inference services.
- No backend, cloud database, or server dependency.
- Maze generation, pathfinding, Q-Learning training, and benchmarking run locally on the device.
- Benchmark history is stored locally in a Room/SQLite database.
- Android backup is disabled so benchmark history is not automatically copied to cloud backup services.

## Network access

The application does not request the Android `INTERNET` permission and does not include networking libraries for application features. Cleartext traffic is explicitly disabled in the manifest.

## Permissions

The application currently requests no runtime permissions.

## Secrets

The project requires no API keys, access tokens, client secrets, or service credentials.

## Local deletion

Users can clear benchmark history from the History screen. Uninstalling AI Maze removes its local application data under normal Android application lifecycle behavior.

## Security review checklist

- [x] No `INTERNET` permission.
- [x] No unnecessary Android permissions.
- [x] No embedded credentials or API keys.
- [x] No external analytics.
- [x] No user identity collection.
- [x] No external file storage.
- [x] Local database access is internal to the application sandbox.
- [x] Android cloud backup disabled.
- [x] Cleartext network traffic disabled.
