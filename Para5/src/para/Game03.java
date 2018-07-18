package para;

import java.util.Scanner;
import java.util.stream.IntStream;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;

import java.util.Random;

public class Game03 extends GameFrame{
  volatile Thread thread;
  final ShapeManager sm, wall, board, dead_wall, sm_score, sm2;
  Vec2 pos;
  Vec2 vel;
  int bpos;
  static final int WIDTH=320;
  static final int HEIGHT=660;
  Random random;

  int score;
  boolean power;
  int power_duration;
  int combo;
  final int score_per_dot = 1;
  final double combo_multiplier = 0.02;
  int timeout;
  Shape s_timeout;


  public Game03(){
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));
    title="BreakOut";
    sm = new OrderedShapeManager();
    wall = new OrderedShapeManager();
    board = new ShapeManager();

    dead_wall = new OrderedShapeManager();
    sm_score = new OrderedShapeManager();
    sm2 = new OrderedShapeManager();

    Attribute wallattr = new Attribute(250,230,200,true,0,0,0);
    wall.add(new Rectangle(0, 0, 0, 320, 20, wallattr));
    wall.add(new Rectangle(1, 0, 0, 20, 300, wallattr));
    wall.add(new Rectangle(2, 300,0, 20, 300, wallattr));

    wallattr = new Attribute(255, 100, 100, true, 0, 0 ,0);
    dead_wall.add(new Rectangle(3, 0,281, 320, 20, wallattr));
    //    wall.add(new Rectangle(3, 0,281, 120, 20, wallattr));
    //    wall.add(new Rectangle(4, 200,281, 120, 20, wallattr));

    random = new Random();
  }

  public void gamestart(int v){
    if(thread != null){
      return;
    }
    sm.clear();
    IntStream.range(0,65*30).forEach(n->{
        int x = n%65;
        int y = n/65;
        if(random.nextDouble() >= 0.98){
          sm2.add(new Rectangle(10+n,30+x*4,50+y*4,3,3,
                               new Attribute(100,255,100,true,0,0,0)));
        }else{
          sm.add(new Rectangle(10+n,30+x*4,50+y*4,3,3,
                               new Attribute(250,100,250,true,0,0,0)));
        }

      });
    thread = new Thread(()->{
        // pos = new Vec2(200,130);
        // vel = new Vec2(2,8);
        bpos = 160;

        reset();

        Attribute attr = new Attribute(150,150,150,true);
        board.put(new Camera(0, 0, 300,attr));
        board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
        canvas.draw(board);
        canvas.draw(sm);
        float time;
        float[] btime = new float[]{1.0f};
        float[] stime = new float[]{1.0f};
        float[] wtime = new float[]{1.0f};

        while(true){
          try{
            Thread.sleep(8);
          }catch(InterruptedException ex){
          }
          if((lefton ==1 || righton ==1)){
            bpos =bpos-8*lefton+8*righton;
            if(bpos<35){
              bpos = 35;
            }else if(285<bpos){
              bpos =285;
            }
            board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
          }
          CollisionChecker ccp = new CollisionCheckerParallel2(true);
          canvas.clear();
          canvas.draw(board);
          if(power){ // ボールがパワーアップしている
            canvas.drawCircle(10000,(int)pos.data[0],(int)pos.data[1],8,
                            new Attribute(255,100,100,true,0,0,0));
            canvas.drawCircle(10001,(int)pos.data[0],(int)pos.data[1],5,
                            new Attribute(100,255,100,true,0,0,0));
          }else{
            canvas.drawCircle(10000,(int)pos.data[0],(int)pos.data[1],5,
                            new Attribute(0,0,0,true,0,0,0));
          }

          if(power_duration < 0){
            power = false;
          }else{
            power_duration--;
          }
          update_score();

          // System.out.println(score);

          canvas.draw(sm);
          canvas.draw(wall);

          canvas.draw(dead_wall);
          canvas.draw(sm2);
          canvas.draw(sm_score);

          canvas.flush();
          if(timeout >= 180){
            // System.out.println("hoge");
            s_timeout = new Digit(5,160 ,160,40, 3, new Attribute(255,200,200));
          }else if(timeout >= 120 && timeout < 180){
            s_timeout = new Digit(5,160 ,160,40, 2, new Attribute(255,200,200));
          }else if(timeout >= 60 && timeout < 120){
            s_timeout = new Digit(5,160 ,160,40, 1, new Attribute(255,200,200));
          }else if(timeout > 0 && timeout < 60){
            s_timeout = new Digit(5,160 ,160,40, 0, new Attribute(255,200,200));
          }
          // System.out.println("hoge");
          if(timeout > 0){
            timeout--;
            sm_score.put(s_timeout);
          }else{
            sm_score.remove(s_timeout);
          }
          // System.out.println("hoge");
          time =1.0f;
          while(0<time){
            btime[0] = time;
            stime[0] = time;
            wtime[0] = time;
            Vec2 tmpbpos = new Vec2(pos);
            Vec2 tmpbvel = new Vec2(vel);
            Vec2 tmpspos = new Vec2(pos);
            Vec2 tmpsvel = new Vec2(vel);
            Vec2 tmpwpos = new Vec2(pos);
            Vec2 tmpwvel = new Vec2(vel);

            Shape s2=ccp.check(sm2, tmpspos, tmpsvel, stime);
            Shape d_w=ccp.check(dead_wall, tmpwpos, tmpwvel, wtime);

            Shape b=ccp.check(board, tmpbpos, tmpbvel, btime);
            Shape s=ccp.check(sm, tmpspos, tmpsvel, stime);
            Shape w=ccp.check(wall, tmpwpos, tmpwvel, wtime);

            if( b != null &&
                (s == null || stime[0]<btime[0]) &&
                (w == null || wtime[0]<btime[0])){
              pos = tmpbpos;
              vel = tmpbvel;
              time = btime[0];
            }else if(s != null){
              sm.remove(s);
              hit();
              if(!power){ // パワーアップ状態では貫通
                pos = tmpspos;
                vel = tmpsvel;
                time = stime[0];
              }else{
                pos = MathUtil.plus(pos, MathUtil.times(vel,time));
                time = 0;
              }
            }else if(w != null){
              pos = tmpwpos;
              vel = tmpwvel;
              time = wtime[0];
            }else if(d_w != null){ //地面に衝突
              reset();
              power = false;
              combo = 0;
            }else if(s2 != null){ //　パワーアップをゲット
              sm2.remove(s2);
              power = true;
              power_duration = 300;
              hit();
              if(!power){ // パワーアップ状態では貫通
                pos = tmpspos;
                vel = tmpsvel;
                time = stime[0];
              }else{
                pos = MathUtil.plus(pos, MathUtil.times(vel,time));
                time = 0;
              }
            }else{
              if(timeout <= 0) pos = MathUtil.plus(pos, MathUtil.times(vel,time));
              time = 0;
            }
          }
        }
      });
    thread.start();
  }

  /** ボールの状態を初期にリセット */
  private void reset(){
    pos = new Vec2(160, 180);
    float f = Double.valueOf(-4 + 8 * random.nextDouble()).floatValue();
    vel = new Vec2(f, 6);
    timeout = 240;
  }

  /** ボール当たり判定の点数計算 */
  private void hit(){
    combo++;
    score += score_per_dot + combo_multiplier * combo;
  }

  private void update_score(){
    sm_score.put(new Digit(0,100 + 140,490 + 120,20, score%10, new Attribute(200,200,200)));
    sm_score.put(new Digit(1,100 + 90,490 + 120,20,(score%100)/10, new Attribute(200,200,200)));
    sm_score.put(new Digit(2,100 + 40 ,490 + 120,20, (score%1000)/100, new Attribute(200,200,200)));
    sm_score.put(new Digit(3,100 - 10 ,490 + 120,20, (score%10000) / 1000, new Attribute(200,200,200)));
    sm_score.put(new Digit(4,100 - 60 ,490 + 120,20, score/10000, new Attribute(200,200,200)));

    canvas.draw(sm_score); // スコアの描画
  }
}
