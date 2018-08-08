/*　1613354 星野シンジ */
package para;

import java.io.IOException;
import java.util.stream.IntStream;

import para.graphic.shape.Rectangle;
import para.graphic.shape.Attribute;
import para.graphic.shape.ShapeManager;
import para.graphic.shape.OrderedShapeManager;
import para.graphic.shape.Vec2;
import para.graphic.shape.MathUtil;
import para.graphic.shape.Shape;
import para.graphic.shape.Circle;
import para.graphic.shape.Digit;
import para.graphic.shape.CollisionChecker;
import para.graphic.target.TranslateTarget;
import para.graphic.target.TranslationRule;
import para.game.GameServerFrame;
import para.game.GameInputThread;
import para.game.GameTextTarget;

import java.util.Random;

public class GameServer01 {
  final Attribute wallattr = new Attribute(250, 230, 200, true, 0, 0, 0);
  final Attribute ballattr = new Attribute(250, 120, 120, true, 0, 0, 0);
  final Attribute scoreattr = new Attribute(60, 60, 60, true, 0, 0, 0);
  final int MAXCONNECTION = 2;
  final GameServerFrame gsf;
  final ShapeManager[] userinput;
  final ShapeManager[] wall;
  final ShapeManager[] blocks;
  final ShapeManager[] ballandscore;
  final Vec2[] pos;
  final Vec2[] vel;
  final int[] score;
  final CollisionChecker checker;

  // ボールパワーアップ関連の変数
  final ShapeManager[] blocks_special; // パワーアップブロックのShapeManager
  final int[] powerup_duration; // 現在のパワーアップ残り時間
  final boolean[] powerup; // 現在パワーアップしているかどうか

  // 妨害ブロック関連の変数
  final ShapeManager[] blocks_special_invisible; // 妨害ブロックのShapeManager
  final int[] invisible_duration; // 現在のボール透明化の残り時間
  final boolean[] invisible; // 現在ボールが透明かしているかどうか

  final int[] combo_count;        // 現在のコンボ数
  final int[] block_count;        // 残りのブロックの数
  final ShapeManager[] dead_wall; // ボールを落とすと死亡する壁

  // ゲームバランスを変える定数
  final int SCORE_PER_BLOCK = 1;
  final double COMBO_MULTIPLIER = 0.06;
  final int DEATH_PENTALTY = -10;
  final double SPECIAL_BLOCK_SPAWN_BASE = 0.90;
  final double SPECIAL_INVIS_BLOCK_RATE = 0.10;
  final int BLOCKS_WIDTH = 20;
  final int BLOCKS_HEIGHT = 10;
  final int BLOCKS_TOTAL = BLOCKS_WIDTH * BLOCKS_HEIGHT;
  final int BLOCK_SIZE = (350 - 60) / (BLOCKS_WIDTH + 1);
  Random random;

  // ゲームの1フレームあたりの時間（ミリ秒）
  final int FRAMETIME = 128;

  // ゲーム終了を示す変数
  private volatile boolean game = true;

  private GameServer01() {
    checker = new CollisionChecker();
    gsf = new GameServerFrame(MAXCONNECTION);
    userinput = new ShapeManager[MAXCONNECTION];
    wall = new OrderedShapeManager[MAXCONNECTION];
    blocks = new OrderedShapeManager[MAXCONNECTION];
    ballandscore = new ShapeManager[MAXCONNECTION];
    pos = new Vec2[MAXCONNECTION];
    vel = new Vec2[MAXCONNECTION];
    score = new int[MAXCONNECTION];

    //拡張で追加された変数の初期化
    blocks_special = new OrderedShapeManager[MAXCONNECTION];
    powerup_duration = new int[MAXCONNECTION];
    powerup = new boolean[MAXCONNECTION];
    combo_count = new int[MAXCONNECTION];
    block_count = new int[MAXCONNECTION];
    dead_wall = new OrderedShapeManager[MAXCONNECTION];
    invisible_duration = new int[MAXCONNECTION];
    invisible = new boolean[MAXCONNECTION];
    blocks_special_invisible = new OrderedShapeManager[MAXCONNECTION];

    for (int i = 0; i < userinput.length; i++) {
      userinput[i] = new ShapeManager();
      ballandscore[i] = new ShapeManager();
      wall[i] = new OrderedShapeManager();
      blocks[i] = new OrderedShapeManager();
      pos[i] = new Vec2(i * 350 + 150, 200);
      vel[i] = new Vec2(0, 0);

      // 拡張で追加された変数の初期化
      blocks_special[i] = new OrderedShapeManager();
      dead_wall[i] = new OrderedShapeManager();
      blocks_special_invisible[i] = new OrderedShapeManager();
    }
  }

  public void start() {
    try {
      gsf.init();
    } catch (IOException ex) {
      System.err.println(ex);
    }
    gsf.welcome();

    int gs = 0;
    while (true) {
      GameInputThread git = gsf.queue.poll();
      if (git != null) {
        int id = git.getUserID();
        init(id);
        startReceiver(git);
      }
      try {
        Thread.sleep(FRAMETIME);
      } catch (InterruptedException ex) {
      }
      // System.out.println("User: " + gsf.getUsermCount());
      if (gsf.getUserCount() == MAXCONNECTION) {
        for (int i = 0; i < MAXCONNECTION; i++) {
          GameTextTarget out = gsf.getUserOutput(i);
          if (out != null) {
            calcForOneUser(i);

            // ボールの描画
            if (!invisible[i]) {
              if (powerup[i]) {
                ballandscore[i].put(new Circle(
                    i * 10000 + 1, (int)pos[i].data[0], (int)pos[i].data[1], 5,
                    new Attribute(100, 255, 100, true)));
              } else {
                ballandscore[i].put(
                    new Circle(i * 10000 + 1, (int)pos[i].data[0],
                               (int)pos[i].data[1], 5, ballattr));
              }
            } else {
              ballandscore[i].put(new Circle(
                  i * 10000 + 1, (int)pos[i].data[0], (int)pos[i].data[1], 5,
                  new Attribute(255, 255, 255, true)));
            }

            putScore(i, score[i]);

            // 負けている方のカメラが減色される
            if (score[i] > score[(i + 1) % MAXCONNECTION]) {
              gs = 0;
            } else {
              gs = (score[(i + 1) % MAXCONNECTION] - score[i]);
            }
            if (game)
              out.gamerstate(gs); // Gamerの状態をクライアントに伝える
            distributeOutput(out);
          }
        }
      }
    }
  }

  private void startReceiver(GameInputThread git) {
    int id = git.getUserID();
    git.init(new TranslateTarget(
                 userinput[id],
                 new TranslationRule(id * 10000, new Vec2(id * 350, 0))),
             new ShapeManager[] {userinput[id], wall[id], blocks[id],
                                 ballandscore[id]});
    git.start();
  }

  private void init(int id) {
    random = new Random();

    wall[id].add(
        new Rectangle(id * 10000 + 5, id * 350 + 0, 0, 320, 20, wallattr));
    wall[id].add(
        new Rectangle(id * 10000 + 6, id * 350 + 0, 0, 20, 300, wallattr));
    wall[id].add(
        new Rectangle(id * 10000 + 7, id * 350 + 300, 0, 20, 300, wallattr));
    dead_wall[id].add(new Rectangle(id * 10000 + 8, id * 350 + 0, 281, 320, 20,
                                    new Attribute(255, 0, 0, true)));
    IntStream.range(0, BLOCKS_TOTAL).forEach(n -> {
      int x = n % BLOCKS_WIDTH;
      int y = n / BLOCKS_WIDTH;

      if (random.nextDouble() >=
          SPECIAL_BLOCK_SPAWN_BASE) { // ランダムな確率で特別なブロックを生成
        if (random.nextDouble() >=
            SPECIAL_INVIS_BLOCK_RATE) { // パワーアップブロック
          blocks_special[id].add(
              new Rectangle(id * 10000 + n, id * 350 + 30 + x * BLOCK_SIZE,
                            30 + y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2,
                            new Attribute(100, 255, 100, true, 0, 0, 0)));
        } else { // 妨害ブロック
          blocks_special_invisible[id].add(
              new Rectangle(id * 10000 + n, id * 350 + 30 + x * BLOCK_SIZE,
                            30 + y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2,
                            new Attribute(100, 100, 255, true, 0, 0, 0)));
        }
      } else { // 通常ブロック
        blocks[id].add(
            new Rectangle(id * 10000 + n, id * 350 + 30 + x * BLOCK_SIZE,
                          30 + y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2,
                          new Attribute(250, 100, 250, true, 0, 0, 0)));
      }
    });
    block_count[id] = BLOCKS_TOTAL;
    reset(id);
    score[id] = 0;
  }

  private void calcForOneUser(int id) {
    if (game) {
      float[] btime = new float[] {1.0f};
      float[] stime = new float[] {1.0f};
      float[] wtime = new float[] {1.0f};
      float time = 1.0f;
      while (0 < time) {
        btime[0] = time;
        stime[0] = time;
        wtime[0] = time;
        Vec2 tmpbpos = new Vec2(pos[id]);
        Vec2 tmpbvel = new Vec2(vel[id]);
        Vec2 tmpspos = new Vec2(pos[id]);
        Vec2 tmpsvel = new Vec2(vel[id]);
        Vec2 tmpwpos = new Vec2(pos[id]);
        Vec2 tmpwvel = new Vec2(vel[id]);
        Shape b = checker.check(userinput[id], tmpbpos, tmpbvel, btime);
        Shape s = checker.check(blocks[id], tmpspos, tmpsvel, stime);
        Shape w = checker.check(wall[id], tmpwpos, tmpwvel, wtime);

        Shape sb = checker.check(blocks_special[id], tmpspos, tmpsvel, stime);
        Shape dw = checker.check(dead_wall[id], tmpspos, tmpsvel, stime);
        Shape sbi = checker.check(blocks_special_invisible[id], tmpspos,
                                  tmpsvel, stime);

        // 当たり判定
        if (b != null && (s == null || stime[0] < btime[0]) &&
            (w == null || wtime[0] < btime[0])) {
          pos[id] = tmpbpos;
          vel[id] = tmpbvel;
          time = btime[0];
        } else if (s != null) { //　通常ブロック
          blocks[id].remove(s); // block hit!
          destroy_block(id);
          pos[id] = tmpspos;
          if (!powerup[id]) { // パワーアップ状態では貫通
            vel[id] = tmpsvel;
          }
          time = stime[0];
        } else if (sb != null) { //　パワーアップをゲット
          blocks_special[id].remove(sb);
          destroy_block(id);
          getPowerUp(id);
          if (!powerup[id]) { // パワーアップ状態では貫通
            vel[id] = tmpsvel;
          }
          pos[id] = tmpspos;
          time = stime[0];
        } else if (sbi != null) { // 妨害ブロックをゲット
          blocks_special_invisible[id].remove(sbi);
          destroy_block(id);
          getInvis(id);
          if (!invisible[id]) { // パワーアップ状態では貫通
            vel[id] = tmpsvel;
          }
          pos[id] = tmpspos;
          time = stime[0];
        } else if (dw != null) { //地面に衝突
          death(id);
          reset(id);
        } else if (w != null) {
          pos[id] = tmpwpos;
          vel[id] = tmpwvel;
          time = wtime[0];
        } else {
          pos[id] = MathUtil.plus(pos[id], MathUtil.times(vel[id], time));
          time = 0;
        }
      }
    }

    // 毎フレームの更新
    if (powerup_duration[id] < 0) {
      powerup[id] = false;
    } else {
      powerup_duration[id]--;
    }

    if (invisible_duration[id] < 0) {
      invisible[id] = false;
    } else {
      invisible_duration[id]--;
    }
  }

  private void putScore(int id, int score) {
    int one = score % 10;
    int ten = (score % 100) / 10;
    int hundred = (score % 1000) / 100;
    int thausand = (score % 10000) / 1000;
    ballandscore[id].put(
        new Digit(id * 10000 + 2, id * 300 + 250, 630, 20, one, scoreattr));
    ballandscore[id].put(
        new Digit(id * 10000 + 3, id * 300 + 200, 630, 20, ten, scoreattr));
    ballandscore[id].put(
        new Digit(id * 10000 + 4, id * 300 + 150, 630, 20, hundred, scoreattr));
    ballandscore[id].put(new Digit(id * 10000 + 5, id * 300 + 100, 630, 20,
                                   thausand, scoreattr));
  }

  private void distributeOutput(GameTextTarget out) {
    if (out == null) {
      return;
    }
    out.clear();
    for (int i = 0; i < MAXCONNECTION; i++) {
      if (gsf.getUserOutput(i) != null) {
        out.draw(wall[i]);
        out.draw(blocks[i]);
        out.draw(userinput[i]);
        out.draw(ballandscore[i]);

        // 拡張で追加されたShapeManager
        out.draw(dead_wall[i]);
        out.draw(blocks_special[i]);
        out.draw(blocks_special_invisible[i]);
      }
    }
    out.flush();
  }

  public static void main(String[] args) {
    GameServer01 gs = new GameServer01();
    gs.start();
  }

  /* ゲームオーバになった時に呼ばれる関数 */
  private void gameover() {
    game = false;
    // 勝敗判定
    int i = 0;
    if (score[0] > score[1]) {
      i = 1;
    }
    GameTextTarget out = gsf.getUserOutput(i);
    out.gamerstate(255);

    Thread timer = new Thread(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
      }
      gsf.disconnectAll();
    });
    timer.start();
  }

  /* ボールを落とした時に呼ばれる */
  private void death(int id) {
    score[id] += DEATH_PENTALTY;
    if (score[id] < 0)
      score[id] = 0;
    combo_count[id] = 0;
    powerup[id] = false;
    powerup_duration[id] = 0;
  }

  /* ブロックを破壊した時に呼ばれる */
  private void destroy_block(int id) {
    score[id] += SCORE_PER_BLOCK + COMBO_MULTIPLIER * combo_count[id];
    combo_count[id]++;
    block_count[id]--;
    if (block_count[id] <= 0)
      gameover();
    if (Math.abs(score[id] - score[(id + 1) % MAXCONNECTION]) > 256)
      gameover();

    if (combo_count[id] != 0) {
      ballandscore[id].put(
          new Digit(id * 10000 + 5000 + 0,
                    (int)pos[id].data[0] + 20 + 16 * combo_count[id] / 30,
                    (int)pos[id].data[1], 10 + combo_count[id] / 30,
                    combo_count[id] % 10, new Attribute(255, 50, 50)));
      if (combo_count[id] >= 10)
        ballandscore[id].put(new Digit(
            id * 10000 + 5000 + 1,
            (int)pos[id].data[0] - 5 + 8 * combo_count[id] / 30,
            (int)pos[id].data[1], 10 + combo_count[id] / 30,
            (combo_count[id] % 100) / 10, new Attribute(255, 50, 50)));
      if (combo_count[id] >= 100)
        ballandscore[id].put(
            new Digit(id * 10000 + 5000 + 2, (int)pos[id].data[0] - 30,
                      (int)pos[id].data[1], 10 + combo_count[id] / 30,
                      combo_count[id] / 100, new Attribute(255, 50, 50)));
    }
  }

  /* パワーアップを取得 */
  private void getPowerUp(int id) {
    powerup[id] = true;
    powerup_duration[id] = 5000 / FRAMETIME;
  }

  /* 妨害ブロックを取得 */
  private void getInvis(int id) {
    invisible[(id + 1) % MAXCONNECTION] = true;
    invisible_duration[id] = 5000 / FRAMETIME;
  }

  /* ボールが死んだ際にリセット */
  private void reset(int id) {
    pos[id] = new Vec2(id * 350 + 150, 250);
    float f = Double.valueOf(-4 + 8 * random.nextDouble()).floatValue();
    vel[id] = new Vec2(f, -12);

    combo_count[id] = 0;
    powerup[id] = false;
    powerup_duration[id] = 0;

    ballandscore[id].remove(id * 10000 + 5000 + 0);
    ballandscore[id].remove(id * 10000 + 5000 + 1);
    ballandscore[id].remove(id * 10000 + 5000 + 2);
  }
}
