package para;

import java.util.Scanner;
import java.util.stream.IntStream;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;

import java.util.Random;

public class Game03 extends GameFrame{
  volatile Thread thread, cam_thread;
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
  final double combo_multiplier = 0.06;
  int timeout;
  Shape s_timeout;
  int ball_count;
  boolean game = true;
  final int FRAMETIME = 33;


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

    ball_count = 24 * 13;
    IntStream.range(0,24*13).forEach(n->{
        int x = n%24;
        int y = n/24;
        if(random.nextDouble() >= 0.90 + 0.01 * v * 2){
          sm2.add(new Rectangle(40+n,40+x*10,40+y*10,9,9,
                               new Attribute(100,255,100,true,0,0,0)));
        }else{
          sm.add(new Rectangle(40+n,40+x*10,40+y*10,9,9,
                               new Attribute(250,100,250,true,0,0,0)));
        }

      });
    thread = new Thread(()->{
        // pos = new Vec2(200,130);
        // vel = new Vec2(2,8);
        bpos = 160;

        reset(v);

        Attribute attr = new Attribute(150,150,150,true);
        board.put(new Camera(0, 0, 300,attr));
        board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
        canvas.draw(board);
        canvas.draw(sm);
        float time;
        float[] btime = new float[]{1.0f};
        float[] stime = new float[]{1.0f};
        float[] wtime = new float[]{1.0f};

        while(game){
          try{
            Thread.sleep(FRAMETIME);
          }catch(InterruptedException ex){
          }
          if((lefton ==1 || righton ==1)){
            bpos =bpos-12*lefton+12*righton;
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
          check_end();

          // System.out.println(score);

          canvas.draw(sm);
          canvas.draw(wall);

          canvas.draw(dead_wall);
          canvas.draw(sm2);
          canvas.draw(sm_score);

          canvas.flush();
          if(timeout >= 3000 / FRAMETIME){
            // System.out.println("hoge");
            s_timeout = new Digit(5,160 ,160,40, 3, new Attribute(255,200,200));
          }else if(timeout >= 2000 / FRAMETIME && timeout < 3000 / FRAMETIME){
            s_timeout = new Digit(5,160 ,160,40, 2, new Attribute(255,200,200));
          }else if(timeout >= 1000 / FRAMETIME && timeout < 2000 / FRAMETIME){
            s_timeout = new Digit(5,160 ,160,40, 1, new Attribute(255,200,200));
          }else if(timeout > 0 && timeout < 1000 / FRAMETIME){
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
                vel = tmpsvel;
              }
              time = stime[0];
              pos = tmpspos;
            }else if(w != null){
              pos = tmpwpos;
              vel = tmpwvel;
              time = wtime[0];
            }else if(d_w != null){ //地面に衝突
              reset(v);
              power = false;
              combo = 0;
            }else if(s2 != null){ //　パワーアップをゲット
              sm2.remove(s2);
              power = true;
              power_duration = 5000 / FRAMETIME;
              hit();
              if(!power){ // パワーアップ状態では貫通
                vel = tmpsvel;
              }
              pos = tmpspos;
              time = stime[0];
            }else{
              if(timeout <= 0) pos = MathUtil.plus(pos, MathUtil.times(vel,time));
              time = 0;
            }
          }
        }

        // ゲーム終了後の処理
        canvas.clear();
        title = "GAMEOVER";
        System.out.println("GAMEOVER");
      });
    thread.start();
  }

  /** ボールの状態を初期にリセット */
  private void reset(int v){
    pos = new Vec2(160, 180);
    float f = Double.valueOf(-4 + 8 * random.nextDouble()).floatValue();
    vel = new Vec2(f, 6 + v);
    timeout = 4000 / FRAMETIME;

    sm_score.remove(6);
    sm_score.remove(7);
    sm_score.remove(8);
  }

  /** ボール当たり判定の点数計算 */
  private void hit(){
    combo++;
    score += score_per_dot + combo_multiplier * combo;
    ball_count--;

    if(combo != 0){
      sm_score.put(new Digit(6,(int)pos.data[0] + 20 + 20 * combo / 30,(int)pos.data[1], 10 + combo / 30, combo % 10, new Attribute(255,50,50)));
      if (combo >= 10) sm_score.put(new Digit(7,(int)pos.data[0] - 5 + 20 * combo / 30,(int)pos.data[1], 10 + combo / 30, (combo % 100) / 10,  new Attribute(255,50,50)));
      if (combo >= 100) sm_score.put(new Digit(8,(int)pos.data[0] - 30,(int)pos.data[1], 10 + combo / 30, combo / 100, new Attribute(255,50,50)));
    }
  }

  private void update_score(){
    sm_score.put(new Digit(0,100 + 140,490 + 120,20, score%10, new Attribute(200,200,200)));
    sm_score.put(new Digit(1,100 + 90,490 + 120,20,(score%100)/10, new Attribute(200,200,200)));
    sm_score.put(new Digit(2,100 + 40 ,490 + 120,20, (score%1000)/100, new Attribute(200,200,200)));
    sm_score.put(new Digit(3,100 - 10 ,490 + 120,20, (score%10000) / 1000, new Attribute(200,200,200)));
    sm_score.put(new Digit(4,100 - 60 ,490 + 120,20, score/10000, new Attribute(200,200,200)));

    canvas.draw(sm_score); // スコアの描画
  }

  /**　終了条件をチェック */
  private void check_end(){
    if (ball_count == 0) game = false;
    title = "GAMEOVER";
  }
}
