package com.puzzleattack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.puzzleattack.game.Input.Cursor;
import com.puzzleattack.game.Input.Gesture;
import com.puzzleattack.game.Screens.PlayScreen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Puzzle {

    private int multiplier;
    public List<Match> renderMatch;
    private PuzzleAttack game;
    private Block tilemap[][];
    private Block nextRow[];
    private Block touchedBlock;
    public int rows, columns;
    private float elapsedTime = 0, repeatKeyPress = 0;
    private PlayScreen playScreen;

    private Cursor keyboardCursor;

    //Used for switchBlocks()
    Vector3 touchSwitch;

    //delay variables for dropBlocks
    Block[] blockArray;
    float dropSoundDelay, switchSoundDelay;

    //Moving up variables
    public float  moveUp;
    private int moveSpeed, noMove, level;
    float moveTimer;
    private boolean stop;
    private float stopTimer;

    private float gameOverTimer = 0.5f;

    //Scroll blocks up stuff
    private int panUp;
    private int scrollCount;

    //Sounds
    private Sound switchSound;

    private AssetManager manager;

    private int score;

    public Runnable clearThread;

    BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
            Gdx.files.internal("fonts/font.png"), false);
    Texture combo = new Texture("Combo.png");

    public Puzzle(AssetManager assMan, PuzzleAttack puzz, Cursor cursor, PlayScreen play, int level) {
        renderMatch = new ArrayList<Match>();

        playScreen = play;

        //Move Speed Variables
        moveUp = -1000;
        noMove = 6;
        moveSpeed = 1;
        this.level = level;
        panUp = 0;
        scrollCount = 0;
        stop = false;

        game = puzz;
        manager = assMan;
        score = 0;
        dropSoundDelay = 0;
        multiplier = 1;
        switchSound = manager.get("audio/sounds/switch_blocks.wav", Sound.class);
        keyboardCursor = cursor;

        //Gestures (for sliding blocks up)
        Gesture gesture = new Gesture(this);
        GestureDetector gd = new GestureDetector(gesture);
        Gdx.input.setInputProcessor(gd);

        //Fill Tilemap with 6 columns of 10
        tilemap = new Block[6][10];
        nextRow = new Block[6];

        //set variables to always have access to the amount of rows and columns in the puzzle.
        rows = tilemap.length;
        columns = tilemap[0].length;

        // Initialize Tiles
        try {
            loadMap(Gdx.files.internal("tilemaps/blocks.txt"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void loadMap(FileHandle filename) throws IOException {
        for (int i = 0; i < 10; i++) {
            String line = (String) game.blockList.get(i);
            for (int j = 0; j < 6; j++){
                if(j < line.length()){
                    char ch = line.charAt(j);
                    tilemap[j][i] = new Block(j, i, Character.getNumericValue(ch));
                }
            }
        }
    }

    //Fill next row array with blocks (Called every time a new row is added)
    public void generateNextRow() {
        for (int a = 0; a < rows; a++) {
            Random r = new Random();
            int blockType = r.nextInt(6);

            if (blockType == 0)
                a--;
            else if(blockType == tilemap[a][0].blockType || (a > 1 && blockType == nextRow[a - 2].blockType))
                a--;
            else
                nextRow[a] = new Block(a, 0, blockType);
        }
    }

    public void update(PuzzleAttack game, Vector3 input, Animation<TextureRegion> cursor, float dt) {
        //update every block in the puzzle
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tilemap[i][j].update(i, j);
            }
        }

        //sees if there is any time left in the stop timer. If so, don't move blocks up.

        if (stopTimer >= 0) {
            if(stopTimer > 2)
                stopTimer = 2;
            stop = true;
            stopTimer -= dt;
        } else
            stop = false;

        //timers used to delay sounds so they aren't repeating so often that it sounds bad.
        if (dropSoundDelay > 0)
            dropSoundDelay -= dt;
        else
            dropSoundDelay = 0;

        if (switchSoundDelay > 0)
            switchSoundDelay -= dt;
        else
            switchSoundDelay = 0;

        //set touched block to null if the block is clearing or empty. This removes the touch cursor
        // from the screen
        if (touchedBlock != null && (touchedBlock.isClearing() || touchedBlock.getBlockType() == 0))
            touchedBlock = null;

        // drop blocks, move up, render matches.
        dropBlocks(dt);
        moveUp();
        renderMatch();

        //detect which block is touched
        if (Gdx.input.justTouched()) {
            keyboardCursor.setEnabled(false);
            Block block;
            Vector2 center = new Vector2();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    block = tilemap[i][j];
                    if (block.getBounds().contains(input.x, input.y) && !block.isClearing() && !block.isDropping() && block.getBlockType() != 0 && block.getBlockType() != 6) {
                        try {
                            touchedBlock = block;
                            touchSwitch = new Vector3(block.getBounds().getCenter(center), 0);
                        } catch (NullPointerException e) {
                        }
                    }
                }
            }
        }

        //detect if touch coordinates have moved far enough for block to be moved. If so, move it
        if (Gdx.input.isTouched() && touchedBlock != null) {
            elapsedTime += Gdx.graphics.getDeltaTime();
            game.batch.draw(cursor.getKeyFrame(elapsedTime, true), touchedBlock.getPosition().x, touchedBlock.getPosition().y + moveUp);

            // checks to see if where your touching is 50 pixels to the right of left of the original
            // place you touched. if so, switch the blocks and move the cursor to that position
            if (input.x >= touchSwitch.x + 50) {

                if (touchedBlock.getCoordinates().x < 5 && !tilemap[(int) (touchedBlock.getCoordinates().x) + 1][(int) (touchedBlock.getCoordinates().y)].isClearing()) {
                    switchBlocks(touchedBlock);
                    touchedBlock = tilemap[(int) (touchedBlock.getCoordinates().x) + 1][(int) (touchedBlock.getCoordinates().y)];
                    touchSwitch.x = touchSwitch.x + 100;
                }
            }
            if (input.x <= touchSwitch.x - 50) {
                if (touchedBlock.getCoordinates().x > 0 && !tilemap[(int) (touchedBlock.getCoordinates().x) - 1][(int) (touchedBlock.getCoordinates().y)].isClearing()) {
                    switchBlocks(tilemap[(int) (touchedBlock.getCoordinates().x) - 1][(int) (touchedBlock.getCoordinates().y)]);
                    touchedBlock = tilemap[(int) (touchedBlock.getCoordinates().x) - 1][(int) (touchedBlock.getCoordinates().y)];
                    touchSwitch.x = touchSwitch.x - 100;
                }
            }
        }

        //if the screen is not touched, make touchedBlock null (so it doesn't always display the cursor)
        if (!Gdx.input.isTouched() || (touchedBlock == null || touchedBlock.isDropping())) {
            try {
                touchedBlock = null;
            } catch (Exception e){}
            touchedBlock = null;
            touchSwitch = null;
        }

        //input listener for keyboard
        //TODO This is a long winded way of doing it, only check once to see if keyboard is enabled
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            repeatKeyPress = 0;
            if(!keyboardCursor.isEnabled())
                keyboardCursor.setEnabled(true);
            keyboardCursor.moveCursor(1);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            repeatKeyPress++;
            if(repeatKeyPress >= 15) {
                keyboardCursor.moveCursor(1);
                repeatKeyPress = 13;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            repeatKeyPress = 0;
            if(!keyboardCursor.isEnabled())
                keyboardCursor.setEnabled(true);
            keyboardCursor.moveCursor(2);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            repeatKeyPress++;
            if(repeatKeyPress >= 15) {
                keyboardCursor.moveCursor(2);
                repeatKeyPress = 13;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            repeatKeyPress = 0;
            if(!keyboardCursor.isEnabled())
                keyboardCursor.setEnabled(true);
            keyboardCursor.moveCursor(3);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            repeatKeyPress++;
            if(repeatKeyPress >= 15) {
                keyboardCursor.moveCursor(3);
                repeatKeyPress = 13;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            repeatKeyPress = 0;
            if(!keyboardCursor.isEnabled())
                keyboardCursor.setEnabled(true);
            keyboardCursor.moveCursor(4);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            repeatKeyPress++;
            if(repeatKeyPress >= 15) {
                keyboardCursor.moveCursor(4);
                repeatKeyPress = 13;
            }
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
            createTrash();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A)){
            for (int i = 0; i < 10; i++){
                System.out.println(tilemap[0][i].getBlockType() + "" + tilemap[1][i].getBlockType() + "" + tilemap[2][i].getBlockType() + "" + tilemap[3][i].getBlockType() + "" + tilemap[4][i].getBlockType() + "" + tilemap[5][i].getBlockType());
            }
        }

        //Separate switchBlocks method for keyboard
        // TODO possibly separate this code out, or even integrate it into the normal switchBlocks method
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            Block a = tilemap[(int)(keyboardCursor.getCoordinates().x)][(int)(keyboardCursor.getCoordinates().y)];
            Block b = tilemap[(int)(keyboardCursor.getCoordinates().x) + 1][(int)(keyboardCursor.getCoordinates().y)];

            a.setMultiTest(false);
            b.setMultiTest(false);

            if(!a.isClearing() && !b.isClearing() && a.getJustMoved() == 0 && b.getJustMoved() == 0 && a.getBlockType() !=6 && b.getBlockType() != 6) {

                if (a.getCoordinates().y != 0 && tilemap[(int) (a.getCoordinates().x)][(int) (a.getCoordinates().y) - 1].getBlockType() == 0) {
                    a.setJustMoved(10);
                    a.setIsDropping(true);
                }
                if (b.getCoordinates().y != 0 && tilemap[(int) (b.getCoordinates().x)][(int) (b.getCoordinates().y) - 1].getBlockType() == 0) {
                    b.setJustMoved(10);
                    b.setIsDropping(true);
                }

                int storeA = a.getBlockType();
                int storeB = b.getBlockType();

                a.setBlockType(storeB);
                b.setBlockType(storeA);

                if(!(storeB == 0))
                    a.setSwitched(80);
                if(!(storeA == 0))
                    b.setSwitched(-80);

                if (switchSoundDelay == 0 && game.soundOn) {
                    switchSound.setVolume(switchSound.play(), game.sound);
                    switchSoundDelay += 0.01;
                }
            }
        }

        //If Shift key is pressed, move blocks up faster
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            moveUp += 5;
            noMove = 0;

            if(moveUp >= 100)
                moveUp = 100;
        }
    }

    //Method to switch blocks
    public void switchBlocks(Block block) {

        //Since you only pass one block to switch blocks, you set Block b
        Block b = tilemap[(int) (block.getCoordinates().x) + 1][(int) (block.getCoordinates().y)];

        //both blocks no longer work for a multiplier
        block.setMultiTest(false);
        b.setMultiTest(false);

        // Store the types of block A and B
        int storeA = block.getBlockType();
        int storeB = b.getBlockType();

        //only switch iuf one of the blocks is not a trash block
        if(storeA != 6 && storeB != 6) {
            // set each block's type to the opposite block's
            block.setBlockType(storeB);
            b.setBlockType(storeA);

            //Switched is the timer used for animating the switch,
            // and if the block's type is not 0, animate it.
            if (!(storeB == 0))
                block.setSwitched(80);
            if (!(storeA == 0))
                b.setSwitched(-80);

            //Check to see if the blocks are of type 0, if they are not, check if there is a 0 underneath them.
            //If there is a 0 underneath them, set the block as dropping, and add 10 to the justMoved timer.
            // (If the justMoved timer is above 0, the block will not drop. This is to create the cartoon-like
            // illusion of the block being suspended in the air for a moment. The logic for that is not here, however.)
            if (block.getCoordinates().y != 0 && tilemap[(int) (block.getCoordinates().x)][(int) (block.getCoordinates().y) - 1].getBlockType() == 0) {
                block.setJustMoved(10);
                block.setIsDropping(true);
            }
            if (b.getCoordinates().y != 0 && tilemap[(int) (b.getCoordinates().x)][(int) (b.getCoordinates().y) - 1].getBlockType() == 0) {
                b.setJustMoved(10);
                b.setIsDropping(true);
            }

            //This plays the switching blocks sound.
            //switchSoundDelay is the timer for delaying the switching sound, so the sound can't be
            //played to often. If switchSoundDelay is more than 0, do not play the sound.
            if (switchSoundDelay == 0 && game.soundOn) {
                switchSound.setVolume(switchSound.play(), game.sound);
                switchSoundDelay += 0.01;
            }
        }
    }

    //moveUp() Gets called every update to move blocks up
    ////noMove increments until it hits what we set for moveSpeed, then increments moveUp.
    ////moveUp is the number that we slowly increment so the render method can add it
    ////to the puzzles X coordinate to make it move up
    public void moveUp() {
        boolean blockInTopRow = false;

        for (int i = 0; i < rows; i++){
            if(tilemap[i][9].getBlockType() != 0){
                blockInTopRow = true;
                break;
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 9; j > -1; j--) {
                if(!(moveUp == 100 && blockInTopRow))
                    tilemap[i][j].moveBounds((int)moveUp);
            }
        }
        scrollCount += panUp;

        if(moveUp >= 100)
            moveUp = 100;

        //Notice the extra set of parenthesis for logic. IF (stop is false OR shift is pressed) OR scrollcount > 120
        if (((!stop|| Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) || scrollCount > 120) &&!(moveUp >= 100 && blockInTopRow)) {

            //scrollCount is for moving the blocks up with touch. We have a buffer of 120 pixels before
            //panning up actually does anything
            if (scrollCount > 120) {
                touchedBlock = null;
                moveUp += (panUp / 3);
                panUp = 0;
            }

            if (!(moveUp >= 100 && blockInTopRow)) {
                switch (level){
                    case 1:
                        moveUp += 0.25;
                        break;
                    case 2:
                        moveUp += 0.35;
                        break;
                    case 3:
                        moveUp += 0.45;
                        break;
                    case 4:
                        moveUp += 0.55;
                        break;
                    case 5:
                        moveUp += 0.75;
                        moveTimer = 0;
                        break;
                    case 6:
                        moveUp += 0.85;
                        moveTimer = 0;
                        break;
                    case 7:
                        moveUp += 1.15;
                        moveTimer = 0;
                        break;
                    case 8:
                        moveUp += 1.35;
                        moveTimer = 0;
                        break;
                    case 9:
                        moveUp += 1.65;
                        moveTimer = 0;
                        break;
                }
            }

            if (moveUp >= 100 && !blockInTopRow) {
                //if using the keyboard, move the cursor up 1 row with the blocks to keep it on the
                // same blocks
                if(keyboardCursor.isEnabled())
                    keyboardCursor.moveCursor(3);

                //loop through every block and move them up a row in the puzzle array
                for (int i = 0; i < rows; i++) {
                    for (int j = 9; j > -1; j--) {
                        if (j == 0) {
                            tilemap[i][0] = nextRow[i];
                        } else {
                            tilemap[i][j] = tilemap[i][j - 1];
                        }
                    }
                }
                //If the screen is touched, and is not in the top row, move up the touch cursor
                //We do a try catch because touchedBlock can return null, so it will crash if we
                // have nothing to check
                try {
                    if (Gdx.input.isTouched() && touchedBlock.getCoordinates().y < 9)
                        touchedBlock = tilemap[(int) touchedBlock.getCoordinates().x][(int) touchedBlock.getCoordinates().y + 1];
                } catch (NullPointerException e) {
                }

                // set moveUp back to 0 so we can start the process over again
                moveUp = 0;
                generateNextRow();
            } else if (blockInTopRow){

            }

            //update every block in the puzzle array. This includes moving their hit boxes for touch
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    tilemap[i][j].update(i, j);
                }
            }
            noMove--;

            gameOverTimer = 0.5f;
        } else if(moveUp >= 100 && blockInTopRow){
            if(gameOverTimer < 2.5f)
                gameOverTimer += Gdx.graphics.getDeltaTime();
            else {
                playScreen.setState(PlayScreen.State.GAMEOVER);
                gameOverTimer = 0;
            }
            /*gameOverTimer = 2.5f;*/
        }

        if(moveUp >= 100)
            moveUp = 100;
    }

    //Immediately starts the dropblocks thread. Most logic for dropping is there
    public void dropBlocks(float dt) {
        Runnable i = new dropThread(game, this, manager, dt);
        new Thread(i).start();
    }

    //The one and only clearBlocks Method. Checks to see if there are blocks to clear, then passes
    //blockArray to the clearThread to actually clear them
    public void clearBlocks(float dt) {
        List<Block> clearBlocks = new ArrayList<Block>();
        blockArray = new Block[0];
        List<Block> blocks = new ArrayList<Block>();
        Block currentBlock;

        //Loop through entire playfield to check for blocks
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // skip the block if it's already clearing or dropping or type 6 (trash block)
                if (!tilemap[i][j].isClearing() && !tilemap[i][j].isDropping() && tilemap[i][j].getBlockType() != 6) {
                    currentBlock = tilemap[i][j];

                    //check to the right of the block to see any matches
                    for (int b = 0; i + b < 6; b++) {
                        if (!tilemap[i + b][j].isDropping() && currentBlock.getBlockType() != 0 && currentBlock.getBlockType() == tilemap[i + b][j].getBlockType() && !tilemap[i + b][j].isClearing()) {
                            blocks.add(tilemap[i + b][j]);
                        } else{
                            break;
                        }
                    }

                    //if we find at least 3 blocks of the same type beside each other, add them to
                    // the clearBlocks array
                    if (blocks.size() > 2) {
                        clearBlocks.addAll(blocks);
                    }

                    //remove all blocks from the blocks[] array so we can reuse it
                    blocks.removeAll(blocks);

                    //Same as above, only checking above the block to see any matches (but not top row)
                    if (j < columns) {
                        for (int b = 0; j + b < 10; b++) {
                            if (!tilemap[i][j + b].isDropping() && currentBlock.getBlockType() != 0 && currentBlock.getBlockType() == tilemap[i][j + b].getBlockType() && !tilemap[i][j + b].isClearing()) {
                                blocks.add(tilemap[i][j + b]);
                            } else
                                break;
                        }

                        if (blocks.size() > 2) {
                            clearBlocks.addAll(blocks);
                        }
                        blocks.removeAll(blocks);
                    }
                }
            }
        }

        //At this point, clearBlocks contains all the blocks that we've identified as matches
        // so we convert it to an array
        blockArray = clearBlocks.toArray(new Block[clearBlocks.size()]);
        if (blockArray.length > 2) {
            //Passes blockArray to the clearBlocks thread to clear
            clearThread = new clearThread(this, blockArray, manager, dt, game, playScreen);
            new Thread(clearThread).start();
        }
        else if(!checkDropping()){
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    getTilemap(i, j).setMultiTest(false);
                }
            }
        }
    }

    //This method checks the renderMatch[] array, and if there are any Matches, displays them
    //This happens because the match logic is located in a separate thread (clearThread) and you
    //can only render things to the screen on the main thread. So we pass the matches to the renderMatch
    // array in the main thread and render them here.
    public void renderMatch(){
        if(renderMatch.size() > 0){

            //Loop through every Match in the array
            for(int i = 0; i < renderMatch.size(); i++) {
                Match thisMatch = renderMatch.get(i);

                // only render the match if it's more than 3 (we don't display matches for 3s)
                if(renderMatch.get(i).getCount() != 3) {
                    game.batch.draw(combo, thisMatch.getX() + 40, thisMatch.getY() + 50);
                    font.draw(game.batch, Integer.toString(renderMatch.get(i).getCount()), thisMatch.getX() + 70, thisMatch.getY() + 110);
                }
                //if this match is part of a multiplier, it will render it here
                if(thisMatch.getMultiplier() > 1){
                    game.batch.draw(combo,thisMatch.getX() + 40, thisMatch.getY() - 10);
                    font.draw(game.batch, "x" + Integer.toString(thisMatch.getMultiplier()), thisMatch.getX() + 57, thisMatch.getY() + 50);
                }
                //each match has a timer property that runs out eventually.
                // when the timer hits 0 (or less than 0, since we're subtracting DeltaTime)
                // it's removed from the array
                if (renderMatch.get(i).getTimer() < 0) {
                    renderMatch.remove(i);
                }

                //subtract DT from this matches timer.
                thisMatch.setTimer(thisMatch.getTimer() - Gdx.graphics.getDeltaTime());
            }
        }
    }

    public void createTrash(){
        for (int i = 0; i < 4; i++) {
            tilemap[i][9].makeTrash(4, 1);
            if(i == 0)
                tilemap[i][9].setFirstTrash(true);
        }
    }

    public boolean checkDropping(){
        boolean falling = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(getTilemap(i, j).getBlockType() == 6)
                    continue;
                else if(getTilemap(i, j).isDropping())
                    falling = true;
            }
        }
        return falling;
    }

    public boolean checkClearing(){
        boolean clearing = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(getTilemap(i, j).isClearing())
                    clearing = true;
            }
        }
        return clearing;
    }

    public Block getTilemap(int i, int j) {
        return tilemap[i][j];
    }

    public Block getNextRow(int i) {
        return nextRow[i];
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMoveUp() {
        return (int)moveUp;
    }

    public void addStopTimer(float addTime) {
        this.stopTimer += addTime;
    }

    public void setPanUp(int panUp) {
        this.panUp = panUp;
    }

    public void setScrollCount(int scrollCount) {
        this.scrollCount = scrollCount;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public float getGameOverTimer() {
        return gameOverTimer;
    }
}
