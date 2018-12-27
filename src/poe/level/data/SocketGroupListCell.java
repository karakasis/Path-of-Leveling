/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.data;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

/**
 *
 * @author Christos
 */
public class SocketGroupListCell extends ListCell<SocketGroup>{
    
    @Override
    protected void updateItem(SocketGroup sg, boolean empty) {
        super.updateItem(sg, empty) ;
        if (empty) {
            setText(null);
        } else {
            Label l = new Label();
            if(sg.getActiveGem()!=null){
                if(!sg.getActiveGem().getGemName().equals("<empty group>")){
                    l.setGraphic(new ImageView(sg.getActiveGem().getSmallIcon()));
                    l.setText(sg.getActiveGem().getGemName());
                }
            }
            else{
                setText(null);
            }
            setGraphic(l);

        }
    }
    
}
