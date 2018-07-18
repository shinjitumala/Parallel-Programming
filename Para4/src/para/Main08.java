/* 1613354 星野シンジ */
package para;

import java.io.*;
import java.net.*;
import para.graphic.shape.*;
import para.graphic.target.*;
import java.util.Scanner;
import para.graphic.parser.MainParser;


/** カメラを起動し、サーバに送るデモ
 */
public class Main08{
  final int MAXCONNECTION = 3;
  Target target;
  ShapeManager sm;

  /** メインメソッド
   * @param args args[0]は相手先のホスト
   */
  public static void main(String[] args){
    final int PORTNO = 30000;
    ShapeManager sm = new ShapeManager();
    sm.put(new Camera(0,0,0));
    try(Socket s = new Socket(args[0],PORTNO)){
      Main08 t = new Main08(s);
      PrintStream ps = new PrintStream(s.getOutputStream());
      Target target = new TextTarget(ps);
      //Target target = new TextTarget(System.out);
      target.init();
      target.clear();
      while(true){
        target.draw(sm);
        target.flush();
        try{
          Thread.sleep(100);
        }catch(InterruptedException ex){
        }
        if(ps.checkError()){
          break;
        }
      }
    }catch(IOException ex){
      System.err.println(ex);
    }
  }

   Main08(Socket s){
    target = new JavaFXTarget("Recieved signal", 320*MAXCONNECTION, 240);
    new Thread(()->{
      try(s){
        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
        sm = new ShapeManager();
        MainParser parser = new MainParser(target, sm);
        parser.parse(new Scanner(r));
      }catch(IOException e){
        System.out.println(e);
      }catch(NullPointerException e){

      }
    }).start();
    new Thread(()->{
      target.init();
      target.clear();
      target.flush();
      while(true){
        target.draw(sm);
        target.flush();
        try{
          Thread.sleep(100);
        }catch(InterruptedException e){

        }
      }
    }).start();
  }
}
