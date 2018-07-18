package para.calc;

import javafx.scene.control.*;
import javafx.application.Platform;

public class Executor2 extends ExecutorBase implements Executor{
  Label label;
  public Executor2(Label label){
    super();
    this.label = label;
  }

  public void writeState(String state){
    System.err.println(Thread.currentThread().getName());//hint
    System.out.println(state);
    Platform.runLater(()->{
        label.setText(state);
        System.err.println(Thread.currentThread().getName() + " LATER");//hint
      });
  }

  synchronized public String operation(String data){
    init(data);
    result = null;
    boolean isSuccess=true;
    while(isSuccess && s.hasNext()){
      isSuccess = onestep();
    }
    return result;
  }
}
