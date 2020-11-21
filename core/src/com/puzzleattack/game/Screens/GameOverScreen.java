package com.puzzleattack.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.Puzzle;
import com.puzzleattack.game.PuzzleAttack;

import java.util.Locale;

/**
 * Created by dlabonte on 24/11/2016.
 */

public class GameOverScreen implements Screen {
    private PuzzleAttack game;
    private AssetManager manager;

    private static Texture background, hudImage, hudborder, playField, backgroundTexture;

    private BitmapFont font;

    private OrthographicCamera gameCam;
    private Viewport gamePort;

    //button shit
    private Skin skin;
    private Stage stage;

    //stats
    private Integer score, time;

    public GameOverScreen(PuzzleAttack puzGame, AssetManager assMan, Texture borderTexture, Integer score, Integer time) {
        game = puzGame;
        manager = assMan;

        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        //textures
        backgroundTexture = borderTexture;
        background = new Texture("background.png");
        hudborder= new Texture("border.png");
        playField = new Texture("Playfield.png");
        hudImage = new Texture("Hud/HUD1.png");


        // button shit
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        // Generate a texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(500, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));
        skin.add("default",font);

        //Configure a TextButton
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.YELLOW);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton textButton = new TextButton("Done",textButtonStyle);
        textButton.setPosition(120, 200);
        stage.addActor(textButton);

        //Event listener for button
        textButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                game.setScreen( new MenuScreen(game, manager));
            }
        });

        this.time = time;
        this.score = score;

        //screen settings
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        gamePort = new ExtendViewport(PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT, gameCam);
        gameCam.position.set(PuzzleAttack.V_WIDTH/2, PuzzleAttack.V_HEIGHT/2, 0);
        stage.setViewport(gamePort);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        //Render Playfield
        game.batch.draw(background, -850, 0, 2560, 1440);
        game.batch.draw(hudImage, 0, 1110);
        game.batch.draw(backgroundTexture, 0, 0);
        game.batch.draw(hudborder, 0, 0);
        game.batch.draw(playField, 60, 0);

        font.draw(game.batch, "Score: " + score.toString(), 250, 700);
        font.draw(game.batch, String.format(Locale.CANADA, "%02d:%02d", time / 60, time % 60), 370, 900);
        font.draw(game.batch, "Time: ", 230, 900);

        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

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

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        font.dispose();
        game.dispose();
        this.dispose();
    }
}
