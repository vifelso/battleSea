package com.byril.hryharenka;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.byril.hryharenka.screen.GameScreen.HEIGHT_INDENT;
import static com.byril.hryharenka.screen.GameScreen.WIDTH_INDENT;

public class Cell extends Actor {
    public static int CELL_WIDTH_ROWS = 10;
    public static int CELL_HEIGHT_ROWS = 10;
    private static int CELL_WIDTH = 50;
    private static int CELL_HEIGHT = 50;


    private int id;
    private Sprite sprite;

    public Cell(Texture texture, final String actorName, int id) {
        this.id = id;
        sprite = new Sprite(texture);
        spritePos(sprite.getX(), sprite.getY());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void spritePos(float x, float y) {
        sprite.setPosition(WIDTH_INDENT + (id % CELL_WIDTH_ROWS) * CELL_WIDTH, HEIGHT_INDENT + (int) (id / CELL_HEIGHT_ROWS) * CELL_HEIGHT);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }


}