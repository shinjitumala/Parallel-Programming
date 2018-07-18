/** 星野シンジ 1613354 */
package para;

import java.util.Scanner;
import java.util.stream.IntStream;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;

public class Main11{
  final JavaFXTarget jf;
  final MainParser ps;
  final ShapeManager sm, wall;
  Thread thread;
  final static String data =
    "shape 10 Circle 80 60 20 Attribute Color 225 105 0 Fill true\n"+
    "shape 11 Circle 1760 60 20 Attribute Color 225 105 0 Fill true\n"+
    "shape 12 Circle 80 900 20 Attribute Color 225 105 0 Fill true\n"+
    "shape 13 Circle 1760 900 20 Attribute Color 225 105 0 Fill true\n";
  Vec2 pos;
  Vec2 vel;
  volatile int bpos;
  final String selector;

  public Main11(String selector){
    this.selector = selector;
    sm = new OrderedShapeManager();
    wall = new OrderedShapeManager();
    jf = new JavaFXTarget("Main11",1840,960);
    ps = new MainParser(jf, sm);
    ps.parse(new Scanner(data));
    Attribute wallattr = new Attribute(250,230,200,true,0,0,0);
    wall.add(new Rectangle(0, 0, 0, 1840, 20, wallattr));
    wall.add(new Rectangle(1, 0, 0, 20, 960, wallattr));
    wall.add(new Rectangle(2, 1820,0, 20, 960, wallattr));
    wall.add(new Rectangle(3, 0,940, 1840, 20, wallattr));
    bpos = 150;
    pos = new Vec2(200,250);
    vel = new Vec2(16*10,61*10);
    //vel = new Vec2(4, 61/4.0f);
  }

  public void start(){
    IntStream.range(0,445*225).forEach(n->{
        int x = n%445;
        int y = n/445;
        sm.add(new Rectangle(20+n,30+x*4,30+y*4,3,3,
                             new Attribute(250,100,250,true,0,0,0)));
      });
    jf.init();
    CollisionChecker ccp;
    switch(selector){
    case "SINGLE":
      ccp = new CollisionCheckerParallel2(false);
      break;
    case "PARALLEL":
      ccp = new CollisionCheckerParallel2(true);
      break;
    case "POOL":
      ccp = new CollisionCheckerParallel3(20);
      break;
    default:
      ccp = new CollisionCheckerParallel2(false);
    }
    thread = new Thread(new Runnable(){
        public void run(){
          float time;
          float[] stime = new float[]{1.0f};
          float[] wtime = new float[]{1.0f};
          long startTimeMS = System.currentTimeMillis();
          long count=-1;
          while(true){
            count++;
            if(count==100){
              long endTimeMS = System.currentTimeMillis();
              System.out.println((endTimeMS-startTimeMS) + "msec");
              try{
                Thread.sleep(5000);
              }catch(InterruptedException e){

              }
              System.exit(0);
            }
            jf.clear();
            jf.drawCircle(1000,(int)pos.data[0],(int)pos.data[1],5,
                          new Attribute(0,0,0,true,0,0,0));
            jf.draw(sm);
            jf.draw(wall);
            jf.flush();
            time =1.0f;
            while(0<time){
              stime[0] = time;
              wtime[0] = time;
              Vec2 tmpspos = new Vec2(pos);
              Vec2 tmpsvel = new Vec2(vel);
              Vec2 tmpwpos = new Vec2(pos);
              Vec2 tmpwvel = new Vec2(vel);
              Shape s=ccp.check(sm, tmpspos, tmpsvel, stime);
              Shape w=ccp.check(wall, tmpwpos, tmpwvel, wtime);
              if(s != null) {
                sm.remove(s);
                pos = tmpspos;
                vel = tmpsvel;
                time = stime[0];
              }else if(w != null){
                pos = tmpwpos;
                vel = tmpwvel;
                time = wtime[0];
              }else{
                pos = MathUtil.plus(pos, MathUtil.times(vel,time));
                time = 0;
              }
            }
          }
        }
      });
    thread.start();
  }

  public static void main(String[] args){
    try{
      Thread.sleep(10000);
    }catch(InterruptedException e){

    }
    new Main11(args[0]).start();
  }
}
