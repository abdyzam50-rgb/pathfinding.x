# PathfindingX

A Baritone-inspired client-side pathfinding framework for Minecraft 1.21.9 (Fabric).

## Features

- **A\* Pathfinder** — weighted A* with configurable heuristic weight and node budget
- **Goal System** — `GoalBlock`, `GoalXZ`, `GoalNear`, `GoalRunAway`, `GoalComposite`
- **Movement Types** — walk, sprint, jump, fall (up to 3 blocks), ladder, swim
- **3D Path Renderer** — line segments + node boxes drawn in world space
- **HUD Overlay** — status, steps remaining, next waypoint distance
- **JSON Config** — saved to `.minecraft/config/pathfinding-x.json`
- **Keybinds** (rebindable in Options > Controls > PathfindingX)
  - `Backspace` — cancel
  - `F7` — toggle HUD
  - `F8` — toggle path rendering
  - `F9` — recompute current goal

## Commands

| Command | Description |
|---|---|
| `/pfx goto <x> <y> <z>` | Path to exact block |
| `/pfx near <x> <y> <z> <range>` | Path to within `range` blocks |
| `/pfx xz <x> <z>` | Path to column (any Y) |
| `/pfx cancel` | Stop and clear path |
| `/pfx status` | Show current status + step count |
| `/pfx config render <true\|false>` | Toggle render in-game |

## Planned

- Humanization layer (randomized timing, mouse movement jitter, gaze targets)
- PvP automation AI (combat goal, strafe movement, target tracking)
- AOTV teleport movement integration (from `atov-pathfinding`)
- Cloth Config GUI screen
