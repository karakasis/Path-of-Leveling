/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.keybinds;

/**
 *
 * @author Christos
 */
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import poe.level.data.Controller;
import poe.level.fx.Preferences_Controller;

public class GlobalKeyListener implements NativeKeyListener {
    static HashSet<Integer> mapper;
    static String[] level_hotkey_remind_key;
    static String[] zones_hotkey_show_hide_key;
    static HashSet<String> level_hotkey_remind_set;
    static HashSet<String> zones_hotkey_show_hide_set;

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        mapper.add(e.getKeyCode());
        handle();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        mapper.remove(e.getKeyCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public void handle() {
        if (mapper.size() == level_hotkey_remind_set.size()) {
            HashSet<String> temp = new HashSet<>();
            for (Integer vk : mapper) {
                temp.add(NativeKeyEvent.getKeyText(vk));
            }
            if (temp.equals(level_hotkey_remind_set)) {
                System.out.println("level_hotkey_remind_set triggered");
                Controller.instance.level_hotkey_remind_key_event();
            }
        }
        if (mapper.size() == zones_hotkey_show_hide_set.size()) {
            HashSet<String> temp = new HashSet<>();
            for (Integer vk : mapper) {
                temp.add(NativeKeyEvent.getKeyText(vk));
            }
            if (temp.equals(zones_hotkey_show_hide_set)) {
                System.out.println("zones_hotkey_show_hide_set triggered");
                Controller.instance.zones_hotkey_show_hide_key_event();
            }
        }
    }

    public static void run() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        // Change the level for all handlers attached to the default logger.
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(Level.OFF);
        }
        mapper = new HashSet<>();
        level_hotkey_remind_key = Preferences_Controller.level_hotkey_remind_key.toString().split("\\+");
        zones_hotkey_show_hide_key = Preferences_Controller.zones_hotkey_show_hide_key.toString().split("\\+");
        level_hotkey_remind_set = new HashSet<>();
        zones_hotkey_show_hide_set = new HashSet<>();
        for (int i = 0; i < level_hotkey_remind_key.length; i++) {
            level_hotkey_remind_set.add(level_hotkey_remind_key[i]);
        }
        for (int i = 0; i < zones_hotkey_show_hide_key.length; i++) {
            zones_hotkey_show_hide_set.add(zones_hotkey_show_hide_key[i]);
        }
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }
}