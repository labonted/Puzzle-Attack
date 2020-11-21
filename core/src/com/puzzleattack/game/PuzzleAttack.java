package com.puzzleattack.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.puzzleattack.game.Screens.GameOverScreen;
import com.puzzleattack.game.Screens.LoadingScreen;
import com.puzzleattack.game.Screens.MenuScreen;
import com.puzzleattack.game.Screens.PlayScreen;

import java.util.ArrayList;

public class PuzzleAttack extends Game {
	public SpriteBatch batch;
	//public static final int V_HEIGHT= 1480;
	public static final int V_HEIGHT= 1280;
	public static final int V_WIDTH = 720;
	public AssetManager manager;
	public float red, green, blue;
	public float volume, sound;
	public boolean soundOn, volumeOn;
	public ArrayList blockList;
	public LoadingScreen loadingScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();

		//set default colour for border
		blue = 0.8f;
		red = 0;
		green = 0;

		//set volume to 100% by default (ranges between 0.01 and 1)
		volume = 0.1f;
		sound = 0.1f;

		//Enable sound by default, can change in settings menu
		soundOn = true;
		volumeOn = true;

		//Create asset manager and music
		manager = new AssetManager();

		loadingScreen = new LoadingScreen(manager, this);

		blockList = loadingScreen.getBlockList();

		setScreen(loadingScreen);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		manager.dispose();
		this.dispose();
	}
}
