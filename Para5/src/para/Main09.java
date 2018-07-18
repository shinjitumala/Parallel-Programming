package para;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ObservableValue;

import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;

public class Main09{
  public static void main(String[] args){
    ShapeManager sm = new OrderedShapeManager();
    //Target target = new JavaFXTarget("color filter", 320,240);
    Target target = new TargetColorFilter(new JavaFXTarget("color filter",
                                                           320,240), 4);
    target.init();
    sm.put(new Camera(0, 0, 0));
    while(true){
      target.clear();
      target.draw(sm);
      target.flush();
      try{
        Thread.sleep(80);
      }catch(InterruptedException e){
      }
    }
  }
}
