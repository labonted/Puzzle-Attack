package com.puzzleattack.game.Input;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.puzzleattack.game.Puzzle;

/**
 * Created by Dylan on 2016-02-14.
 */
public class Gesture implements GestureDetector.GestureListener {

    private Puzzle puzzle;

    public Gesture(Puzzle puz){
        puzzle = puz;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
 /*       System.out.println("Pan performed, delta:" + Float.toString(deltaX) +
                "," + Float.toString(deltaY));*/
        if(deltaY < 0){
            puzzle.setPanUp(Math.abs((int) deltaY));
        }
        else
            puzzle.setPanUp(0);


        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        puzzle.setPanUp(0);
        puzzle.setScrollCount(0);
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance){

        return false;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){

        return false;
    }

    @Override
    public void pinchStop() {

    }

}


