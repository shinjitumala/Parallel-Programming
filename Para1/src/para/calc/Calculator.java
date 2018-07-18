/**
 * 16B13354 星野シンジ
 */

package para.calc;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.Pos;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * JavaFX 電卓アプリケーションのメインクラス
 */
public class Calculator extends Application{
  Label input;
  Label output;
  StringBuilder buff;
  Executor ex;
  public Calculator(){
    input = new Label();
    output = new Label();
    buff = new StringBuilder();
    ex = new Executor1();
  }
  String[] buttonname = {"9","8","7","+",
                         "6","5","4","-",
                         "3","2","1","*",
                         "0",".",",","/"};

  public void start(Stage stage){
    VBox root = new VBox();
    root.setAlignment(Pos.TOP_CENTER);
    GridPane grid = new GridPane();
    Scene scene = new Scene(root, 200,300);
    Button[] buttons = new Button[16];

    // ＝ボタンの初期化
    Button buttoncal = new Button("=");
    buttoncal.setPrefHeight(56);
    buttoncal.setOnAction(e->{
      CallCalculator(buff.toString());
    });

    // <ボタンの初期化
    Button buttondel = new Button("<");
    buttondel.setPrefHeight(56);
    buttondel.setOnAction(e->{
      DeleteLabelInput();
    });
    StackPane stack = new StackPane();
    stack.getChildren().add(new Rectangle(140,30,Color.WHITE));
    stack.getChildren().add(input);
    root.getChildren().addAll(stack, output);

    //　ボタンのイベントハンドラを設定する
    EventHandler<ActionEvent> ButtonPress = new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e){
        Button tb = (Button) e.getSource();
        AppendLabelInput(tb.getText());
      }
    };
    for(int i=0;i<16;i++){
      buttons[i] = new Button(buttonname[i]);
    }

    // ボタン配置
    grid.setAlignment(Pos.CENTER);
    grid.addRow(1, buttons[0], buttons[1], buttons[2], buttons[3]);
    grid.addRow(2, buttons[4], buttons[5], buttons[6], buttons[7]);
    grid.addRow(3, buttons[8], buttons[9], buttons[10], buttons[11]);
    grid.addRow(4, buttons[12], buttons[13], buttons[14], buttons[15]);
    grid.add(buttondel, 4, 1, 1, 2);
    grid.add(buttoncal, 4, 3, 1, 2);

    // ボタンの大きさを設定する
    for(int i = 0; i < 16; i++){
      buttons[i].setPrefHeight(28);
      buttons[i].setPrefWidth(28);
      buttons[i].setOnAction(ButtonPress);
    }
    root.getChildren().add(grid);


    stage.setWidth(200);
    stage.setHeight(200);
    stage.setScene(scene);
    stage.setTitle("JavaFX Calc");
    stage.show();
  }

  /**
   * Label input に文字列を追加する
   */
  private void AppendLabelInput(String s){
    buff.append(s);
    input.setText(buff.toString());
  }

  /**
   * Label input から一文字削除する
   */
  private void DeleteLabelInput(){
    if(buff.length() ==  0) return; // もともと文字がなかった時に、Exceptionが飛ばないための処理
    buff.deleteCharAt(buff.length() - 1);
    input.setText(buff.toString());
  }

  /**
   * ExecutorクラスのOperationメソッドに引数で与えられたStringを送り、結果をLabel outputに返す。
   */
  private void CallCalculator(String s){
    String result = ex.operation(s);
    output.setText(result);
  }
}
