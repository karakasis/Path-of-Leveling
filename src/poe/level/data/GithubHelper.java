package poe.level.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Logger;

public class GithubHelper {
    private static final Logger m_logger = Logger.getLogger(GithubHelper.class.getName());

    private static final String GEMS_JSON_REMOTE_PATH = "/json/gems.json";
    private static final String DATA_JSON_REMOTE_PATH = "/json/data.json";
//    private static final String GEMS_JSON_REMOTE_PATH = "/src/json/gems.json";
//    private static final String DATA_JSON_REMOTE_PATH = "/src/json/data.json";
    private final String m_repoOwner;
    private final String m_branch;
    private String m_branchSHA;

    public GithubHelper(String repoOwner, String branch) {
        m_repoOwner = repoOwner;
        m_branch = branch;
    }

    public void init() {
        getBranchSha();
    }

    private void downloadJsonFile(URL remoteURL, File outputFile, File outputTimeFile, String commitTime) throws IOException {
        // Since time is date only, it doesn't care about time.
        Util.downloadFile(remoteURL, outputFile);
        try (FileWriter writer = new FileWriter(outputTimeFile, false)) {
            writer.write(commitTime);
        }
    }

    public void downloadGemsJsonFileIfNeeded(File outputFile, File outputTimeFile) throws IOException {
        String latestCommitTime = isJsonDownloadNeeded("https://api.github.com/repos/" + m_repoOwner + "/Path-of-Leveling/commits?path=" + GEMS_JSON_REMOTE_PATH + "&sha=" + m_branchSHA,
            outputFile,
            outputTimeFile);
        if (latestCommitTime != null) {
            m_logger.info("Detected that the " + outputFile.getPath() + " needs to be downloaded.");
            downloadJsonFile(new URL("https://raw.githubusercontent.com/" + m_repoOwner + "/Path-of-Leveling/" + m_branch + GEMS_JSON_REMOTE_PATH), outputFile, outputTimeFile, latestCommitTime);
        } else {
            m_logger.info("Detected that the " + outputFile.getPath() + " is up to date.");
        }
    }

    public void downloadDataJsonFileIfNeeded(File outputFile, File outputTimeFile) throws IOException {
        String latestCommitTime = isJsonDownloadNeeded("https://api.github.com/repos/" + m_repoOwner + "/Path-of-Leveling/commits?path=" + DATA_JSON_REMOTE_PATH + "&sha=" + m_branchSHA,
            outputFile,
            outputTimeFile);
        if (latestCommitTime != null) {
            m_logger.info("Detected that the " + outputFile.getPath() + " needs to be downloaded.");
            downloadJsonFile(new URL("https://raw.githubusercontent.com/" + m_repoOwner + "/Path-of-Leveling/" + m_branch + DATA_JSON_REMOTE_PATH), outputFile, outputTimeFile, latestCommitTime);
        } else {
            m_logger.info("Detected that the " + outputFile.getPath() + " is up to date.");
        }
    }

    private String isJsonDownloadNeeded(String metadataURL, File outputFile, File outputTimeFile) throws IOException {
        boolean needed = !outputFile.exists() || !outputTimeFile.exists();
        String commitTimeReturn = null;
        String existingDate = null;
        if (outputTimeFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(outputTimeFile))) {
                existingDate = br.readLine();
                metadataURL += "&since=" + existingDate;
            }
        }
        Util.HttpResponse response = Util.httpToString(metadataURL);
        if (response.responseCode == 200) {
            commitTimeReturn = getDateStringFromCommitsJSON(response.responseString);
            if (commitTimeReturn != null) {
                if (existingDate == null) {
                    needed = true;
                } else {
                    Calendar existingCal = javax.xml.bind.DatatypeConverter.parseDateTime(existingDate);
                    Calendar apiCal = javax.xml.bind.DatatypeConverter.parseDateTime(commitTimeReturn);
                    if (apiCal.equals(existingCal)) {
                        needed = false;
                    } else if (apiCal.after(existingCal)) {
                        needed = true;
                    }
                }
            }
        }
        if (needed) {
            return commitTimeReturn;
        }
        return null;
    }

    private void getBranchSha() {
        Util.HttpResponse response = Util.httpToString("https://api.github.com/repos/" + m_repoOwner + "/Path-of-Leveling/branches");
        if (response.responseCode == 200) {
            JSONArray array = new JSONArray(response.responseString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject branchObj = array.getJSONObject(i);
                if (m_branch.equalsIgnoreCase(branchObj.getString("name"))) {
                    JSONObject commitObj = branchObj.getJSONObject("commit");
                    m_branchSHA = commitObj.getString("sha");
                    m_logger.info("Found branch SHA: " + m_branchSHA);
                    return;
                }
            }
        }
        m_logger.severe("Failed to get Github branch SHA!");
    }

    private static String getDateStringFromCommitsJSON(String responseString) {
        String date = null;
        try {
            JSONArray commits = new JSONArray(responseString);
            if (commits.length() > 0) {
                JSONObject firstCommit = commits.getJSONObject(0);
                date = firstCommit.getJSONObject("commit").getJSONObject("author").getString("date");
            } else {
                m_logger.info("Commits array was empty");
            }
        } catch (JSONException je) {
            m_logger.severe("Caught exception trying to get commit date: " + je.getMessage());
            je.printStackTrace();
        }
        return date;
    }
}
