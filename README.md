# Puzzle Attack

An open source clone of Puzzle League written in Java using LibGDX

This project is licensed under [MIT](LICENSE).

**Table of contents**:

1. [Disclaimer](#disclaimer)
2. [Building](#building)

## Disclaimer

**Puzzle Attack** is a game written in Java using the [libGDX](https://libgdx.badlogicgames.com/) libraries. It's 
based on the Panel de Pon/Puzzle League series of games from Nintendo and Intelligent Systems. I am in no way affiliated with
them. This is a project I made for fun and to learn.

## Building

To build the project:

1. `git clone https://github.com/labonted/Puzzle-Attack.git`.
2. `cd Puzzle-Attack`
3. You can build for either desktop or android:
   1. For desktop, use `./gradlew desktop:dist`
   2. For Android, use `./gradlew android:assembleRelease`
4. You're done! The generated files are under `build`:
   1. Desktop build is under `desktop/build/libs/*.jar`
   2. Android build is under `android/build/outputs/apk/debug/*.apk`
