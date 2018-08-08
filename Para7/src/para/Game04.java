/** 1613354 星野シンジ */
package para;

import java.util.Scanner;
import java.util.List;
import java.net.*;
import java.io.*;
import para.graphic.target.*;
import para.graphic.opencl.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;

import para.graphic.target.TargetFilter;

public class Game04 extends GameFrame {
  TargetImageFilter inputside;
  final Target outputside;
  volatile Thread thread;
  InputStream istream;
  ShapeManager osm;
  ShapeManager ism;
  String serveraddress;
  static final int WIDTH = 700;
  static final int HEIGHT = 700;

  final Target record; // 録画用

  public Game04() {
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));

    title = "Game04";
    outputside = canvas;
    record = new TargetRecorder("recorddelay", outputside);
    osm = new OrderedShapeManager();
    ism = new ShapeManager();

    record.init();
    record.clear();
  }

  public void init() {
    List<String> params = getParameters().getRaw();
    if (params.size() != 0) {
      serveraddress = params.get(0);
    } else {
      serveraddress = "localhost";
    }
  }

  public void gamestart(int v) {
    if (thread != null) {
      return;
    }
    try {
      Socket socket;
      socket = new Socket(serveraddress, para.game.GameServerFrame.PORTNO);
      istream = socket.getInputStream();
      OutputStream ostream = socket.getOutputStream();
      inputside = new TargetImageFilter(new TextTarget(WIDTH, HEIGHT, ostream),
                                        this, "imagefilter.cl", "Filter8");
    } catch (IOException ex) {
      System.err.print("To:" + serveraddress + " ");
      System.err.println(ex);
      System.exit(0);
    }

    /* ユーザ入力をサーバに送信するスレッド */
    thread = new Thread(() -> {
      int x = 150;
      Attribute attr = new Attribute(200, 128, 128);
      ism.put(new Camera(0, 0, 300, attr));
      ism.put(new Rectangle(1, x, 30 * 1 + 225, 80 - 10 * v, 10, attr)); // 難易度が高いと、バーが短くなる

      //録画用
      record.draw(ism);
      record.draw(osm);
      record.flush();
      inputside.draw(ism);
      while (true) {
        try {
          Thread.sleep(80);
        } catch (InterruptedException ex) {
          thread = null;
          break;
        }
        if ((lefton == 1 || righton == 1)) {
          x = x - 15 * lefton + 15 * righton;
          ism.put(new Rectangle(1, x, 30 * 1 + 225, 80 - 10 * v, 10, attr));
        }
        inputside.setParameter(gamerstate);
        inputside.draw(ism);
      }
    }, "UserInput");
    thread.start();

    /* 受信したデータを画面に出力するスレッド */
    Thread thread2 = new Thread(() -> {
      GameMainParser parser = new GameMainParser(this, outputside, osm);
      BufferedReader br = new BufferedReader(new InputStreamReader(istream));
      parser.parse(new Scanner(istream)); // loop
      System.out.println("connection closed");
      thread.interrupt();
    });
    thread2.start();
  }
}
