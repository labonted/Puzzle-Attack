package com.puzzleattack.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.PuzzleAttack;

public class MenuScreen implements Screen {

    private PuzzleAttack game;
    private AssetManager manager;
    private Stage stage;
    private Skin skin;
    private BitmapFont font, titleFont;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private TextureRegion backTile1, backTile2;
    //uncomment for music, along with below block
    //private Music music;
    boolean dark, newRow = true;

    private Texture background;

    public MenuScreen(final PuzzleAttack game, AssetManager assMan) {
        this.game = game;
        manager = assMan;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        //Screen settings
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        gamePort = new ExtendViewport(PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT, gameCam);
        gameCam.position.set(PuzzleAttack.V_WIDTH/2, PuzzleAttack.V_HEIGHT/2, 0);
        stage.setViewport(gamePort);

        //uncomment for music, the mp3 file must be in assets
        /*if(game.volumeOn) {
            music = manager.get("audio/music/title_screen.mp3", Music.class);
            music.setVolume(game.volume);
            music.setLooping(true);
            music.play();
        }*/

        background = manager.get("TiledBack.jpg", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(500, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        //create font
        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        titleFont = new BitmapFont(Gdx.files.internal("fonts/block/block.fnt"), false);

        skin.add("default",font);

        //Configure a TextButton
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.YELLOW);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
        skin.add("Options", textButtonStyle);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton textButton = new TextButton("PLAY",textButtonStyle);
        final TextButton optionsButton = new TextButton("OPTIONS",textButtonStyle);
        textButton.setPosition(120, 550);
        optionsButton.setPosition(120, 250);
        stage.addActor(textButton);
        stage.addActor(optionsButton);

        //Event listener for button
        textButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                /*if(game.volumeOn)
                    music.stop();*/
                game.setScreen( new DifficultyScreen(game, manager));
            }
        });
        optionsButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                /*if(game.volumeOn)
                    music.stop();*/
                game.setScreen( new OptionScreen(game, manager));
            }
        });
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        game.batch.draw(background, (int)(gameCam.viewportWidth/2) * -1 , (int)(gameCam.viewportHeight/2) * -1, (int)(gameCam.viewportWidth), (int)(gameCam.viewportHeight), (int)(gameCam.viewportWidth * 1.5), (int)(gameCam.viewportHeight * 1.5));

        titleFont.draw(game.batch, "PUzzLE", 140, 1200);
        titleFont.draw(game.batch, "AttACK", 140, 1000);
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void dispose () {
        stage.dispose();
        skin.dispose();
        font.dispose();
        game.dispose();
        this.dispose();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
}
