package poe.resizer;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

//originally created by Alexander Berg
//https://stackoverflow.com/questions/19455059/allow-user-to-resize-an-undecorated-stage
//
//modified by Joseph Adomatis
//extracted ResizeListener class and made public, added arg for CWinMaxButton for detecting Maximized Window, using my own logger
//changed MouseDragged routines and private variables to make use of screen absolute values instead of relative values
//MouseDragged also updated to respect Min/Max sizes
public class ResizeListener implements EventHandler<MouseEvent> {
    public ResizeListener(Stage stage) {
        this.stage = stage;
        isPressed = false;
        cursorEvent = Cursor.DEFAULT;
        border = 3;
        stageStartH = 0;
        stageStartW = 0;
        stageStartX = 0;
        stageStartY = 0;
        this.addResizeListener();
    }

    private void addResizeListener() {
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_ENTERED, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, this);
        this.stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, this);
        ObservableList<Node> children = this.stage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child);
        }
    }
    private void addListenerDeeply(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, this);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, this);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, this);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, this);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, this);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addListenerDeeply(child);
            }
        }
    }
    @Override
    public void handle(MouseEvent mouseEvent) {

            // Check with the maximize button to see if window is currently maximized
            if(stage.isMaximized()){
                // We do not resize Maximized windows
                return;
            }
        EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
        Scene scene = stage.getScene();

        // set minHeight vars in such a way as to ensure that there is always a border over which we can continue to resize again
        // if stage MinHeight were 0 and you resized to 0, the draggable zone is gone and you cannot resize anymore
        // so regardless of app preference, we artificially set min sizes to leave all borders
        double minHeight = stage.getMinHeight() > (border*2) ? stage.getMinHeight() : (border*2);
        double minWidth = stage.getMinWidth() > (border*2) ? stage.getMinWidth() : (border*2);

        double maxHeight = stage.getMaxHeight();
        double maxWidth = stage.getMaxWidth();

        // capture the position of the mouse cursor relative to the stage anchor point at the time of the event
        double mouseEventX = mouseEvent.getSceneX();
        double mouseEventY = mouseEvent.getSceneY();

        // capture the current scene Height and Width
        double sceneHeight = scene.getHeight();
        double sceneWidth = scene.getWidth();

        // capture the screen max visual Height and Width
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();

        // if MOUSE_MOVED and its new position is over one of the stage borders, we want to update the cursor to be one of the resize variety
        if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
            if (mouseEventX < border && mouseEventY < border) {
                cursorEvent = Cursor.NW_RESIZE;
            } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.SW_RESIZE;
            } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                cursorEvent = Cursor.NE_RESIZE;
            } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.SE_RESIZE;
            } else if (mouseEventX < border) {
                cursorEvent = Cursor.W_RESIZE;
            } else if (mouseEventX > sceneWidth - border) {
                cursorEvent = Cursor.E_RESIZE;
            } else if (mouseEventY < border) {
                cursorEvent = Cursor.N_RESIZE;
            } else if (mouseEventY > sceneHeight - border) {
                cursorEvent = Cursor.S_RESIZE;
            } else {
                cursorEvent = Cursor.DEFAULT;
            }
            scene.setCursor(cursorEvent);
            // if MOUSE_EXITED the stage screen area and we'd pressed but did not release the mouse button, then we want to maintain our current cursor
            // otherwise, since the mouse is outside our stage, we return it to the default cursor
        } else if(MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)){
            if(!isPressed){
                scene.setCursor(Cursor.DEFAULT);
            }
            // similarly, if MOUSE ENTERED the stage screen area and we'd pressed but did not release the mouse button, then we want to maintain the current cursor
            // otherwise, since the mouse is coming back to us, we dont want to keep whatever other cursor may have been set by other windows so we return to default
        } else if(MouseEvent.MOUSE_ENTERED.equals(mouseEventType) || MouseEvent.MOUSE_ENTERED_TARGET.equals(mouseEventType)){
            if(!isPressed){
                scene.setCursor(Cursor.DEFAULT);
            }
            // if MOUSE_PRESSED we might need to keep track that we pressed it and are initiating a potential drag/resize event
        } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
            // right now we dont care if mouse was pressed, but we might
            boolean iCare = false;
            // check the cursor type, if it is a resize cursor then mouse is over a border and we DO care that we pressed the mouse
            if(Cursor.N_RESIZE.equals(cursorEvent) || Cursor.S_RESIZE.equals(cursorEvent) || Cursor.E_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent)){
                iCare = true;
            } else if(Cursor.NE_RESIZE.equals(cursorEvent) || Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.SE_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)){
                iCare = true;
            }
            // if we care that we pressed the mouse, we need to capture the initial data that will be used by our drag event handler to actually resize the window
            if(iCare){
                stageStartH = stage.getHeight();
                stageStartW = stage.getWidth();
                stageStartX = stage.getX();
                stageStartY = stage.getY();
                mouseStartX = mouseEvent.getScreenX();
                mouseStartY = mouseEvent.getScreenY();
                isPressed = true;
            }
            // if MOUSE_RELEASED, we don't care what the mouse does anymore so release our flag
        } else if(MouseEvent.MOUSE_RELEASED.equals(mouseEventType)){
            isPressed = false;
            // if MOUSE_DRAGGED, this handler might have something to do
        } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
            // if the cursor is still default, then this handler doesnt care about the drag event so ignore everything else
            // this handler only cares if the cursor is of the resize variety
            if(Cursor.DEFAULT.equals(cursorEvent)){
                return;
            }
            // Check if there is a vertical component to the window resize
            // The only time there isn't a vertical component is if the mouse is strictly on the west or east side of the stage
            if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                // There is a vertical component.
                // If we are resizing the north side however, we will be resetting both the Y coordinate of the stage anchor as well as the stage height
                if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                    double mouseDifY = mouseStartY - stageStartY;
                    // we are moving the north side
                    // figure out where the south side of the stage is
                    double finalY = stageStartY + stage.getHeight();
                    // we are free to move the north side until it reaches the point where the distance to the south side is greater than maxHeight
                    // OR, we run into the top of the screen
                    double minStageY = (finalY - maxHeight) > 0 ? (finalY - maxHeight): 0;
                    double minMouseY = minStageY + mouseDifY;
                    // we are free to move the north side until it reaches the point where the distance to the south side is less than minHeight
                    double maxStageY = finalY - minHeight;
                    double maxMouseY = maxStageY + mouseDifY;
                    // capture the absolute position of the mouse at the time of the event
                    double curMouseY = mouseEvent.getScreenY();
                    if(curMouseY < minMouseY){
                        stage.setY(minStageY);
                        // Our mouse passed the value at which we would breach max height
                        // We dont want the curMouseY to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseY = minMouseY;
                    } else if(curMouseY > maxMouseY){
                        stage.setY(maxStageY);
                        // Our mouse passed the value at which we would breach min height
                        // We dont want the curMouseY to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseY = maxMouseY;
                    } else {
                        stage.setY(curMouseY - mouseDifY);
                    }
                    double newY = stage.getY();
                    double newHeight = finalY - newY;
                    stage.setHeight(newHeight);
                    // Our stage and mouse start variables were set via the mouse pressed event handle
                    // If we did above procedure in the mouse released event handle, it would work, but there would be no display update till mouse released.
                    // By using mouse dragged event handle, we get display update each event cycle... but we have to constantly update our start variables for the next cycle
                    // While dragging mouse, you aren't releasing and re-pressing it to update the variables....
                    stageStartY = stage.getY();
                    stageStartH = stage.getHeight();
                    mouseStartY = curMouseY;
                } else {
                    // Else, we are resizing the south side, and the Y coordinate remains fixed. We only change the stage height
                    // figure out where the current south side actually is
                    double curFinalY = stageStartY + stageStartH;
                    double mouseDifY = mouseStartY - curFinalY;
                    // we are free to move the north side until it reaches the point where the distance to the south side is greater than maxHeight
                    // OR, we run into the bottom of the screen
                    double maxFinalY = (stageStartY + maxHeight) < screenHeight ? (stageStartY + maxHeight) : screenHeight;
                    double maxMouseY = maxFinalY + mouseDifY;
                    // we are free to move the south side until the point where the distance from anchor to south side is less than minHeight
                    double minFinalY = stageStartY + minHeight;
                    double minMouseY = minFinalY + mouseDifY;
                    // capture the absolute position of the mouse at the time of the event
                    double curMouseY = mouseEvent.getScreenY();
                    if (curMouseY < minMouseY) {
                        stage.setHeight(minHeight);
                        // Our mouse passed the value at which we would breach min height
                        // We don't want the curMouseY to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseY = minMouseY;
                    } else if(curMouseY > maxMouseY){
                        double newFinalY = maxMouseY - mouseDifY;
                        double newHeight = newFinalY - stageStartY;
                        stage.setHeight(newHeight);
                        // Our mouse passed the value at which we would breach max height
                        // We don't want the curMouseY to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseY = maxMouseY;
                    } else {
                        double newFinalY = curMouseY - mouseDifY;
                        double newHeight = newFinalY - stageStartY;
                        stage.setHeight(newHeight);
                    }
                    // Our stage and mouse start variables were set via the mouse pressed event handle
                    // If we did above procedure in the mouse released event handle, it would work, but there would be no display update till mouse released.
                    // By using mouse dragged event handle, we get display update each event cycle... but we have to constantly update our start variables for the next cycle
                    // While dragging mouse, you aren't releasing and re-pressing it to update the variables....
                    stageStartY = stage.getY();
                    stageStartH = stage.getHeight();
                    mouseStartY = curMouseY;
                }
            }
            // Check if there is a horizontal component to the window resize
            // The only time there isn't a horizontal component is if the mouse is strictly on the north or south side of the stage.
            if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                // There is a horizontal component.
                // If we are resizing the west side however, we will be resetting both the X coordinate of the stage anchor as well as the stage width.
                if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                    // we are moving the west side
                    // figure out where the east side of the stage is
                    double mouseDifX = mouseStartX - stageStartX;
                    double finalX = stageStartX + stageStartW;
                    // we are free to move the west side until it reaches the point where the distance to the east side is greater than maxWidth
                    // OR, we run into the left of the screen
                    double minStageX = (finalX - maxHeight) > 0 ? (finalX - maxHeight): 0;
                    double minMouseX = minStageX + mouseDifX;
                    // we are free to move the west side until it reaches the point where the distance to the east side is less than minWidth
                    double maxStageX = finalX - minWidth;
                    double maxMouseX = maxStageX + mouseDifX;
                    // capture the absolute position of the mouse at the time of the event
                    double curMouseX = mouseEvent.getScreenX();
                    if(curMouseX < minMouseX){
                        stage.setX(minStageX);
                        // Our mouse passed the value at which we would breach max width
                        // We don't want the curMouseX to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseX = minMouseX;
                    } else if(curMouseX > maxMouseX){
                        stage.setX(maxStageX);
                        curMouseX = maxMouseX;
                        // Our mouse passed the value at which we would breach min width
                        // We don't want the curMouseX to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                    } else {
                        stage.setX(curMouseX - mouseDifX);
                    }
                    double newX = stage.getX();
                    double newWidth = finalX - newX;
                    stage.setWidth(newWidth);
                    // Our stage and mouse start variables were set via the mouse pressed event handle
                    // If we did above procedure in the mouse released event handle, it would work, but there would be no display update till mouse released.
                    // By using mouse dragged event handle, we get display update each event cycle... but we have to constantly update our start variables for the next cycle
                    // While dragging mouse, you aren't releasing and re-pressing it to update the variables....
                    stageStartX = stage.getX();
                    stageStartW = stage.getWidth();
                    mouseStartX = curMouseX;
                } else {
                    // Else, we are resizing the east side, and the X coordinate remains fixed. We only change the stage width.
                    // figure out where the current east side actually is
                    double curFinalX = stageStartX + stageStartW;
                    double mouseDifX = mouseStartX - curFinalX;
                    // we are free to move the east side until the point where the distance from anchor to east side is less than minWidth
                    double minFinalX = stageStartX + minWidth;
                    double minMouseX = minFinalX + mouseDifX;
                    // we are free to move the east side until it reaches the point where the distance to the west side is greater than maxWidth
                    // OR, we run into the right of the screen
                    double maxFinalX = (stageStartX + maxWidth) < screenWidth ? (stageStartX + maxWidth) : screenWidth;
                    double maxMouseX = maxFinalX + mouseDifX;
                    // capture the absolute position of the mouse at the time of the event
                    double curMouseX = mouseEvent.getScreenX();
                    if (curMouseX < minMouseX) {
                        stage.setWidth(minWidth);
                        curMouseX = minMouseX;
                        // Our mouse passed the value at which we would breach min width
                        // We don't want the curMouseX to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                    } else if(curMouseX > maxMouseX){
                        double newFinalX = maxMouseX - mouseDifX;
                        double newWidth = newFinalX - stageStartX;
                        stage.setWidth(newWidth);
                        // Our mouse passed the value at which we would breach max width
                        // We don't want the curMouseY to update any more until the mouse is back over the border.
                        // Otherwise, the window border would resize relative to mouse movement, not relative to absolute mouse position
                        curMouseX = maxMouseX;
                    } else {
                        double newFinalX = curMouseX - mouseDifX;
                        double newWidth = newFinalX - stageStartX;
                        stage.setWidth(newWidth);
                    }
                    // Our stage and mouse start variables were set via the mouse pressed event handle
                    // If we did above procedure in the mouse released event handle, it would work, but there would be no display update till mouse released.
                    // By using mouse dragged event handle, we get display update each event cycle... but we have to constantly update our start variables for the next cycle
                    // While dragging mouse, you aren't releasing and re-pressing it to update the variables....
                    stageStartX = stage.getX();
                    stageStartW = stage.getWidth();
                    mouseStartX = curMouseX;
                }
            }
        }
    }
    // <editor-fold defaultstate="collapsed" desc="***** Private Variable Declarations *****">
    private boolean isPressed;
    private Cursor cursorEvent;
    private double mouseStartX;
    private double mouseStartY;
    private double stageStartH;
    private double stageStartW;
    private double stageStartX;
    private double stageStartY;
    private final int border;
    private final Stage stage;
// </editor-fold>
}