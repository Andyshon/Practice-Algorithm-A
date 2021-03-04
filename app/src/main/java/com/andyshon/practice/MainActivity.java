package com.andyshon.practice;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.andyshon.practice.enums.Direction;
import com.andyshon.practice.enums.Mode;
import com.andyshon.practice.enums.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andy Shkatula andyshon.shkatula@gmail.com
 * @since 03.03.2021
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btnSize;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initAdapter();
    }

    private void initUI() {
        setTitle("Algorithm A*");
        btnSize = findViewById(R.id.btnSetSize);
        setOnClickListener(this, R.id.btnSetSize, R.id.btnSetStartPoint, R.id.btnSetEndPoint, R.id.btnSetBlocks, R.id.btnCalculate);
    }

    private void reset() {
        running = false;
        mode = Mode.NONE;
        size = Size.SMALL;
        adapter.reset();
        btnSize.setText("Size");
        setTitle("Algorithm A*");
    }

    private void initAdapter() {
        adapter = new Adapter(this);
        adapter.setClickListener(new Adapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (mode) {
                    case NONE:
                        mode = Mode.NONE;
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        break;
                    case START_POINT:
                        mode = Mode.START_POINT;
                        adapter.marAsStartPoint(position);
                        break;
                    case END_POINT:
                        mode = Mode.END_POINT;
                        adapter.marAsEndPoint(position);
                        break;
                    case BLOCK:
                        mode = Mode.BLOCK;
                        adapter.markAsBlock(position);
                        break;
                }
            }
        });
        ((RecyclerView) findViewById(R.id.recyclerView)).setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.btnSetSize):
                chooseSize(v);
                break;
            case(R.id.btnSetStartPoint):
                chooseStartPoint();
                break;
            case(R.id.btnSetEndPoint):
                chooseEndPoint();
                break;
            case(R.id.btnSetBlocks):
                chooseBlocks();
                break;
            case(R.id.btnCalculate):
                calculate();
                break;
        }
    }

    private void calculate() {
        mode = Mode.NONE;
        int start = adapter.startPointPos;
        final int end = adapter.endPointPos;
        int playerSize = 1; // player size can be 1x1, 2x2, 3x3
        if (size == Size.MEDIUM) playerSize = 2;
        else if (size == Size.BIG) playerSize = 3;

        if (start == -1 && end == -1) {
            setTitle("Please set start & end points");
            return;
        }
        else if (start == -1 && end != -1) {
            setTitle("Please set start point");
            return;
        }
        else if (start != -1 && end == -1) {
            setTitle("Please set end point");
            return;
        }
        else
            setTitle("Calculating..");

        Set<Integer> blocks = adapter.blocksPos;
        // sort blocks by ascending
        List<Integer> numbersList = new ArrayList<>(blocks);
        Collections.sort(numbersList);
        blocks = new LinkedHashSet<>(numbersList);
        Log.e(TAG, "blocks:" + blocks);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int iterationCounter = 0;
                final Set<Integer> path = new HashSet<>();

                while (running) {
                    iterationCounter++;

                    Direction direction = whereToGo(currentPos, end);
                    switch (direction) {
                        case LEFT:
                            currentPos -= 1;
                            break;
                        case RIGHT:
                            if (noBlockAt(currentPos + 1))
                                currentPos += 1;
                            else {
                                if (noBlockAt(currentPos + maxValue)) {
                                    currentPos += maxValue;
                                    path.add(currentPos);
                                    if (noBlockAt(currentPos + 1)) {
                                        currentPos += 1;
                                        path.add(currentPos);
                                        if (noBlockAt(currentPos + 1)) {
                                            currentPos += 1;
                                            path.add(currentPos);
                                        }
                                    }
                                } else if (noBlockAt(currentPos - maxValue)) {
                                    currentPos -= maxValue;
                                }
                            }
                            break;
                        case UP:
                            if (noBlockAt(currentPos - maxValue)) {
                                currentPos -= maxValue;
                            } else {
                                if (noBlockAt(currentPos + 1)) {
//                                currentPos += 1;
                                }
                            }
                            break;
                        case DOWN:
                            currentPos += maxValue;
                            break;
                        default:
                            break;
                    }
                    path.add(currentPos);

                    if (currentPos == end) {
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayPath(path, true);
                            }
                        });
                    }
                    if (iterationCounter >= 1000) {
                        Log.e(TAG, "too much iterations -> can't find the path!");
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayPath(path, false);
                            }
                        });
                    }
                    Log.e(TAG, "path: " + path);
                }
            }
        });
        if (!running) {
            running = true;
            currentPos = start;
            thread.start();
        }
    }

    private Direction whereToGo(int currentPos, int end) {

        Direction direction = Direction.NONE;


        if (getXPos(currentPos) == getXPos(end)) {
            if (getYPos(currentPos) < getYPos(end)) {
                direction = Direction.DOWN;
            }
            else if (getYPos(currentPos) > getYPos(end)) {
                direction = Direction.UP;
            }
        }
        else {
            if (getXPos(currentPos) < getXPos(end)) {
                direction = Direction.RIGHT;
            }
            else if (getXPos(currentPos) > getXPos(end)) {
                direction = Direction.LEFT;
            }
        }

        if (getYPos(currentPos) == getYPos(end)) {
            if (getXPos(currentPos) < getXPos(end)) {
                direction = Direction.RIGHT;
            }
            else if (getXPos(currentPos) > getXPos(end)) {
                direction = Direction.LEFT;
            }
        }
        else {
            if (getYPos(currentPos) < getYPos(end)) {
                direction = Direction.DOWN;
            }
            else if (getYPos(currentPos) > getYPos(end)) {
                direction = Direction.UP;
            }
        }
        Log.e(TAG, "Direction to go = " + direction);
        return direction;
    }

    private int getXPos(int point) {
        if (point % maxValue == 0)
            return point;
        else
            return point % maxValue;
    }

    private int getYPos(int point) {
        if (point % maxValue > 0) {
            return point / maxValue + 1;
        }
        return point / maxValue;
    }

    private void displayPath(Set<Integer> steps, boolean found) {
        if (found)
            setTitle("Path drawn");
        else
            setTitle("Path not found, try to reset");

        adapter.drawPath(steps);
    }

    private boolean noBlockAt(int pos) {
        return !adapter.blocksPos.contains(pos);
    }

    private void chooseSize(View view) {
        setTitle("Algorithm A*");
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.menu_sizes);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case(R.id.size_small):
                        size = Size.SMALL;
                        btnSize.setText("Size " + size.name());
                        return true;
                    case(R.id.size_medium):
                        size = Size.MEDIUM;
                        btnSize.setText("Size " + size.name());
                        return true;
                    case(R.id.size_big):
                        size = Size.BIG;
                        btnSize.setText("Size " + size.name());
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void setOnClickListener(View.OnClickListener listener, @IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}