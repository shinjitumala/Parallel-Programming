/*
学籍番号 : 1613354
氏名 : 星野シンジ
*/
package para.graphic.parser;
import java.util.Scanner;

import para.graphic.shape.Attribute;
import para.graphic.shape.Triangle;

class TriangleParser implements ShapeParser{
  TriangleParser(){
  }
  @Override
  public Triangle parse(Scanner s, int id){
    int a_x = s.nextInt();
    int a_y = s.nextInt();
    int b_x = s.nextInt();
    int b_y = s.nextInt();
    int c_x = s.nextInt();
    int c_y = s.nextInt();
    Triangle ret;
    Attribute attr=null;
    if(s.hasNext("Attribute")){
      attr = AttributeParser.parse(s);
    }
    ret = new Triangle(id, a_x, a_y, b_x, b_y, c_x, c_y, attr);
    return ret;
  }

}
