package poeapi;

import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.CharacterInfo;
import poe.level.data.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class POEAPIHelper {

    public static ArrayList<String> getPOEActiveLeagues() {
        Util.HttpResponse response = Util.httpToString("http://api.pathofexile.com/leagues?compact=1");
        ArrayList<String> result = new ArrayList<>();
        if (response.responseCode == 200) {
            JSONArray arr = new JSONArray(response.responseString);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getJSONObject(i).getString("id"));
            }
        }
        return result;
    }

    public static long getCharacterExperience(String account, String character, String league) {
        Util.HttpResponse response = Util.httpToString("https://www.pathofexile.com/character-window/get-characters?accountName=" + account);
        long result = -1;
        if (response.responseCode == 200) {
            JSONArray arr = new JSONArray(response.responseString);
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString("league").equalsIgnoreCase(league) &&
                    arr.getJSONObject(i).getString("name").equalsIgnoreCase(character)) {
                    result = arr.getJSONObject(i).getLong("experience");
                    break;
                }
            }
        }
        return result;
    }

    public static ArrayList<CharacterInfo> getCharacters(String account) {
        Util.HttpResponse response = Util.httpToString("https://www.pathofexile.com/character-window/get-characters?accountName=" + account);
        ArrayList<CharacterInfo> result = new ArrayList<>();

        if (response.responseCode == 200) {
            JSONArray arr = new JSONArray(response.responseString);
            for (int i = 0; i < arr.length(); i++) {
                CharacterInfo ci = new CharacterInfo();
                ci.loadedFromPOEAPI = true;

                JSONObject charObj = arr.getJSONObject(i);
                ci.characterName = charObj.getString("name");
                ci.experience = charObj.getLong("experience");
                ci.level = charObj.getInt("level");
                ci.ascendancyName = charObj.getString("class");
                ci.setClassNameFromInt(charObj.getInt("classId"));
                ci.league = charObj.getString("league");
                result.add(ci);
            }
        }
        result.sort(new CharacterInfo.CharacterLeagueComparator());
        return result;
    }

    public static void main(String[] args) {
    /*ArrayList<String> result = getPOEActiveLeagues();
    System.out.println(result);*/
        long experience = getCharacterExperience("protuhj", "FlashBadmanners", "SSF Standard");
        System.out.println("Experience: " + experience);
    }
}
