package para;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.MainParser;

/** クライアントからの通信を受けて描画するサーバプログラム。
 * 監視ポートは30000番
 */
public class Main07{
  final public int PORTNO=30000;
  final int MAXCONNECTION=3;
  final Target target;
  final ShapeManager[] sms;
  final ServerSocket ss;

  /** 受け付け用ソケットを開くこと、受信データの格納場所を用意すること
   * を行う
   */
  public Main07(){
    target = new JavaFXTarget("Server", 320*MAXCONNECTION, 240);
    //target = new TextTarget(System.out);
    ServerSocket tmp=null;
    try{
      tmp = new ServerSocket(PORTNO);
    }catch(IOException ex){
      System.err.println(ex);
      System.exit(1);
    }
    ss = tmp;
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

  public static void main(String[] args){
    Main07 m = new Main07();
    m.init();
    m.start();
  }
}
