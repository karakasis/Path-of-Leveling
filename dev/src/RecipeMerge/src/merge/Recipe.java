package merge;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

class Recipe {
    private String tooltip;
    private final List<String> mods = new ArrayList<>();

    void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    void addMod(String mod){
        mods.add(mod);
    }

    JSONObject toJsonObject(){
        JSONObject obj = new JSONObject();
        obj.put("tooltip",tooltip);
        obj.put("mods",mods);
        return obj;
    }


}
