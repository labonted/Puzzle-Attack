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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.PuzzleAttack;

public class DifficultyScreen implements Screen {
    private PuzzleAttack game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private AssetManager manager;
    private Stage stage;
    final Slider difficultySlider;
    private Skin skin;
    private BitmapFont font, titleFont;

    private Texture background;

    public DifficultyScreen(final PuzzleAttack game, AssetManager assMan){
        this.game = game;

        manager = assMan;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        background = manager.get("TiledBack.jpg", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        //create font
        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        titleFont = new BitmapFont(Gdx.files.internal("fonts/block/block.fnt"), false);

        skin.add("default",font);

        //button skin settings
        Pixmap pixmap = new Pixmap(500, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        //Configure textbutton style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.YELLOW);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);
        skin.add("Options", textButtonStyle);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton playButton = new TextButton("Start!",textButtonStyle);
        playButton.setPosition(120, 550);
        stage.addActor(playButton);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton backButton = new TextButton("Back",textButtonStyle);
        backButton.setPosition(120, 250);
        stage.addActor(backButton);

        //Event listener for button
        backButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                game.setScreen( new MenuScreen(game, manager));
            }
        });

        Texture knob = new Texture("SmallKnob.png");
        knob.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);;

        skin.add("knob", knob);

        Texture slideBar = new Texture("slidebar.png");
        slideBar.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);;

        skin.add("slider", slideBar);
        //configure slider Style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.newDrawable("knob");
        sliderStyle.background = skin.newDrawable("slider");

        difficultySlider = new Slider(1, 9, 1, false, sliderStyle);
        difficultySlider.setWidth(480);


        difficultySlider.setPosition(130, 780);
        stage.addActor(difficultySlider);
        difficultySlider.setValue(5);
        difficultySlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                //might not need this as I just need to grab the value when I switch to the play screen
            }
        });

        //Event listener for button
        playButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                game.setScreen( new PlayScreen(game, manager, (int)difficultySlider.getValue()));
            }
        });

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
        game.batch.draw(background, (int)(gameCam.viewportWidth/2) * -1 , (int)(gameCam.viewportHeight/2) * -1, (int)(gameCam.viewportWidth), (int)(gameCam.viewportHeight), (int)(gameCam.viewportWidth * 1.5), (int)(gameCam.viewportHeight * 1.5));
        titleFont.draw(game.batch, "LeVel", 187, 1200);
        font.draw(game.batch, "Difficulty", 130, 920);
        font.draw(game.batch, Integer.toString((int)difficultySlider.getValue()), 585, 917);
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

    }
}
