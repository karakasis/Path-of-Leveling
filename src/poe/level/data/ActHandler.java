package poe.level.data;
import java.util.ArrayList;
import java.util.HashMap;

public class ActHandler {
    
    private static ActHandler mInstance;
  
    public static synchronized ActHandler getInstance() {
    if (mInstance == null ) {
      mInstance = new ActHandler();
    }
    return mInstance;
  }
    
    private ArrayList<Act> acts;
    private ArrayList<Zone> zonesWithRecipes;
    public HashMap<Zone,Boolean> recipeMap;

    public ActHandler(){
        acts = new ArrayList<>();
        zonesWithRecipes = new ArrayList<>();
        recipeMap = new HashMap<>();
    }

    public void putAct(Act a){
        acts.add(a);
    }

    public ArrayList<Act> getActs(){
        return acts;
    }
    
    public void putZone(Zone a){
        zonesWithRecipes.add(a);
    }

    public ArrayList<Zone> getZonesWithRecipes(){
        return zonesWithRecipes;
    }
}
