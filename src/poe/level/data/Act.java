package poe.level.data;
import java.util.ArrayList;

public class Act {
    int actid;
    String act;
    public ArrayList<Zone> zones;

    public Act(int actId, String act){
        this.actid = actId;
        this.zones = new ArrayList<Zone>();
        this.act= act;
    }

    public ArrayList<Zone> getZones(){
        return zones;
    }

    public void putZone(Zone z){
        zones.add(z);
    }
}
