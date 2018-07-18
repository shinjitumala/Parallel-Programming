package para;

import para.graphic.shape.*;
import para.graphic.target.*;
import java.util.*;
import java.nio.*;
import java.util.stream.*;

/**
 * カメラ画像の図形を出力する際、減色処理を行うフィルタを実装したTargetの装飾クラス。
 * 減色時の代表色をカメラ画像に応じて自動調整する
 */
public class TargetColorFilter extends TargetFilter{
  protected int[] inimage = new int[Camera.WIDTH*Camera.HEIGHT];
  protected byte[] outimage = new byte[Camera.WIDTH*Camera.HEIGHT*4];
  protected ByteBuffer outbuffer = ByteBuffer.wrap(outimage);
  protected int sample[];

  /**
   * @param target 装飾対象のTargetクラス
   * @param quantizecount 3原色の1色当たりの量子化数
   */
  public TargetColorFilter(Target target, int quantizecount){
    super(target);
    //prepare quantizecount^3 color samples
    sample = initSample(quantizecount);
  }

  protected ByteBuffer process(ByteBuffer input){
    byte[] tmp = new byte[3];
    input.rewind();
    for(int i=0;i<input.limit()/3;i++){
      input.get(tmp, 0, 3);
      inimage[i] = ((tmp[2]&0xff)<<16)|((tmp[1]&0xff)<<8)|(tmp[0]&0xff);
    }
    Map<Integer,Integer> classified =
      IntStream.range(0,Camera.WIDTH*Camera.HEIGHT)
        .boxed()
        .collect(Collectors.toMap(n->n,
                   n->{int min=600000;
                   int label=0;
                   for(int i=0;i<sample.length;i++){
                     int dp = distancepow(inimage[n], sample[i]);
                       if(dp < min){
                         label=i;
                         min = dp;
                       }
                     }
                   return label;
               }));
    classified.forEach((k,v)->{outimage[k*4+0]=(byte)0xff;
                               outimage[k*4+1]=(byte)((sample[v]>>16)&0xff);
                               outimage[k*4+2]=(byte)((sample[v]>>8)&0xff);
                               outimage[k*4+3]=(byte)(sample[v]&0xff);});
    /* do no filtering, just through output
    classified.forEach((k,v)->{outimage[k*4+0]=(byte)0xff;
                               outimage[k*4+1]=(byte)((inimage[k]>>16)&0xff);
                               outimage[k*4+2]=(byte)((inimage[k]>>8)&0xff);
                               outimage[k*4+3]=(byte)(inimage[k]&0xff);});
    */
    input.rewind();
    outbuffer.rewind();
    return outbuffer;
  }

  protected int distancepow(int a, int b){
    int disr = ((a & 0xff0000)>>16) - ((b & 0xff0000)>>16);
    int disg = ((a & 0x00ff00)>> 8) - ((b & 0x00ff00)>> 8);
    int disb = ((a & 0x0000ff)    ) - ((b & 0x0000ff)    );
    return disr*disr+disg*disg+disb*disb;
  }

  protected int[] initSample2x2x2(){
    int [] sample = new int[8];
    sample[0] = (  0<<16)|(  0<<8)|  0;
    sample[1] = (  0<<16)|(  0<<8)|255;
    sample[2] = (  0<<16)|(255<<8)|  0;
    sample[3] = (255<<16)|(  0<<8)|  0;
    sample[4] = (  0<<16)|(255<<8)|255;
    sample[5] = (255<<16)|(255<<8)|  0;
    sample[6] = (255<<16)|(  0<<8)|255;
    sample[7] = (255<<16)|(255<<8)|255;
    return sample;
  }

  protected int[] initSample(int n){
    int qcount = n;
    int count = qcount*qcount*qcount;
    int step = 256/(n-1);
    int [] sample = new int[count];
    for(int i=0;i<count;i++){
      sample[i] = (Math.max((step*(i/(qcount*qcount))-1), 0) <<16)|
                  (Math.max(step*((i/qcount)%qcount)-1,0)<<8)|
                   Math.max(step*(i%qcount)-1,0);
    }
    /* print color coordinate */
    for(int i=0;i<sample.length;i++){
      int c1 = ((sample[i] & 0xff0000)>>16);
      int c2 = ((sample[i] & 0x00ff00)>>8);
      int c3 = ((sample[i] & 0x0000ff)   );
      //System.out.println(c1+" "+c2+" "+" "+c3);
    }
    /**/
    return sample;
  }
}
