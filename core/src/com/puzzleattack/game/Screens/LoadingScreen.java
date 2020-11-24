package com.puzzleattack.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.PuzzleAttack;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadingScreen implements Screen{

    private AssetManager manager;
    private PuzzleAttack game;
    private int progress;
    private Texture background;
    private BitmapFont font;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private ArrayList blockList;

    public LoadingScreen(AssetManager assMan, PuzzleAttack game){
        this.game = game;
        manager = assMan;

        try {
            blockList = loadMap(Gdx.files.internal("tilemaps/blocks.txt"), 3);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /*manager.load("audio/music/test_basic.ogg", Music.class);
        manager.load("audio/music/test_music.wav", Music.class);
        manager.load("audio/music/spider.mp3", Music.class);
        manager.load("audio/music/title_screen.mp3", Music.class);
        manager.load("audio/music/options.mp3", Music.class);*/
        manager.load("audio/sounds/dropping.wav", Sound.class);
        manager.load("audio/sounds/switch_blocks.wav", Sound.class);
        manager.load("audio/sounds/Pause.wav", Sound.class);
        manager.load("audio/sounds/Unpause.wav", Sound.class);
        manager.load("audio/sounds/block_disappear.ogg", Sound.class);
        manager.load("audio/sounds/combo.ogg", Sound.class);
        manager.load("border.png", Texture.class);
        manager.load("borderColor.png", Texture.class);
        manager.load("pipe.png", Texture.class);
        manager.load("slidebar.png", Texture.class);
        manager.load("TiledBack.jpg", Texture.class);

        background = new Texture("TiledBack.jpg");
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        gamePort = new ExtendViewport(PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT, gameCam);
        gameCam.position.set(PuzzleAttack.V_WIDTH/2, PuzzleAttack.V_HEIGHT/2, 0);

    }

    public void render (float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        progress = (int)(manager.getProgress() * 100);

        game.batch.draw(background, (int)(gameCam.viewportWidth/2) * -1 , (int)(gameCam.viewportHeight/2) * -1, (int)(gameCam.viewportWidth), (int)(gameCam.viewportHeight), (int)(gameCam.viewportWidth * 1.5), (int)(gameCam.viewportHeight * 1.5));
        font.draw(game.batch, "LOADING", 140, 1200);
        font.draw(game.batch,Integer.toString(progress) + "%", 140, 1000);
        if(manager.update()) {
            game.setScreen(new MenuScreen(game, manager));
        }
        game.batch.end();

    }

    private ArrayList loadMap(FileHandle filename, int mapNum) throws IOException {
        ArrayList lines = new ArrayList();
        int mapCount = 0;

        BufferedReader reader = new BufferedReader(Gdx.files.internal("tilemaps/blocks.txt").reader());
        while (true) {
            String line = reader.readLine();

            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            if(mapNum == mapCount){
                lines.add(line);
            } else if(line.startsWith("#"))
                mapCount += 1;

        }
        return lines;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public ArrayList getBlockList() {
        return blockList;
    }
}
