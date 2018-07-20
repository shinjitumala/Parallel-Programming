#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

__kernel void Delay(const int width, const int height,
         __global uchar* latest, __global uchar* previous,
         __global uchar* output, const float p){
  int x = get_global_id(0);
  int y = get_global_id(1);
  int addr = (y * width + x) * 3;

  int tmp;
  for(int c = 0; c < 3; c++){
    tmp = latest[addr + c] * p + previous[addr + c] * (1 - p);
    output[(y * width + x) * 4 + c] = tmp;
    previous[addr + c] = tmp;
  }
  output[(y * width + x) * 4 + 3] = 0xff;
}
