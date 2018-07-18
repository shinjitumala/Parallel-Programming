/*
1613354
星野シンジ
*/
package para;

import java.util.Scanner;
import java.util.Random;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;
import para.Garden;

/** モグラたたきゲームの雛形
 */
public class Game01 extends GameFrame{
  final static int WIDTH=400;
  final static int HEIGHT=700;
  final int MCOUNT=9;
  final int XCOUNT=3;
  Target inputside;
  Target outputside;
  Thread thread;
  ShapeManager osm;
  ShapeManager ism;
  private long prev;
  Random rand;
  private int last;
  private  int[] slot;

  private boolean[] slot_mole; // スコア計算に用いるためのフラグ。見逃したモグラの判定に使う。
  private volatile boolean game = true; // ゲームが終了するとfalseになる。スレッドを安全に終了させるために用いる。
  private int score = 0; // ゲームのスコア。

  Attribute mogattr;

  int M_level; // 難易度。時間が経つとだんだん難しくなる。
  int lives = 5; // ５匹モグラを殺しそこねるとゲームが終了する。
  ShapeManager bsm; // 背景のためのShapeManager
  ShapeManager ssm; // スコア表示のためのShapeManager

  public Game01(){
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));
    canvas.init();
    title = "Mole";
    outputside = canvas;
    inputside = canvas;
    osm = new ShapeManager();
    ism = new ShapeManager();

    bsm = new OrderedShapeManager(); // 背景のためのShapeManager
    ssm = new ShapeManager(); // スコア表示のためのShapeManager

    rand = new Random(System.currentTimeMillis());
    slot = new int[MCOUNT];

    slot_mole = new boolean[MCOUNT];

    mogattr = new Attribute(158,118,38);

    // 背景の初期化
    Attribute tmp = new Attribute(0, 134, 57);
    bsm.put(new Rectangle(1, 0, 0, 400, 400, tmp));
    tmp = new Attribute(129, 164, 255);
    bsm.put(new Rectangle(2, 0, 400, 400, 300, tmp));
    Attribute tmp2 = new Attribute(0, 0, 0);
    tmp = new Attribute(110, 35, 27);
    for(int i = 0; i < MCOUNT; i++){
      bsm.put(new Circle(20 + 2 * i + 1, (i%XCOUNT) * 130 + 60, (i/XCOUNT) * 130 + 60, 40, tmp));
      bsm.put(new Circle(20 + 2 * i + 2, (i%XCOUNT) * 130 + 60, (i/XCOUNT) * 130 + 60, 30, tmp2));
    }
    Garden.setMole(50, 50, 600, 25, bsm);

    inputside.draw(bsm); // 背景を描画
    inputside.flush();

  }

  public void gamestart(int v){


    Attribute attr = new Attribute(250,80,80);
    Attribute attr_outer = new Attribute(0, 0, 0, false);


    if(thread != null){
      return;
    }
    thread = new Thread(()->{
        while(game){ // ゲームが終わると、falseになる
          try{
            Thread.sleep(16);
          }catch(InterruptedException ex){
          }
          SynchronizedPoint p = xy.copy();
          if(prev != p.getTime()){
            prev = p.getTime();
            ism.put(new Circle(v, (int)p.getXY()[0], (int)p.getXY()[1],
                               20, attr));
            ism.put(new Circle(v + 1, (int)p.getXY()[0], (int)p.getXY()[1], 20, attr_outer));
            ism.put(new Circle(v + 2, (int)p.getXY()[0], (int)p.getXY()[1], 21, attr_outer));
            ism.put(new Circle(v + 3, (int)p.getXY()[0], (int)p.getXY()[1], 22, attr_outer));
            Shape s = InsideChecker.check(osm,
                                          new Vec2(p.getXY()[0], p.getXY()[1]));
            if(s != null){
              //System.out.println(p.getXY()[0]+" "+p.getXY()[1]+" "+p.getTime());
              int t_score = 100 + (int)(((double)slot[(s.getID()-10)/10] / 2000) * 50);
              slot[(s.getID()-10)/10]=0;
              slot_mole[(s.getID()-10)/10] = false; // モグラを殺したことを記録する。
              System.out.println("Killed a mole! Scored " + t_score + " points!");
              score += t_score;

              ism.put(new Digit(v + 4, -50 + (int)p.getXY()[0], 50+(int)p.getXY()[1], 20, t_score / 100, new Attribute(90, 255, 90)));
              ism.put(new Digit(v + 5, -50 + (int)p.getXY()[0] + 50, 50+(int)p.getXY()[1], 20, (t_score%100) / 10, new Attribute(90, 255, 90)));
              ism.put(new Digit(v + 6, -50 + (int)p.getXY()[0] + 100, 50+(int)p.getXY()[1], 20, t_score % 10, new Attribute(90, 255, 90)));
            }else{
              System.out.println("Missed a shot! Lost 200 points!");
              ism.put(new Digit(v + 4, -50 + (int)p.getXY()[0], 50+(int)p.getXY()[1], 20, 2, new Attribute(156, 22, 22)));
              ism.put(new Digit(v + 5, -50 + (int)p.getXY()[0] + 50, 50+(int)p.getXY()[1], 20, 0, new Attribute(156, 22, 22)));
              ism.put(new Digit(v + 6, -50 + (int)p.getXY()[0] + 100, 50+(int)p.getXY()[1], 20, 0, new Attribute(156, 22, 22)));
              score -= 200;
              if(score < 0) score = 0;
            }
          }else if(300 < System.currentTimeMillis()-prev){
            ism.remove(v);
            ism.remove(v + 1);
            ism.remove(v + 2);
            ism.remove(v + 3);
            ism.remove(v + 4);
            ism.remove(v + 5);
            ism.remove(v + 6);
          }
          inputside.clear();
          inputside.draw(bsm); // 背景を描画
          mole();
          update_score();

          inputside.draw(ism);
          inputside.flush();
        }
      });
    thread.start();
  }

  private void mole(){

    M_level++; // 難易度を少しずつあげる。

    int appear = rand.nextInt(100);
    if((99 - M_level / 600) <= appear){
      int head = rand.nextInt(MCOUNT-1)+1;
      int duration = 2000;

      if(slot[(last+head)%MCOUNT] <=0){
        slot[(last+head)%MCOUNT] = duration;
        slot_mole[(last+head)%MCOUNT] = true;
      }
    }
    for(int i=0;i<MCOUNT;i++){
      slot[i]=slot[i]-10;
    }
    for(int i=0;i<MCOUNT;i++){
      if(0<slot[i] && 600 >= slot[i]){
        //osm.put(new Circle(10+i*10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60, slot[i]/25,mogattr));
        Garden.setMole(10 + i * 10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60, slot[i] / 10, osm);
      }else if(600 < slot[i] && slot[i] <= 1600){
        Garden.setMole(10 + i * 10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60, 25, osm);
      }else if(1600 < slot[i]){
        Garden.setMole(10 + i * 10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60, (2000 - slot[i]) / 10, osm);
      }else if(slot[i] <= 0 && slot_mole[i] == true){
        Garden.removeMole(10 + i * 10, osm);
        slot_mole[i] = false;
        lives--;
        System.out.println("You missed a mole!");
        if(lives <= 0){ // ライフがゼロになってゲーム終了。
          System.out.println("GAME OVER");
          game = false;
        }
      }else{
        //osm.remove(10+i*10);
        Garden.removeMole(10 + i * 10, osm);
      }
    }
    inputside.draw(osm);
  }

  private void update_score(){
    ssm.put(new Digit(0,150 + 190,350 + 120,30, score%10, new Attribute(200,200,200)));
    ssm.put(new Digit(1,150 + 120,350 + 120,30,(score%100)/10, new Attribute(200,200,200)));
    ssm.put(new Digit(2,150 + 50,350 + 120,30, (score%1000)/100, new Attribute(200,200,200)));
    ssm.put(new Digit(3,150 - 20,350 + 120,30, (score%10000) / 1000, new Attribute(200,200,200)));
    ssm.put(new Digit(4,150 - 90,350 + 120,30, score/10000, new Attribute(200,200,200)));

    ssm.put(new Digit(5, 150, 350 + 250, 50, lives, new Attribute(200, 200, 200)));

    inputside.draw(ssm); // スコアの描画
  }
}
