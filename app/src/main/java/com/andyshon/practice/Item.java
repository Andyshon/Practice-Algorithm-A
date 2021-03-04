package com.andyshon.practice;

import com.andyshon.practice.enums.Mode;

/**
 * @author Andy Shkatula andyshon.shkatula@gmail.com
 * @since 03.03.2021
 */
class Item {
    int pos;
    Mode type = Mode.NONE;

    public Item(int pos) {
        this.pos = pos;
    }
}
