package com.puzzleattack.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

/**
 * Created by Dylan on 2016-01-03.
 */
public class Block {

    int blockType, justMoved, multitimer, switched;
    float dropped;
    private boolean isClearing, isDropping, counted, multiTest, firstTrash;
    private Vector3 position;
    private Vector3 coordinates;
    private Rectangle bounds;
    private Vector2 trashSize;

    public Block(int x, int y, int type) {
        position = new Vector3((x * 100) + 61, (y * 100), 0);
        coordinates = new Vector3(x, y, 0);

        //block states
        isDropping = false;
        isClearing = false;
        counted = false;
        multiTest = false;
        justMoved = 0;
        dropped = 0;
        switched = 0;

        trashSize = new Vector2(1, 1);
        firstTrash = false;

        //create random blockType
        //Random r = new Random();
        //blockType = r.nextInt(6);

        blockType = type;

        //create tap detecting rectangle
        bounds = new Rectangle(position.x, position.y, 100, 100);
    }

    public void update(int x, int y) {
        position = new Vector3((x * 100) + 61, (y * 100), 0);
        coordinates = new Vector3(x, y, 0);
        if(dropped > 0 && dropped < 0.3) {
            dropped+= Gdx.graphics.getDeltaTime();
        }
        else
            dropped = 0;
    }


    public void makeTrash(int x, int y){
        this.blockType = 6;
        trashSize.x = x;
        trashSize.y = y;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getCoordinates() {
        return coordinates;
    }

    public void moveBounds(int moveUp) {
        if (moveUp == 100)
            this.bounds = new Rectangle(position.x, position.y, 100, 100);
        else
            this.bounds = new Rectangle(position.x, position.y + moveUp, 100, 100);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getBlockType() {
        return blockType;
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
    }

    public boolean isClearing() {
        return isClearing;
    }

    public void setIsClearing(boolean isClearing) {
        this.isClearing = isClearing;
    }

    public boolean isDropping() {
        return isDropping;
    }

    public void setIsDropping(boolean isDropping) {
        this.isDropping = isDropping;
    }

    public int getJustMoved() {
        return justMoved;
    }

    public void setJustMoved(int justMoved) {
        this.justMoved = justMoved;
    }

    public boolean isCounted() {
        return counted;
    }

    public void setCounted(boolean counted) {
        this.counted = counted;
    }

    public boolean isMultiTest() {
        return multiTest;
    }

    public void setMultiTest(boolean multiTest) {
        this.multiTest = multiTest;
    }

    public int getMultitimer() {
        return multitimer;
    }

    public void setMultitimer(int multitimer) {
        this.multitimer = multitimer;
    }

    public float getDropped() {
        return dropped;
    }

    public void setDropped(int dropped) {
        this.dropped = dropped;
    }

    public int getSwitched() {
        return switched;
    }

    public void setSwitched(int switched) {
        this.switched = switched;
    }

    public boolean isFirstTrash() {
        return firstTrash;
    }

    public void setFirstTrash(boolean firstTrash) {
        this.firstTrash = firstTrash;
    }

    public Vector2 getTrashSize() {
        return trashSize;
    }

    public void setTrashSize(Vector2 trashSize) {
        this.trashSize = trashSize;
    }
}
