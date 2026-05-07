# FirstGame
This is my first attempt at writing a game in Java. It is now a small software-rendered 3D space survival game with stylized low-poly ships, a nebula star-tunnel backdrop, enemies flying through depth, forward-firing energy shots, score chasing, and survival-focused arcade pacing.

## Controls
- `Enter`: start
- `WASD`: move
- `Space`: fire
- `P`: pause or resume
- `R`: restart after a run or while paused
- `Esc`: quit

## Run locally
From the project root:

```sh
mkdir -p out
javac -d out FirstGame/FirstGame/src/com/main/*.java FirstGame/FirstGame/src/Main.java
java -cp out com.main.Game
```
