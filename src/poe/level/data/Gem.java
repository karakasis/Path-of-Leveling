/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Christos
 */
public class Gem {
    
    public class Info{
        public String quest_name;
        public String npc;
        public int act;
        public String town;
        public ArrayList<String> available_to;
    }
    
    public transient Image gemIcon;
    public transient Image smallGemIcon;
    public int id;
    
    public String name;
    public String quest_name;
    public String npc;
    public int act;
    public int required_lvl;
    public int level_added;
    public boolean isVaal;
    public ArrayList<String> available_to;
    public String town;
    public String color;
    public String iconPath;
    public transient Label cachedLabel;
    
    public String iconDirPath;
    
    public boolean replaced;
    public Gem replacedWith;
    public boolean replaces;
    public Gem replacesGem;
    
    //IDS FOR JSON CONVERTION
    public int id_replaced;
    public int id_replaces;
    
    public boolean isRewarded;
    public Info reward;
    public ArrayList<Info> buy;
    
    public boolean isActive;
    public boolean isSupport;
    public HashSet<String> tags;
    
    public boolean isBought(){
        if(buy.size()>0){
            return true;
        }else{
            return false;
        }
    }
    
     public Gem(){
        available_to = new ArrayList<>();
        level_added = -1;
        replaced = false;
        replacedWith = null;
        replaces = false;
        replacesGem = null;
        id = -1;
        id_replaced = -1;
        id_replaces = -1;
        tags = new HashSet<>();
    }
    
    public Gem(Gem dupe){
        available_to = new ArrayList<>();
        level_added = -1;
        replaced = false;
        replacedWith = null;
        replaces = false;
        replacesGem = null;
        id = -1;
        id_replaced = -1;
        id_replaces = -1;
        this.gemIcon = dupe.getIcon();
        this.smallGemIcon = dupe.getSmallIcon();
        this.name = dupe.getGemName();
        //more
        this.id = dupe.id;
        this.quest_name = dupe.quest_name;
        this.npc = dupe.npc;
        this.act = dupe.act;
        this.required_lvl = dupe.required_lvl;
        this.isVaal = dupe.isVaal;
        this.available_to = new ArrayList<>(dupe.available_to);
        this.town = dupe.town;
        this.color = dupe.color;
        this.iconPath = dupe.iconPath;
        this.cachedLabel= dupe.cachedLabel;
        this.iconDirPath = dupe.iconDirPath;
        this.isRewarded = dupe.isRewarded;
        this.tags = new HashSet<>(dupe.tags);
        this.isActive = dupe.isActive;
        this.isSupport = dupe.isSupport;
    }
    
    public int getLevelAdded(){
        if(level_added!=-1){
            return level_added;
        }else
            return required_lvl;
    }
    
    public Image getIcon(){
        //hopefully resized
        return gemIcon;
    }
    
    public Image getSmallIcon(){
        //hopefully resized
        return smallGemIcon;
    }
    
    public String getGemName(){
        return name;
    }
    
    public Gem dupeGem(){
        return new Gem(this);
    }
    
    public ArrayList<String> getChar(){
        return available_to;
    }

    public void putChar(String z){
        available_to.add(z);
    }
    
    public String getGemColor(){
        return color;
    }

    public Label getLabel(){
        cachedLabel = new Label();
        cachedLabel.setText(name);
        cachedLabel.setGraphic(new ImageView(gemIcon));
        return cachedLabel;
    }
    
    public void resizeImage(){
        BufferedImage before = SwingFXUtils.fromFXImage(gemIcon, null);
        int w = before.getWidth();
        int h = before.getHeight();
        // Create a new image of the proper size
        int w2 = (int) (w * 0.7);
        int h2 = (int) (h * 0.7);
        BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(0.7, 0.7);
        AffineTransformOp scaleOp 
            = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

        after = scaleOp.filter(before, after);
        smallGemIcon = SwingFXUtils.toFXImage(after, null);
        //ImageIcon imageIcon = new ImageIcon(dimg);
    }
    
}
