// 1613354 Hoshino Shinji
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

// OpenCL Kernel Function
__kernel void Gray(const int width, const int height,
                        __global uchar* input, __global uchar* output,
                        float parameter){
/**
 * int width :  image width
 * int height : image height
 * __const uchar* input :  input image from camera
 * __global uchar* output :  output from this program
 * float parameter : this is not used in this program
 */

  int x = get_global_id(0);
  int y = get_global_id(1);
  int addr = (y*width+x)*3;



  // the following is do nothing sample
  float color = input[addr] * 0.212671 + input[addr + 1] * 0.715160 + input[addr + 2] * 0.072169;
  output[(y*width+x)*4  ] = color;
  output[(y*width+x)*4+1] = color;
  output[(y*width+x)*4+2] = color;
  output[(y*width+x)*4+3] = 0xff;
}
