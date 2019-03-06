package poeapi;

import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.CharacterInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class POEAPIHelper {
    private static String http(String url) {

        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Path-Of-Leveling");
            connection.connect();
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                connection.disconnect();
                in.close();
//        System.out.println(response);
                return response.toString();
            } else {
                connection.disconnect();
            }

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    public static ArrayList<String> getPOEActiveLeagues() {
        String response = http("http://api.pathofexile.com/leagues?compact=1");
        ArrayList<String> result = new ArrayList<>();
        if (response != null) {
            JSONArray arr = new JSONArray(response);
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getJSONObject(i).getString("id"));
            }
        }
        return result;
    }

    public static long getCharacterExperience(String account, String character, String league) {
        String response = http("https://www.pathofexile.com/character-window/get-characters?accountName=" + account);
        long result = -1;
        if (response != null) {
            JSONArray arr = new JSONArray(response);
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
        String response = http("https://www.pathofexile.com/character-window/get-characters?accountName=" + account);
        ArrayList<CharacterInfo> result = new ArrayList<>();

        if (response != null) {
            JSONArray arr = new JSONArray(response);
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
