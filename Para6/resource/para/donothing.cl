#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

// OpenCL Kernel Function
__kernel void DoNothing(const int width, const int height,
         __global uchar* latest, __global uchar* old,
         __global uchar* output, const float p){
  int x = get_global_id(0);
  int y = get_global_id(1);
  output[(y*width+x)*4  ] =
    (uchar)(latest[(y*width+x)*3  ]);
  output[(y*width+x)*4+1] =
    (uchar)(latest[(y*width+x)*3+1]);
  output[(y*width+x)*4+2] =
    (uchar)(latest[(y*width+x)*3+2]);
  output[(y*width+x)*4+3] = 0xff;
}
