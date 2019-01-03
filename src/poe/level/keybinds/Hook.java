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
import java.awt.AWTKeyStroke;
import java.util.Map.Entry;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javax.swing.KeyStroke;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import poe.level.data.Controller;
import poe.level.fx.Preferences_Controller;

public class Hook {
	private static boolean run = true;
	public static void run() {
		// might throw a UnsatisfiedLinkError if the native library fails to load or a RuntimeException if hooking fails 
		GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input

		System.out.println("Global keyboard hook successfully started, press [escape] key to shutdown. Connected keyboards:");
		for(Entry<Long,String> keyboard:GlobalKeyboardHook.listKeyboards().entrySet())
			System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());
		
		keyboardHook.addKeyListener(new GlobalKeyAdapter() {
			@Override 
                        public void keyPressed(GlobalKeyEvent event) {
                            
                            KeyCombination handle = handle(event);
                            if(handle!=null){
                                if(handle.getDisplayText().toUpperCase().equals(
                                    Preferences_Controller.zones_hotkey_show_hide_key.getDisplayText().toUpperCase())){
                                    System.out.println("Match ");
                                    Controller.instance.zones_hotkey_show_hide_key_event();
                                }else{
                                    System.out.println(handle.getDisplayText().toUpperCase() +" generated");
                                    System.out.println(Preferences_Controller.zones_hotkey_show_hide_key
                                            .getDisplayText().toUpperCase());
                                }
                                
                                if(handle.getDisplayText().toUpperCase().equals(
                                    Preferences_Controller.level_hotkey_remind_key.getDisplayText().toUpperCase())){
                                    System.out.println("Match ");
                                    Controller.instance.level_hotkey_remind_key_event();
                                }else{
                                    System.out.println(handle.getDisplayText().toUpperCase() +" generated");
                                    System.out.println(Preferences_Controller.level_hotkey_remind_key
                                            .getDisplayText().toUpperCase());
                                }
                            }
                            test();
                            
                            //event.
			}
			@Override 
                        public void keyReleased(GlobalKeyEvent event) {
                            //System.out.println(event); 
                        }
                        
		});
		/*
		try {
			while(run) Thread.sleep(128);
		} catch(InterruptedException e) {   }
		  finally { keyboardHook.shutdownHook(); }*/

	}
        
        public static KeyCombination handle(GlobalKeyEvent event){
            String key_bind = "";
            KeyCombination keyCombination = null;
            AWTKeyStroke keyStroke = KeyStroke.getAWTKeyStroke(event.getVirtualKeyCode(), 0);
            String ks_string = keyStroke.toString().replace("pressed", "").trim();
            if(event.isControlPressed()){
                key_bind = "Ctrl+";
                if(ks_string.equals("Ctrl")){
                    key_bind = "Ctrl";
                }else{
                    key_bind += ks_string;
                }

            }else if(event.isShiftPressed()){
                key_bind = "Shift+";
                if(ks_string.equals("Shift")){
                    key_bind = "Shift";
                }else{
                    key_bind += ks_string;
                }

            }else{
                key_bind += ks_string;
            }
            try{
                keyCombination = KeyCombination.keyCombination(key_bind);
                //System.out.println("key code : " + keyCombination.getName());
                //zones_hotkey_show_hide_key = keyCombination;
            }catch(IllegalArgumentException e){
                System.out.println(":incorect:");
                //zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
            }finally{
                return keyCombination;
            }
        }
        
        public void handleCode(GlobalKeyEvent event){
            if(Preferences_Controller.level_hotkey_remind_key.toString().contains("\\+")){
                String[] split = Preferences_Controller.level_hotkey_remind_key.toString().split("\\+");
                if(split.length>=3){

                }else{
                    String prefix = split[0];
                    String suffix = split[1];
                    AWTKeyStroke ks = KeyStroke.getAWTKeyStroke(suffix.charAt(0));
                    if(ks.getKeyCode() == event.getVirtualKeyCode()){
                        if(prefix.equals("Shift") && event.isShiftPressed()){
                            System.out.println("Match ");
                        }else if(prefix.equals("Control") && event.isControlPressed()){

                            System.out.println("Match ");
                        }else{

                            System.out.println("NO Match ");
                        }
                    }else{
                        System.out.println("NO Match ");
                    }
                }
            }else{
                AWTKeyStroke ks = KeyStroke.getAWTKeyStroke(
                        Preferences_Controller.level_hotkey_remind_key.toString().charAt(0));
                if(ks.getKeyCode() == event.getVirtualKeyCode()){
                    System.out.println("Match ");
                }else{
                    System.out.println("NO Match ");
                }
            }
        }
        
        public static void test(){
            String key_bind = "";
            KeyCombination keyCombination = null;
            for(int i=0 ; i<=254; i++){
                key_bind = "";
                AWTKeyStroke keyStroke = KeyStroke.getAWTKeyStroke(i, 0);
                String ks_string = keyStroke.toString().replace("pressed", "").trim();

                key_bind += ks_string;
            try{
                keyCombination = KeyCombination.keyCombination(key_bind);
                System.out.println("key code : " + keyCombination.getName());
                //zones_hotkey_show_hide_key = keyCombination;
            }catch(IllegalArgumentException e){
                System.out.println(":incorect:");
                //zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
            }
            }
            
        }
}