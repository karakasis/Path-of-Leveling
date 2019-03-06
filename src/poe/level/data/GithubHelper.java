package poe.level.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;

public class GithubHelper {
    private final Logger m_logger = Logger.getLogger(GithubHelper.class.getName());

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
}
