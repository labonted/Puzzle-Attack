package com.puzzleattack.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.Input.Cursor;
import com.puzzleattack.game.Puzzle;
import com.puzzleattack.game.PuzzleAttack;
import com.puzzleattack.game.Scenes.Hud;


/**
 * Created by Dylan on 2016-01-03.
 */
public class PlayScreen implements Screen {
    private PuzzleAttack game;
    private static Texture background, hudImage, hudborder, playField, backgroundTexture;
    public static Texture red, green, blue, purple, orange, darkRed, darkGreen, darkBlue, darkPurple, darkOrange, trash;
    private Pixmap borderBackground;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Puzzle puzzle;
    private Hud hud;
    private BitmapFont font;
    private AssetManager manager;
    private Cursor keyboardCursor;

    //State for pausing
    private State state;

    //for music
    private Music music;
    private Sound pause, unpause;

    private int score;

    private float elapsedTime;
    private int dropTime = 1;
    private float freezeDrop = 0;

    //for start timer
    private float startTimer = 3.99f;
    private String startTimerString;

    private int borderY, borderMove;

    private boolean shakeRight, noShake;
    //cursor animation
    private Animation<TextureRegion> cursor;
    private Animation<TextureRegion> keycursor;
    private Animation<TextureRegion> clear;
    private Animation<TextureRegion> blueDrop;
    private Animation<TextureRegion> greenDrop;
    private Animation<TextureRegion> orangeDrop;
    private Animation<TextureRegion> purpleDrop;
    private Animation<TextureRegion> redDrop;

    public PlayScreen(PuzzleAttack game, AssetManager assMan, int level){
        // assign local variables
        this.game = game;
        this.manager = assMan;
        keyboardCursor = new Cursor();
        puzzle = new Puzzle(manager, game, keyboardCursor, this, level);

        //Set State to RUN initially
        state = State.START;

        //Playfield Textures
        backgroundTexture = new Texture("borderColor.png");
        background = new Texture("back15.jpg");
        hudborder= new Texture("border.png");
        playField = new Texture("Playfield.png");
        hudImage = new Texture("Hud/HUD1.png");

        borderMove = 0;
        borderY = -2500;

        backgroundTexture.getTextureData().prepare();
        borderBackground = backgroundTexture.getTextureData().consumePixmap();

        for(int x = 0; x < borderBackground.getWidth(); x++){
            for(int y = 0; y < borderBackground.getHeight(); y++) {
                if(borderBackground.getPixel(x, y) == Color.rgba8888(Color.BLACK)){
                    borderBackground.drawPixel(x, y, Color.rgba8888(game.red, game.green, game.blue,1));
                }
            }
        }

        backgroundTexture = new Texture(borderBackground);

        //create HUD object
        hud = new Hud(game.batch);

        //set score to 0
        score = 0;

        //variable used in cursor animation. Updates in update method below
        elapsedTime = 0;

        //used to shake scree at the end
        shakeRight = false;
        noShake = false;

        //Create font
        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        //Assign textures for block set
        red = new Texture("blocks/Red.png");
        green = new Texture("blocks/Green.png");
        blue = new Texture("blocks/Blue.png");
        purple = new Texture("blocks/Purple.png");
        orange = new Texture("blocks/Orange.png");

        darkRed = new Texture("blocks/RedDark.png");
        darkGreen = new Texture("blocks/GreenDark.png");
        darkBlue = new Texture("blocks/BlueDark.png");
        darkPurple = new Texture("blocks/PurpleDark.png");
        darkOrange = new Texture("blocks/OrangeDark.png");

        trash = new Texture("blocks/trash.png");

        //texture filters
        red.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        green.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        blue.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        purple.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        orange.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        darkRed.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        darkGreen.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        darkBlue.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        darkPurple.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        darkOrange.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //Dropped blocks animations
        TextureAtlas blueAtlas = new TextureAtlas(Gdx.files.internal("drop/blueDrop.pack"));
        blueDrop = new Animation<TextureRegion>(1/16f, blueAtlas.getRegions());

        TextureAtlas greenAtlas = new TextureAtlas(Gdx.files.internal("drop/greenDrop.pack"));
        greenDrop = new Animation<TextureRegion>(1/16f, greenAtlas.getRegions());

        TextureAtlas orangeAtlas = new TextureAtlas(Gdx.files.internal("drop/orangeDrop.pack"));
        orangeDrop = new Animation<TextureRegion>(1/16f, orangeAtlas.getRegions());

        TextureAtlas purpleAtlas = new TextureAtlas(Gdx.files.internal("drop/purpleDrop.pack"));
        purpleDrop = new Animation<TextureRegion>(1/16f, purpleAtlas.getRegions());

        TextureAtlas redAtlas = new TextureAtlas(Gdx.files.internal("drop/redDrop.pack"));
        redDrop = new Animation<TextureRegion>(1/16f, redAtlas.getRegions());

        //make cursor animation
        TextureAtlas cursorAtlas = new TextureAtlas(Gdx.files.internal("cursor/cursor.atlas"));
        cursor = new Animation<TextureRegion>(1/10f, cursorAtlas.getRegions());

        //Keyboard cursor animation
        TextureAtlas keycursorAtlas= new TextureAtlas(Gdx.files.internal("cursor/keycursor.pack"));
        keycursor= new Animation<TextureRegion>(1/10f, keycursorAtlas.getRegions());

        //make flashing animation
        TextureAtlas clearAtlas = new TextureAtlas(Gdx.files.internal("flashing/flashing.pack"));
        clear = new Animation<TextureRegion>(1/20f, clearAtlas.getRegions());

        //sounds
        pause = manager.get("audio/sounds/Pause.wav", Sound.class);
        unpause = manager.get("audio/sounds/Unpause.wav", Sound.class);

        //Start Music
        /*music = manager.get("audio/music/test_basic.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(game.volume);*/

        //generate initial next row
        puzzle.generateNextRow();

        //screen settings
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        gamePort = new ExtendViewport(PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT, gameCam);
        gameCam.position.set(PuzzleAttack.V_WIDTH/2, PuzzleAttack.V_HEIGHT/2, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        //Render Playfield
        game.batch.draw(background, -850, 0, 2560, 1440);
        game.batch.draw(hudImage, 0, 1110 + borderY);
        game.batch.draw(backgroundTexture, 0, borderY);
        game.batch.draw(hudborder, 0, borderY);
        game.batch.draw(playField, 60, borderY);

        //game.batch.draw(border, 0, 0);

        if(state == State.GAMEOVER && freezeDrop > 0.5)
            font.draw(game.batch, "Game Over", 257, 600);

        if(state == State.RUN || state == State.GAMEOVER || state == State.START) {
            //set up shake
            int shake;

            if(puzzle.getGameOverTimer() > 0.5 && shakeRight && !noShake){
                shake = (int)(puzzle.getGameOverTimer() * 2);
                shakeRight = false;
                noShake = true;
            } else if(puzzle.getGameOverTimer() > 0.5 && !shakeRight && !noShake){
                shake = 0 - (int)(puzzle.getGameOverTimer() * 3);
                shakeRight = true;
                noShake = true;
            }else {
                shake = 0;
                noShake = false;
            }

            // Loop though block array to display blocks
            for (int i = 0; i < puzzle.rows; i++) {
                for (int j = 0; j < puzzle.columns; j++) {

                    /*int mod_i;
                    int mod_j;

                    //mod _i and mod_j are used to change i and j to properly represent coordinates and
                    //position the blocks in the playfield
                    if(puzzle.getGameOverTimer() > 0 && shakeRight){
                        mod_i = (100 * i) + 61 + puzzle.getTilemap(i, j).getSwitched() + ((int)puzzle.getGameOverTimer() * 3);
                        mod_j = (100 * j) + puzzle.getMoveUp();
                        shakeRight = false;
                    } else if(puzzle.getGameOverTimer() > 0 && !shakeRight){
                        mod_i = (100 * i) + 61 + puzzle.getTilemap(i, j).getSwitched() - ((int)puzzle.getGameOverTimer() * 3);
                        mod_j = (100 * j) + puzzle.getMoveUp();
                        shakeRight = true;
                    }else{
                        mod_i = (100 * i) + 61 + puzzle.getTilemap(i, j).getSwitched();
                        mod_j = (100 * j) + puzzle.getMoveUp();
                    }*/


                    int mod_i = (100 * i) + 61 + puzzle.getTilemap(i, j).getSwitched() + shake;
                    int mod_j = (100 * j) + puzzle.getMoveUp();

                    //If the block is switching, will use the switched variable (in the block object)
                    // to render it to the correct place on screen. Will only bother to go through
                    // this if switched is more or less than 0
                    if (puzzle.getTilemap(i, j).getSwitched() > 0) {
                        puzzle.getTilemap(i, j).setSwitched(puzzle.getTilemap(i, j).getSwitched() - 40);
                        if (puzzle.getTilemap(i, j).getSwitched() == 1) {
                            puzzle.getTilemap(i, j).setSwitched(0);
                        }
                    } else if (puzzle.getTilemap(i, j).getSwitched() < 0) {
                        puzzle.getTilemap(i, j).setSwitched(puzzle.getTilemap(i, j).getSwitched() + 40);
                        if (puzzle.getTilemap(i, j).getSwitched() == -1) {
                            puzzle.getTilemap(i, j).setSwitched(0);
                        }
                    }

                    //Picks the colour of block to render to the screen based on its blockType. If the
                    // block was just dropped, will display the reactionary animation
                    switch (puzzle.getTilemap(i, j).getBlockType()) {
                        case 1:
                            if (puzzle.getTilemap(i, j).getDropped() > 0 && puzzle.getTilemap(i, j).getDropped() < 5)
                                game.batch.draw(redDrop.getKeyFrame(puzzle.getTilemap(i, j).getDropped(), false), mod_i, mod_j);
                            else {
                                puzzle.getTilemap(i, j).setDropped(0);
                                game.batch.draw(red, mod_i, mod_j);
                            }
                            break;
                        case 2:
                            if (puzzle.getTilemap(i, j).getDropped() > 0 && puzzle.getTilemap(i, j).getDropped() < 5)
                                game.batch.draw(greenDrop.getKeyFrame(puzzle.getTilemap(i, j).getDropped(), false), mod_i, mod_j);
                            else {
                                puzzle.getTilemap(i, j).setDropped(0);
                                game.batch.draw(green, mod_i, mod_j);
                            }
                            break;
                        case 3:
                            if (puzzle.getTilemap(i, j).getDropped() > 0 && puzzle.getTilemap(i, j).getDropped() < 5)
                                game.batch.draw(blueDrop.getKeyFrame(puzzle.getTilemap(i, j).getDropped(), false), mod_i, mod_j);
                            else {
                                puzzle.getTilemap(i, j).setDropped(0);
                                game.batch.draw(blue, mod_i, mod_j);
                            }
                            break;
                        case 4:
                            if (puzzle.getTilemap(i, j).getDropped() > 0 && puzzle.getTilemap(i, j).getDropped() < 5)
                                game.batch.draw(purpleDrop.getKeyFrame(puzzle.getTilemap(i, j).getDropped(), false), mod_i, mod_j);
                            else {
                                puzzle.getTilemap(i, j).setDropped(0);
                                game.batch.draw(purple, mod_i, mod_j);
                            }
                            break;
                        case 5:
                            if (puzzle.getTilemap(i, j).getDropped() > 0 && puzzle.getTilemap(i, j).getDropped() < 5) {
                                game.batch.draw(orangeDrop.getKeyFrame(puzzle.getTilemap(i, j).getDropped(), false), mod_i, mod_j);
                            } else {
                                puzzle.getTilemap(i, j).setDropped(0);
                                game.batch.draw(orange, mod_i, mod_j);
                            }
                            break;
                        case 6:
                            game.batch.draw(trash, mod_i, mod_j);
                            break;
                    }

                    // if block is currently clearing, will display clearing animation over top of it
                    if (puzzle.getTilemap(i, j).isClearing() && puzzle.getTilemap(i, j).getBlockType() != 0)
                        game.batch.draw(clear.getKeyFrame(elapsedTime, true), mod_i, mod_j);

                    // MULTI TEST DEBUG TEXTURE
                    /*if (puzzle.getTilemap(i, j).isMultiTest() && puzzle.getTilemap(i, j).getBlockType() != 0)
                        game.batch.draw(trash, mod_i, mod_j);*/

                    //if the keyboard cursor is being used, display it on screen.
                    if (keyboardCursor.isEnabled())
                        game.batch.draw(keycursor.getKeyFrame(elapsedTime, true), (keyboardCursor.getCoordinates().x * 100) + 61, (keyboardCursor.getCoordinates().y * 100) + puzzle.getMoveUp());
                }
            }

            //Separate loop for displaying next row (dark blocks)
            for (int i = 0; i < puzzle.rows; i++) {
                int mod_i = (100 * i) + 61 + shake;
                int j = puzzle.getMoveUp() - 100;

                switch (puzzle.getNextRow(i).getBlockType()) {
                    case 1:
                        game.batch.draw(darkRed, mod_i, j);
                        break;
                    case 2:
                        game.batch.draw(darkGreen, mod_i, j);
                        break;
                    case 3:
                        game.batch.draw(darkBlue, mod_i, j);
                        break;
                    case 4:
                        game.batch.draw(darkPurple, mod_i, j);
                        break;
                    case 5:
                        game.batch.draw(darkOrange, mod_i, j);
                        break;
                }
            }
        }
        else
            font.draw(game.batch, "Paused", 287, 600);

        if (state == State.START && startTimer < 1 && puzzle.moveUp == 0)
            font.draw(game.batch, "GO!", 335, 600);
        else if(state == State.START && startTimer > 0 && puzzle.moveUp == 0){
            startTimerString = Integer.toString((int)startTimer);
            font.draw(game.batch, startTimerString, 352, 600);
        }

        font.draw(game.batch, Float.toString(puzzle.getGameOverTimer()), 800, 1200);

        this.update(delta);

        game.batch.end();

        //set our batch to draw what the HUD sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
    }

    public void update(float delta)
    {
        //update elapsed time for cursor animation

        // detects touch input
        Vector3 input = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        gameCam.unproject(input);

        //draw HUD
        hud.draw(puzzle, game.batch, delta, borderY);

        //if game isn't paused update hud and puzzle objects
        switch (state){
            case START:
                //puzzle.moveUp = -1000;
                if (borderY < 0) {
                    borderMove += 1;
                    borderY += borderMove;
                    if(borderY > 0)
                        borderY = 0;
                }
                else
                    borderY = 0;

                if(borderY >= 0 && puzzle.getMoveUp() <= 0){
                    puzzle.moveUp += 10;
                    if(puzzle.moveUp > 0)
                        puzzle.moveUp = 0;
                }

                if (startTimer > 0 && puzzle.moveUp >= 0)
                    startTimer -= delta;
                else if(startTimer <=0)
                    state = state.RUN;
                    break;
            case RUN:
                /*if(!music.isPlaying() && game.volumeOn)
                    music.play();*/
                elapsedTime += delta;
                hud.update(puzzle, game.batch, delta);
                puzzle.update(game, input, cursor, delta);
                break;
            case PAUSE:
                pause.play(game.sound);
                music.setVolume((float)(game.volume * 0.1));
                state = State.STOP;
                break;
            case RESUME:
                unpause.play(game.sound);
                music.setVolume(game.volume);
                state = State.RUN;
                break;
            case STOP:
                break;
            case GAMEOVER:
                if(freezeDrop >= 4) {
                    //music.stop();
                    game.setScreen(new GameOverScreen(game, manager, backgroundTexture, hud.getScore(), hud.getWorldTimer()));
                }
                if(puzzle.moveUp > -1200 && freezeDrop > 0.5) {
                    puzzle.moveUp -= dropTime;
                    dropTime += 3;

                }
                freezeDrop += delta;
                break;

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            if(getState() == State.RUN){
                setState(State.PAUSE);
            }
            else if(getState() == State.STOP){
               setState(State.RESUME);
            }
        }
    }

    public enum State{
        START,
        PAUSE,
        RUN,
        RESUME,
        STOP,
        GAMEOVER
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {
        state = State.PAUSE;
    }

    @Override
    public void resume() {
        //state = State.RESUME;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.dispose();
        font.dispose();
        manager.dispose();
        music.dispose();
        pause.dispose();
        unpause.dispose();
        this.dispose();
        borderBackground.dispose();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
