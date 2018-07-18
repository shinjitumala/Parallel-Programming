/*
1613354
星野シンジ
*/

package para.graphic.shape;
import java.util.*;
import java.util.AbstractQueue;
import para.graphic.target.Target;


/** 描画順序をidの順とする図形集合
 */
public class OrderedShapeManager extends ShapeManager{
  public OrderedShapeManager(){
    super(new TreeSet<Shape>(new ShapeComparator()));
  }
}

/**　Shapeオブジェクト同士で識別子の大小を比較するComparator
　*/
class ShapeComparator implements Comparator<Shape>{
  public int compare(Shape shape1, Shape shape2){
    return shape1.getID() - shape2.getID();
  }
}
