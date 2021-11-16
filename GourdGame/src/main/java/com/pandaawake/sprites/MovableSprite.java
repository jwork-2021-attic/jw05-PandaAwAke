package com.pandaawake.sprites;

import com.pandaawake.Config;
import com.pandaawake.scene.GameMap;
import com.pandaawake.scene.Scene;
import com.pandaawake.utils.Direction;
import com.pandaawake.utils.FloatPair;
import com.pandaawake.utils.IntPair;
import com.pandaawake.utils.Pair;

import java.util.Set;
import java.util.TreeSet;

public class MovableSprite extends Sprite {

    protected float movingSpeed = 4.0f;     // How many tiles will this sprite move in 1 second
    protected FloatPair targetDeltaPos = new FloatPair(0.0f, 0.0f);
    protected FloatPair targetPos = new FloatPair(0.0f, 0.0f);
    protected float movingTime = 0.0f;      // How many seconds have this sprite been moving

    protected Status status = Status.Ok;    // The sprite's status

    public enum Status {
        Ok, Moving
    }

    public MovableSprite(boolean blocking, Scene scene, int spriteWidth, int spriteHeight) {
        super(blocking, scene, spriteWidth, spriteHeight);
    }

    public MovableSprite(boolean blocking, Scene scene, int spriteWidth, int spriteHeight, int spriteRenderWidth, int spriteRenderHeight) {
        super(blocking, scene, spriteWidth, spriteHeight, spriteRenderWidth, spriteRenderHeight);
    }

    public MovableSprite(boolean blocking, Scene scene, float movingSpeed, int spriteWidth, int spriteHeight) {
        super(blocking, scene, spriteWidth, spriteHeight);
        this.movingSpeed = movingSpeed;
    }

    public MovableSprite(boolean blocking, Scene scene, float movingSpeed, int spriteWidth, int spriteHeight, int spriteRenderWidth, int spriteRenderHeight) {
        super(blocking, scene, spriteWidth, spriteHeight, spriteRenderWidth, spriteRenderHeight);
        this.movingSpeed = movingSpeed;
    }

    public Status getStatus() {
        return status;
    }
    public float getTargetX() {
        return targetPos.first;
    }
    public float getTargetY() {
        return targetPos.second;
    }
    public FloatPair getTargetPos() {
        return targetPos;
    }
    @Override
    public void setX(float x) {
        posX = x;
        targetPos.first = x;
    }
    @Override
    public void setY(float y) {
        posY = y;
        targetPos.second = y;
    }
    @Override
    public void setPos(float x, float y) {
        posX = x;
        posY = y;
        targetPos.first = x;
        targetPos.second = y;
    }


    private Pair<Integer, Integer> tryMove(Direction direction) {
        int newX = Math.round(posX), newY = Math.round(posY);
        switch (direction) {
            case left:
                newX -= 1;
                break;
            case up:
                newY -= 1;
                break;
            case right:
                newX += 1;
                break;
            case down:
                newY += 1;
                break;
        }
        return new Pair<Integer, Integer>(newX, newY);
    }

    public boolean doMove(Direction direction) {
        if (status != Status.Ok) {
            return false;
        }
        Pair<Integer, Integer> newPosition = tryMove(direction);
        if (scene.spriteCanMoveTo(this, newPosition.first, newPosition.second)) {
            status = Status.Moving;
            targetDeltaPos.first = newPosition.first - posX;
            targetDeltaPos.second = newPosition.second - posY;
            targetPos.first = newPosition.first.floatValue();
            targetPos.second = newPosition.second.floatValue();
            movingTime = 0.0f;
            return true;
        }
        return false;
    }

    /**
     * This function will help scene to judge if this sprite can move to (targetX, targetY).
     * @param targetX
     * @param targetY
     * @return The collision box of this try.
     */
    public Set<IntPair> tryToMoveCollisionBox(int targetX, int targetY) {
        int left, right, top, bottom;
        Set<IntPair> collisionBox = new TreeSet<>();
        if (posX == -1.0f) {
            left = (int) Math.round(Math.floor(targetX));
            right = (int) Math.round(Math.ceil(targetX)) + spriteWidth - 1;
            top = (int) Math.round(Math.floor(targetY));
            bottom = (int) Math.round(Math.ceil(targetY)) + spriteHeight - 1;
        } else {
            left = (int) Math.round(Math.floor(Math.min(targetX, posX)));
            right = (int) Math.round(Math.ceil(Math.max(targetX, posX))) + spriteWidth - 1;
            top = (int) Math.round(Math.floor(Math.min(targetY, posY)));
            bottom = (int) Math.round(Math.ceil(Math.max(targetY, posY))) + spriteHeight - 1;
        }

        for (int x = Math.max(left, 0); x <= right && x < Config.MapWidth; x++) {
            for (int y = Math.max(top, 0); y <= bottom && y < Config.MapHeight; y++) {
                collisionBox.add(new IntPair(x, y));
            }
        }
        return collisionBox;
    }

    // Collision box
    @Override
    public Set<IntPair> getCollisionBox() {
        int left = (int) Math.round(Math.floor(Math.min(targetPos.first, posX)));
        int right = (int) Math.round(Math.ceil(Math.max(targetPos.first, posX))) + spriteWidth - 1;
        int top = (int) Math.round(Math.floor(Math.min(targetPos.second, posY)));
        int bottom = (int) Math.round(Math.ceil(Math.max(targetPos.second, posY))) + spriteHeight - 1;
        Set<IntPair> collisionBox = new TreeSet<>();
        for (int x = Math.max(left, 0); x <= right && x < Config.MapWidth; x++) {
            for (int y = Math.max(top, 0); y <= bottom && y < Config.MapHeight; y++) {
                collisionBox.add(scene.getGameMap().getTile(x, y).getIntPair());
            }
        }
        return collisionBox;
    }

    // Rendering box
    @Override
    public Set<IntPair> getRenderingBox() {
        int left = (int) Math.round(Math.floor(Math.min(targetPos.first, posX)));
        int right = (int) Math.round(Math.ceil(Math.max(targetPos.first, posX))) + spriteRenderWidth - 1;
        int top = (int) Math.round(Math.floor(Math.min(targetPos.second, posY)));
        int bottom = (int) Math.round(Math.ceil(Math.max(targetPos.second, posY))) + spriteRenderHeight - 1;
        Set<IntPair> renderingBox = new TreeSet<>();
        for (int x = Math.max(left, 0); x <= right && x < Config.MapWidth; x++) {
            for (int y = Math.max(top, 0); y <= bottom && y < Config.MapHeight; y++) {
                renderingBox.add(scene.getGameMap().getTile(x, y).getIntPair());
            }
        }
        return renderingBox;
    }



    @Override
    public void OnUpdate(float timestep) {
        if (status == Status.Moving) {
            movingTime += timestep;
            if (movingTime >= 1.0f / movingSpeed) {
                status = Status.Ok;
                this.posX = targetPos.first;
                this.posY = targetPos.second;
            } else {
                this.posX += targetDeltaPos.first * movingSpeed * timestep;
                this.posY += targetDeltaPos.second * movingSpeed * timestep;
            }
        }
        
    }
}
