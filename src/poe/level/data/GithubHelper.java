package poe.level.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GithubHelper {
    private static final Logger m_logger = Logger.getLogger(GithubHelper.class.getName());

    private static final String GEMS_JSON_REMOTE_PATH = "/json/gems.json";
    private static final String DATA_JSON_REMOTE_PATH = "/json/data.json";
    private final String m_repoOwner;
    private final String m_branch;
    private String m_branchSHA;

    public GithubHelper(String repoOwner, String branch) {
        m_logger.info("Initializing new GithubHelper for " + repoOwner + " on the " + branch + " branch.");
        m_repoOwner = repoOwner;
        m_branch = branch;
    }

    public boolean init() {
        return getBranchSha();
    }

    public void downloadGemsJsonFileIfNeeded(File outputFile, File outputTimeFile) throws IOException {
        if (m_branchSHA == null) {
            // If we don't know what the default branch is on a repository, we need the SHA to be able to query on other branches.
            throw new IllegalStateException("Can't query Github for commit information without the branch SHA.");
        }
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
        if (m_branchSHA == null) {
            // If we don't know what the default branch is on a repository, we need the SHA to be able to query on other branches.
            throw new IllegalStateException("Can't query Github for commit information without the branch SHA.");
        }
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

    public static ReleaseInfo getLatestReleaseInfo() {
        Util.HttpResponse response = Util.httpToString("https://api.github.com/repos/karakasis/Path-of-Leveling/releases");
        if (response.responseCode == 200) {
            try {
                JSONArray releaseArray = new JSONArray(response.responseString);
                if (releaseArray.length() > 0) {
                    ReleaseInfo info = new ReleaseInfo();
                    JSONObject releaseObj = releaseArray.getJSONObject(0);
                    info.version = releaseObj.getString("name").trim();
                    JSONArray assetsArr = releaseObj.getJSONArray("assets");
                    boolean found = false;
                    for (int i = 0; i < assetsArr.length(); i++) {
                        JSONObject assetObj = assetsArr.getJSONObject(i);
                        if ("PathOfLeveling.jar".equalsIgnoreCase(assetObj.getString("name").trim())) {
                            found = true;
                            info.byteSize = assetObj.getLong("size");
                            info.downloadURL = assetObj.getString("browser_download_url");
                            break;
                        }
                    }
                    if (!found) {
                        m_logger.warning("Failed to find the PathOfLeveling.jar asset!");
                        return null;
                    }
                    info.releaseNotes = releaseObj.getString("body");
                    m_logger.info("Latest release is: " + info);
                    return info;

                }
            } catch (Exception ex) {
                m_logger.log(Level.SEVERE, "Exception while parsing release information: " + ex.getMessage(), ex);
            }
        }
        return null;
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
                    // If commit time is the same, but the outputFile might not exist, and an update may still be required.
                    if (apiCal.after(existingCal)) {
                        // A newer file exists, an update is needed.
                        needed = true;
                        m_logger.info("A newer " + outputFile.getName() + " file exists on the server, need to update");
                    }
                }
            }
        } else {
            throw new IOException("Failed to get update information for " + outputFile.getName() + " HTTP error code: " + response.responseCode);
        }
        if (needed) {
            return commitTimeReturn;
        }
        return null;
    }

    /**
     * Gets the branch SHA value for the branch that this {@link GithubHelper} represents.
     * @return true if the branch was successfully retrieved, false otherwise.
     */
    private boolean getBranchSha() {
        Util.HttpResponse response = Util.httpToString("https://api.github.com/repos/" + m_repoOwner + "/Path-of-Leveling/branches");
        if (response.responseCode == 200) {
            try {
                JSONArray array = new JSONArray(response.responseString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject branchObj = array.getJSONObject(i);
                    if (m_branch.equalsIgnoreCase(branchObj.getString("name"))) {
                        JSONObject commitObj = branchObj.getJSONObject("commit");
                        m_branchSHA = commitObj.getString("sha");
                        m_logger.info("Found branch SHA: " + m_branchSHA);
                        return true;
                    }
                }
            } catch (JSONException je) {
                m_logger.severe("JSONException caught while trying to get the branch SHA information. " + je.getMessage());
            }
        }
        m_logger.severe("Failed to get Github branch SHA!");
        return false;
    }

    /**
     * Parse the given responseString for the commit JSON Object from Github.
     * @param responseString The response string retrieved from Github Commits API endpoint.
     * @return null if the date could not be retrieved, the ISO 8601 datetime string otherwise.
     * @see <a href="https://developer.github.com/v3/repos/commits"/>
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601"/>
     */
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

    /**
     * Download the desired JSON file, and write the associated commitTime to the outputTimeFile.
     * @param remoteURL The URL of the JSON file to download.
     * @param outputFile The {@link File} object for the JSON file to be downloaded to.
     * @param outputTimeFile The {@link File} object for the time file to be written to.
     * @param commitTime The commit time, from Github, to write to outputTimeFile, to be used for future update queries.
     * @throws IOException if the file download or file write fails for some reason.
     */
    private void downloadJsonFile(URL remoteURL, File outputFile, File outputTimeFile, String commitTime) throws IOException {
        // Since time is date only, it doesn't care about time.
        Util.downloadFile(remoteURL, outputFile);
        try (FileWriter writer = new FileWriter(outputTimeFile, false)) {
            writer.write(commitTime);
        }
    }

    public static class ReleaseInfo {
        public String version;
        public long byteSize;
        public String releaseNotes;
        public String downloadURL;

        @Override
        public String toString() {
            return "ReleaseInfo{" +
                "version='" + version + '\'' +
                ", byteSize=" + byteSize +
                ", releaseNotes='" + releaseNotes + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                '}';
        }
    }
}
