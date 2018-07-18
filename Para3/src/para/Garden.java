/*
1613354
星野シンジ
*/
package para;

import para.graphic.shape.*;

/**
 * 庭のものを登録、削除するメソッド集
 */
public class Garden{
  private static Attribute body = new Attribute(200,150,150,true);
  private static Attribute nose = new Attribute(250, 80, 80,true);
  private static Attribute eye  = new Attribute( 10, 10, 10,true);

  private static Attribute blackout = new Attribute(0, 0, 0, true);

  /**
   * 一匹のモグラを登録します。
   * @param delegateid モグラを構成する図形の先頭のid
   * @param x x座標
   * @param y y座標
   * @param level 出ている程度
   * @param sm このShapeManagerにモグラ図形が登録される
   */
  public static void setMole(int delegateid, int x, int y,
                               int level, ShapeManager sm){
    if(level<15){
      sm.put(new Circle(delegateid, x, y, level, body));
      sm.put(new Circle(delegateid+1, x, y, 1, body));
      sm.put(new Circle(delegateid+2, x, y, 1, body));
      sm.put(new Circle(delegateid+3, x, y, 1, body));
      sm.put(new Circle(delegateid+4, x, y, 1, body));
      sm.put(new Circle(delegateid+5, x, y, 1, body));
      sm.put(new Circle(delegateid+6, x, y, 1, body));
      sm.put(new Circle(delegateid+7, x, y, 1, body));
    }else if(level<20){
      sm.put(new Circle(delegateid, x, y, level, body));
      sm.put(new Circle(delegateid+1, x, y+level-10, level/4, nose));
      sm.put(new Circle(delegateid+2, x+6+level/10, y+2, level/9+1, eye));
      sm.put(new Circle(delegateid+3, x-6-level/10, y+2, level/9+1, eye));
      sm.put(new Circle(delegateid+4, x, y, 1, body));
      sm.put(new Circle(delegateid+5, x, y, 1, body));
      sm.put(new Circle(delegateid+6, x, y, 1, body));
      sm.put(new Circle(delegateid+7, x, y, 1, body));
    }else if(level<25){
      sm.put(new Circle(delegateid, x, y, level, body));
      sm.put(new Circle(delegateid+1, x, y+level-10, level/4, nose));
      sm.put(new Circle(delegateid+2, x+9, y+2, 3, eye));
      sm.put(new Circle(delegateid+3, x-9, y+2, 3, eye));
      sm.put(new Rectangle(delegateid+4, x-12, y+10, 2+level/12, 2, eye));
      sm.put(new Rectangle(delegateid+5, x-12, y+14, 2+level/12, 2, eye));
      sm.put(new Rectangle(delegateid+6, x+9, y+10, 2+level/12, 2, eye));
      sm.put(new Rectangle(delegateid+7, x+9, y+14, 2+level/12, 2, eye));
    }else{
      sm.put(new Circle(delegateid, x, y, 30, body));
      sm.put(new Circle(delegateid+1,   x, y+15, 6, nose));
      sm.put(new Circle(delegateid+2, x+10, y+4, 3, eye));
      sm.put(new Circle(delegateid+3, x-10, y+4, 3, eye));
      sm.put(new Rectangle(delegateid+4, x-15, y+12, 6, 2, eye));
      sm.put(new Rectangle(delegateid+5, x-15, y+16, 6, 2, eye));
      sm.put(new Rectangle(delegateid+6, x+10, y+12, 6, 2, eye));
      sm.put(new Rectangle(delegateid+7, x+10, y+16, 6, 2, eye));
    }
  }

  /**
   * 一匹のモグラを削除します。
   * @param delegateid モグラを構成する図形の先頭のid
   * @param sm このShapeManagerからモグラ図形が削除される
   */
  public static void removeMole(int delegateid, ShapeManager sm){
    for(int i=0;i<8;i++){
      sm.remove(delegateid+i);
    }
  }

  /**
   * 一匹の黒いモグラを登録します。
   * @param delegateid モグラを構成する図形の先頭のid
   * @param x x座標
   * @param y y座標
   * @param level 出ている程度
   * @param sm このShapeManagerにモグラが登録される
   */
  public static void blackMole(int delegateid, int x, int y, int level, ShapeManager sm){
    sm.put(new Circle(delegateid, x, y, level, blackout));
    sm.put(new Circle(delegateid+1, x, y, 1, blackout));
    sm.put(new Circle(delegateid+2, x, y, 1, blackout));
    sm.put(new Circle(delegateid+3, x, y, 1, blackout));
    sm.put(new Circle(delegateid+4, x, y, 1, blackout));
    sm.put(new Circle(delegateid+5, x, y, 1, blackout));
    sm.put(new Circle(delegateid+6, x, y, 1, blackout));
    sm.put(new Circle(delegateid+7, x, y, 1, blackout));
  }
}
