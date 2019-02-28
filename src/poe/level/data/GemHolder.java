/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import poe.level.data.Gem.Info;

/**
 *
 * @author Christos
 */
public class GemHolder {
    public ArrayList<Gem> gems;

    private ArrayList<Gem> witch;
    private ArrayList<Gem> shadow;
    private ArrayList<Gem> templar;
    private ArrayList<Gem> ranger;
    private ArrayList<Gem> duelist;
    private ArrayList<Gem> marauder;
    private ArrayList<Gem> scion;

    private ArrayList<Gem> dropOnly;

    private static GemHolder mInstance;
    private Gem dummie;
    public String className;
    private HashMap<String, ArrayList<HashMap<Zone, ArrayList<Gem>>>> the_ultimate_map;
    private ArrayList<HashMap<Zone, ArrayList<Gem>>> zone_gems;

    public static synchronized GemHolder getInstance() {
        if (mInstance == null) {
            mInstance = new GemHolder();
        }
        return mInstance;
    }

    public GemHolder() {
        gems = new ArrayList<>();
        witch = new ArrayList<>();
        shadow = new ArrayList<>();
        templar = new ArrayList<>();
        ranger = new ArrayList<>();
        duelist = new ArrayList<>();
        marauder = new ArrayList<>();
        scion = new ArrayList<>();

        dropOnly = new ArrayList<>();

        dummie = new Gem();
        dummie.name = "<empty group>";

        the_ultimate_map = new HashMap<>();
        the_ultimate_map.put("witch", null);
        the_ultimate_map.put("shadow", null);
        the_ultimate_map.put("templar", null);
        the_ultimate_map.put("ranger", null);
        the_ultimate_map.put("duelist", null);
        the_ultimate_map.put("marauder", null);
        the_ultimate_map.put("scion", null);
    }

    private void placeGemInClass(Gem a, String ch) {
        // ch = classname
        // a = gem object
        if (ch.equals("Witch")) {
            witch.add(a);
        } else if (ch.equals("Shadow")) {
            shadow.add(a);
        } else if (ch.equals("Templar")) {
            templar.add(a);

        } else if (ch.equals("Ranger")) {
            ranger.add(a);
        } else if (ch.equals("Duelist")) {
            duelist.add(a);
        } else if (ch.equals("Marauder")) {
            marauder.add(a);
        } else if (ch.equals("Scion")) {
            scion.add(a);
        }
    }

    public void putGem(Gem a) {
        gems.add(a);
        if (a.isRewarded) {
        } // not configured this feature yet.
        if (a.isBought()) {
            // class to quest name
            HashMap<String, ArrayList<String>> mm = new HashMap<>();
            for (Info inf : a.buy) {
                for (String class_ : inf.available_to) {
                    if (mm.containsKey(class_)) {
                        mm.get(class_).add(inf.quest_name);
                    } else {
                        ArrayList<String> q = new ArrayList<>();
                        q.add(inf.quest_name);
                        mm.put(class_, q);
                    }
                }
            }
            boolean classCanGetIt;
            for (String class_ : mm.keySet()) {
                classCanGetIt = false;
                String[] questline = Zone.questline;
                for (int i = 0; i < questline.length; i++) {
                    if (mm.get(class_).contains(questline[i])) {
                        for (Info inf : a.buy) {
                            if (questline[i].equals(inf.quest_name)) {
                                Gem duped = a.dupeGem();
                                duped.act = inf.act;
                                duped.npc = inf.npc;
                                duped.quest_name = inf.quest_name;
                                duped.available_to = inf.available_to;
                                placeGemInClass(duped, class_);
                                classCanGetIt = true;
                                break;
                            }
                        }
                        break;
                    }
                }
                if (!classCanGetIt) {
                    // System.err.println("CLASS : "+class_+" GEM : "+a.getGemName());
                }
            }

        } else {
            // System.err.println(" GEM : "+a.getGemName() + " not asssigned to any class");
            dropOnly.add(a);
        }

    }

    public ArrayList<Gem> getGems() {
        return gems;
    }

    public ArrayList<Gem> getGemsClass() {

        if (className.toUpperCase().equals("WITCH")) {
            return witch;
        } else if (className.toUpperCase().equals("SHADOW")) {
            return shadow;
        } else if (className.toUpperCase().equals("TEMPLAR")) {
            return templar;
        } else if (className.toUpperCase().equals("RANGER")) {
            return ranger;
        } else if (className.toUpperCase().equals("DUELIST")) {
            return duelist;
        } else if (className.toUpperCase().equals("MARAUDER")) {
            return marauder;
        } else if (className.toUpperCase().equals("SCION")) {
            return scion;
        }
        return null;
    }

    public ArrayList<Gem> getGemOther() {
        /*
         * if(className.toUpperCase().equals("WITCH")){ if(witchO.isEmpty()){ for(Gem g
         * : gems){ if(!witch.contains(g)){ witchO.add(g); } }
         * 
         * } return witchO; }else if(className.toUpperCase().equals("SHADOW")){
         * if(shadowO.isEmpty()){ for(Gem g : gems){ if(!shadow.contains(g)){
         * shadowO.add(g); } }
         * 
         * } return shadowO; }else if(className.toUpperCase().equals("TEMPLAR")){
         * if(templarO.isEmpty()){ for(Gem g : gems){ if(!templar.contains(g)){
         * templarO.add(g); } }
         * 
         * } return templarO; }else if(className.toUpperCase().equals("RANGER")){
         * if(witchO.isEmpty()){ for(Gem g : gems){ if(!ranger.contains(g)){
         * rangerO.add(g); } }
         * 
         * } return rangerO; }else if(className.toUpperCase().equals("DUELIST")){
         * if(duelistO.isEmpty()){ for(Gem g : gems){ if(!duelist.contains(g)){
         * duelistO.add(g); } }
         * 
         * } return duelistO; }else if(className.toUpperCase().equals("MARAUDER")){
         * if(marauderO.isEmpty()){ for(Gem g : gems){ if(!marauder.contains(g)){
         * marauderO.add(g); } }
         * 
         * } return marauderO; }else if(className.toUpperCase().equals("SCION")){
         * if(scionO.isEmpty()){ for(Gem g : gems){ if(!scion.contains(g)){
         * scionO.add(g); } }
         * 
         * } return scionO; } return null;
         */
        return dropOnly;
    }

    public ArrayList<HashMap<Zone, ArrayList<Gem>>> getAll() {
        if (the_ultimate_map.get(className.toLowerCase()) == null) {
            the_ultimate_map.put(className.toLowerCase(), getGemClassAndQuest());
        }
        return the_ultimate_map.get(className.toLowerCase());
    }

    public ArrayList<HashMap<Zone, ArrayList<Gem>>> getGemClassAndQuest() {
        HashSet<String> avoidDuplicate = new HashSet<>();
        zone_gems = new ArrayList<>();
        ArrayList<Gem> initial = GemHolder.getInstance().getGemsClass();
        // ArrayList<Gem> newList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (i == 4)
                continue; // skip act 5
            HashMap<Zone, ArrayList<Gem>> map = new HashMap<>();
            for (Zone z : ActHandler.getInstance().getActs().get(i).zones) {
                if (z.questRewardsSkills && !avoidDuplicate.contains(z.quest)) {
                    avoidDuplicate.add(z.quest);
                    ArrayList<Gem> zoneList = new ArrayList<>();
                    for (Gem g : initial) {
                        if (g.quest_name == null) {
                            System.out.println(g.getGemName());
                        } else {
                            if (g.quest_name.equals(z.quest)) {
                                zoneList.add(g);
                            }
                        }
                    }
                    map.put(z, zoneList);
                }
            }
            zone_gems.add(map);
        }

        return zone_gems;
    }

    public Gem tossDummie() {
        return dummie;
    }

    public Gem createGemFromCache(String gemName, String className) {
        ArrayList<Gem> gems = null;
        if (className.toUpperCase().equals("WITCH")) {
            gems = new ArrayList<>(witch);
        } else if (className.toUpperCase().equals("SHADOW")) {
            gems = new ArrayList<>(shadow);
        } else if (className.toUpperCase().equals("TEMPLAR")) {
            gems = new ArrayList<>(templar);
        } else if (className.toUpperCase().equals("RANGER")) {
            gems = new ArrayList<>(ranger);
        } else if (className.toUpperCase().equals("DUELIST")) {
            gems = new ArrayList<>(duelist);
        } else if (className.toUpperCase().equals("MARAUDER")) {
            gems = new ArrayList<>(marauder);
        } else if (className.toUpperCase().equals("SCION")) {
            gems = new ArrayList<>(scion);
        }
        if (gems != null)
            for (Gem g : gems) {
                if (g.getGemName().equals(gemName)) {
                    return g.dupeGem();
                }
            }
        // if we reach here no gem was found so it is placed on the drop-only
        gems = new ArrayList<>(getGemOther());
        for (Gem g : gems) {
            if (g.getGemName().equals(gemName)) {
                return g.dupeGem();
            }
        }
        return null;
    }
}
