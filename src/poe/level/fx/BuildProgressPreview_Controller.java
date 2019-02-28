/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.SocketGroup;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class BuildProgressPreview_Controller implements Initializable {

    @FXML
    private Line baseline;
    @FXML
    private Line tail_line;
    @FXML
    private AnchorPane level_axis_pane;
    @FXML
    private AnchorPane timeline_pane;

    @FXML
    private Label build_label;
    @FXML
    private Label class_label;
    @FXML
    private Label ascend_label;

    public void start(Build build) {

        // reverse the replacements::
        for (SocketGroup sg : build.getSocketGroup()) {
            if (sg.replaceGroup()) {
                for (SocketGroup sg2 : build.getSocketGroup()) {
                    if (sg.getGroupReplaced().equals(sg2)) {
                        sg2.setReplacesGroup(true);
                        sg2.setGroupThatReplaces(sg);
                        break;
                    }
                }
            }
            for (Gem g : sg.getGems()) {
                if (g.replaced) {
                    for (Gem g2 : sg.getGems()) {
                        if (g.replacedWith.equals(g2)) {
                            g2.replaces = true;
                            g2.replacesGem = g;
                            break;
                        }
                    }
                }
            }
        }

        HashSet<Integer> totalLevels = new HashSet<>();
        HashMap<Integer, ArrayList<Gem>> gemsOnLevelsMap = new HashMap<>();
        HashMap<SocketGroup, Integer> highestYValuePerSocketGroup = new HashMap<>();
        HashMap<Gem, SocketGroup> gemToSocket_map = new HashMap<>();
        HashMap<SocketGroup, Integer> draw_previous_y = new HashMap<>();
        // calcute the baseline
        ArrayList<SocketGroup> sorted_sg = new ArrayList<>();
        for (SocketGroup sg : build.getSocketGroup()) {
            sorted_sg.add(sg);
        }
        sorted_sg.sort(new Comparator<SocketGroup>() {
            @Override
            public int compare(SocketGroup o1, SocketGroup o2) {
                return o1.getActiveGem().getLevelAdded() - o2.getActiveGem().getLevelAdded();
            }

        });

        // here we add each gem to according level it gets added.
        for (SocketGroup sg : sorted_sg) {
            highestYValuePerSocketGroup.put(sg, 0); // here we iniate all sg's values to 0
            for (Gem g : sg.getGems()) {
                totalLevels.add(g.level_added);
                if (gemsOnLevelsMap.containsKey(g.level_added)) {
                    gemsOnLevelsMap.get(g.level_added).add(g);
                } else {
                    ArrayList<Gem> gemsOnThisLevel_local = new ArrayList<>();
                    gemsOnThisLevel_local.add(g);
                    gemsOnLevelsMap.put(g.level_added, gemsOnThisLevel_local);
                }
                gemToSocket_map.put(g, sg);
            }
        }

        // now we need to implement the visual offset for each level.
        ArrayList<Integer> level_list = new ArrayList<>();
        level_list.addAll(totalLevels);
        Collections.sort(level_list);

        // calculate highestYValuePerSocketGroup values

        for (Integer level : level_list) {
            ArrayList<Gem> get = gemsOnLevelsMap.get(level);
            HashMap<SocketGroup, Integer> helper_map = new HashMap<>();
            for (Gem g : get) {
                if (helper_map.containsKey(gemToSocket_map.get(g))) {
                    int get1 = helper_map.get(gemToSocket_map.get(g));
                    get1++;
                    helper_map.put(gemToSocket_map.get(g), get1);
                } else {
                    helper_map.put(gemToSocket_map.get(g), 1);// iniate it
                }
            }
            // update highestYValuePerSocketGroup if a new bigger value has been added in
            // this level iteration
            for (SocketGroup sg : sorted_sg) {
                if (helper_map.containsKey(sg)) {
                    if (highestYValuePerSocketGroup.get(sg) < helper_map.get(sg)) {
                        highestYValuePerSocketGroup.put(sg, helper_map.get(sg));
                    }
                }
            }
        }

        // now we can calculate the previous y draw values
        for (int index = 0; index < sorted_sg.size(); index++) {
            int previous_y = 0;
            for (int i = 0; i < index; i++) {
                previous_y = previous_y + highestYValuePerSocketGroup.get(sorted_sg.get(i));
            }
            draw_previous_y.put(sorted_sg.get(index), previous_y);// excluding this sg's y value
        }

        // calculate x values and the main x level bar size
        HashMap<Integer, Integer> levelToX_Location = new HashMap<>();
        int first_level_offset;
        first_level_offset = level_list.get(0) - 1; // *remember to check null lists and shits
        first_level_offset--;
        int offsetAdder;
        if (first_level_offset > 0) {
            first_level_offset = first_level_offset * 20;
            levelToX_Location.put(level_list.get(0), first_level_offset);
            offsetAdder = first_level_offset + 134;
        } else {
            levelToX_Location.put(level_list.get(0), 0);
            offsetAdder = 134;
        }
        for (int i = 0; i < level_list.size() - 1; i++) {
            int offset = level_list.get(i + 1) - level_list.get(i);
            // example offset = 4 - 2 = 2;
            offset = offset - 1; // then offset = 1;
            if (offset > 0)
                offset = offset * 20;
            else
                offset = 0;
            // example offest = 1 * 20 = 20;
            offsetAdder = offsetAdder + offset + 134;
            levelToX_Location.put(level_list.get(i + 1), offsetAdder);

        }

        // so now we should have all X locations of every line.
        int baselineSize = levelToX_Location.get(level_list.get(level_list.size() - 1));
        baseline.setEndX(baselineSize);
        tail_line.setStartX(baselineSize + 1);
        tail_line.setEndX(baselineSize + 31);

        // generate the levels on the header pane;;
        for (Integer level : level_list) {
            Label l = new Label();
            level_axis_pane.getChildren().add(l);
            l.setText(level.toString());
            l.setLayoutX(55 + levelToX_Location.get(level));
            l.setLayoutY(5);

            // i will assume this list is ordered actually. and this is very important!
            ArrayList<Gem> gemsOnThisLevel_local = gemsOnLevelsMap.get(level);
            int total_y = 0;
            HashMap<SocketGroup, Integer> miniCounters = new HashMap<>();
            for (Gem g : gemsOnThisLevel_local) {

                // now i have to find where to put this image view on Y .
                //
                if (!miniCounters.containsKey(gemToSocket_map.get(g))) {
                    miniCounters.put(gemToSocket_map.get(g), 0);
                }
                // int y_value_t = draw_previous_y.get(gemToSocket_map.get(g)); // this is the
                // max of a line
                // int y_value = (int) highestYValuePerSocketGroup.get(gemToSocket_map.get(g));
                // //this is the max of a socket groups line
                // int final_y = y_value_t-y_value;
                int final_y = draw_previous_y.get(gemToSocket_map.get(g));
                final_y = 10 * final_y + 47 * final_y;
                int current_mini_counter = miniCounters.get(gemToSocket_map.get(g));
                current_mini_counter++;
                miniCounters.put(gemToSocket_map.get(g), current_mini_counter);

                // draw the horizontal link line
                Line line_hor = new Line();
                timeline_pane.getChildren().add(line_hor);
                line_hor.setLayoutX(60);
                line_hor.setLayoutY(0);
                line_hor.setStartY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1) + 47 / 2);
                line_hor.setEndY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1) + 47 / 2);
                line_hor.setStartX(levelToX_Location.get(level));
                line_hor.setEndX(levelToX_Location.get(level) + 15);

                ImageView iv = new ImageView();
                timeline_pane.getChildren().add(iv);
                iv.setFitHeight(47);
                iv.setFitWidth(47);
                iv.setPreserveRatio(false);
                iv.setImage(g.gemIcon);
                iv.setLayoutY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1));
                iv.setLayoutX(60 + levelToX_Location.get(level));
                Tooltip.install(iv, new Tooltip(g.getGemName()));

                // THE REPLACE IS HERE for socket group
                if (gemToSocket_map.get(g).getActiveGem().equals(g) && gemToSocket_map.get(g).replacesGroup()) {
                    // IF THIS GEM IS AN ACTIVE GEM AND ITS SOCKET GROUP REPLACES ANOTHER ONE
                    Gem replace = gemToSocket_map.get(g).getGroupThatReplaces().getActiveGem();
                    ImageView iv_replace = new ImageView();
                    timeline_pane.getChildren().add(iv_replace);
                    iv_replace.setFitHeight(40);
                    iv_replace.setFitWidth(40);
                    iv_replace.setPreserveRatio(false);
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(getClass().getResource("/icons/replace_group.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iv_replace.setImage(SwingFXUtils.toFXImage(img, null));
                    iv_replace.setLayoutY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1));
                    iv_replace.setLayoutX(60 + levelToX_Location.get(level) + 47);
                    Tooltip.install(iv_replace,
                            new Tooltip(g.getGemName() + " replaces " + replace.getGemName() + "'s socket group."));

                    ImageView iv_re_gem = new ImageView();
                    timeline_pane.getChildren().add(iv_re_gem);
                    iv_re_gem.setFitHeight(47);
                    iv_re_gem.setFitWidth(47);
                    iv_re_gem.setPreserveRatio(false);
                    iv_re_gem.setImage(replace.gemIcon);
                    iv_re_gem.setLayoutY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1));
                    iv_re_gem.setLayoutX(60 + levelToX_Location.get(level) + 47 + 40);
                    Tooltip.install(iv_re_gem, new Tooltip(replace.getGemName()));
                }
                if (g.replaces) {
                    // IF THIS GEM replaces another gem
                    Gem replace = g.replacesGem;
                    ImageView iv_replace = new ImageView();
                    timeline_pane.getChildren().add(iv_replace);
                    iv_replace.setFitHeight(40);
                    iv_replace.setFitWidth(40);
                    iv_replace.setPreserveRatio(false);
                    BufferedImage img = null;
                    try {
                        img = ImageIO.read(getClass().getResource("/icons/replace_normal.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iv_replace.setImage(SwingFXUtils.toFXImage(img, null));
                    iv_replace.setLayoutY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1));
                    iv_replace.setLayoutX(60 + levelToX_Location.get(level) + 47);
                    Tooltip.install(iv_replace,
                            new Tooltip(g.getGemName() + " replaces " + replace.getGemName() + "."));

                    ImageView iv_re_gem = new ImageView();
                    timeline_pane.getChildren().add(iv_re_gem);
                    iv_re_gem.setFitHeight(47);
                    iv_re_gem.setFitWidth(47);
                    iv_re_gem.setPreserveRatio(false);
                    iv_re_gem.setImage(replace.gemIcon);
                    iv_re_gem.setLayoutY(final_y + 10 * current_mini_counter + 47 * (current_mini_counter - 1));
                    iv_re_gem.setLayoutX(60 + levelToX_Location.get(level) + 47 + 40);
                    Tooltip.install(iv_re_gem, new Tooltip(replace.getGemName()));
                }

                // if the gem belongs to the 1st socket group
                total_y = final_y + 10 * current_mini_counter + 47 * current_mini_counter; // +10 is end offset on the y
                                                                                           // axis.
            }

            // and the lines?
            Line line = new Line();
            timeline_pane.getChildren().add(line);
            line.setLayoutX(60);
            line.setLayoutY(0);
            line.setStartY(0);
            line.setEndY(total_y);
            line.setStartX(levelToX_Location.get(level));
            line.setEndX(levelToX_Location.get(level));
        }
        boolean alternateCounter = true;
        for (SocketGroup sg : sorted_sg) {
            Pane p = new Pane();
            timeline_pane.getChildren().add(p);
            AnchorPane.setLeftAnchor(p, Double.valueOf(0));
            AnchorPane.setRightAnchor(p, Double.valueOf(0));
            int layouty = draw_previous_y.get(sg);
            p.setLayoutX(0);
            p.setLayoutY(10 * layouty + 47 * layouty);
            if (sorted_sg.indexOf(sg) == sorted_sg.size() - 1) {
                p.setPrefHeight(
                        10 * highestYValuePerSocketGroup.get(sg) + 47 * highestYValuePerSocketGroup.get(sg) + 10);
            } else {
                p.setPrefHeight(10 * highestYValuePerSocketGroup.get(sg) + 47 * highestYValuePerSocketGroup.get(sg));
            }
            p.setOpacity(0.1);
            if (alternateCounter) {
                p.setStyle("-fx-background-color: black;");
                alternateCounter = false;
            } else {
                p.setStyle("-fx-background-color: yellow;");
                alternateCounter = true;
            }
        }
        if (build.getName() != null) {
            String build_con = "";
            if (build.getName().length() >= 20) {
                build_con = build.getName().substring(0, 20);
                build_con += "..";
            } else {
                build_con = build.getName();
            }
            build_label.setText("Build: " + build_con);
        } else {
            build_label.setText("Build: no_name");
        }
        if (build.getAsc() != null) {
            ascend_label.setText("Ascendancy: " + build.getAsc());
        } else {
            ascend_label.setText("Ascendancy: no_ascendancy");
        }
        if (build.getClassName() != null) {
            class_label.setText("Class: " + build.getClassName());
        } else {
            class_label.setText("Class: no_class");
        }

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

}
