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

public class TargetMosaicFilter extends TargetFilter{
  private final int BLOCKSIZE = 8*8;
  private final String kernelsource;
  private final String kernelname;
  private final CLSetup cl;
  protected CLBuffer<ByteBuffer> input,output;
  protected final CLCommandQueue queue;
  protected final CLProgram program;
  protected final CLKernel kernel;
  private boolean isFirst=true;

  public TargetMosaicFilter(Target target, String kernelsource,
                              String kernelname){
    super(target);
    cl = CLSetupCreator.createCLSetup();
    cl.initContext();
    queue = cl.createQueue();
    program = cl.createProgramFromResource(this, kernelsource);
    kernel = program.createCLKernel(kernelname);
    this.kernelsource = kernelsource;
    this.kernelname = kernelname;
  }
  
  protected ByteBuffer process(ByteBuffer oneshot){
    if(isFirst){
      input = cl.createByteBuffer(Camera.WIDTH*Camera.HEIGHT*3, READ_ONLY);
      output = cl.createByteBuffer(Camera.WIDTH*Camera.HEIGHT*4, WRITE_ONLY);
      kernel.setArg(0, Camera.WIDTH);
      kernel.setArg(1, Camera.HEIGHT);
      kernel.setArg(2, input);//コンスタントメモリ
      kernel.setNullArg(3, BLOCKSIZE*3*4);//ローカルメモリ, 色3Channel, int4byte
      kernel.setArg(4, output);//グローバルメモリ
      kernel.setArg(5, 0);//使っていないパラメータ
      isFirst=false;
    }
    oneshot.rewind();
    input = input.cloneWith(oneshot);
    queue.putWriteBuffer(input, false);//inputのデータをカーネルプログラムへ 
    queue.putBarrier()
         .put2DRangeKernel(kernel, 0, 0,Camera.WIDTH/2,Camera.HEIGHT/2,4,4)//演算
         .putBarrier()
         .putReadBuffer(output, true);//outputへデータコピー、ホストプログラムは待つ
    ByteBuffer buffer = output.getBuffer();
    buffer.rewind();
    return buffer;
  }

}
