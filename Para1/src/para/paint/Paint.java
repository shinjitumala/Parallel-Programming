/**
 * 16B13354 星野シンジ
 */

package para.paint;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.beans.value.ObservableValue;

import javafx.scene.image.Image;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Affine;

/**
 * JavaFX お絵描きアプリケーションのメインクラス
 */
public class Paint extends Application{
  Canvas canvas;
  Canvas canvas2;
  GraphicsContext gc;
  GraphicsContext gc2;
  double oldx;
  double oldy;
  final int SIZE=600;
  Button clear;
  double line_color[] = {0, 0, 1};
  double line_property[] = {1, 5};
  int mouse_status;

  /**
   * お絵描きプログラムの準備をして、ウィンドウを開きます
   */
  public void start(Stage stage){
    Group group = new Group();
    canvas = new Canvas(SIZE,SIZE);
    canvas2 = new Canvas(SIZE / 20, SIZE / 20);
    gc = canvas.getGraphicsContext2D();
    gc2 = canvas2.getGraphicsContext2D();
    drawShapes(gc);
    canvas.setOnMousePressed(ev->{
        oldx = ev.getX();
        oldy = ev.getY();
        switch(mouse_status){
          case 1: // 円のスタンプ
            StampCircle(ev.getX(), ev.getY());
            break;
          case 2: // 四角のスタンプ
            StampSquare(ev.getX(), ev.getY());
            break;
          case 3: // バッテンのスタンプ
            StampX(ev.getX(), ev.getY());
            break;
          case 4: // 円のスタンプ（中身は白）
            StampCircle2(ev.getX(), ev.getY());
            break;
          default: // 何もしない
            break;
        }
      });


    clear = new Button("clear");
    clear.setOnAction(e->{
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,SIZE,SIZE);});

    BorderPane bp = new BorderPane();
    HBox clear_pane = new HBox();
    VBox vb = new VBox();
    Slider sliderr = new Slider(0, 1, 0);
    Slider sliderg = new Slider(0, 1, 0);
    Slider sliderb = new Slider(0, 1, 1);
    Slider slidero = new Slider(0, 1, 1);
    Slider sliderw = new Slider(0, 1, 0.5);

    // 回転機能
    HBox utility_box = new HBox();
    Button rotate_left = new Button("Rotate L");
    rotate_left.setOnAction(e->{
      RotateCanvas(-90);
    });
    Button rotate_right = new Button("Rotate R");
    rotate_right.setOnAction(e->{
      RotateCanvas(90);
    });

    //　スタンプ機能
    HBox utility_box2 = new HBox();
    Button b_line = new Button("line");
    b_line.setOnAction(e->{
      mouse_status = 0;
    });
    Button b_circle = new Button("Circle");
    b_circle.setOnAction(e->{
      mouse_status = 1;
    });
    Button b_square = new Button("Square");
    b_square.setOnAction(e->{
      mouse_status = 2;
    });
    Button b_X = new Button("X");
    b_X.setOnAction(e->{
      mouse_status = 3;
    });
    Button b_circle2 = new Button("Circle 2");
    b_circle2.setOnAction(e->{
      mouse_status = 4;
    });

    // 線の色と線の色のプレビューの初期化
    UpdateLineProperties();

    // イベントハンドラ設定
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                           new EventHandler<MouseEvent>(){
                             public void handle(MouseEvent ev){
                               switch (mouse_status){
                                 case 0: // 線を描く
                                   gc.strokeLine(oldx, oldy, ev.getX(), ev.getY());
                                   oldx = ev.getX();
                                   oldy = ev.getY();
                                   break;
                                 default: //　何もしない
                                   break;
                               }

                             }
                           });
    sliderr.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldv, Number nv)->{
      line_color[0] = (double) nv;
      UpdateLineProperties();
    });
    sliderg.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldv, Number nv)->{
      line_color[1] = (double) nv;
      UpdateLineProperties();
    });
    sliderb.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldv, Number nv)->{
      line_color[2] = (double) nv;
      UpdateLineProperties();
    });
    slidero.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldv, Number nv)->{
      line_property[0] = (double) nv;
      UpdateLineProperties();
    });
    sliderw.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldv, Number nv)->{
      line_property[1] = 10 * (double) nv;
      UpdateLineProperties();
    });

    // GUIの設定
    vb.setAlignment(Pos.CENTER);
    vb.getChildren().add(sliderr);
    vb.getChildren().add(sliderg);
    vb.getChildren().add(sliderb);

    vb.getChildren().add(slidero);
    vb.getChildren().add(sliderw);

    clear_pane.setAlignment(Pos.CENTER);
    clear_pane.getChildren().add(clear);
    clear_pane.getChildren().add(canvas2);

    utility_box.setAlignment(Pos.CENTER);
    utility_box.getChildren().add(rotate_left);
    utility_box.getChildren().add(rotate_right);

    utility_box2.setAlignment(Pos.CENTER);
    utility_box2.getChildren().add(b_line);
    utility_box2.getChildren().add(b_circle);
    utility_box2.getChildren().add(b_circle2);
    utility_box2.getChildren().add(b_square);
    utility_box2.getChildren().add(b_X);

    vb.getChildren().add(clear_pane);
    vb.getChildren().add(utility_box);
    vb.getChildren().add(utility_box2);
    bp.setTop(vb);
    bp.setCenter(canvas);
    Scene scene = new Scene(bp);
    stage.setScene(scene);
    stage.setTitle("JavaFX Draw");
    stage.show();
  }

  /**
   * 初期化メソッド、startメソッドの呼び出され方とは異なる呼び出され方をする。必要ならば定義する
   */
  public void init(){
    mouse_status = 0;
  }

  /**
   * 図形を描きます。
   * 図形描画の実装サンプルです
   */
  private void drawShapes(GraphicsContext gc) {
    gc.setFill(Color.WHITE);
    gc.fillRect(0,0,SIZE,SIZE);
    gc.setFill(Color.GREEN);
    gc.setStroke(Color.BLUE);
    gc.setLineWidth(4);
    gc.strokeLine(40, 10, 10, 40);
    gc.fillOval(60, 10, 30, 30);
    gc.strokeOval(110, 10, 30, 30);
    gc.fillRoundRect(160, 10, 30, 30, 10, 10);
  }

  /**
   * 線の色及び色のプレビューボックスの更新を行う
   */
  private void UpdateLineProperties(){
    gc.setStroke(Color.color(line_color[0], line_color[1], line_color[2], line_property[0]));
    gc2.setFill(Color.color(line_color[0], line_color[1], line_color[2], line_property[0]));
    gc2.fillRect(0,0,SIZE / 20,SIZE / 20);
    gc.setLineWidth(line_property[1]);
  }

  /**
   * キャンバスをi度回転させる
   */
  private void RotateCanvas(int i){
    Image snapshot = canvas.snapshot(null, null);
    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, SIZE, SIZE);

    gc.save();
    Affine rotate = new Affine();
    rotate.appendRotation(i, SIZE / 2, SIZE / 2);
    gc.setTransform(rotate);
    gc.drawImage(snapshot, 0, 0);
    gc.restore();
  }

  /**
   * 与えられた座標を中心に丸を描く
   */
  private void StampCircle(double x, double y){
    gc.setFill(Color.color(line_color[0], line_color[1], line_color[2], line_property[0]));
    gc.fillOval(x - line_property[1] * 10 / 2, y - line_property[1] * 10 / 2, line_property[1] * 10, line_property[1] * 10);
  }

  /**
   * 与えられた座標を中心に四角を描く
   */
  private void StampSquare(double x, double y){
    gc.setFill(Color.color(line_color[0], line_color[1], line_color[2], line_property[0]));
    gc.fillRect(x - line_property[1] * 10 / 2, y - line_property[1] * 10 / 2, line_property[1] * 10, line_property[1] * 10);
  }

  /**
   * 与えられた座標にバッテンを描く
   */
  private void StampX(double x, double y){
    gc.strokeLine(x - line_property[1] * 10 / 2, y - line_property[1] * 10 / 2, x + line_property[1] * 10 / 2, y + line_property[1] * 10 / 2);
    gc.strokeLine(x - line_property[1] * 10 / 2, y + line_property[1] * 10 / 2, x + line_property[1] * 10 / 2, y - line_property[1] * 10 / 2);
  }

  /**
   * 与えられた座標に円を描く
   */
  private void StampCircle2(double x, double y){
   gc.strokeOval(x - line_property[1] * 10 / 2, y - line_property[1] * 10 / 2, line_property[1] * 10, line_property[1] * 10);
  }
}
