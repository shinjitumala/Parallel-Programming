/** 1613354 星野シンジ */
package para;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.MainParser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** クライアントからの通信を受けて描画するサーバプログラム。
 * 監視ポートは30000番
 */
public class Main07{
  final public int PORTNO=30000;
  final int MAXCONNECTION=3;
  final Target target;
  final ShapeManager[] sms;
  ServerSocket ss = null;

  final Executor exec;
  volatile boolean[] connection = new boolean[MAXCONNECTION];

  /** 受け付け用ソケットを開くこと、受信データの格納場所を用意すること
   * を行う
   */
  public Main07(){
    target = new JavaFXTarget("Server", 320*MAXCONNECTION, 240);
    //target = new TextTarget(System.out);

    exec = Executors.newFixedThreadPool(3);
    try{
      ss = new ServerSocket(PORTNO);
    }catch(IOException e){
      System.out.println(e);
    }
    sms = new ShapeManager[MAXCONNECTION];
    for(int i=0;i<MAXCONNECTION;i++){
      sms[i] = new OrderedShapeManager();
    }
  }

  /** 受け付けたデータを表示するウィンドウの初期化とそこに受信データを表示するスレッドの開始
   */
  public void init(){
    target.init();
    target.clear();
    target.flush();
    new Thread(()->{
        while(true){
          for(ShapeManager sm: sms){
            synchronized(sm){
              target.draw(sm);
            }
          }
          target.flush();
          try{
            Thread.sleep(100);
          }catch(InterruptedException ex){
          }
        }
    }).start();
  }

  /** 受信の処理をする
   */
  /*
  public void start(){
    int i=0;
    while(true){
      try(Socket s = ss.accept()){
        BufferedReader r =
          new BufferedReader(new InputStreamReader(s.getInputStream()));
        ShapeManager dummy = new ShapeManager();
        MainParser parser
          = new MainParser(new TranslateTarget(sms[i],
                            new TranslationRule(10000*i, new Vec2(320*i,0))),
                           dummy);
        parser.parse(new Scanner(r));
      }catch(IOException ex){
        System.err.print(ex);
      }
      i=(i+1)%MAXCONNECTION;
    }
  }
  */

  /** 通信待ち */
  public void start(){
    while(true){
      try{
        Socket s = ss.accept();
        Runnable r = new Runnable(){
          public void run(){
            communicate(s);
          }
        };
        exec.execute(r);
      }catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  /** サーバとの１セッションを扱う
   */
  private void communicate(Socket socket){
    int i = 0;
    while(connection[i]){
      i = (i + 1) % MAXCONNECTION;
    }
    System.out.println("Connection number " + i + " started.");
    connection[i] = true;
    try(socket){
      BufferedReader r =
        new BufferedReader(new InputStreamReader(socket.getInputStream()));
      ShapeManager dummy = new ShapeManager();
      MainParser parser
        = new MainParser(new TranslateTarget(sms[i],
                          new TranslationRule(10000*i, new Vec2(320*i,0))),
                         dummy);
      parser.parse(new Scanner(r));
    }catch(IOException ex){
      System.err.print(ex);
    }
    System.out.println("Connection number " + i + " ended.");
    connection[i] = false;
  }

  public static void main(String[] args){
    Main07 m = new Main07();
    m.init();
    m.start();
  }
}
