# Architecture

## Design goals

AI Maze favors a small number of clear boundaries over framework-heavy abstraction.

1. Algorithm code should be independently testable.
2. UI code should not contain maze/search learning rules.
3. Maze validity should have one source of truth.
4. Benchmark results should not collapse fundamentally different algorithm families into misleading common metrics.
5. The application should remain offline and require no secrets.

## Domain layer

### Maze model

`Maze` owns immutable topology and validates dimensions, endpoints, grid shape, and reciprocal shared walls.

Movement flows through `Maze.move()` and `Maze.validNeighbors()`. Manual play, Q-Learning, A*, Dijkstra, and Random therefore share the exact same movement rules.

### Generation

`DepthFirstMazeGenerator` uses an internal mutable construction grid and exports a validated immutable `Maze`.

Generation is deterministic for a given:

- row count
- column count
- seed

### Manual play

`MazePlayState` is immutable. Legal moves create a new state while blocked moves leave state unchanged. Completion and move counting live in the domain layer rather than the UI.

### Reinforcement learning

`QLearningTrainer` contains:

- Q-table
- epsilon-greedy action selection
- temporal-difference update
- configurable alpha/gamma/epsilon schedule
- reward policy
- episode metrics
- learned greedy path extraction

It has no Android or Compose dependency.

### Pathfinding

A* and Dijkstra share `SearchResult`, which reports:

- path
- path length
- nodes explored
- execution time

### Benchmarking

`BenchmarkSuite` deliberately emits separate result types:

- `DeterministicSearchBenchmark`
- `ReinforcementLearningBenchmark`
- `StochasticBaselineBenchmark`

This prevents the UI from implying that, for example, Q-Learning training time is the same concept as A* search time.

## Data layer

Room stores benchmark snapshots only.

```text
BenchmarkHistoryRepository
        |
BenchmarkHistoryDao
        |
AiMazeDatabase (Room / SQLite)
```

No maze files, accounts, tokens, or remote data are stored.

## UI layer

Compose screens consume domain results and expose user intent through callbacks/state.

`MazeBoard` is a shared Canvas-based visualization used by manual play and AI-training experiences.

Navigation is centralized in `AiMazeNavHost`.

## Dependency direction

```text
UI ---> Domain
 |        ^
 v        |
Data -----+
```

The domain layer does not depend on UI or persistence.

## Testing strategy

Unit tests cover:

- maze invariants
- wall consistency
- movement
- seeded generation
- connectivity and acyclicity
- Q-Learning reproducibility and learning behavior
- A* correctness
- Dijkstra correctness
- Random valid movement
- benchmark-family separation
- representative larger-maze smoke tests

Android-specific persistence and UI behavior are kept thin so most correctness can be validated without an emulator.
