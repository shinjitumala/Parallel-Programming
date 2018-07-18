/*
学籍番号 : 1613354
氏名 : 星野シンジ
*/
package para.graphic.target;
import para.graphic.shape.Attribute;
import para.graphic.shape.ShapeManager;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.lang.IllegalStateException;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Paint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
//import javafx.scene.paint.Color; // not Color in javafx module
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Graphics;

import javax.imageio.*;
import java.io.*;

public class JavaFXTarget implements Target{
  int WIDTH;
  int HEIGHT;
  static AtomicInteger counter;
  JavaFXCanvasTarget canvas;
  BufferedImage buffer;
  Graphics graphics;
  String title;
  Group root;
  Scene scene;
  Stage stage;
  WritableImage image;

  static{
    try{
      Platform.startup​(new Runnable(){public void run(){};});
      //Platform.startup​(()->{});
    }catch(IllegalStateException e){
    }
  }

  public JavaFXTarget(){
    this("");
  }

  public JavaFXTarget(String title){
    this(title, 320, 240);
  }

  public JavaFXTarget(String title, int width, int height){
    WIDTH = width;
    HEIGHT = height;
    this.title = title;
    counter = new AtomicInteger();
    canvas = new JavaFXCanvasTarget(width, height);
    image = new WritableImage(width, height);
    root = new Group();
    scene = new Scene(root, width, height);//, Color.BLACK);
    root.getChildren().add(canvas);
    buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    graphics = buffer.getGraphics();
    graphics.setColor(Color.BLACK);
  }

  public synchronized void clear(){
    canvas.clear();
  }

  public synchronized void draw(final ShapeManager sm){
  /* What is happened, when ``synchronized'' is not used */
    sm.draw(this);
  }

  public synchronized void draw(final Target t, final ShapeManager sm){
    t.drawCore(sm);
  }

  public void drawCore(final ShapeManager sm){
    canvas.draw(sm);
  }

  public BufferedImage getBufferedImage(){
    return canvas.getBufferedImage();
  }

  public synchronized BufferedImage copyBufferedImage(){
    return canvas.copyBufferedImage();
  }

  @Override
  public synchronized BufferedImage copyBufferedImage3Channel(){
    return canvas.copyBufferedImage3Channel();
  }

  public void init(){
    Platform.runLater(new Runnable(){
        public void run(){
          counter.incrementAndGet();
          stage = new Stage();
          stage.setTitle(title);
          stage.setOnCloseRequest(ev->{//ev.consume();
              if(counter.decrementAndGet()==0){
                System.exit(0);
              }
            });
          stage.setScene(scene);
          scene.setOnKeyPressed(ev->{
              if(ev.getCode()== KeyCode.ESCAPE){
                System.exit(0);
              };
            });
          stage.show();
        }
      });
  }

  public void addKeyPressHandler(EventHandler<? super KeyEvent> hander){
    scene.addEventHandler(KeyEvent.KEY_PRESSED, hander);
  }

  public void addKeyReleasedHandler(EventHandler<? super KeyEvent> hander){
    scene.addEventHandler(KeyEvent.KEY_RELEASED, hander);
  }

  public void finish(){
  }

  public void flush(){
    canvas.flush();
  }

  /**
   *  円を描画する
   */
  @Override
  public void drawCircle(int id,int x, int y, int r, Attribute attr){
    canvas.drawCircle(id, x, y, r, attr);
  }
  /**
   *  矩形を描画する
   */
  @Override
  public void drawRectangle(int id,int x,int y,int w,int h,
      Attribute attr){
    canvas.drawRectangle(id, x, y, w, h, attr);
  }

  /**
   *  画像図形を描画する
   */
  @Override
  public void drawImage(int id, int x, int y, BufferedImage img,
                        Attribute attr){
    canvas.drawImage(id, x, y, img, attr);
  }

  /**
   *  三角形を描画する
   */
  @Override
  public void drawTriangle(int id, int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, Attribute attr){
    canvas.drawTriangle(id, a_x, a_y, b_x, b_y, c_x, c_y, attr);
  }

}
