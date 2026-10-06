# Google Play Release Preparation

## Suggested listing

**App name**

AI Maze

**Short description**

Learn Q-Learning and pathfinding by watching AI solve generated mazes offline.

**Full description**

AI Maze is an interactive, offline learning app that makes artificial intelligence and pathfinding visible.

Generate reproducible mazes, solve them yourself, train a Q-Learning agent, and compare reinforcement learning with A*, Dijkstra, and a Random baseline on the same maze.

Highlights:

- Procedurally generated mazes from 8×8 through 20×20
- Reproducible maze seeds
- Manual maze solving
- Q-Learning implemented locally on the device
- Training metrics including reward, steps, success rate, epsilon, and learned path
- A* and Dijkstra shortest-path search
- Random-agent baseline
- Fair algorithm benchmarking
- Local benchmark history
- Educational explanations of each algorithm
- Light and dark themes
- No account required
- No ads
- No subscriptions
- No internet connection required

AI Maze is designed for students, developers, and anyone curious about how reinforcement learning and classical search algorithms behave.

## Category suggestion

Education

## Monetization

Free.

- No ads
- No in-app purchases
- No subscriptions

## Data Safety preparation

Based on the current application implementation:

- Data collected: none
- Data shared: none
- User accounts: none
- Advertising: none
- Analytics: none
- Location: not collected
- Personal information: not collected
- Financial information: not collected
- Device identifiers: not collected for application features
- Network access: not required

Benchmark history is created by the user and stored inside the app's local Room database. It is not transmitted to a server.

Verify these answers against the final signed build before submitting the Data Safety form.

## Permissions

The current manifest requests no runtime permissions and no `INTERNET` permission.

## Screenshots to capture after final device verification

Capture clean portrait screenshots of:

1. Home
2. Play Maze with an active generated maze
3. Train AI after training with learned path and metrics
4. AI vs Algorithms benchmark results
5. Benchmark History
6. How It Works

Use a consistent emulator/device and theme.

## Feature graphic

Create a simple 1024×500 feature graphic showing:

- AI Maze name
- a maze motif
- a highlighted learned path
- a small AI/learning visual cue

Avoid claims such as "best AI" or misleading performance comparisons.

## Release sequence

1. Run local unit tests and debug build.
2. Perform manual emulator/device testing.
3. Generate the signed Android App Bundle.
4. Create the app in Google Play Console.
5. Complete Store Listing.
6. Complete App Content / Data Safety using the final build behavior.
7. Upload screenshots and feature graphic.
8. Upload the signed AAB to the required testing track.
9. Resolve all Play Console warnings/errors.
10. Complete any testing requirement shown for the developer account.
11. Promote the verified build to production when eligible.

## Final checks before upload

- package name remains `com.akhil.aimaze`
- `versionCode` is higher than every previously uploaded build
- signed release installs and launches
- no debug UI/logging is exposed
- all algorithm screens work offline
- benchmark history works after process restart
- no accidental network permission or dependency was added
- privacy/security documentation matches actual behavior

Google Play policy and testing requirements can change, so the Play Console should be treated as the source of truth at submission time.
