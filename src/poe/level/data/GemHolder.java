/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    
    
    private static GemHolder mInstance;
    private Gem dummie;
    public String className;
    private HashMap<String,ArrayList<HashMap<Zone,ArrayList<Gem>>>> the_ultimate_map;
    private ArrayList<HashMap<Zone,ArrayList<Gem>>> zone_gems;
  
    public static synchronized GemHolder getInstance() {
    if (mInstance == null ) {
      mInstance = new GemHolder();
    }
    return mInstance;
  }
    
    public GemHolder(){
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
    
    public void putGem(Gem a){
        gems.add(a);
        
        if(a.available_to.size()>0){
            for(String ch : a.available_to){
                if(ch.equals("witch")){
                    if(a.getGemName().equals("Arcane Surge")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        witch.add(duped);
                    }else{
                        witch.add(a);
                    }
                }else if(ch.equals("shadow")){
                    if(a.getGemName().equals("Lesser Poison")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        shadow.add(duped);
                    }else{
                        shadow.add(a);
                    }
                }else if(ch.equals("templar")){
                    if(a.getGemName().equals("Elemental Proliferation")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        templar.add(duped);
                    }else{
                        templar.add(a);
                    }
                }else if(ch.equals("ranger")){
                    if(a.getGemName().equals("Pierce")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        ranger.add(duped);
                    }else{
                        ranger.add(a);
                    }
                }else if(ch.equals("duelist")){
                    if(a.getGemName().equals("Chance to Bleed")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        duelist.add(duped);
                    }else{
                        duelist.add(a);
                    }
                }else if(ch.equals("marauder")){
                    if(a.getGemName().equals("Ruthless")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        marauder.add(duped);
                    }else{
                        marauder.add(a);
                    }
                }else if(ch.equals("scion")){
                    if(a.getGemName().equals("Onslaught")){
                        Gem duped = a.dupeGem();
                        duped.quest_name = "Enemy at the Gate";
                        scion.add(duped);
                    }else{
                        scion.add(a);
                    }
                }
            }
        }
    }

    public ArrayList<Gem> getGems(){
        return gems;
    }
    
    public ArrayList<Gem> getGemsClass(){
        
        if(className.toUpperCase().equals("WITCH")){
            return witch;
        }else if(className.toUpperCase().equals("SHADOW")){
            return shadow;
        }else if(className.toUpperCase().equals("TEMPLAR")){
            return templar;
        }else if(className.toUpperCase().equals("RANGER")){
            return ranger;
        }else if(className.toUpperCase().equals("DUELIST")){
            return duelist;
        }else if(className.toUpperCase().equals("MARAUDER")){
           return marauder;
        }else if(className.toUpperCase().equals("SCION")){
            return scion;
        }
        return null;
    }
    
    public ArrayList<Gem> getGemOther(){
        if(className.toUpperCase().equals("WITCH")){
            if(witchO.isEmpty()){
                for(Gem g : gems){
                    if(!witch.contains(g)){
                        witchO.add(g);
                    }
                }
                
            }
            return witchO;
        }else if(className.toUpperCase().equals("SHADOW")){
            if(shadowO.isEmpty()){
                for(Gem g : gems){
                    if(!shadow.contains(g)){
                        shadowO.add(g);
                    }
                }
                
            }
            return shadowO;
        }else if(className.toUpperCase().equals("TEMPLAR")){
            if(templarO.isEmpty()){
                for(Gem g : gems){
                    if(!templar.contains(g)){
                        templarO.add(g);
                    }
                }
                
            }
            return templarO;
        }else if(className.toUpperCase().equals("RANGER")){
            if(witchO.isEmpty()){
                for(Gem g : gems){
                    if(!ranger.contains(g)){
                        rangerO.add(g);
                    }
                }
                
            }
            return rangerO;
        }else if(className.toUpperCase().equals("DUELIST")){
            if(duelistO.isEmpty()){
                for(Gem g : gems){
                    if(!duelist.contains(g)){
                        duelistO.add(g);
                    }
                }
                
            }
            return duelistO;
        }else if(className.toUpperCase().equals("MARAUDER")){
            if(marauderO.isEmpty()){
                for(Gem g : gems){
                    if(!marauder.contains(g)){
                        marauderO.add(g);
                    }
                }
                
            }
           return marauderO;
        }else if(className.toUpperCase().equals("SCION")){
            if(scionO.isEmpty()){
                for(Gem g : gems){
                    if(!scion.contains(g)){
                        scionO.add(g);
                    }
                }
                
            }
            return scionO;
        }
        return null;
    }

    public ArrayList<HashMap<Zone,ArrayList<Gem>>> getAll(){
        if(the_ultimate_map.get(className.toLowerCase()) == null){
            the_ultimate_map.put(className.toLowerCase(),getGemClassAndQuest());
        }
        return the_ultimate_map.get(className.toLowerCase());
    }
    
    public ArrayList<HashMap<Zone,ArrayList<Gem>>> getGemClassAndQuest(){
        if(zone_gems == null){
            HashSet<String> avoidDuplicate = new HashSet<>();
            zone_gems = new ArrayList<>();
            ArrayList<Gem> initial = GemHolder.getInstance().getGemsClass();
            //ArrayList<Gem> newList = new ArrayList<>();
            for(int i=0; i<4; i++){
                HashMap<Zone,ArrayList<Gem>> map = new HashMap<>();
                for(Zone z : ActHandler.getInstance().getActs().get(i).zones){
                    if(z.questRewardsSkills && !avoidDuplicate.contains(z.quest)){
                        avoidDuplicate.add(z.quest);
                        ArrayList<Gem> zoneList = new ArrayList<>();
                        for(Gem g : initial){
                            if(g.quest_name == null){
                                System.out.println(g.getGemName());
                            }else{
                                if(g.quest_name.equals(z.quest)){
                                    zoneList.add(g);
                                }
                            }
                        }
                        map.put(z, zoneList);
                    }
                }
                zone_gems.add(map);
            }
        }
        
        
        return zone_gems;
    }
    
    
    public Gem tossDummie(){
        return dummie;
    }
    
    public Gem createGemFromCache(String gemName){
        for(Gem g : gems){
            if(g.getGemName().equals(gemName)){
                return g.dupeGem();
            }
        }
        return null;
    }
    
    public ArrayList<Gem> custom(String query){
        String query_lower = query.toLowerCase();
        ArrayList<Gem> list = new ArrayList<>();
        for(Gem g : getGemsClass()){
            String gem_lower = g.getGemName().toLowerCase();
            if(gem_lower.contains(query_lower)){
                list.add(g);
            }
        }
        return list;
    }
}
