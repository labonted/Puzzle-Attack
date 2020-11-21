package com.puzzleattack.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.puzzleattack.game.Screens.PlayScreen;

/**
 * Created by Dylan on 2016-01-30.
 */
public class clearThread implements Runnable {

    private PuzzleAttack game;
    private boolean multiplier;
    private Block[] clearBlocks;
    private int clearCount;
    private long id;
    private float dt;
    private Sound combo;
    private Puzzle puzzle;
    private AssetManager manager;
    private PlayScreen playScreen;


    public clearThread(Puzzle p, Block[] blocks, AssetManager assMan, float delta, PuzzleAttack puzzleAttack, PlayScreen play) {
        game = puzzleAttack;
        clearBlocks = blocks;
        puzzle = p;
        manager = assMan;
        dt = delta;
        clearCount = 0;
        multiplier = false;
        playScreen = play;

        combo = manager.get("audio/sounds/combo.ogg", Sound.class);
    }

    public void run() {

        //loop through clearBlocks array, set to clearing.
        //Also sets switched to 0, so block will immediately go into the combo. Makes switching into
        //a combo look better
        for (int i = 0; i < clearBlocks.length; i++) {
            clearBlocks[i].setIsClearing(true);
            clearBlocks[i].setSwitched(0);
        }

        //If the sound is on, play the combo sound
        if(game.soundOn)
            combo.setVolume(combo.play(), game.sound);

        //add 2 to the stop timer
        puzzle.addStopTimer(2);

        //Count how many UNIQUE blocks there are (the array will contain duplicates if more than a 3 match)
        //also checks for multiplier
        for (int i = 0; i < clearBlocks.length; i++) {
            if (!clearBlocks[i].isCounted()) {

                // we use the set counted variable to keep track of which blocks we've already counted.
                // if a block is in the array twice, we'll know not to count it
                clearBlocks[i].setCounted(true);
                clearCount++;
            }
            if(clearBlocks[i].isMultiTest()){
                multiplier = true;
            }
        }

        // if multiplier equals true, increase multiplier
        if(multiplier == true)
            puzzle.setMultiplier(puzzle.getMultiplier() + 1);
        else
            puzzle.setMultiplier(1);

        for (int i = 0; i < puzzle.rows; i++) {
            for (int j = 0; j < puzzle.columns; j++) {
                puzzle.getTilemap(i, j).setMultiTest(false);
            }
        }

        // set counted as false so it doesn't screw up the next time we count this block
        for (int i = 0; i < clearBlocks.length; i++) {
            clearBlocks[i].setCounted(false);
        }

        //This is where it puts together the match, and then adds it to the renderMatch array in
        //the puzzle. The puzzle then draws the result to the screen. We don't just draw it here
        // because you cannot draw in a separate thread than the main thread
        puzzle.renderMatch.add(new Match(clearCount, (int) (clearBlocks[clearBlocks.length - 1].getPosition().x), (int) (clearBlocks[clearBlocks.length - 1].getPosition().y+ puzzle.moveUp), 1, puzzle.getMultiplier()));

        //The pause between flashing the blocks and actually clearing them
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // here's where we actually clear the blocks. Loop through the clearBlocks array
        for (int i = 0; i < clearBlocks.length; i++) {

            //If this block is already type 0, skip everything and go to the next iteration of the loop
            if (clearBlocks[i].getBlockType() == 0)
                continue;

            while(playScreen.getState() == PlayScreen.State.STOP){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // increase clearCount
            clearCount++;

            //Set this block type to 0 (clear the block)
            clearBlocks[i].setBlockType(0);

            // play the popping sound, which will be a higher pitch depending on what iteration of the
            // loop you're on (how many blocks have been cleared)
            if(game.soundOn) {
                combo.setVolume(id = manager.get("audio/sounds/block_disappear.ogg", Sound.class).play(), game.sound);
                manager.get("audio/sounds/block_disappear.ogg", Sound.class).setPitch(id, (0.15f * i) + 1f);
            }

            //add points
            puzzle.setScore(puzzle.getScore() + 100);

            // pause between clearing each block
            try {
                Thread.sleep(160);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // set all the blocks as not clearing anymore
        for (int i = 0; i < clearBlocks.length; i++) {
            for (int j = (int)(clearBlocks[i].getCoordinates().y); j < 10; j++) {
                puzzle.getTilemap((int)clearBlocks[i].getCoordinates().x, j).setMultiTest(true);
            }
            clearBlocks[i].setIsClearing(false);
        }
    }
}
