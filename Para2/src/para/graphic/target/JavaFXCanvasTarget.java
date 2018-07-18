/*
学籍番号 : 1613354
氏名 : 星野シンジ
*/
package para.graphic.target;
import para.graphic.shape.Attribute;
import para.graphic.shape.ShapeManager;

import javafx.application.Platform;
import javafx.stage.Stage;
import java.lang.IllegalStateException;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
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

import javafx.scene.shape.Polygon;

/**
 *  JavaFXの Canvas を出力装置とするTargetの実装
 */
public class JavaFXCanvasTarget extends Canvas implements Target{
  final int WIDTH;
  final int HEIGHT;
  Canvas canvas;
  GraphicsContext gc;
  BufferedImage buffer;
  Graphics graphics;
  Group group;
  WritableImage image;

  public JavaFXCanvasTarget(){
    this(320, 240);
  }

  /**
   *  JavaFXCanvasTarget のインスタンスを作成
   *  @param width 　幅
   *  @param height　高さ
   */
  public JavaFXCanvasTarget(int width, int height){
    super(width, height);
    WIDTH = width;
    HEIGHT = height;
    image = new WritableImage(width, height);
    gc = getGraphicsContext2D();
    buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    graphics = buffer.getGraphics();
    graphics.setColor(Color.BLACK);
  }

  public synchronized void clear(){
    Color c = graphics.getColor();
    graphics.setColor(new Color(255,255,255,255));
    graphics.fillRect(0, 0, WIDTH, HEIGHT);
    graphics.setColor(c);
  }

  public synchronized void draw(final ShapeManager sm){
  /* What is happened, when ``synchronized'' is not used */
    predraw();
    sm.draw(this);
    postdraw();
  }

  public synchronized void draw(final Target t, final ShapeManager sm){
    predraw();
    t.drawCore(sm);
    postdraw();
  }

  public void drawCore(final ShapeManager sm){
    draw(sm);
  }

  private void predraw(){
    /*
    Paint p = gc.getFill();
    gc.setColor(Color.WHITE);
    gc.fillRect(0, 0, WIDTH, HEIGHT);
    gc.setFill(p);
    */
    /*
    Color c = graphics.getColor();
    graphics.setColor(new Color(255,255,255,255));
    graphics.fillRect(0, 0, WIDTH, HEIGHT);
    graphics.setColor(c);
    */
  }

  private void postdraw(){
  }

  public BufferedImage getBufferedImage(){
    return buffer;
  }

  public synchronized BufferedImage copyBufferedImage(){
    return new BufferedImage(buffer.getColorModel(),
                             (WritableRaster)buffer.getData(),
                             buffer.isAlphaPremultiplied(),null);
  }

  @Override
  public synchronized BufferedImage copyBufferedImage3Channel(){
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
                                            BufferedImage.TYPE_3BYTE_BGR);
    Graphics graphics = image.getGraphics();
    graphics.drawImage(this.buffer,0,0,null);
    return image;
  }

  public void init(){
  }

  public void addKeyPressHandler(EventHandler<? super KeyEvent> hander){
    setFocusTraversable(true);
    addEventHandler(KeyEvent.KEY_PRESSED, hander);
  }

  public void addKeyReleasedHandler(EventHandler<? super KeyEvent> hander){
    setFocusTraversable(true);
    addEventHandler(KeyEvent.KEY_RELEASED, hander);
  }

  public void finish(){
  }

  public void flush(){
    SwingFXUtils.toFXImage(buffer, image);
    Platform.runLater(()->{
        gc.drawImage(image,0,0);
      });
  }

  private void setColor(Attribute attr){
    if(attr!=null){
      int c[]=attr.getColor();
      graphics.setColor(new java.awt.Color(c[0],c[1],c[2]));
    }
  }

  /**
   *  円を描画する
   */
  @Override
  public void drawCircle(int id,int x, int y, int r, Attribute attr){
    setColor(attr);
    if(attr!=null && attr.getFill()){
      graphics.fillArc(x-r,y-r,r*2,r*2,0,360);
    }else{
      graphics.drawArc(x-r,y-r,r*2,r*2,0,360);
    }
  }
  /**
   *  矩形を描画する
   */
  @Override
  public void drawRectangle(int id, int x, int y, int w, int h,
                            Attribute attr){
    setColor(attr);
    if(attr!=null && attr.getFill()){
      graphics.fillRect(x, y, w, h);
    }else{
      graphics.drawRect(x, y, w, h);
    }
  }

  /**
   *  画像図形を描画する
   */
  @Override
  public void drawImage(int id, int x, int y, BufferedImage img,
                        Attribute attr){
    setColor(attr);
    if(attr != null){
      if(attr.getFill()){
        graphics.fillRect(x, y, img.getWidth(), img.getHeight());
        graphics.drawImage(img,x,y,null);
        graphics.drawRect(x, y, img.getWidth(), img.getHeight());
      }else{
        graphics.drawImage(img,x,y,null);
        graphics.drawRect(x, y, img.getWidth(), img.getHeight());
      }
    }else{
      graphics.drawImage(img,x,y,null);
    }
  }

  @Override
  public void drawTriangle(int id, int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, Attribute attr){

    setColor(attr);
    if(attr != null && attr.getFill()){
      int xPoints[] = {a_x, b_x, c_x};
      int yPoints[] = {a_y, b_y, c_y};
      graphics.fillPolygon(xPoints, yPoints, 3);
    }else{
      int xPoints[] = {a_x, b_x, c_x};
      int yPoints[] = {a_y, b_y, c_y};
      graphics.drawPolygon(xPoints, yPoints, 3);
    }
  }


}
