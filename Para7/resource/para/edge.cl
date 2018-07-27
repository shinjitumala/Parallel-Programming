// 1613354 Hoshino Shinji
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

int addr(const int width, const int height, int x, int y){
  if(y < 0) y = 0;
  if(height - 1 < y) y = height - 1;
  if(x < 0) x = 0;
  if(width - 1 < x) x = width - 1;
  return (y * width * 3 + x * 3);
}

float bound(const float in){
  if(in < 0) return 0;
  if(in > 255) return 0;
  return in;
}

float edge(__global const uchar* in, const int width, const int height, const int x, const int y, const int shift, const int s){
  float ac, bc;
  ac = in[addr(width, height, x - 1, y - 1) + shift] * -1
     + in[addr(width, height, x    , y - 1) + shift] * 0
     + in[addr(width, height, x + 1, y - 1) + shift] * 1

     + in[addr(width, height, x - 1, y    ) + shift] * -2
     + in[addr(width, height, x    , y    ) + shift] * 0
     + in[addr(width, height, x + 1, y    ) + shift] * 2

     + in[addr(width, height, x - 1, y + 1) + shift] * -1
     + in[addr(width, height, x    , y + 1) + shift] * 0
     + in[addr(width, height, x + 1, y + 1) + shift] * 1;

  bc = in[addr(width, height, x - 1, y - 1) + shift] * -1
     + in[addr(width, height, x    , y - 1) + shift] * -2
     + in[addr(width, height, x + 1, y - 1) + shift] * -1

     + in[addr(width, height, x - 1, y    ) + shift] * 0
     + in[addr(width, height, x    , y    ) + shift] * 0
     + in[addr(width, height, x + 1, y    ) + shift] * 0

     + in[addr(width, height, x - 1, y + 1) + shift] * 1
     + in[addr(width, height, x    , y + 1) + shift] * 2
     + in[addr(width, height, x + 1, y + 1) + shift] * 1;

  return (ac * ac + bc * bc) * s / 1600;
}

// OpenCL Kernel Function
__kernel void Edge(const int width, const int height, __global const uchar* in, __global uchar *outb, const float s){
  // get index of global data array
  int lx = get_global_id(0);
  int ly = get_global_id(1);

  int oadd = (ly * width + lx) * 4;

  outb[oadd    ] = bound(edge(in, width, height, lx, ly, 0, s));
  outb[oadd + 1] = bound(edge(in, width, height, lx, ly, 1, s));
  outb[oadd + 2] = bound(edge(in, width, height, lx, ly, 2, s));
  outb[oadd + 3] = 255;
}
