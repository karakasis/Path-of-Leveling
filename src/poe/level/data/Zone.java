package poe.level.data;
import java.util.ArrayList;

public class Zone {
    public static String[] questline = new String[]{
        "Enemy at the Gate",
        "Mercy Mission",
        "Breaking Some Eggs",
        "The Caged Brute",
        "The Siren's Cadence",
        "Intruders in Black",
        "Sharp and Cruel",
        "The Root of the Problem",
        "Lost in Love",
        "Sever the Right Hand",
        "A Fixture of Fate",
        "Breaking the Seal",
        "The Eternal Nightmare",
        "Fallen from Grace"
        };
    int level;
    public String name;
    public boolean hasPassive;
    public boolean hasTrial;
    ArrayList<String> image;
    String altimage;
    String note;
    String quest;
    boolean questRewardsSkills;
    String actName;
    int actID;
    public boolean hasRecipe;
    public class recipeInfo{
        public String tooltip;
        public ArrayList<String> mods;
    }
    public recipeInfo rInfo;

    public Zone(String zoneName, int zoneLevel, ArrayList<String> images, String altimage
            , String note, boolean hasPassive, boolean hasTrial , String quest ,boolean questRewardsSkills
            , String actName, int actID){
        this.level = zoneLevel;
        this.name = zoneName;
        this.hasPassive = hasPassive;
        this.hasTrial = hasTrial;
        this.note = note;
        this.altimage = altimage;
        this.image = images; //maybe new list
        this.quest= quest;
        this.questRewardsSkills = questRewardsSkills;
        this.actName = actName;
        this.actID = actID;
        rInfo = new recipeInfo();
    }
    
    public int getZoneLevel(){
        return level;
    }
    
    public String getZoneQuest(){
        return quest;
    }
    
    public ArrayList<String> getImages(){
        return image;
    }
    
    public String getActName(){
        return actName;
    }
    
    public String getZoneNote(){
        return note;
    }
    
    public String altImage(){
        return altimage;
    }
}
