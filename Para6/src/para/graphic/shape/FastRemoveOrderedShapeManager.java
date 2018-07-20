package para.graphic.shape;
import java.util.*;

/** 描画順序をidの順とする図形集合
 */
public class FastRemoveOrderedShapeManager extends ShapeManager{
  final HashMap<Integer, Shape> subdata;
  /** 図形の集合を生成する（空集合）
   */
  public FastRemoveOrderedShapeManager(){
    super(new TreeSet<Shape>(new ShapeComparator()));
    subdata = new HashMap<Integer, Shape>();
  }
    /** 図形の集合を空集合にする
   */
  public synchronized void clear(){
    data.clear();
    subdata.clear();
  }
  /** 図形の集合に図形を追加する
   *  @param in 追加する図形
   */
  public synchronized void add(Shape in){
    data.add(in);
    subdata.put(in.id, in);
  }

  /** 図形の集合中の図形を置き換える
   *  @param in 置き換え先の図形
   *  @return もともと図形がリスト中にあったら true, なければ false
   */
  public synchronized boolean replace(Shape in){
    Shape stored = subdata.get(in.id);
    if(stored !=null && data.remove(stored)){
      data.add(in);
      subdata.put(in.id, in);
      return true;
    }
    return false;
  }

  /** 図形の集合の複製をつくる
   */
  public synchronized FastRemoveOrderedShapeManager duplicate(){
    FastRemoveOrderedShapeManager ret = new FastRemoveOrderedShapeManager();
    for(Shape s:data){
      ret.add(s);
    }
    return ret;
  }

  /** 図形の集合から図形を削除する
   *  @param id 削除する図形の識別子
   *  @return もともと図形がリスト中にあったら true, なければ false
   */
  public synchronized boolean remove(int id){
    Shape stored = subdata.get(id);
    if(stored !=null && data.remove(stored)){
      return true;
    }
    return false;
  }

  /** 図形の集合から図形を削除する
   *  @param shape 削除する図形
   *  @return 図形のidと一致する要素がリスト中にあれば true, なければ false
   */
  public synchronized boolean remove(Shape shape){
    Shape stored = subdata.get(shape.id);
    if(stored !=null && data.remove(stored)){
      return true;
    }
    return false;
  }
}
