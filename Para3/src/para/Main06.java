/*
1613354
星野シンジ
*/
package para;

import para.graphic.shape.*;
import para.graphic.target.*;

/** 増加する数値と減少する数値をマルチスレッドを使って表示するデモ
 */
public class Main06{
  final JavaFXTarget t1 = new JavaFXTarget("increment", 320,240);
  final JavaFXTarget t2 = new JavaFXTarget("decrement", 320,240);
  private final Thread ts1;
  private final Thread ts2;

  private Main06(){
    ShapeManager sm1 = new ShapeManager();
    for(int i=0; i<8; i++){
      sm1.put(new Circle(i, 20+i*40, 20, 10,
                         new Attribute(200, 250, 200, true)));
      sm1.put(new Circle(8+i, 20+i*40, 220, 10,
                         new Attribute(200, 250, 200, true)));
    }
    ShapeManager sm2;
    sm2 = sm1.duplicate();
    ts1 = new Thread(new Runner(t1, sm1, 1));
    ts2 = new Thread(new Runner(t2, sm2, -1));
  }

  public void start(){
    t1.init();
    t2.init();
    ts1.start();
    ts2.start();
  }

  public static void main(String[] args){
    Main06 m = new Main06();
    m.start();
  }

  private class Runner implements Runnable{
    final Target target;
    final ShapeManager sm;
    final int add;
    private Runner(Target t, ShapeManager sm, int add){
      target = t;
      this.sm = sm;
      this.add = add;
    }
    public void run(){
      int i=0;
      while(true){
        target.clear();
        target.draw(sm);
        target.flush();
        i = i+add;
        if(i<0){
          i=999;
        }else if(999<i){
          i=0;
        }
        sm.put(new Digit(100,230,120,30, i%10, new Attribute(200,200,200)));
        sm.put(new Digit(101,160,120,30,(i%100)/10, new Attribute(200,200,200)));
        sm.put(new Digit(102, 90,120,30, i/100, new Attribute(200,200,200)));
        try{
          Thread.sleep(70);
        }catch(InterruptedException ex){
        }
      }
    }
  }
}
