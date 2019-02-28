/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.Gem.Info;
import poe.level.fx.BuildsPanel_Controller;
import poe.level.fx.POELevelFx;

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

    private ArrayList<Gem> witchO;
    private ArrayList<Gem> shadowO;
    private ArrayList<Gem> templarO;
    private ArrayList<Gem> rangerO;
    private ArrayList<Gem> duelistO;
    private ArrayList<Gem> marauderO;
    private ArrayList<Gem> scionO;

    private ArrayList<Gem> dropOnly;

    private static GemHolder mInstance;
    private Gem dummie;
    public String className;
    private HashMap<String, ArrayList<HashMap<Zone, ArrayList<Gem>>>> the_ultimate_map;
    private ArrayList<HashMap<Zone, ArrayList<Gem>>> zone_gems;

    private HashSet<Gem> gem_pool;

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

        witchO = new ArrayList<>();
        shadowO = new ArrayList<>();
        templarO = new ArrayList<>();
        rangerO = new ArrayList<>();
        duelistO = new ArrayList<>();
        marauderO = new ArrayList<>();
        scionO = new ArrayList<>();

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

    public void pool() {
        gem_pool = new HashSet<>();
        for (Gem g : gems) {
            gem_pool.add(g);
        }
    }

    public void init_remaining_in_pool() {
        /*
         * for(Gem g : gem_pool){ g.isRewarded = false; g.reward = null; g.buy = new
         * ArrayList<>(); System.out.println(g.getGemName()); }
         */

        JSONArray gems = new JSONArray();
        Collections.sort(this.gems, new Comparator<Gem>() {
            @Override
            public int compare(Gem s1, Gem s2) {
                return s1.getGemName().compareToIgnoreCase(s2.getGemName());
            }
        });
        int counter = 0;
        for (Gem g : this.gems) {
            JSONObject bObj = new JSONObject();
            bObj.put("id", counter);
            bObj.put("name", g.getGemName());
            bObj.put("required_lvl", g.required_lvl);
            bObj.put("color", g.getGemColor());
            bObj.put("iconPath", g.iconPath);
            bObj.put("isReward", g.isRewarded);
            bObj.put("isVaal", g.isVaal);
            JSONObject rewardObj = new JSONObject();
            if (g.isRewarded) {
                rewardObj.put("quest_name", g.reward.quest_name);
                rewardObj.put("npc", g.reward.npc);
                rewardObj.put("act", g.reward.act);
                rewardObj.put("town", g.reward.town);
                JSONArray av_array = new JSONArray();
                for (String s : g.reward.available_to) {
                    av_array.put(s);
                }
                rewardObj.put("available_to", av_array);
            }
            bObj.put("reward", rewardObj);
            JSONArray buy_array = new JSONArray();
            for (Info gInf : g.buy) {
                JSONObject bInfObj = new JSONObject();
                bInfObj.put("quest_name", gInf.quest_name);
                bInfObj.put("npc", gInf.npc);
                bInfObj.put("act", gInf.act);
                bInfObj.put("town", gInf.town);
                JSONArray av_array = new JSONArray();
                for (String s : gInf.available_to) {
                    av_array.put(s);
                }
                bInfObj.put("available_to", av_array);
                buy_array.put(bInfObj);
            }
            bObj.put("buy", buy_array);
            bObj.put("isActive", g.isActive);
            bObj.put("isSupport", g.isSupport);
            JSONArray tags = new JSONArray();
            for (String gInf : g.tags) {
                tags.put(gInf);
            }
            bObj.put("gemTags", tags);
            gems.put(bObj);
            counter++;
        }

        String gem_to_json = gems.toString();

        // Gson gson = new Gson();
        // String build_to_json = gson.toJson(linker.get(activeBuildID).build);
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(POELevelFx.directory + "\\Path of Leveling\\Gems\\gems_new.json");
            bw = new BufferedWriter(fw);
            bw.write(gem_to_json);
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateGemInfo(ArrayList<String> gemlist) {
        String gemname = gemlist.get(0);
        if (gemname.equals("Siphoning Trap")) {
            System.out.println();
        }
        if (gemname.endsWith("Support")) {
            int lastIndexOf = gemname.lastIndexOf("Support");
            gemname = gemname.substring(0, lastIndexOf - 1);
        }
        boolean found = false;
        for (Gem g : gems) {
            // if(gemname.contains(g.getGemName()) || g.getGemName().contains(gemname)){
            if (gemname.equals(g.getGemName())) {
                found = true;

                if (gemlist.get(1).equals("N/A")) {
                    g.isRewarded = false;
                    g.reward = null;
                } else {
                    String rew_string = gemlist.get(1);
                    int rew_act = Integer.parseInt(rew_string.charAt(4) + "");
                    int indexOf = rew_string.indexOf("with");
                    String rew_act_str = rew_string.substring(12, indexOf).trim();
                    rew_string = rew_string.trim();
                    String classes_str = rew_string.substring(indexOf + 5, rew_string.length() - 1);
                    String[] split = classes_str.split(",");
                    ArrayList<String> av_list = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        av_list.add(split[i].trim());
                    }

                    g.reward = g.new Info();
                    g.reward.act = rew_act;
                    g.reward.npc = "Unknown";
                    g.reward.quest_name = rew_act_str;
                    g.reward.town = "Unknwon";
                    g.reward.available_to = new ArrayList<>(av_list);
                }

                g.buy = new ArrayList<>();

                for (int k = 2; k < gemlist.size(); k++) {

                    String rew_string = gemlist.get(k);
                    int rew_act = Integer.parseInt(rew_string.charAt(4) + "");
                    int indexOf = rew_string.indexOf("with");
                    int indexOf_from = rew_string.lastIndexOf("from");
                    String rew_act_str = rew_string.substring(12, indexOf_from).trim();
                    String npc_name = rew_string.substring(indexOf_from + 5, indexOf - 1).trim();
                    rew_string = rew_string.trim();
                    String classes_str = rew_string.substring(indexOf + 5, rew_string.length() - 1).trim();
                    String[] split;
                    if (classes_str.equals("any character")) {
                        split = new String[] { "Marauder", "Witch", "Scion", "Ranger", "Duelist", "Shadow", "Templar" };
                    } else {
                        split = classes_str.split(",");
                    }
                    ArrayList<String> av_list = new ArrayList<>();
                    for (int i = 0; i < split.length; i++) {
                        av_list.add(split[i].trim());
                    }

                    Info inf = g.new Info();
                    inf.act = rew_act;
                    inf.npc = npc_name;
                    inf.quest_name = rew_act_str;
                    inf.town = "Unknwon";
                    inf.available_to = new ArrayList<>(av_list);
                    g.buy.add(inf);

                }

                gem_pool.remove(g);
                break;
            }
        }
        if (!found) {
            System.out.println("gem : " + gemname + " not found.");
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

    public ArrayList<Gem> custom(String query) {
        String query_lower = query.toLowerCase();
        ArrayList<Gem> list = new ArrayList<>();
        for (Gem g : getGemsClass()) {
            String gem_lower = g.getGemName().toLowerCase();
            if (gem_lower.contains(query_lower)) {
                list.add(g);
            }
        }
        return list;
    }
}
