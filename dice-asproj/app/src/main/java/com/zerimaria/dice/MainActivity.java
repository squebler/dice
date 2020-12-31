package com.zerimaria.dice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity implements View.OnTouchListener {

    private final int ROW_HEIGHT = 60;

    private List<TableDie> tableDice;
    private Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //Remove title bar; must go above setContentView to avoid crash, I heard.

        setContentView(R.layout.activity_main);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setOnTouchListener(this);

        tableDice = new ArrayList<>();
        rand = new Random();
    }

    public void dieGenClick(View view) {
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
        updateStats();
        resetClick(view);

        btnDieAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: do I have a memory leak here, creating button views that never go away?

                flexboxLayout.removeView(v);

                tableDice.remove(v.getTag());

                updateStats();
                resetClick(v);
                //todo: update total to value of remaining dice
            }
        });
    }

    private void updateStats() {
        float sumOfAvgs = 0;
        int max = 0;
        int min = 0;
        for (TableDie tableDie : tableDice) {
            min++;
            max += tableDie.getNumSides();
            sumOfAvgs += tableDie.getAverage();
        }
        ((TextView)findViewById(R.id.lblMin)).setText("min: " + Float.toString(min));
        ((TextView)findViewById(R.id.lblMax)).setText("max: " + Float.toString(max));
        ((TextView)findViewById(R.id.lblAvg)).setText("avg: " + Float.toString(sumOfAvgs));
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
        int total = 0;
        for (TableDie tableDie : tableDice) {
            int rollValue = rand.nextInt(tableDie.getNumSides()) + 1;
            total += rollValue;
            tableDie.getButton().setText(Integer.toString(rollValue));
        }
        ((TextView)findViewById(R.id.lblTotal)).setText(Integer.toString(total));
    }

    public void resetClick(View view) {
        for (TableDie tableDie : tableDice) {
            tableDie.getButton().setText(tableDie.getName());
        }
        ((TextView)findViewById(R.id.lblTotal)).setText(Integer.toString(0));
    }

    public void clearClick(View view) {
        FlexboxLayout flexboxLayout = findViewById(R.id.flexboxLayout);
        flexboxLayout.removeAllViews();
        tableDice.clear();
        updateStats();
        ((TextView)findViewById(R.id.lblTotal)).setText(Integer.toString(0));
    }

    public void settingsClick(View view) {
//        rollClick(view);

//        Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
//        startActivity(openSettingsIntent);

        new ColorPickerDialogFragment().show(getSupportFragmentManager(), null);
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        rollClick(view);

        return true;
    }
}
