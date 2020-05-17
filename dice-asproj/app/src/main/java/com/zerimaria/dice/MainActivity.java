package com.zerimaria.dice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private final int ROW_HEIGHT = 60;

    private int rollValueAllDice = 0;
    private List<TableDie> tableDice;
    private Random rand;

    private TextView lblRollValue;
    private EditText tbPlus;
    private TextView lblEquals;
    private TextView lblTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //Remove title bar; must go above setContentView to avoid crash, I heard.

        setContentView(R.layout.activity_main);

        tableDice = new ArrayList<>();
        rand = new Random();

        tbPlus = findViewById(R.id.tbPlus);
        tbPlus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tbPlus.getText().length() < 1) {
                    tbPlus.setText("0");
                    tbPlus.setSelection(0,1);
                }

                updateStats();
            }
        });

        lblRollValue = findViewById(R.id.lblRollValue);
        lblEquals = findViewById(R.id.lblEquals);
        lblTotal = findViewById(R.id.lblTotal);
        lblRollValue.setVisibility(View.INVISIBLE);
        lblEquals.setVisibility(View.INVISIBLE);
        lblTotal.setVisibility(View.INVISIBLE);
    }

    public void dieGenClick(View view) {
        tbPlus.clearFocus();

//        lblRollValue.setVisibility(View.INVISIBLE);
//        lblEquals.setVisibility(View.INVISIBLE);
//        lblTotal.setVisibility(View.INVISIBLE);

        final Button btnDieGen = (Button)view;

        final FlexboxLayout flexboxLayout = findViewById(R.id.flexboxLayout);

        final Button btnDieAct = new Button(this);
        btnDieAct.setText(btnDieGen.getText());
        btnDieAct.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnDieGen.getTextSize());
        btnDieAct.setBackground(btnDieGen.getBackground());
        btnDieAct.setPadding(
                btnDieGen.getPaddingLeft(),
                btnDieGen.getPaddingTop(),
                btnDieGen.getPaddingRight(),
                btnDieGen.getPaddingBottom()
        );

        flexboxLayout.addView(btnDieAct);
        FlexboxLayout.LayoutParams lpBtnDieAct = (FlexboxLayout.LayoutParams) btnDieAct.getLayoutParams();
        lpBtnDieAct.setFlexBasisPercent(-1); // use explicit size; don't stretch
        lpBtnDieAct.setHeight(btnDieGen.getHeight());
        lpBtnDieAct.setWidth(btnDieGen.getWidth());
        float marginVert = dpToPx(ROW_HEIGHT, this) - btnDieGen.getHeight();
        float marginHorz = dpToPx(10, this);
        lpBtnDieAct.setMargins((int)(marginHorz / 2), (int)(marginVert / 2), (int)(marginHorz / 2), (int)(marginVert / 2));
        btnDieAct.setLayoutParams(lpBtnDieAct);

        TableDie newDie = new TableDie(btnDieAct, getNumSides(btnDieGen.getId()));
        tableDice.add(newDie);
        btnDieAct.setTag(newDie);
        if (rollValueAllDice != 0) { // Roll it if there's already a roll result, otherwise just display its face.
            int rollValueDie = rand.nextInt(newDie.getNumSides()) + 1;
            rollValueAllDice += rollValueDie;
            newDie.getButton().setText(Integer.toString(rollValueDie));
        }
        update_rollValueAllDice();
        updateStats();
//        prepClick(view);

        btnDieAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: do I have a memory leak here, creating button views that never go away?

                flexboxLayout.removeView(v);

                tableDice.remove(v.getTag());

                update_rollValueAllDice();
                updateStats();
            }
        });
    }

    private int getPlusValue() {
        try {
            return Integer.parseInt(tbPlus.getText().toString());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateStats() {
        int plusValue = getPlusValue();
        float sumOfAvgs = plusValue;
        int max = plusValue;
        int min = plusValue;
        for (TableDie tableDie : tableDice) {
            min++;
            max += tableDie.getNumSides();
            sumOfAvgs += tableDie.getAverage();
        }
        ((TextView)findViewById(R.id.lblMin)).setText("min: " + Float.toString(min));
        ((TextView)findViewById(R.id.lblMax)).setText("max: " + Float.toString(max));
        ((TextView)findViewById(R.id.lblAvg)).setText("avg: " + Float.toString(sumOfAvgs));
        ((TextView)findViewById(R.id.lblTotal)).setText(Integer.toString(rollValueAllDice + plusValue));
        ((TextView)findViewById(R.id.lblRollValue)).setText(Integer.toString(rollValueAllDice));
    }

    private int getNumSides(int dieRid) {
        if (dieRid == R.id.btnD4) return 4;
        if (dieRid == R.id.btnD6) return 6;
        if (dieRid == R.id.btnD8) return 8;
        if (dieRid == R.id.btnD10) return 10;
        if (dieRid == R.id.btnD12) return 12;
        if (dieRid == R.id.btnD20) return 20;

        throw new RuntimeException("Invalid dieRid");
    }

    public void rollClick(View view) {
        tbPlus.clearFocus();

        rollValueAllDice = 0;
        for (TableDie tableDie : tableDice) {
            int rollValueDie = rand.nextInt(tableDie.getNumSides()) + 1;
            rollValueAllDice += rollValueDie;
            tableDie.getButton().setText(Integer.toString(rollValueDie));
        }

        updateStats();

        lblRollValue.setVisibility(View.VISIBLE);
        lblEquals.setVisibility(View.VISIBLE);
        lblTotal.setVisibility(View.VISIBLE);
    }

    private void update_rollValueAllDice() {
        rollValueAllDice = 0;
        for (TableDie tableDie : tableDice) {
            if (!tableDie.getName().equals(tableDie.getButton().getText().toString())) {
                int rollValueDie = Integer.parseInt(tableDie.getButton().getText().toString());
                rollValueAllDice += rollValueDie;
            }
        }
    }

    public void prepClick(View view) {
        tbPlus.clearFocus();

        for (TableDie tableDie : tableDice) {
            tableDie.getButton().setText(tableDie.getName());
        }

        rollValueAllDice = 0;
        updateStats();

        lblRollValue.setVisibility(View.INVISIBLE);
        lblEquals.setVisibility(View.INVISIBLE);
        lblTotal.setVisibility(View.INVISIBLE);
    }

    public void clearClick(View view) {
        tbPlus.clearFocus();

        FlexboxLayout flexboxLayout = findViewById(R.id.flexboxLayout);
        flexboxLayout.removeAllViews();
        tableDice.clear();
        rollValueAllDice = 0;
        tbPlus.setText("0");
        updateStats();

        lblRollValue.setVisibility(View.INVISIBLE);
        lblEquals.setVisibility(View.INVISIBLE);
        lblTotal.setVisibility(View.INVISIBLE);
    }

    public void backgroundClick(View view) {
        tbPlus.clearFocus();
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
