package com.byril.hryharenka;

import java.util.ArrayList;
import java.util.List;


public class Ship {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;

    private int id;
    private int orientation = HORIZONTAL;
    private int decks;
    private List<Integer> cellIds = new ArrayList<>();

    public Ship(int id, int decks) {
        this.id = id;
        this.decks = decks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDecks() {
        return decks;
    }

    public void setDecks(int decks) {
        this.decks = decks;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public List<Integer> getCellIds() {
        return cellIds;
    }

    public void setCellIds(List<Integer> cellIds) {
        this.cellIds = cellIds;
    }

}