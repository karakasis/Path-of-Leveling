/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.io.File;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import poe.level.fx.Main_Stage;
import poe.level.fx.POELevelFx;
import poe.level.fx.Preferences_Controller;
import poe.level.fx.overlay.*;


/**
 *
 * @author Xrhstos
 */
public class Controller {

    public static Controller instance;

    public void zones_hotkey_show_hide_key_event(){
        if(!zone_stage_lock)
            Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        zone_stage.event_show_hide();
                    }
                });
    }

    public void level_hotkey_remind_key_event(){
        if(!level_stage_lock)
            Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        level_stage.event_remind();
                    }
                });
    }

    public void recipe_hotkey_mark_key_event(){
        if(!zone_stage_lock){
            if(zone_checkpoint!=null
            && zone_checkpoint.hasRecipe
            && ActHandler.getInstance().recipeMap.get(zone_checkpoint) == false){
                //need to save to file and change the map in the method below
                Preferences_Controller.updateRecipeFile(zone_checkpoint);

                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        zone_stage.event_mark_recipe();
                    }
                });
            }

        }
    }

    public void refreshRecipePopup(){
        if(!zone_stage_lock){
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    placeholder_stageGameMode.close();
                    placeholder_stageGameMode.loadRecipes();
                    RecipeOverlay_Controller.gameModeOn = true;
                }
            });
        }
    }

    public void recipe_hotkey_preview_key_event(){
        if(!zone_stage_lock){
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        placeholder_stageGameMode.loadRecipes();
                        RecipeOverlay_Controller.gameModeOn = true;
                        Preferences_Controller.gameModeOn = false;
                        placeholder_stageGameMode.show();
                    }
                });

        }
    }

    public void closePlaceholderStage() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                placeholder_stageGameMode.close();
            }
        });
    }
    public void settings_event(){
        Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    placeholder_stageGameMode.loadSettings();
                    Preferences_Controller.gameModeOn = true;
                    RecipeOverlay_Controller.gameModeOn = false;
                    placeholder_stageGameMode.manualDisableUIToggle();
                    placeholder_stageGameMode.show();
                }
            });
    }

    public void gem_gui_next_event(){
        if(Preferences_Controller.gem_UI_toggle){
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    level_stage.slideBeta(1); //next goes to right
                    //System.out.println("consumed_next");
                }
            });
        }
    }

    public void gem_gui_previous_event(){
        if(Preferences_Controller.gem_UI_toggle){
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    level_stage.slideBeta(0); //previous goes to left
                    //System.out.println("consumed_previous");
                }
            });
        }
    }

    public void gemUItoggled(boolean beta){
        if(!level_stage_lock)
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    level_stage.resetFXMLS(beta);
                }
            });
    }

    public static boolean LOCK; //locs all overlays and keybinds
    public static boolean PRESS; //locs all overlays and keybinds
    public int playerLevel;
    public String playerName;
    public int monsterLevel;
    public String currentZone;
    private Tail _tObj;
    String path;

    private ZoneOverlay_Stage zone_stage;
    private LevelOverlay_Stage xp_stage;
    private GemOverlay_Stage level_stage;
    private PlaceholderStageGameMode placeholder_stageGameMode;
    private boolean zone_stage_lock;
    private boolean xp_stage_lock;
    private boolean level_stage_lock;
    private Build build;

    private Zone zone_checkpoint;
    private HashSet<String> duplicates;
    private boolean skipActs;
    private boolean act6detected;
    private boolean releaseLock;

    public static int[] findSafe(int currentLevel){
        int[] safe = new int[3];
        if(currentLevel - 3 <= 0){
            safe[0] = 1;
        }else{
            safe[0]=currentLevel - (currentLevel/16 + 3);
        }
        safe[1]=currentLevel/16 + 3 + currentLevel;
        safe[2]=currentLevel/16 + 3;
        return safe;
    }

    public static int effectiveDifference(int playerlvl, int arealevel, int safezone){
        return (Math.max(Math.abs(playerlvl-arealevel) - safezone,0));
    }

    public static double xpmultiplier(int effectiveDiff,int playerlvl){
        double effpow = Math.pow(effectiveDiff,2.5);
        double leftInner = (playerlvl + 5) / (playerlvl + 5 + effpow);
        double leftPow = Math.pow(leftInner,1.5);
        if(playerlvl<95)
            //return Math.max(Math.pow( playerlvl+5/ (playerlvl+5+ Math.pow( effectiveDiff,2.5)) , 1.5),0.01);
            return Math.max(leftPow,0.01)*100;
        else
            return Math.max(leftPow * (1 / (1 + 0.1*(playerlvl-94))),0.01)*100;
    }

    public static double findxpmulti(int playerlvl,int arealvl){
        int safe = findSafe(playerlvl)[2];
        int effDif = effectiveDifference(playerlvl,arealvl,safe);
        double a = xpmultiplier(effDif,playerlvl);
        //System.out.println(" for level "+playerlvl+" safe zone is " + safe + " and eff dif is "+ effDif);
        //System.out.println("xp multi is "+(int)a+"%");
        return a;
    }

    public static double height;
    public static double width;
    //public Controller(Stage zone, Stage xp, Stage level, Build build) {
    public Controller(boolean zone_b, boolean xp, boolean level, Build build) {
        LOCK = false;
        PRESS = false;
        instance = this;
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        height = primScreenBounds.getHeight();
        width = primScreenBounds.getWidth();
        if(zone_b){
            zone_stage = new ZoneOverlay_Stage();
        }
        if(xp){
            xp_stage = new LevelOverlay_Stage();
        }
        if(level){
            level_stage = new GemOverlay_Stage(build,Preferences_Controller.gem_UI_toggle);
        }
        System.out.println("Starting a new leveling enviroment. ");
        System.out.println("Zones: " + zone_b);
        System.out.println("XP: " + xp);
        System.out.println("Leveling: " +level);
        placeholder_stageGameMode = new PlaceholderStageGameMode(this);
        /*
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                new Test();
            }
        });*/

        act6detected = false;
        skipActs = false;
        releaseLock = true;
        playerLevel = Main_Stage.playerLevel;
        playerName = Main_Stage.characterName;
        monsterLevel = 1;
        //zone_stage = (ZoneOverlay_Stage) zone;
        //xp_stage = (LevelOverlay_Stage) xp;
        //level_stage = (GemOverlay_Stage) level;
        this.build = build;

        //zone_stage_lock = zone_stage == null;
        //xp_stage_lock = xp_stage == null;
        //level_stage_lock = level_stage == null;
        zone_stage_lock = !zone_b;
        xp_stage_lock= !xp;
        level_stage_lock= !level;

        path = Preferences_Controller.poe_log_dir;

        duplicates = new HashSet<>();
        duplicates.add("The Reliquary");
        duplicates.add("The Ossuary");
        duplicates.add("The Torched Courts");
        duplicates.add("The Lunaris Temple Level 2");
        duplicates.add("The Solaris Temple Level 1");
        duplicates.add("The Solaris Temple Level 2");
        duplicates.add("The Cathedral Rooftop");
        duplicates.add("The Control Blocks");
        duplicates.add("The Fellshrine Ruins");
        duplicates.add("The Den");
        duplicates.add("The Northern Forest");
        duplicates.add("The Dread Thicket");
        duplicates.add("The Lunaris Temple Level 1");
        duplicates.add("The Cavern of Anger");
        duplicates.add("The Broken Bridge");
        duplicates.add("The Crossroads");
        duplicates.add("The Lower Prison");
        duplicates.add("Prisoner's Gate");
        duplicates.add("The Western Forest");
        duplicates.add("The Riverways");
        duplicates.add("The Wetlands");
        duplicates.add("The Southern Forest");
        duplicates.add("The Mud Flats");
        duplicates.add("The Twilight Strand");
        duplicates.add("The Coast");
        duplicates.add("The Chamber of Sins Level 1");
        duplicates.add("The Chamber of Sins Level 2");

        start();

    }

    public void start(){
        _tObj = new Tail();
        //true
        if (!POELevelFx.DEBUG) {
            _tObj.setUpTailer(new File(path), this);
        } else {
            // Choose your poison (default to the game's log)
            //
                       //_tObj.setUpTailer(new File(path), this);
            _tObj.setUpTailer(new File("src/logs.txt"), this);
            //_tObj.setUpTailer(new File(path), this);
        }

        //manually input the level 1 gems
        if(!level_stage_lock){
            level_stage.update(playerLevel);
        }
    }


    public void lvlupdate(){
        if(!xp_stage_lock){
            System.out.println("New level is "+ playerLevel+".");
            if(zone_checkpoint!=null){
                xp_stage.update(playerLevel,zone_checkpoint.getZoneLevel());
            }
        }
        if(!level_stage_lock){
            level_stage.update(playerLevel);
            if(playerLevel == 2){
                level_stage.update(1); //remind gems on hillock kill
            }
        }
        if(build!=null){
            build.setCharacterLevel(playerLevel);
        }
    }

    public void zoneupdate(){
        boolean zoneDetect = false;
        if(playerLevel>=38 && releaseLock){
            if(currentZone.equals("Lioneye's Watch")){
                act6detected = true;
            }
            if((currentZone.equals("The Twilight Strand") || currentZone.equals("The Coast")) && act6detected){
                skipActs = true;
                releaseLock = false;
            }
        }
        if(playerLevel>=50){
            skipActs = true;
            releaseLock = false;
        }

        System.out.println("Trying to identify zone.");
        //add a reset thing for when zone in uknown
        //System.out.println("New zone is "+ currentZone+".");
        boolean skippedFirstPart = false;
        outerloop:
        for(Act a : ActHandler.getInstance().getActs()){
            for(Zone zone : a.getZones()){
                if (zone.name.equals(currentZone)){
                    if(duplicates.contains(currentZone) && skipActs && !skippedFirstPart){
                        skippedFirstPart = true;
                        continue;
                    }
                    zone_checkpoint = zone;
                    monsterLevel = zone.level;
                    zoneDetect = true;
                    System.out.println("Zone identified. " + zone.name);
                    if(zone.hasPassive){
                        System.out.println("This zone contains a passive skill quest.");
                    }
                    if(zone.hasTrial){
                        System.out.println("This zone contains a lab trial.");
                    }

                    break outerloop;
                }
            }
        }
        if(zoneDetect ){
            if(!zone_stage_lock){
                zone_stage.queue(zone_checkpoint);
            }
            if(!xp_stage_lock){
                xp_stage.update(playerLevel,zone_checkpoint.getZoneLevel());
            }
        }else{
            if(!xp_stage_lock){
                xp_stage.reset(playerLevel);
            }
            if(!zone_stage_lock){
                zone_stage.reset();
            }
        }
    }

}
