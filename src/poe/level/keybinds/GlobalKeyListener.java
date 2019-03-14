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
import java.util.Arrays;
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
        static String[] recipes_hotkey_mark_key;
        static String[] recipe_hotkey_preview_key;
        static String[] level_hotkey_beta_next_key;
        static String[] level_hotkey_beta_previous_key;
        static String[] lock_keybinds_hotkey_key;
        static HashSet<String> level_hotkey_remind_set;
        static HashSet<String> zones_hotkey_show_hide_set;
        static HashSet<String> recipes_hotkey_mark_set;
        static HashSet<String> recipe_hotkey_preview_key_set;
        static HashSet<String> level_hotkey_beta_next_key_set;
        static HashSet<String> level_hotkey_beta_previous_key_set;
        static HashSet<String> lock_keybinds_hotkey_key_set;
        
        @Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		//System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
                mapper.add(e.getKeyCode());
                String input = "Key Pressed: ";
                for(Integer a : mapper){
                    input+= "+ " + NativeKeyEvent.getKeyText(a);
                }
                //System.out.println(input);
                handle();
	}

        @Override
	public void nativeKeyReleased(NativeKeyEvent e) {
            mapper.remove(e.getKeyCode());
            //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

        @Override
	public void nativeKeyTyped(NativeKeyEvent e) {
            //System.out.println("Key Typed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

        public void handle(){
            if(!Controller.LOCK) {
                if (mapper.size() == level_hotkey_remind_set.size()) {
                    HashSet<String> temp = new HashSet<>();
                    for (Integer vk : mapper) {
                        temp.add(NativeKeyEvent.getKeyText(vk));
                    }
                    if (temp.equals(level_hotkey_remind_set)) {
                        //System.out.println("level_hotkey_remind_set triggered");
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
                if (mapper.size() == recipes_hotkey_mark_set.size()) {
                    HashSet<String> temp = new HashSet<>();
                    for (Integer vk : mapper) {
                        temp.add(NativeKeyEvent.getKeyText(vk));
                    }
                    if (temp.equals(recipes_hotkey_mark_set)) {
                       // System.out.println("recipes_hotkey_mark_set triggered");
                        Controller.instance.recipe_hotkey_mark_key_event(); //<
                    }
                }
                if (mapper.size() == recipe_hotkey_preview_key_set.size()) {
                    HashSet<String> temp = new HashSet<>();
                    for (Integer vk : mapper) {
                        temp.add(NativeKeyEvent.getKeyText(vk));
                    }
                    if (temp.equals(recipe_hotkey_preview_key_set)) {
                        //System.out.println("recipe_hotkey_preview_key_set triggered");
                        Controller.instance.recipe_hotkey_preview_key_event(); //<
                    }
                }
                if (mapper.size() == level_hotkey_beta_next_key_set.size()) {
                    HashSet<String> temp = new HashSet<>();
                    for (Integer vk : mapper) {
                        temp.add(NativeKeyEvent.getKeyText(vk));
                    }
                    if (temp.equals(level_hotkey_beta_next_key_set)) {
                        //System.out.println("level_hotkey_beta_next_key_set triggered");
                        Controller.instance.gem_gui_next_event(); //<
                    }
                }
                if (mapper.size() == level_hotkey_beta_previous_key_set.size()) {
                    HashSet<String> temp = new HashSet<>();
                    for (Integer vk : mapper) {
                        temp.add(NativeKeyEvent.getKeyText(vk));
                    }
                    if (temp.equals(level_hotkey_beta_previous_key_set)) {
                        //System.out.println("level_hotkey_beta_previous_key_set triggered");
                        Controller.instance.gem_gui_previous_event(); //<
                    }
                }
            }
            if(mapper.size() == lock_keybinds_hotkey_key_set.size()){
                HashSet<String> temp = new HashSet<>();
                for(Integer vk : mapper){
                    temp.add(NativeKeyEvent.getKeyText(vk));
                }
                if(temp.equals(lock_keybinds_hotkey_key_set)){
                    System.out.println("lock_keybinds_hotkey_key_set triggered");
                    toggleLockKeybind(); //<
                }
            }
        }


    private static void toggleLockKeybind(){
        Controller.LOCK = !Controller.LOCK;
    }

	public static void run() {
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
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

                setUpKeybinds();
		GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
	}

	public static void setUpKeybinds(){
        mapper = new HashSet<>();
        level_hotkey_remind_key = Preferences_Controller.level_hotkey_remind_key.toString().split("\\+");
        zones_hotkey_show_hide_key = Preferences_Controller.zones_hotkey_show_hide_key.toString().split("\\+");
        recipes_hotkey_mark_key = Preferences_Controller.recipe_hotkey_mark_key.toString().split("\\+");
        recipe_hotkey_preview_key = Preferences_Controller.recipe_hotkey_preview_key.toString().split("\\+");
        level_hotkey_beta_next_key = Preferences_Controller.level_hotkey_beta_next_key.toString().split("\\+");
        level_hotkey_beta_previous_key = Preferences_Controller.level_hotkey_beta_previous_key.toString().split("\\+");
        lock_keybinds_hotkey_key = Preferences_Controller.lock_keybinds_hotkey_key.toString().split("\\+");

        level_hotkey_remind_set = new HashSet<>();
        zones_hotkey_show_hide_set = new HashSet<>();
        recipes_hotkey_mark_set = new HashSet<>();
        recipe_hotkey_preview_key_set = new HashSet<>();
        level_hotkey_beta_next_key_set = new HashSet<>();
        level_hotkey_beta_previous_key_set = new HashSet<>();
        lock_keybinds_hotkey_key_set = new HashSet<>();

        level_hotkey_remind_set.addAll(Arrays.asList(level_hotkey_remind_key));
        zones_hotkey_show_hide_set.addAll(Arrays.asList(zones_hotkey_show_hide_key));
        recipes_hotkey_mark_set.addAll(Arrays.asList(recipes_hotkey_mark_key));
        recipe_hotkey_preview_key_set.addAll(Arrays.asList(recipe_hotkey_preview_key));
        level_hotkey_beta_next_key_set.addAll(Arrays.asList(level_hotkey_beta_next_key));
        level_hotkey_beta_previous_key_set.addAll(Arrays.asList(level_hotkey_beta_previous_key));
        lock_keybinds_hotkey_key_set.addAll(Arrays.asList(lock_keybinds_hotkey_key));
    }
}