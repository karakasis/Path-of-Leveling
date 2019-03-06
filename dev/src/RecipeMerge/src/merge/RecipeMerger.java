package merge;


import org.json.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RecipeMerger {
    private static final String dataFilepath = "RecipeMerge\\data.json";
    private static final String recipeFilepath = "RecipeMerge\\recipe.json";
    private static final String outputFilepath = "RecipeMerge\\newData.json";
    private Map<String, merge.Recipe> recipes;
    private JSONArray zones;
    private JSONArray acts;

    public static void main(String[] args) {
        new RecipeMerger().merge();
    }

    private void merge() {
        parseJsons();
        updateZoneData();
        writeNewData();
    }

    private void writeNewData() {
        try (FileWriter file = new FileWriter(outputFilepath)) {

            file.write(acts.toJSONString().replace("\\/","/"));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateZoneData() {
        for (JSONObject zone : (Iterable<JSONObject>) zones) {
            String name = (String) zone.get("name");
            Long level = (Long) zone.get("level");
            name += "[L" + level + "]";

            if (recipes.containsKey(name)) {
                Recipe recipe = recipes.get(name);
                JSONObject recipeJson = recipe.toJsonObject();
                zone.put("hasRecipe", true);
                zone.put("recipe", recipeJson);
                recipes.remove(name);
            } else {
                zone.put("hasRecipe", false);
            }
        }
    }

    private void parseJsons() {
        JSONArray recipesJSON = (JSONArray) Objects.requireNonNull(parseFileAtPath(recipeFilepath)).get("recipes");
        recipes = parseRecipes(recipesJSON);
        acts = (JSONArray) Objects.requireNonNull(parseFileAtPath(dataFilepath)).get("acts");
        zones = getAllZones(acts);
    }

    private Map<String, Recipe> parseRecipes(JSONArray recipesJson) {
        Map<String, Recipe> recipes = new HashMap<>();
        for (JSONObject recipeJson : (Iterable<JSONObject>) recipesJson) {
            String area = (String) recipeJson.get("unlock");
            String tooltip = (String) recipeJson.get("tooltip");
            String mod = (String) recipeJson.get("mod");
            String itemClasses = (String) recipeJson.get("itemClasses");
            if (itemClasses.contains("Two Hand") && !itemClasses.contains("One Hand")) {
                mod += " (2H)";
            }
            if (!itemClasses.contains("Two Hand") && itemClasses.contains("One Hand")) {
                mod += " (1H)";
            }
            if (recipes.containsKey(area)) {
                Recipe recipe = recipes.get(area);
                recipe.addMod(mod);
            } else {
                Recipe recipe = new Recipe();
                recipe.setTooltip(tooltip);
                recipe.addMod(mod);
                recipes.put(area, recipe);
            }
        }
        return recipes;
    }

    private JSONArray getAllZones(JSONArray acts) {
        JSONArray allZones = new JSONArray();
        for (JSONObject act : (Iterable<JSONObject>) acts) {
            JSONArray zonesInAct = (JSONArray) act.get("zones");
            allZones.addAll(zonesInAct);
        }
        return allZones;
    }

    private JSONObject parseFileAtPath(String path) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(new FileReader(path));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
