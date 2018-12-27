package poe.level.data;
import java.util.ArrayList;

public class Zone {
    int level;
    String name;
    boolean hasPassive;
    boolean hasTrial;
    ArrayList<String> image;
    String altimage;
    String note;
    String quest;
    boolean questRewardsSkills;
    String actName;
    int actID;

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
