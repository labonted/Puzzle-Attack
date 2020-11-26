# Puzzle Attack

An open source clone of Puzzle League written in Java using LibGDX

This project is licensed under [MIT](LICENSE).

**Table of contents**:

1. [Disclaimer](#disclaimer)
2. [How To Play](#how-to-play)
3. [Building](#building)

## Description

**Puzzle Attack** is a puzzle game based on the Panel de Pon/Puzzle League series of games from Nintendo and Intelligent Systems. It's written in Java using the [libGDX](https://libgdx.badlogicgames.com/) libraries. 

## How To Play

In **Puzzle Attack**, the goal is to clear as many blocks as you can before your field fills up to the top with blocks. You can clear out blocks to earn points by moving blocks left and right (not up and down) and lining up three or more of the same colour block. You can also create combos (and earn extra points!) if a previous combination drops a block into another combination.

You can play on either your desktop or Android smartphone. It supports keyboard, mouse and touch control, and you can switch between them on the fly.

**Touchscreen**

&nbsp;&nbsp;&nbsp;&nbsp;Tap and hold a block to move it left and right  
&nbsp;&nbsp;&nbsp;&nbsp;Swipe up to raise blocks

**Keyboard**  

&nbsp;&nbsp;&nbsp;&nbsp;Arrow Keys: Move Cursor  
&nbsp;&nbsp;&nbsp;&nbsp;Spacebar: Switch highlighted blocks  
&nbsp;&nbsp;&nbsp;&nbsp;Left Shift: Speed up raising blocks  
   
**Mouse** (very similar to touchscreen) 

&nbsp;&nbsp;&nbsp;&nbsp;Click and hold a block to move it left and right  
&nbsp;&nbsp;&nbsp;&nbsp;Click and mouse up to raise blocks

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
   
   ## Disclaimer

**Puzzle Attack** is a game written in Java using the [libGDX](https://libgdx.badlogicgames.com/) libraries. It's 
based on the Panel de Pon/Puzzle League series of games from Nintendo and Intelligent Systems. I am in no way affiliated with
them. This is a project I made for fun and to learn.

