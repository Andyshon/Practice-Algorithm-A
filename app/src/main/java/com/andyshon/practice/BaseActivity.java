package com.andyshon.practice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andyshon.practice.enums.Mode;
import com.andyshon.practice.enums.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andy Shkatula andyshon.shkatula@gmail.com
 * @since 03.03.2021
 */
class BaseActivity extends AppCompatActivity {

    String TAG = "Practice";

    Mode mode = Mode.NONE;
    Size size = Size.SMALL;
    int maxValue = 20;
    int currentPos;

    Thread thread;
    boolean running = false;

    List<Integer> rightBounds = new ArrayList<>();
    List<Integer> leftBounds = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 1; i<= 381; i += maxValue) {
            leftBounds.add(i);
        }

        for (int i = maxValue; i<= 400; i += maxValue) {
            rightBounds.add(i);
        }
    }

    void setTitle(String s) {
        getSupportActionBar().setTitle(s);
    }

    void chooseStartPoint() {
        setTitle("Choose Start Point");
        mode = Mode.START_POINT;
    }

    void chooseEndPoint() {
        setTitle("Choose End Point");
        mode = Mode.END_POINT;
    }

    void chooseBlocks() {
        setTitle("Choose Blocks");
        mode = Mode.BLOCK;
    }
}
