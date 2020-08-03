package com.byril.hryharenka;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.byril.hryharenka.screen.MainScreen;


public class SeaBattle extends Game {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;

    private Screen main;
    private Screen game;

    @Override
    public void create() {
        startGame();
    }

    public void startGame() {
        main = new MainScreen(this);
        setScreen(main);
    }

    public Screen getMain() {
        return main;
    }

    public void setMain(Screen main) {
        this.main = main;
    }

    public Screen getGame() {
        return game;
    }

    public void setGame(Screen game) {
        this.game = game;
    }
}
