package para;
import para.graphic.shape.Attribute;
import para.graphic.shape.Circle;
import para.graphic.shape.Image;
import para.graphic.shape.Shape;
import para.graphic.shape.ShapeManager;
import para.graphic.target.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;

/** 画像ファイルを画像図形に利用して表示するデモ */
public class Main03{
  /** 画像ファイル名 */
  private final static String IMAGEFILENAME = "sweet.png";
  /** メインメソッド
   * @param args このプログラムでは値は使用されません
   */
  public static void main(String[] args){
    ShapeManager sm = new ShapeManager();
    Target target;
    target = new JavaFXTarget("DisplayShapes", 300, 300);
//    target = new TextTarget(System.out);
    target.init();
    File imagefile=null;
    BufferedImage bimage = null;
    try {
      imagefile = new File(IMAGEFILENAME);
      bimage = ImageIO.read(imagefile);
    }catch(IOException e){
      System.err.println(e);
    }

    while(true){
      target.clear();
      for(int i=0;i<256;i=i+50){
        for(int ii=0;ii<256;ii=ii+50){
          Shape s = new Image(i*256+ii,ii,i,bimage,
                              new Attribute(ii, i, 255-i, true));
          sm.add(s);
          target.draw(sm);
          target.flush();
          try{
            Thread.sleep(200);
          }catch(InterruptedException e){
          }
        }
      }
      sm.clear();
    }
  }
}
