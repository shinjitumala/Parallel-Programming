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

/** スライダにより残像効果が変わるプログラム
 */
public class Main14 extends Application{
  final Thread thread;
  final JavaFXCanvasTarget jfc;
  final TargetDelayFilter filter;
  final Target target;
  final ShapeManager sm;
  volatile int value;

  public Main14(){
    sm = new OrderedShapeManager();
    jfc = new JavaFXCanvasTarget(360,280);

    //filter = new TargetDelayFilter(jfc, "donothing.cl", "DoNothing");
    filter = new TargetDelayFilter(jfc, "delay.cl", "Delay");
    target = new TargetRecorder("recorddelay", filter);
    target.init();
    target.clear();
    sm.add(new Camera(0,20,20));
    thread = new Thread(new Runnable(){
        public void run(){
          while(true){
            //System.out.println(Thread.currentThread().getName());
            target.draw(sm);
            target.flush();
            try{
              Thread.sleep(80);
            }catch(InterruptedException e){
            }
          }
        }
      });
  }

  public void start(Stage stage){
    BorderPane pane = new BorderPane();
    pane.setCenter(jfc);
    Slider slider = new Slider(0, 1.0, 0.2);
    pane.setBottom(slider);
    slider.valueProperty().addListener(
      (ObservableValue<? extends Number> ov,
       Number old_val, Number new_val)->{
        //System.out.println(Thread.currentThread().getName());
        filter.setParameter((float)slider.getValue());
      });
    Scene scene = new Scene(pane);
    stage.setTitle("DelaySlider");
    stage.setScene(scene);
    stage.setOnCloseRequest(ev->{System.exit(0);});
    stage.sizeToScene();
    stage.show();
    thread.start();
  }
}
