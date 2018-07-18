package para;
import para.graphic.shape.Attribute;
import para.graphic.shape.Circle;
import para.graphic.shape.Shape;
import para.graphic.shape.ShapeManager;
import para.graphic.target.*;

/** 点滅する円を描画するデモ */
public class Main00{
  /** メインメソッド
   * @param args このプログラムでは値は使用されません
   */
  public static void main(String[] args){
    ShapeManager sm = new ShapeManager();
    Target target;
    target = new JavaFXTarget("DisplayShapes");
    //target = new TextTarget(System.out);
    target.init();
    while(true){
      for(int i=0;i<256;i=i+50){
        Attribute attr = new Attribute(i, i, 255-i, true);
        Shape s = new Circle(i,100,100,256-i,attr);
        sm.add(s);
        target.draw(sm);
        target.flush();
        try{
          Thread.sleep(100);
        }catch(InterruptedException e){
        }
      }
      target.clear();
      sm.clear();
    }
  }
}
