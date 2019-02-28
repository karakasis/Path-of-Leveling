package poe.level.data;

import java.util.ArrayList;

public class ActHandler {

    private static ActHandler mInstance;

    public static synchronized ActHandler getInstance() {
        if (mInstance == null) {
            mInstance = new ActHandler();
        }
        return mInstance;
    }

    public ArrayList<Act> acts;

    public ActHandler() {
        acts = new ArrayList<>();
    }

    public void putAct(Act a) {
        acts.add(a);
    }

    public ArrayList<Act> getActs() {
        return acts;
    }
}
