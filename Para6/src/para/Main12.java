package para;
import para.graphic.shape.*;
import para.graphic.target.*;
import para.graphic.opencl.*;

/** カメラ画像をOpenCLで加工して表示するプログラム。
 * カラー画像を白黒画像にする<br>
 * 黄色い丸も一緒に表示<br>
 */
public class Main12{
  public Main12(){
  }

  public void start(){
    Target target = new TargetImageFilter(            
                 new JavaFXTarget("GrayscaleFilter",320,240),this,
                                  "gray.cl", "Gray");
    ShapeManager sm = new OrderedShapeManager();
    sm.add(new Camera(20,0,0));
    target.init();
    target.clear();
    target.flush();
    int i=0;
    while(true){
      sm.put(new Circle(30, i, 200, 10, new Attribute(255,255,0,true)));
      target.clear();
      target.draw(sm);
      target.flush();
      i = (i+3)%320;
      try{
        Thread.sleep(10);
      }catch(InterruptedException e){
      }
    }
  }
  
  /** メインメソッド
   * @param args このプログラムでは値は使用されません
   */
  public static void main(String[] args){
    Main12 main= new Main12();
    main.start();
  }
}
