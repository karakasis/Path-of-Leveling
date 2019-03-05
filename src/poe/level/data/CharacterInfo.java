package poe.level.data;

import java.util.Comparator;
import java.util.Objects;

public class CharacterInfo {
    private final java.util.logging.Logger m_logger = java.util.logging.Logger.getLogger(CharacterInfo.class.getName());
    public String className = "";
    public String ascendancyName = "";
    public String characterName = "";
    public int level = -1;

    // Loaded from pathofexile.com
    public boolean loadedFromPOEAPI = false;
    public long experience = -1;
    public String league = "";

    public void copyFrom(CharacterInfo charInfo) {

        String before = null;
        if (!equals(charInfo)) {
            before = toString();
        }
        this.className = charInfo.className;
        this.ascendancyName = charInfo.ascendancyName;
        this.characterName = charInfo.characterName;
        this.level = charInfo.level;

        this.loadedFromPOEAPI = charInfo.loadedFromPOEAPI;
        this.experience = charInfo.experience;
        this.league = charInfo.league;
        if (before != null) {
            m_logger.info("CharacterInfo changed from: " + before);
            m_logger.info("To: " + toString());
        }

    }

    @Override
    public String toString() {
        return "CharacterInfo{" +
            "className='" + className + '\'' +
            ", ascendancyName='" + ascendancyName + '\'' +
            ", characterName='" + characterName + '\'' +
            ", level=" + level +
            ", loadedFromPOEAPI=" + loadedFromPOEAPI +
            ", experience=" + experience +
            ", league='" + league + '\'' +
            '}';
    }

    public void setClassNameFromInt(int classIDFromAPI) {
        switch (classIDFromAPI) {
            case 0:
                className = "Scion";
                break;
            case 1:
                className = "Marauder";
                break;
            case 2:
                className = "Ranger";
                break;
            case 3:
                className = "Witch";
                break;
            case 4:
                className = "Duelist";
                break;
            case 5:
                className = "Templar";
                break;
            case 6:
                className = "Shadow";
                break;
            default:
                className = "Unknown";
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterInfo that = (CharacterInfo) o;
        return level == that.level &&
            loadedFromPOEAPI == that.loadedFromPOEAPI &&
            experience == that.experience &&
            Objects.equals(className, that.className) &&
            Objects.equals(ascendancyName, that.ascendancyName) &&
            Objects.equals(characterName, that.characterName) &&
            Objects.equals(league, that.league);
    }

    public static class CharacterLeagueComparator implements Comparator<CharacterInfo> {

        @Override
        public int compare(CharacterInfo o1, CharacterInfo o2) {
            return o1.league.compareTo(o2.league);
        }
    }


}
