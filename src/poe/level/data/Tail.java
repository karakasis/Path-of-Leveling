package poe.level.data;

import java.io.File;
// Import the Java classes
import java.util.ArrayList;

import javafx.application.Platform;

/**
 * Implements console-based log file tailing, or more specifically, tail
 * following: it is somewhat equivalent to the unix command "tail -f"
 */
public class Tail implements LoggerListener {

    /**
     * The log file tailer
     */
    private Logger tailer;
    private ArrayList<String> log;
    private File file;
    private Controller parent;

    /**
     * Creates a new Tail instance to follow the specified file
     */
    public Tail() {

    }

    public void setUpTailer(File file, Controller parent) {
        this.file = file;
        this.parent = parent;
        tailer = new Logger(this.file, 1000, false);
        tailer.addLogFileTailerListener(this);
        tailer.start();

        log = new ArrayList<String>();
    }

    /**
     * A new line has been added to the tailed log file
     *
     * @param line
     *            The new line that has been added to the tailed log file
     */
    public void newLogFileLine(String line) {
        log.add(line);
        if (line.contains("You have entered ")) {

            System.out.println(line);
            int padding = line.indexOf("You have entered");
            String zone = "";
            // line.length()-1 because log format of poe is entered The Twilight Strand. so
            // we skip last dot
            for (int i = padding + 17; i < line.length() - 1; i++) {
                zone += line.charAt(i);
            }
            parent.currentZone = zone;

            System.out.println(zone);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    parent.zoneupdate();
                }
            });
        }
        if (line.contains("is now level ")) {

            System.out.println(line);
            int sufPad = line.lastIndexOf(':');
            String charname = "";
            for (int j = sufPad + 2; j < line.length(); j++) {
                char a = line.charAt(j);
                if (a == ' ')
                    break;
                else
                    charname += a;
            }
            if (charname.equals(parent.playerName)) {
                int padding = line.indexOf("is now level");
                String plvl = "";
                for (int i = padding + 13; i < line.length(); i++) {
                    plvl += line.charAt(i);
                }
                int plvlint = Integer.parseInt(plvl);
                parent.playerLevel = plvlint;
                System.out.println(charname + " is now level " + plvlint + ".");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        parent.lvlupdate();
                    }
                });

            }

        }

    }

    public void _startTailing() {

        tailer = new Logger(file, 1000, false);
        tailer.addLogFileTailerListener(this);

        tailer.start();

    }

    public void _stopTailing() {
        tailer.stopTailing();
    }

}
