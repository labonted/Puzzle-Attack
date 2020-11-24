package com.puzzleattack.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.puzzleattack.game.Puzzle;
import com.puzzleattack.game.PuzzleAttack;

import java.util.Locale;

public class Hud {

    public Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    private BitmapFont font;
    private BitmapFont fontSmall;
    private BitmapFont fontSmallest;

    public Hud(SpriteBatch sb){

        worldTimer = 0;
        timeCount = 0;
        score = 0;

        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"),
                Gdx.files.internal("fonts/font.png"), false);

        fontSmall = new BitmapFont(Gdx.files.internal("fonts/font_small.fnt"),
                Gdx.files.internal("fonts/font_small.png"), false);
        fontSmallest = new BitmapFont(Gdx.files.internal("fonts/font-15.fnt"),
                Gdx.files.internal("fonts/font-15.png"), false);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, PuzzleAttack.V_WIDTH, PuzzleAttack.V_HEIGHT);
        sb.setProjectionMatrix(camera.combined);
        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport, sb);
    }

    public void draw(Puzzle puzzle, SpriteBatch batch, float dt, int borderY){
        fontSmall.draw(batch, "Time", 321, 1260 - borderY);
        fontSmall.draw(batch, String.format(Locale.CANADA, "%02d:%02d", worldTimer / 60, worldTimer % 60), 320, 1220 - borderY);

        font.draw(batch, score.toString(), 50, 1160 - borderY);
        fontSmall.draw(batch, "P1 Score", 30, 1215 - borderY);
        fontSmallest.draw(batch, "Level", 240, 1187 - borderY);
        font.draw(batch, "5", 255, 1165 - borderY);

        font.draw(batch, "000000", 520, 1160 - borderY);
        fontSmall.draw(batch, "P2 Score", 552, 1215 - borderY);
        fontSmallest.draw(batch, "Level", 430, 1187 - borderY);
        font.draw(batch, "5", 445, 1165 - borderY);
    }

    public void update(Puzzle puzzle, SpriteBatch batch, float dt){
        timeCount += dt;

        score = puzzle.getScore();

        if(timeCount >=1) {
            worldTimer++;
            timeCount = 0;
        }
    }

    public Integer getScore() {
        return score;
    }

    public Integer getWorldTimer() {
        return worldTimer;
    }
}
