// 1613354 Hoshino Shinji
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

__kernel void Max(__constant float* a, __constant float* b, __constant float* c, __global float* d, int numElements){
  int i = get_global_id(0);

  if(numElements < i) return;

  d[i] = a[i];
  d[i] = (b[i] > d[i]) * b[i] + (d[i] >= b[i]) * d[i];
  d[i] = (c[i] > d[i]) * c[i] + (d[i] >= c[i]) * d[i];
}
