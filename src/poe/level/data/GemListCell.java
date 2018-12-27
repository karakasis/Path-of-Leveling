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
public class GemListCell extends ListCell<Gem>{
    
    @Override
    protected void updateItem(Gem gem, boolean empty) {
        super.updateItem(gem, empty) ;
        if (empty) {
            setText(null);
        } else {
            Label l = new Label();
            if(!gem.getGemName().equals("<empty group>")){
                l.setGraphic(new ImageView(gem.getSmallIcon()));
                l.setText(gem.getGemName());
            }else{
                setText(null);
            }
            setGraphic(l);

        }
    }
    
}
