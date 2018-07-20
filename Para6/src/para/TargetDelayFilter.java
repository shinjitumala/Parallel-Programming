package para;
import para.graphic.target.*;
import para.graphic.shape.Camera;
import java.nio.*;
import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;
import static com.jogamp.opencl.CLMemory.Mem.*;
import easycl.CLSetup;
import easycl.CLSetupCreator;

public class TargetDelayFilter extends TargetFilter{
  private final String kernelsource;
  private final String kernelname;
  private final CLSetup cl;
  protected CLBuffer<ByteBuffer> cllatest, clold, output;
  protected final CLCommandQueue queue;
  protected final CLProgram program;
  protected final CLKernel kernel;
  private boolean isFirst=true;
  protected volatile float parameter;

  /*
  public TargetDelayFilter(Target target){
    this(target, "donothing.cl", "DoNothing");
  }
  */

  public TargetDelayFilter(Target target, String kernelsource,
                              String kernelname){
    super(target);
    cl = CLSetupCreator.createCLSetup();
    cl.initContext();
    queue = cl.createQueue();
    program = cl.createProgramFromResource(this, kernelsource);
    kernel = program.createCLKernel(kernelname);
    this.kernelsource = kernelsource;
    this.kernelname = kernelname;
    parameter = 0.2f;
  }
  
  protected ByteBuffer process(ByteBuffer oneshot){
    if(isFirst){
      cllatest = cl.createByteBuffer(Camera.WIDTH*Camera.HEIGHT*3, READ_ONLY);
      clold = cl.createByteBuffer(Camera.WIDTH*Camera.HEIGHT*3, READ_WRITE);
      output = cl.createByteBuffer(Camera.WIDTH*Camera.HEIGHT*4, WRITE_ONLY);
      oneshot.rewind();
      cllatest = cllatest.cloneWith(oneshot);
      oneshot.rewind();
      clold = clold.cloneWith(oneshot);
      cllatest.getBuffer().rewind();
      clold.getBuffer().rewind();
      queue.putWriteBuffer(cllatest, false)//cllatestのデータをカーネルプログラムへ
           .putWriteBuffer(clold, false);  //cloldのデータをカーネルプログラムへ
      kernel.setArg(0,Camera.WIDTH);
      kernel.setArg(1,Camera.HEIGHT);
      isFirst=false;
    }else{
      oneshot.rewind();
      cllatest = cllatest.cloneWith(oneshot);
      queue.putWriteBuffer(cllatest,false);//cllatestのデータをカーネルプログラムへ
    }
    kernel.setArg(2, cllatest);       
    kernel.setArg(3, clold);
    kernel.setArg(4, output);
    kernel.setArg(5, parameter);
    queue.putBarrier()
         .put2DRangeKernel(kernel, 0, 0, Camera.WIDTH, Camera.HEIGHT,0,0)//演算
         .putBarrier()
         .putReadBuffer(output, true);//outputへデータコピー、終了まで待つ
    ByteBuffer buffer = output.getBuffer();
    buffer.rewind();
    return buffer;
  }

  public void setParameter(float p){
    parameter = p;
  }
}
