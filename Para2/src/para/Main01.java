package para;

import java.util.Scanner;

import para.graphic.parser.*;
import para.graphic.target.*;
import para.graphic.shape.*;

/**
 * かっこいい車が走り過ぎるデモ
 */
public class Main01{
  Attribute bodycolor = new Attribute(255,0,0);
  Attribute tirecolor = new Attribute(0,0,0);
  Attribute windcolor = new Attribute(0,0,0,false);
  Attribute lampcolor;
  Attribute windowcolor = new Attribute(0,255,255);
  ShapeManager sm;
  JavaFXTarget target;
  
  private Main01(){
    sm = new ShapeManager();
    target = new JavaFXTarget("Ferrari", 320,240);
    target.init();
  }

  private void loop(){
    int lampi=0;
    while(true){
      for(int i=-190;i<330;i++){
        lampi = (lampi+2)%128+128;
        lampcolor = new Attribute(lampi, lampi, 0);
        target.clear();
        Shape s;
        s = new Rectangle(0, 118+i, 125, 72, 25, bodycolor);
        sm.put(s);
        s = new Rectangle(1, 118+i, 95, 50, 30, bodycolor);
        sm.put(s);
        s = new Rectangle(2, 138+i, 100, 30, 28, windowcolor);
        sm.put(s);
        s = new Circle(3, 185+i, 130, 4, lampcolor);
        sm.put(s);
        s = new Circle(4, 170+i,150,15, tirecolor);
        sm.put(s);
        s = new Circle(5, 135+i,150,15, tirecolor);
        sm.put(s);
        s = new Rectangle(10, 40+i,120,60,5, windcolor);
        sm.put(s);
        s = new Rectangle(11, 30+i,140,60,5, windcolor);
        sm.put(s);
        s = new Rectangle(12, 45+i,150,60,5, windcolor);
        sm.put(s);
        target.draw(sm);
        target.flush();
         try{
          Thread.sleep(10);
        }catch(InterruptedException e){
        }
      }
    }
  }
  
  public static void main(String[] args){
    Main01 m = new Main01();
    m.loop();
  }
}
