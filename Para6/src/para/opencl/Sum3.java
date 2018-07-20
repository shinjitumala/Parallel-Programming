package para.opencl;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import easycl.*;
import java.nio.*;
import java.io.IOException;
import java.io.*;
import java.util.*;

import static java.lang.System.*;
import static com.jogamp.opencl.CLMemory.Mem.*;

/** OpenCLを使用例を知るための簡単なデモ。
 * BufferAとBufferBとBufferCのそれぞれのi番目の要素を足してBufferDのi番目に格納する
 */
public class Sum3{
  /** OpenCLの初期化、データ初期化、演算、結果出力、OpenCL資源の開放をする
   */
  public Sum3(){
    //OpenCL演算環境の準備
    CLSetup cl = CLSetupCreator.createCLSetup();
    cl.initContext();

    FloatBuffer tmpfb;

    tmpfb =  loadData("data/dataa.txt");
    CLBuffer<FloatBuffer> BufferA = cl.createBuffer(tmpfb, READ_ONLY);
    tmpfb.rewind();

    tmpfb = loadData("data/datab.txt");
    CLBuffer<FloatBuffer> BufferB = cl.createBuffer(tmpfb, READ_ONLY);
    tmpfb.rewind();

    tmpfb = loadData("data/datac.txt");
    CLBuffer<FloatBuffer> BufferC = cl.createBuffer(tmpfb, READ_ONLY);
    tmpfb.rewind();

    CLBuffer<FloatBuffer> BufferD = cl.createFloatBuffer(tmpfb.limit(), WRITE_ONLY);
    int datasize = tmpfb.limit();
    CLCommandQueue queue = cl.createQueue();

    CLProgram program = cl.createProgramFromResource(this, "sum3.cl");
    CLKernel kernel = program.createCLKernel("Sum");
    kernel.putArgs(BufferA, BufferB, BufferC, BufferD);
    kernel.setArg(4, datasize);

    BufferC.getBuffer().rewind();

    queue.putWriteBuffer(BufferA, false)
      .putWriteBuffer(BufferB, false)
      .putWriteBuffer(BufferC, false)
      .putBarrier()
      .put1DRangeKernel(kernel, 0, datasize, 1)
      .putBarrier()
      .putReadBuffer(BufferD, true);

    FloatBuffer fb = BufferD.getBuffer();
    fb.rewind();
    for(int i = 0; i < fb.limit(); i++){
      System.out.print(fb.get() + " ");
    }
    System.out.println();
    cl.release();
  }

  static public FloatBuffer loadData(String fname){
    return HelloOpenCL.loadData(fname);
  }

  public static void main(String[] argv){
    new Sum3();
  }
}
