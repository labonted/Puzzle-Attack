package com.puzzleattack.game.Input;


import com.badlogic.gdx.math.Vector2;

/**
 * Created by dlabonte on 01/05/2016.
 */
public class Cursor {

    private boolean enabled;
    private Vector2 coordinates;

    public Cursor(){
        enabled = false;
        coordinates = new Vector2(2, 3);
    }

    public void moveCursor(int direction){
        switch (direction){
            case 1:
                if(coordinates.x < 4)
                    coordinates.x++;
                break;
            case 2:
                if(coordinates.x > 0)
                    coordinates.x--;
                break;
            case 3:
                if(coordinates.y < 9)
                    coordinates.y++;
                break;
            case 4:
                if(coordinates.y > 0)
                    coordinates.y--;
                break;
        }
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Vector2 getCoordinates() {
        return coordinates;
    }

}
