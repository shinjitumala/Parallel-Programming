/*
学籍番号 : 1613354
氏名 : 星野シンジ
*/
package para.graphic.shape;
import para.graphic.target.Target;

/**
 * 三角形
 */
public class Triangle extends Shape{
  /** 頂点の座標 */
  private int a_x, a_y, b_x, b_y, c_x, c_y;
  /** 属性 */
  private Attribute attr;

  /** 三角形を生成する
   * @param id 識別子
   * @param a_x 三角形の頂点の座標
   * @param a_y 三角形の頂点の座標
   * @param b_x 三角形の頂点の座標
   * @param b_y 三角形の頂点の座標
   * @param c_x 三角形の頂点の座標
   * @param c_y 三角形の頂点の座標
   */
  public Triangle(int id, int a_x, int a_y, int b_x, int b_y, int c_x, int c_y){
    this(id, a_x, a_y, b_x, b_y, c_x, c_y, null);
  }

  /** 三角形を生成する
   * @param id 識別子
   * @param a_x 三角形の頂点の座標
   * @param a_y 三角形の頂点の座標
   * @param b_x 三角形の頂点の座標
   * @param b_y 三角形の頂点の座標
   * @param c_x 三角形の頂点の座標
   * @param c_y 三角形の頂点の座標
   * @param attr 属性
   */
  public Triangle(int id, int a_x, int a_y, int b_x, int b_y, int c_x, int c_y, Attribute attr){
    super(id, a_x, a_y, b_x, b_y);
    this.a_x = a_x;
    this.b_x = b_x;
    this.c_x = c_x;
    this.a_y = a_y;
    this.b_y = b_y;
    this.c_y = c_y;
    this.attr = attr;
  }

  /** 属性を取得する */
  @Override
  public Attribute getAttribute(){
    return attr;
  }

  /** この三角形を出力する
   * @param target 出力先
   */
  @Override
  public void draw(Target target){
    target.drawTriangle(id, a_x, a_y, b_x, b_y, c_x, c_y, attr);
  }
}
