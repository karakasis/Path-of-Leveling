/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.io.File;
import java.util.HashSet;

import javafx.application.Platform;
import poe.level.fx.Main_Stage;
import poe.level.fx.overlay.GemOverlay_Stage;
import poe.level.fx.overlay.LevelOverlay_Stage;
import poe.level.fx.overlay.ZoneOverlay_Stage;

/**
 *
 * @author Xrhstos
 */
public class Controller {

    public static Controller instance;

    public void zones_hotkey_show_hide_key_event() {
        if (!zone_stage_lock)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    zone_stage.event_show_hide();
                }
            });
    }

    public void level_hotkey_remind_key_event() {
        if (!level_stage_lock)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    level_stage.event_remind();
                }
            });
    }

    public int playerLevel;
    public String playerName;
    public String currentZone;
    private Tail _tObj;

    private ZoneOverlay_Stage zone_stage;
    private LevelOverlay_Stage xp_stage;
    private GemOverlay_Stage level_stage;
    private boolean zone_stage_lock;
    private boolean xp_stage_lock;
    private boolean level_stage_lock;
    private Build build;

    private Zone zone_checkpoint;
    private HashSet<String> duplicates;
    private boolean skipActs;
    private boolean act6detected;
    private boolean releaseLock;

    public static int[] findSafe(int currentLevel) {
        int[] safe = new int[3];
        if (currentLevel - 3 <= 0) {
            safe[0] = 1;
        } else {
            safe[0] = currentLevel - (currentLevel / 16 + 3);
        }
        safe[1] = currentLevel / 16 + 3 + currentLevel;
        safe[2] = currentLevel / 16 + 3;
        return safe;
    }

    public static int effectiveDifference(int playerlvl, int arealevel, int safezone) {
        return (Math.max(Math.abs(playerlvl - arealevel) - safezone, 0));
    }

    public static double xpmultiplier(int effectiveDiff, int playerlvl) {
        double effpow = Math.pow(effectiveDiff, 2.5);
        double leftInner = (playerlvl + 5) / (playerlvl + 5 + effpow);
        double leftPow = Math.pow(leftInner, 1.5);
        if (playerlvl < 95)
            // return Math.max(Math.pow( playerlvl+5/ (playerlvl+5+ Math.pow(
            // effectiveDiff,2.5)) , 1.5),0.01);
            return Math.max(leftPow, 0.01) * 100;
        else
            return Math.max(leftPow * (1 / (1 + 0.1 * (playerlvl - 94))), 0.01) * 100;
    }

    public static double findxpmulti(int playerlvl, int arealvl) {
        int safe = findSafe(playerlvl)[2];
        int effDif = effectiveDifference(playerlvl, arealvl, safe);
        double a = xpmultiplier(effDif, playerlvl);
        System.out.println(" for level " + playerlvl + " safe zone is " + safe + " and eff dif is " + effDif);
        System.out.println("xp multi is " + (int) a + "%");
        return a;
    }

    public Controller(boolean zone_b, boolean xp, boolean level, Build build) {
        instance = this;
        if (zone_b) {
            zone_stage = new ZoneOverlay_Stage();
        }
        if (xp) {
            xp_stage = new LevelOverlay_Stage();
        }
        if (level) {
            level_stage = new GemOverlay_Stage(build);
        }
        act6detected = false;
        skipActs = false;
        releaseLock = true;
        playerLevel = Main_Stage.playerLevel;
        playerName = Main_Stage.characterName;
        this.build = build;

        zone_stage_lock = !zone_b;
        xp_stage_lock = !xp;
        level_stage_lock = !level;

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

    public void start() {
        _tObj = new Tail();
        // _tObj.setUpTailer(new
        // File("C:\\Users\\Christos\\Documents\\NetBeansProjects\\POE-level-fx\\src\\a.txt"),
        // this);
        _tObj.setUpTailer(new File("src/logs.txt"), this);
        // _tObj.setUpTailer(new File(path), this);

        // manually input the level 1 gems
        if (playerLevel == 1 && !level_stage_lock) {
            level_stage.update(playerLevel);
        }
    }

    public void lvlupdate() {
        if (!xp_stage_lock) {
            System.out.println("New level is " + playerLevel + ".");
            if (zone_checkpoint != null) {
                xp_stage.update(playerLevel, zone_checkpoint.getZoneLevel());
            }
        }
        if (!level_stage_lock) {
            level_stage.update(playerLevel);
            if (playerLevel == 2) {
                level_stage.update(1); // remind gems on hillock kill
            }
        }
        if (build != null) {
            build.level = playerLevel;
        }
    }

    public void zoneupdate() {
        boolean zoneDetect = false;
        if (playerLevel >= 38 && releaseLock) {
            if (currentZone.equals("Lioneye's Watch")) {
                act6detected = true;
            }
            if ((currentZone.equals("The Twilight Strand") || currentZone.equals("The Coast")) && act6detected) {
                skipActs = true;
                releaseLock = false;
            }
        }
        if (playerLevel >= 50) {
            skipActs = true;
            releaseLock = false;
        }

        System.out.println("Trying to identify zone.");
        // add a reset thing for when zone in uknown
        boolean skippedFirstPart = false;
        outerloop: for (Act a : ActHandler.getInstance().getActs()) {
            for (Zone zone : a.getZones()) {
                if (zone.name.equals(currentZone)) {
                    if (duplicates.contains(currentZone) && skipActs && !skippedFirstPart) {
                        skippedFirstPart = true;
                        continue;
                    }
                    zone_checkpoint = zone;
                    zoneDetect = true;
                    System.out.println("Zone identified.");
                    if (zone.hasPassive) {
                        System.out.println("This zone contains a passive skill quest.");
                    }
                    if (zone.hasTrial) {
                        System.out.println("This zone contains a lab trial.");
                    }

                    break outerloop;
                }
            }
        }
        if (zoneDetect) {
            if (!zone_stage_lock) {
                zone_stage.queue(zone_checkpoint);
            }
            if (!xp_stage_lock) {
                xp_stage.update(playerLevel, zone_checkpoint.getZoneLevel());
            }
        } else {
            if (!xp_stage_lock) {
                xp_stage.reset(playerLevel);
            }
            if (!zone_stage_lock) {
                zone_stage.reset();
            }
        }
    }

}
