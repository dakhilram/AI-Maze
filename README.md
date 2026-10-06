# AI Maze

AI Maze is an offline Android application for visualizing reinforcement learning and classical pathfinding inside procedurally generated mazes.

The project is built as a portfolio-quality demonstration of Kotlin, Jetpack Compose, Q-Learning, graph search, algorithm benchmarking, local persistence, testing, and Android architecture.

## Highlights

- Fully offline: no backend, API keys, accounts, ads, analytics, or network permission.
- Procedural perfect mazes generated with randomized depth-first search.
- Reproducible maze topology through deterministic seeds.
- Manual maze solving with wall-aware movement.
- Q-Learning implemented from scratch in Kotlin.
- A* and Dijkstra shortest-path search.
- Seeded Random agent as a stochastic baseline.
- Benchmarking that keeps deterministic search, reinforcement learning, and stochastic metrics conceptually separate.
- Local benchmark history with Room.
- Jetpack Compose UI with light/dark themes.
- Unit tests for maze invariants, generation, movement, Q-Learning, pathfinding, baselines, and benchmarking.

## Algorithms

### Maze generation

AI Maze uses iterative randomized depth-first search (recursive backtracker). Every cell begins enclosed by walls. The generator repeatedly visits an unvisited neighbor, removes the shared wall in both cells, and backtracks when no unvisited neighbor remains.

For a maze with `V` cells, the generator creates exactly `V - 1` passages. The resulting maze is connected and acyclic, so there is exactly one path between every pair of cells.

### Q-Learning

Each maze cell is a state. The action space is:

- North
- East
- South
- West

The agent uses epsilon-greedy exploration and updates Q-values with the standard temporal-difference rule:

```text
Q(s,a) <- Q(s,a) + alpha * [r + gamma * max Q(s',a') - Q(s,a)]
```

Training exposes:

- episode number
- reward
- step count
- success
- exploration rate
- success rate
- learned greedy path

### A*

A* performs deterministic shortest-path search using Manhattan distance as an admissible heuristic for the unit-cost rectangular grid.

### Dijkstra

Dijkstra explores states in increasing path-cost order. It also returns an optimal path but does not use a goal-directed heuristic.

### Random baseline

The Random agent chooses only among valid neighboring cells. It is intentionally weak and stochastic. It is evaluated across repeated trials rather than presented as equivalent to a deterministic shortest-path algorithm.

## Benchmark methodology

All algorithms operate on the same generated maze for a benchmark run.

**A* / Dijkstra**

- path length
- nodes explored
- one-shot execution time

**Q-Learning**

- training episodes
- training success rate
- learned path length
- final epsilon
- training time

**Random baseline**

- trial count
- success rate
- average steps among successful trials
- best successful path

Q-Learning training time is intentionally not presented as directly equivalent to A*/Dijkstra one-shot search time.

## Architecture

```text
com.akhil.aimaze
|
+-- domain
|   +-- maze              Immutable maze model and movement rules
|   +-- maze.generation   Seeded DFS maze generation
|   +-- play              Manual play state
|   +-- rl                Q-Learning
|   +-- pathfinding       A* and Dijkstra
|   +-- baseline          Random agent
|   +-- benchmark         Fair benchmark orchestration
|
+-- data
|   +-- history           Room database, DAO, repository
|
+-- ui
    +-- components        Shared maze visualization
    +-- navigation        Compose navigation
    +-- screens           Home, Play, Train, Compare, History, How It Works
    +-- theme             AI Maze Material 3 theme
```

The domain layer is intentionally independent from Android and Compose where possible so algorithm logic can be tested as ordinary Kotlin.

See [Architecture](docs/ARCHITECTURE.md) and [Privacy & Security](docs/PRIVACY_SECURITY.md).

## Technology

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Navigation Compose
- Room / SQLite
- Gradle Kotlin DSL
- JUnit 4
- GitHub Actions

Current Android configuration:

- minSdk 24
- targetSdk 37
- compileSdk 37

## Build

Clone the repository and open it in Android Studio, or build from a terminal.

Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew test
./gradlew assembleDebug
```

The debug APK is produced under:

```text
app/build/outputs/apk/debug/
```

## Privacy

AI Maze does not require internet access or runtime permissions. Benchmark history remains in the application sandbox and Android backup is disabled.

## Project goals

This repository demonstrates practical understanding of:

- reinforcement-learning fundamentals
- Q-Learning and epsilon-greedy exploration
- reward design and state/action spaces
- A* and Dijkstra
- stochastic baselines
- algorithm benchmarking
- deterministic procedural generation
- Kotlin and Android development
- Jetpack Compose visualization
- local persistence
- software architecture and testing

## Status

Core application functionality is implemented. Final device verification, release signing, screenshots/GIFs, and Google Play Console submission are release-stage tasks.
