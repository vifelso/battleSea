package com.byril.hryharenka.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;


public class MainScreen implements Screen {

    private Stage stage;

    private Button startBtn;
    private Button exitBtn;
    private Game game;

    public MainScreen(Game game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        startBtn = new Button(new SpriteDrawable(new Sprite(new TextureRegion(new Texture("button_play.png")))));
        startBtn.setPosition(Gdx.graphics.getWidth() / 2 - startBtn.getWidth() / 2, 0);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen());
            }
        });


        exitBtn = new Button(new SpriteDrawable(new Sprite(new TextureRegion(new Texture("button_exit.png")))));
        exitBtn.setPosition(Gdx.graphics.getWidth() / 2 - exitBtn.getWidth() / 2, 100);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        stage.addActor(startBtn);
        stage.addActor(exitBtn);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(242 / 255f, 242 / 255f, 242 / 255f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.clear();
        stage.dispose();
    }
}