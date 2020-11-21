package com.puzzleattack.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Dylan on 2016-01-31.
 */
public class dropThread implements Runnable {

    private PuzzleAttack game;
    private Puzzle puzzle;
    private AssetManager manager;
    private float delta;
    private Sound drop;

    public dropThread(PuzzleAttack game, Puzzle puz, AssetManager assMan, float dt) {
        this.game = game;
        puzzle = puz;
        manager = assMan;
        delta = dt;
        drop =  manager.get("audio/sounds/dropping.wav", Sound.class);
    }

    public void run() {
        //Loop through every block in the puzzle
        for (int i = 0; i < 6; i++) {
            for (int j = 1; j < 10; j++) {

                //if block is trash
                if(puzzle.getTilemap(i, j).getBlockType() == 6 && puzzle.getTilemap(i, j).isFirstTrash()){
                    Block initialBlock = puzzle.getTilemap(i, j);
                    int count = 0;

                    for(int t = 0; t < initialBlock.getTrashSize().x; t++) {
                        if (puzzle.getTilemap(i + t, j - 1).getBlockType() == 0 && !puzzle.getTilemap(i + t, j - 1).isClearing())
                            count++;
                    }

                    if(!puzzle.getTilemap(i, j).isDropping()) {
                        puzzle.getTilemap(i, j).setJustMoved(10);
                        puzzle.getTilemap(i, j).setIsDropping(true);
                    }

                    if (count == initialBlock.getTrashSize().x){
                        for(int t = 0; t < initialBlock.getTrashSize().x; t++) {
                            for (int r = j; r < 10; r++) {
                                if (!puzzle.getTilemap(i + t, r).isClearing()) {
                                    puzzle.getTilemap(i + t, r - 1).setBlockType(puzzle.getTilemap(i + t, r).getBlockType());
                                    puzzle.getTilemap(i + t, r - 1).setIsDropping(true);
                                    puzzle.getTilemap(i + t, r - 1).setFirstTrash(puzzle.getTilemap(i + t, r).isFirstTrash());
                                    puzzle.getTilemap(i + t, r - 1).setTrashSize(puzzle.getTilemap(i + t, r).getTrashSize());
                                    puzzle.getTilemap(i + t, r).setIsDropping(false);
                                    puzzle.getTilemap(i + t, r).dropped = 0.14f;
                                    if (r == 9) {
                                        puzzle.getTilemap(i + t, r).setBlockType(0);
                                        if(puzzle.getTilemap(i + t, 0).isDropping())
                                            puzzle.getTilemap(i + t, 0).dropped = 0.14f;
                                        puzzle.getTilemap(i + t, 0).setIsDropping(false);
                                    }
                                } else
                                    break;
                            }
                        }
                    }
                }

                //If the block isn't empty, and the block below it is empty and it's not clearing, prepare to drop
                else if (puzzle.getTilemap(i, j).getBlockType() != 6 && puzzle.getTilemap(i, j).getBlockType() != 0 && puzzle.getTilemap(i, j - 1).getBlockType() == 0 && !puzzle.getTilemap(i, j - 1).isClearing() && !puzzle.getTilemap(i, j).isClearing()) {

                    //if the block isn't already dropping, delay it for 10 frames and set as dropping
                    if(!puzzle.getTilemap(i, j).isDropping()) {
                        puzzle.getTilemap(i, j).setJustMoved(10);
                        puzzle.getTilemap(i, j).setIsDropping(true);
                    }

                    //If justMoved is more than 0, subtract one. Ends up animating the block switch
                    if((puzzle.getTilemap(i, j).getJustMoved() > 0)){
                        puzzle.getTilemap(i, j).setJustMoved(puzzle.getTilemap(i, j).getJustMoved() - 1);
                        break;
                    }

                    //Loops through every block above this one in the column and drops it as well (So they all drop at the same time)
                    for (int r = j; r < 10; r++) {
                        if (!puzzle.getTilemap(i, r).isClearing() && puzzle.getTilemap(i, r).getBlockType() != 6)
                                puzzle.getTilemap(i, r - 1).setBlockType(puzzle.getTilemap(i, r).getBlockType());
                            else {
                                System.out.println("got here");
                                puzzle.getTilemap(i, r - 1).setBlockType(0);
                                break;
                            }
                            puzzle.getTilemap(i, r - 1).setIsDropping(true);
                            puzzle.getTilemap(i, r).setIsDropping(false);
                            puzzle.getTilemap(i, r).dropped = 0.14f;
                            if (r == 9) {
                                puzzle.getTilemap(i, r).setBlockType(0);
                                if(puzzle.getTilemap(i, 0).isDropping())
                                    puzzle.getTilemap(i, 0).dropped = 0.14f;
                                puzzle.getTilemap(i, 0).setIsDropping(false);
                            }
                    }

                }

                //Notice the brackets to separate logic. If the block isn't dropping (If it was, it
                // would have gone through the first if) and is dropping, and below it does NOT have
                // and empty block, then the block was dropping and has finished. Set as not dropping
                // and play the sound of the block landing
                else if (puzzle.getTilemap(i, j).getCoordinates().y == 0 || (puzzle.getTilemap(i, j - 1).getBlockType() != 0 && puzzle.getTilemap(i, j).isDropping() && !puzzle.getTilemap(i, j - 1).isDropping())) {
                    puzzle.getTilemap(i, j).setIsDropping(false);

                    puzzle.getTilemap(i, j).dropped = 0.14f;
                    if (puzzle.dropSoundDelay == 0 && game.soundOn) {
                        puzzle.dropSoundDelay += 0.05;

                        drop.setVolume(drop.play(), game.sound);
                    }
                // if none of these conditions are met, set block dropping to false just to catch any weird ones
                } else {
                    puzzle.getTilemap(i, j).setIsDropping(false);

                }
            }
        }
        // run clearBlocks
        puzzle.clearBlocks(delta);
    }
}





