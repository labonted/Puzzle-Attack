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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.PuzzleAttack;

import org.w3c.dom.css.RGBColor;

import java.util.ArrayList;

public class CustomFieldScreen implements Screen {

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
    private static Texture background, border, backgroundTexture, pipe;
    private Pixmap borderBackground, readBackground;
    private int updateCounter;
    ArrayList<Vector2> pixels;

    public CustomFieldScreen(final PuzzleAttack game, AssetManager assMan) {

        this.game = game;
        manager = assMan;

        border = manager.get("border.png", Texture.class);
        backgroundTexture = manager.get("borderColor.png", Texture.class);
        backgroundTexture.getTextureData().prepare();
        readBackground = backgroundTexture.getTextureData().consumePixmap();
        borderBackground = readBackground;

        ///////TEST MUSIC
        /*music = manager.get("audio/music/options.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(game.volume);
        if(game.volumeOn)
            music.play();*/

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        background = manager.get("TiledBack.jpg", Texture.class);
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        pipe = manager.get("pipe.png", Texture.class);
        pipe.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Pixmap pixmap = new Pixmap(500, 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        Pixmap knobMap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        knobMap.setColor(Color.BLUE);
        knobMap.fill();

        skin.add("knob", new Texture(knobMap));

        Pixmap sliderMap = new Pixmap(3000, 50, Pixmap.Format.RGBA8888);
        sliderMap.setColor(Color.WHITE);
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
        backButton.setPosition(120, 250);
        stage.addActor(backButton);

        //update counter for colour updates to limit
        updateCounter = 0;

        //Event listener for button
        backButton.addListener(new ChangeListener() {
            public void changed (ChangeListener.ChangeEvent event, Actor actor) {
                //music.stop();
                game.setScreen( new OptionScreen(game, manager));
            }
        });

        //pixels for painting
        pixels = new ArrayList<Vector2>();

        //configure slider Style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.newDrawable("knob", Color.BLACK);
        sliderStyle.background = skin.newDrawable("slider", Color.RED);

        //Red slider
        final Slider redSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        redSlider.setValue(game.red);
        redSlider.setWidth(500);
        redSlider.setPosition(130, 800);
        stage.addActor(redSlider);
        redSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(redSlider.isDragging()) {
                    game.red = (redSlider.getValue());
                    //borderMap.setColor(game.red, game.green, game.blue, 1);
                    //borderMap.fill();
                }
                //borderBackground = new Texture(borderBackground);
            }

        });

        redSlider.addListener(new DragListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
            }
        });


        //sliderStyle.background = skin.newDrawable("slider", Color.BLUE);

        //Green slider
        final Slider greenSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        greenSlider.setValue(game.green);
        greenSlider.setWidth(500);
        greenSlider.setPosition(130, 650);
        stage.addActor(greenSlider);
        greenSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(greenSlider.isDragging()) {
                    game.green = (greenSlider.getValue());
                    //borderMap.setColor(game.red, game.green, game.blue, 1);
                    //borderMap.fill();
                }
                //borderBackground = new Texture(borderMap);
            }

        });

        greenSlider.addListener(new DragListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
            }
        });

        //Blue slider
        final Slider blueSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        blueSlider.setValue(game.blue);
        blueSlider.setWidth(500);
        blueSlider.setPosition(130, 500);
        stage.addActor(blueSlider);
        blueSlider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if(blueSlider.isDragging()) {
                    game.blue = (blueSlider.getValue());
                    //borderMap.setColor(game.red, game.green, game.blue, 1);
                    //borderMap.fill();
                }
                //borderBackground = new Texture(borderMap);
            }
        });

        blueSlider.addListener(new DragListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                paintPixels();
            }
        });

        getPixels();
        paintPixels();

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
        game.batch.draw(backgroundTexture,0, 0);
        game.batch.draw(border, 0, 0);
        font.draw(game.batch, "Red", 130, 900);
        font.draw(game.batch, "Green", 130, 750);
        font.draw(game.batch, "Blue", 130, 600);
        game.batch.draw(pipe, 35, (pipe.getHeight() * -1));
        game.batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void getPixels() {
        for (int x = 0; x < readBackground.getWidth(); x++) {
            for (int y = 0; y < readBackground.getHeight(); y++) {
                if (readBackground.getPixel(x, y) == Color.rgba8888(Color.BLACK)) {
                    pixels.add(new Vector2(x, y));
                }
            }
        }
    }

    public void paintPixels(){
        int color = Color.rgba8888(game.red, game.green, game.blue, 1);
        if(updateCounter == 0) {
            for (Vector2 pixel : pixels) {
                borderBackground.drawPixel((int) pixel.x, (int) pixel.y, color);
            }
            backgroundTexture = new Texture(borderBackground, Pixmap.Format.RGBA8888, false);
            updateCounter = 0;
        } else
            updateCounter++;
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
        borderBackground.dispose();
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

