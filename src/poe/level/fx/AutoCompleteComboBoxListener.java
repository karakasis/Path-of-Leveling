/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXComboBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 *
 * @author Christos
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {
    private JFXComboBox<T> comboBox;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;
    private StringConverter<T> sc;

    AutoCompleteComboBoxListener(final JFXComboBox<T> comboBox) {
        this.comboBox = comboBox;
        data = comboBox.getItems();

        // this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
        this.sc = this.comboBox.getConverter();
    }

    @Override
    public void handle(KeyEvent event) {

        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCharacter().matches("[a-z]"))
            comboBox.setValue((T) String.valueOf(comboBox.getValue()).toUpperCase());

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.isControlDown()
                || event.getCode() == KeyCode.HOME || event.getCode() == KeyCode.END
                || event.getCode() == KeyCode.TAB) {
            return;
        }

        // if(comboBox.getItems().isEmpty()) comboBox.getItems().addAll(data);

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T aData : data) {
            if (sc.toString(aData).toLowerCase()
                    .contains(AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase())) {
                list.add(aData);
            }
            /*
             * if (String.valueOf(aData).toLowerCase().startsWith(
             * AutoCompleteComboBoxListener.this.comboBox
             * .getEditor().getText().toLowerCase())) { list.add(aData); }
             */
        }
        String t = comboBox.getEditor().getText();
        comboBox.getItems().clear();
        comboBox.getItems().addAll(list);
        comboBox.getEditor().setText(t);
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if (!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
}
