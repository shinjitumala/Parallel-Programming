package para;

import java.util.*;

import para.graphic.parser.MainParser;
import para.graphic.shape.ShapeManager;
import para.graphic.target.Target;
import para.graphic.target.JavaFXTarget;

/** 円と画像図形を表示するデモ */
public class Main02{
  /** メインメソッド
   * @param args このプログラムでは値は使用されません
   */
  public static void main(String[] args){
    String data =
      "reset\n"+
      "target clear\n"+
      "shape 10 Triangle 20 20 40 40 20 40 Attribute Color 100 40 60 Fill true\n"+
      "shape 16 Triangle 70 70 90 90 70 90 Attribute Color 100 100 200 Fill false\n"+
      "shape 11 Circle 300 40 30 Attribute Color 100 70 30 Fill false\n"+
      "shape 12 Circle 80 260 20 Attribute Color 100 120 10 Fill false\n"+
      "shape 13 Circle 300 260 40 Attribute Color 100 140 60 Fill true\n"+
      "shape 14 Circle 190 150 60 Attribute Color 200 200 100 Fill true\n"+
      "shape 15 Image 180 140\n"+
"iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAAXNSR0IArs4c6QAAAAZiS0dEAP8A\n"+
"/wD/oL2nkwAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9wDFwAUI6+PxSUAAAAZdEVYdENv\n"+
"bW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAACmklEQVQ4y62TwUuTYRzHP3vfvZu5uZlzbKPR\n"+
"wF6pKaSmIAReIj126VAdI7To6rGD9ge4zgphRIsuQRB0qIshhKAe9CLNZ6a5ubWc2/IdbO+7V7uo\n"+
"KGxzQQ88l+f35fN8f98fP1YjQ5OrkaHD1cjQJP/hWFYjQ4cAyHaU8N2XSsdwDzBQyOeJr68fGIZR\n"+
"9vn9o7dHRqKNACVgBrsbW/d9lI7hR8DAcfFSMCi53O4L2Wz2lRDicUMO1z696JXcobey73q4mqCQ\n"+
"zwPgbm1NAc9VVZ2uCxRCjAIzDocDh8NBJpOpKvw2P4+nvf3P1XD4lqqqy/VaHgPweDwYhlHz5+bm\n"+
"ZvK5XEtub+/ZeRkONDU1oSgKuVyuprC3vx9FUSzZbPZmPaAVwOv1AhAKhdja2qqao1GpIEkSsiS1\n"+
"CSGOW14Gpk9HYBFCHMqyjNfrRZZl0uk0pmlWBVYMA38gcLq0D6SBqeNhWcul0oqmaT1OpxNZlqu2\n"+
"4W5trdVhy9GdEEKgquq0pOv6G8MwSCaTWK3WM1BN0xpdkMARtF9qcbmi/kBgJh6Lsby4iK7rJ6pY\n"+
"LPYvWxcAxi0AQogAMLm5sTG2u7uLz+c7UV30eOjq6sI0TZLJ5HnQfQlAVdUUMN7m8Tw9ME0jtbND\n"+
"oVAAIB6LUS6XURSlEZctlmqvr2dnbwB3rnR2TthsNq6Fw9jtdtLp9JlIqq5eveK7aLTkcDrt4e5u\n"+
"gsEguq7XXM2jsyTVqw4MDj60Wq0Ui0UymQw2m62uO2Pjy4p0TiYfHU7n5x/xOL8zGRKJRE1hZXMO\n"+
"Y+39vbpAVVU1l8s16nK7vxY1jV+pVHVYYmFX//4BzLLzPIf09vX9vBwKPTiEqe3t7ZKmaaRTqePh\n"+
"LAFjZnLhCeXCHDD3F+KMCptkiZxiAAAAAElFTkSuQmCC &\n"+
      "Attribute Color 255 69 200 Fill false\n"+
      "target draw\n"+
      "target flush\n";

    ShapeManager sm = new ShapeManager();
    Target target;
    target = new JavaFXTarget("DisplayShapes",370,300);
    //target = new TextTarget(System.out);
    target.init();
    MainParser mp = new MainParser(target,sm);
    Scanner s;
    while(true){
      s = new Scanner(data);
      mp.parse(s);
    }
  }
}
