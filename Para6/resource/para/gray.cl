#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

// OpenCL Kernel Function
__kernel void Gray(const int width, const int height,
                        __constant uchar* input, __global uchar* output,
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
  output[(y*width+x)*4  ] = input[addr  ];
  output[(y*width+x)*4+1] = input[addr+1];
  output[(y*width+x)*4+2] = input[addr+2];
  output[(y*width+x)*4+3] = 0xff;
}
