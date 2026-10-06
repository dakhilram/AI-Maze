# Maze Rush

Maze Rush is a polished offline Android maze game built with Kotlin and Jetpack Compose.

The player experience is intentionally game-first: swipe controls, animated movement, sound and haptic feedback, multiple modes, local records, difficulty scaling, and replayable procedurally generated levels.

The repository also demonstrates deeper engineering work such as procedural generation, pathfinding, persistence, testing, and Android CI.

## Game modes

### Maze Run

Classic endless play.

- Swipe directly on the maze
- Move and time tracking
- Par scoring
- 1–3 star result
- Easy / Normal / Hard / Expert sizes
- Restart and next-maze flow
- Animated player movement
- Goal pulse
- Wall-hit shake, sound, and haptics

### Time Attack

Escape before time expires.

- 3-2-1 countdown
- Difficulty-specific time limits
- Countdown sounds
- Win / timeout feedback
- Local run records

### Beat the Bot

Race the same maze against a moving opponent.

- Shared maze topology
- Real-time bot movement
- Countdown start
- Win / loss result
- Local race history
- Difficulty scaling

### Records

Local on-device history for:

- Maze Run clears
- Time Attack clears
- Beat the Bot wins/losses
- Stars
- Move counts
- Completion times

No account or server is required.

## Android engineering

- Kotlin
- Jetpack Compose
- Material 3
- Compose animations
- Gesture input
- Canvas rendering
- Sound feedback with Android ToneGenerator
- Haptic feedback
- Room / SQLite
- Navigation Compose
- Coroutine-driven timers and race loops
- Adaptive launcher icon
- GitHub Actions build + test pipeline
- Installable debug APK artifact from CI

## Game / algorithm engineering

The player does not need to know about the algorithms used internally.

Under the hood the project contains:

- seeded randomized depth-first maze generation
- immutable maze/domain model
- A* shortest-path search
- Dijkstra shortest-path search
- random baseline agent
- Q-Learning experiments used as developer learning / portfolio work
- benchmark tooling and tests

Only gameplay-relevant behavior is exposed to the player.

## Architecture

```text
UI / Game screens
      |
      v
Game state + domain
      |
      +-- Maze generation
      +-- Movement rules
      +-- Pathfinding
      +-- Scoring
      |
      v
Local persistence (Room)
```

Most maze and algorithm logic is ordinary Kotlin with no Compose dependency, making it straightforward to unit test.

## Privacy

Maze Rush is fully offline.

- no account
- no ads
- no analytics
- no backend
- no API keys
- no INTERNET permission
- no runtime permissions
- Android cloud backup disabled

## Build locally

Windows:

```powershell
.\gradlew.bat test assembleDebug
```

Debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install a CI test build

Every successful `main` build uploads an artifact named:

```text
maze-rush-debug-apk
```

Open the repository's **Actions** tab, open the latest successful **Android CI** run, and download the artifact.

Unzip it and install `app-debug.apk` on an Android device after allowing installation from that source.

## Resume-ready summary

**Maze Rush — Android Game | Kotlin, Jetpack Compose, Room**

Built an offline procedural maze game with swipe-based controls, animated Canvas rendering, sound/haptic feedback, timed challenges, real-time bot races, local score persistence, seeded maze generation, pathfinding-based gameplay, unit tests, and GitHub Actions CI producing installable Android builds.

## Status

Active development / device testing.
