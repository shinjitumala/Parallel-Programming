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

/** スライダにより表示図形が入れ替わるデモ
 */
public class Main05 extends Application{
  final Thread thread;
  final JavaFXCanvasTarget jfc;
  final ShapeManager sm;
  volatile int value;

  public Main05(){
    sm = new OrderedShapeManager();
    jfc = new JavaFXCanvasTarget(320,240);
    jfc.init();
    thread = new Thread(new Runnable(){
        public void run(){
          int j=0;
          while(true){
            System.out.println(Thread.currentThread().getName());
            Garden.setMole(100, 100, 100, value, sm);
            jfc.clear();
            jfc.draw(sm);
            jfc.flush();
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
    Slider slider = new Slider(0, 32, 0);
    pane.setBottom(slider);
    slider.valueProperty().addListener(
      (ObservableValue<? extends Number> ov,
       Number old_val, Number new_val)->{
        System.out.println(Thread.currentThread().getName());
        value = (int)slider.getValue();
      });
    Scene scene = new Scene(pane);
    stage.setTitle("Slider");
    stage.setScene(scene);
    stage.setOnCloseRequest(ev->{System.exit(0);});
    stage.sizeToScene();
    stage.show();
    thread.start();
  }
}
