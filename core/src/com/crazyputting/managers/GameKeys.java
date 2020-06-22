package com.crazyputting.managers;

public class GameKeys {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int ENTER = 4;
    public static final int ESCAPE = 5;
    public static final int SPACE = 6;
    private static final int NUM_KEYS = 7;
    private static final boolean[] keys;
    private static final boolean[] pkeys;

    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        System.arraycopy(keys, 0, pkeys, 0, NUM_KEYS);
    }

    public static void setKey(int k, boolean b) {
        keys[k] = b;
    }

    public static boolean isDown(int k) {
        return keys[k];
    }

    public static boolean isPressed(int k) {
        return keys[k] && !pkeys[k];
    }

}
