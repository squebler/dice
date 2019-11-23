package com.zerimaria.dice;

import android.widget.Button;

public class TableDie {

    private Button button;
    private int numSides;
    private String name;

    public TableDie(Button button, int numSides)
    {
        this.button = button;
        this.numSides = numSides;
        name = "d" + numSides;
    }

    public Button getButton() {
        return this.button;
    }

    public int getNumSides() {
        return this.numSides;
    }

    public String getName() {
        return this.name;
    }

    public float getAverage() {
        float sumOfValues = 0;
        for (int i = 0; i < numSides; i++) {
            sumOfValues += i + 1;
        }
        return sumOfValues / numSides;
    }
}
