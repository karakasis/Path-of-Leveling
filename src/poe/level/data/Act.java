package poe.level.data;

import java.util.ArrayList;

public class Act {
    public ArrayList<Zone> zones;

    public Act(int actId, String act) {
        this.zones = new ArrayList<Zone>();
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void putZone(Zone z) {
        zones.add(z);
    }
}
