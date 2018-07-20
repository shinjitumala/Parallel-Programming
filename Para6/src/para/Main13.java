package para;
import para.graphic.shape.*;
import para.graphic.target.*;
import para.graphic.opencl.*;

/** カメラ画像をOpenCLで加工して表示する例
 * カラー画像をモザイク化する
 */
public class Main13{
  public Main13(){
  }

  public void start(){
    Target target = new TargetMosaicFilter(
                      new JavaFXTarget("MosaicFilter",360,280),
                      "mosaic.cl", "Mosaic");
    ShapeManager sm = new OrderedShapeManager();
    sm.add(new Camera(20,20,20));
    target.init();
    target.clear();
    target.flush();
    while(true){
      target.clear();
      target.draw(sm);
      target.flush();
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
    Main13 main= new Main13();
    main.start();
  }
}
