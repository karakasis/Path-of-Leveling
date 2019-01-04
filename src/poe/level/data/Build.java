/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import java.util.ArrayList;
import java.util.Comparator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Christos
 */
public class Build {
    String buildName;
    String className;
    String ascendancyName;
    public String characterName;
    public int level;
    ArrayList<SocketGroup> gems;
    
    public Build(String buildName,String className
            , String ascendancyName){
        this.buildName = buildName;
        this.className = className;
        this.ascendancyName = ascendancyName;
        gems = new ArrayList<>();
        level = -1;
        characterName = "";
    }
    
    public String getName(){
        return buildName;
    }
    
    public String getClassName(){
        return className;
    }

    public String getAsc(){
        return ascendancyName;
    }

    public ArrayList<SocketGroup> getSocketGroup(){
        return gems;
    }
    
    public boolean validate(){
        System.out.println(">>>>Validating build :"+this.buildName+"... <<<<");
        for(SocketGroup sg : getSocketGroup()){
            if(sg.getActiveGem()==null){
                System.out.println(">>>>A socket group doesn't have a valid main gem.<<<<");
                return false;
            }else{
                System.out.println("SocketGroup # "+sg.getActiveGem().getGemName());
                System.out.println("- Use at level: "+sg.fromGroupLevel);
                if(sg.replaceGroup()){
                    if(sg.getGroupReplaced().getActiveGem()==null){
                        System.out.println(">>>>Socket group -"+sg.getActiveGem().getGemName()+" replaces with a socket group that doesn't have a valid main gem.");
                        return false;
                    }
                    if(sg.getGroupReplaced().getFromGroupLevel()!=sg.getUntilGroupLevel()){
                        System.out.println(">>>>Socket group -"+sg.getActiveGem().getGemName()+"- replaces with -"
                                +sg.getGroupReplaced().getActiveGem().getGemName()+"- and group levels don't match.");
                        return false;
                    }
                    System.out.println("- Replace at level: "+sg.untilGroupLevel);
                    System.out.println("- Replace with : -"+sg.getGroupReplaced().getActiveGem().getGemName()+"- .");
                }
                
                //inner loop
                ArrayList<Gem> sorted = new ArrayList<>(sg.getGems());
                sorted.sort(Comparator.comparing(Gem::getLevelAdded));
                for(Gem g : sorted){
                    if(g.getGemName().equals("<empty group>")){
                        System.out.println(">>>>A gem in this socket group is not set.<<<<");
                        return false;
                    }
                    System.out.println("--Gem # "+g.getGemName());
                    System.out.println("--- Use at level: "+g.getLevelAdded());
                    if(g.replaced){
                        if(g.replacedWith==null){
                            System.out.println(">>>>Gem -"+g.getGemName()+" replacement has not been set.");
                            return false;
                        }
                        if(g.replacedWith.getLevelAdded()<=g.getLevelAdded()){
                            System.out.println(">>>>Gem -"+g.getGemName()+"- replaces with -"
                                    +g.replacedWith.getGemName()+"- and change level don't match.");
                            return false;
                        }
                        System.out.println("--- Replace at level: "+g.replacedWith.getLevelAdded());
                        System.out.println("--- Replace with : -"+g.replacedWith.getGemName()+"- .");
                    }
                }
            }
        }
        return true;
    }
    
    public String validate_failed_string(){
        String error = "";
        System.out.println(">>>>Validating build :"+this.buildName+"... <<<<");
        error += ">>>>Validating build :"+this.buildName+"... <<<<\n";
        for(SocketGroup sg : getSocketGroup()){
            if(sg.getActiveGem()==null){
                System.out.println(">>>>A socket group doesn't have a valid main gem.<<<<");
                error += ">>>>A socket group doesn't have a valid main gem.<<<<";
                return error;
            }else{
                System.out.println("SocketGroup # "+sg.getActiveGem().getGemName());
                error+="SocketGroup # "+sg.getActiveGem().getGemName()+"\n";
                System.out.println("- Use at level: "+sg.fromGroupLevel);
                error+="- Use at level: "+sg.fromGroupLevel+"\n";
                if(sg.replaceGroup()){
                    if(sg.getGroupReplaced().getActiveGem()==null){
                        System.out.println(">>>>Socket group -"+sg.getActiveGem().getGemName()+" replaces with a socket group that doesn't have a valid main gem.");
                        error+=">>>>Socket group -"+sg.getActiveGem().getGemName()+" replaces with a socket group that doesn't have a valid main gem.\n";
                        return error;
                    }
                    if(sg.getGroupReplaced().getFromGroupLevel()!=sg.getUntilGroupLevel()){
                        System.out.println(">>>>Socket group -"+sg.getActiveGem().getGemName()+"- replaces with -"
                                +sg.getGroupReplaced().getActiveGem().getGemName()+"- and group levels don't match.");
                        error+=">>>>Socket group -"+sg.getActiveGem().getGemName()+"- replaces with -"
                                +sg.getGroupReplaced().getActiveGem().getGemName()+"- and group levels don't match.\n";
                        return error;
                    }
                    System.out.println("- Replace at level: "+sg.untilGroupLevel);
                    error+="- Replace at level: "+sg.untilGroupLevel+"\n";
                    System.out.println("- Replace with : -"+sg.getGroupReplaced().getActiveGem().getGemName()+"- .");
                    error+="- Replace with : -"+sg.getGroupReplaced().getActiveGem().getGemName()+"- .\n";
                }
                
                //inner loop
                ArrayList<Gem> sorted = new ArrayList<>(sg.getGems());
                sorted.sort(Comparator.comparing(Gem::getLevelAdded));
                for(Gem g : sorted){
                    if(g.getGemName().equals("<empty group>")){
                        System.out.println(">>>>A gem in this socket group is not set.<<<<");
                        error+=">>>>A gem in this socket group is not set.<<<<\n";
                        return error;
                    }
                    System.out.println("--Gem # "+g.getGemName());
                    error+="--Gem # "+g.getGemName()+"\n";
                    System.out.println("--- Use at level: "+g.getLevelAdded());
                    error+="--- Use at level: "+g.getLevelAdded()+"\n";
                    if(g.replaced){
                        if(g.replacedWith==null){
                            System.out.println(">>>>Gem -"+g.getGemName()+" replacement has not been set.");
                            error+=">>>>Gem -"+g.getGemName()+" replacement has not been set.\n";
                            return error;
                        }
                        if(g.replacedWith.getLevelAdded()<=g.getLevelAdded()){
                            System.out.println(">>>>Gem -"+g.getGemName()+"- replaces with -"
                                    +g.replacedWith.getGemName()+"- and change level don't match.");
                            error+=">>>>Gem -"+g.getGemName()+"- replaces with -"
                                    +g.replacedWith.getGemName()+"- and change level don't match.\n";
                            return error;
                        }
                        System.out.println("--- Replace at level: "+g.replacedWith.getLevelAdded());
                        error+="--- Replace at level: "+g.replacedWith.getLevelAdded()+"\n";
                        System.out.println("--- Replace with : -"+g.replacedWith.getGemName()+"- .");
                        error+="--- Replace with : -"+g.replacedWith.getGemName()+"- .\n";
                    }
                }
            }
        }
        return error;
    }
}
