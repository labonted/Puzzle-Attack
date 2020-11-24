package com.puzzleattack.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.PuzzleAttack;

public class OptionScreen implements Screen {

    private PuzzleAttack game;
    private AssetManager manager;
    private Stage stage;
    private Skin skin;
    private BitmapFont font, titleFont;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Music music;
    private Sound testSound;
    private Float soundTimer;

    private static Texture background;

    public OptionScreen(final PuzzleAttack game, AssetManager assMan) {
        this.game = game;
        manager = assMan;

        ///////TEST MUSIC
       /* music = manager.get("audio/music/options.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(game.volume);
        if(game.volumeOn)
            music.play();*/

        testSound = manager.get("audio/sounds/block_disappear.ogg", Sound.class);
        soundTimer = 0.15f;

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        background = manager.get("TiledBack.jpg", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(500, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        Pixmap knobMap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        knobMap.setColor(Color.BLUE);
        knobMap.fill();

        skin.add("knob", new Texture(knobMap));

        Pixmap sliderMap = new Pixmap(3000, 50, Pixmap.Format.RGBA8888);
        sliderMap.setColor(Color.BLUE);
        sliderMap.fill();

        skin.add("slider", new Texture(sliderMap));

        //create font
        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        titleFont = new BitmapFont(Gdx.files.internal("fonts/block/block.fnt"), false);

        skin.add("default",font);

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
        final TextButton backButton = new TextButton("Back",textButtonStyle);
        backButton.setPosition(120, 300);
        stage.addActor(backButton);

        //Event listener for button
        backButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                //music.stop();
                game.setScreen( new MenuScreen(game, manager));
            }
        });


        // Playfield editor
        final TextButton fieldButton = new TextButton("Edit Play Field",textButtonStyle);
        fieldButton.setPosition(120, 50);
        stage.addActor(fieldButton);

        //Event listener for button
        fieldButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                //music.stop();
                game.setScreen( new CustomFieldScreen(game, manager));
            }
        });

        //configure Music toggles
            //temp pixmap for styles
        Pixmap volMap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        volMap.setColor(Color.WHITE);
        volMap.fill();

        skin.add("volMap", new Texture(volMap));
            //style
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOff = skin.newDrawable("volMap", Color.RED);
        checkBoxStyle.checkboxOn = skin.newDrawable("volMap", Color.GREEN);
        checkBoxStyle.font = font;

        //Create checkbox for volume
        final CheckBox volCheck = new CheckBox(null, checkBoxStyle);
        volCheck.setChecked(true);
        volCheck.setPosition(50, 760);
        stage.addActor(volCheck);

        volCheck.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(volCheck.isChecked()) {
                    //music.play();
                    game.volumeOn = true;
                }
                else{
                    game.volumeOn = false;
                    //music.stop();
                }
            }
        });

        //Create checkbox for sounds
        final CheckBox soundCheck = new CheckBox(null, checkBoxStyle);
        soundCheck.setChecked(true);
        soundCheck.setPosition(50, 560);
        stage.addActor(soundCheck);

        soundCheck.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(soundCheck.isChecked())
                    game.soundOn = true;
                else
                    game.soundOn = false;
            }
        });

        Texture knob = new Texture("Knob.png");

        //texture filters
        knob.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //configure slider Style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(knob));
        sliderStyle.background = skin.newDrawable("slider", Color.BLUE);

        //Music volume slider
        //final Slider volumeSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        final Slider volumeSlider = new Slider(0, 1, (float)Math.pow(0.01f, 3), false, sliderStyle);
        volumeSlider.setWidth(500);
        volumeSlider.setPosition(130, 735);
        stage.addActor(volumeSlider);
        volumeSlider.setValue(game.volume);
        volumeSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(volumeSlider.isDragging() && volCheck.isChecked())
                    //music.setVolume(game.volume);

                game.volume = volumeSlider.getValue();
                //music.setVolume(game.volume);
            }
        });

        //Sound volume slider
        final Slider soundSlider = new Slider(0, 1, (float)Math.pow(0.01f, 3), false, sliderStyle);
        soundSlider.setWidth(500);

        soundSlider.setPosition(130, 535);
        stage.addActor(soundSlider);
        soundSlider.setValue(game.sound);
        soundSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(soundSlider.isDragging() && game.soundOn) {
                    if(soundTimer <=0) {
                        testSound.setVolume(testSound.play(), game.sound);
                        soundTimer = 0.15f;
                    }
                    soundTimer -= Gdx.graphics.getDeltaTime();
                }
                else {
                    testSound.stop();
                }
                game.sound = soundSlider.getValue();
                System.out.println("slider: " + soundSlider.getValue());
            }
        });

        //Screen settings
        gameCam = new OrthographicCamera();
        gameCam.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        gamePort = new ExtendViewport(PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT, gameCam);
        gameCam.position.set(PuzzleAttack.V_WIDTH/2, PuzzleAttack.V_HEIGHT/2, 0);
        stage.setViewport(gamePort);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.begin();
        game.batch.draw(background, (int)(gameCam.viewportWidth/2) * -1 , (int)(gameCam.viewportHeight/2) * -1, (int)(gameCam.viewportWidth), (int)(gameCam.viewportHeight), (int)(gameCam.viewportWidth * 1.5), (int)(gameCam.viewportHeight * 1.5));
        titleFont.draw(game.batch, "Options", 110, 1200);
        font.draw(game.batch, "Music Volume", 130, 900);
        font.draw(game.batch, "Sound Volume", 130, 700);
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
        music.dispose();
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
