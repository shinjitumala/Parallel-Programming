// 1613354 Hoshino Shinji
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable

// OpenCL Kernel Function
__kernel void Mosaic(const int width, const int height,
                     __global uchar* input,
                     __local int* ldata,
                     __global uchar* output,
                        float parameter){
  int x = get_global_id(0);
  int y = get_global_id(1);
  int lx = get_local_id(0);
  int ly = get_local_id(1);

  /**
   * copy one block area colors to the local memory for this work group
   */

  /*
  ldata[((ly*2  )*8+(lx*2  ))*3  ] = input[((y*2  )*width+(x*2  ))*3  ];
  ldata[((ly*2  )*8+(lx*2  ))*3+1] = input[((y*2  )*width+(x*2  ))*3+1];
  ldata[((ly*2  )*8+(lx*2  ))*3+2] = input[((y*2  )*width+(x*2  ))*3+2];
  ldata[((ly*2  )*8+(lx+1)*2)*3  ] = input[((y*2  )*width+(x+1)*2)*3  ];
  ldata[((ly*2  )*8+(lx+1)*2)*3+1] = input[((y*2  )*width+(x+1)*2)*3+1];
  ldata[((ly*2  )*8+(lx+1)*2)*3+2] = input[((y*2  )*width+(x+1)*2)*3+2];
  ldata[((ly*2+1)*8+(lx*2  ))*3  ] = input[((y+1)*2*width+(x*2  ))*3  ];
  ldata[((ly*2+1)*8+(lx*2  ))*3+1] = input[((y+1)*2*width+(x*2  ))*3+1];
  ldata[((ly*2+1)*8+(lx*2  ))*3+2] = input[((y+1)*2*width+(x*2  ))*3+2];
  ldata[((ly*2+1)*8+(lx+1)*2)*3  ] = input[((y+1)*2*width+(x+1)*2)*3  ];
  ldata[((ly*2+1)*8+(lx+1)*2)*3+1] = input[((y+1)*2*width+(x+1)*2)*3+1];
  ldata[((ly*2+1)*8+(lx+1)*2)*3+2] = input[((y+1)*2*width+(x+1)*2)*3+2];
  */
  for(int c=0; c<3; c++){
    ldata[((ly*2  )*8+(lx*2  ))*3+c] = input[((y*2  )*width+(x*2  ))*3+c];
    ldata[((ly*2  )*8+(lx*2+1))*3+c] = input[((y*2  )*width+(x*2+1))*3+c];
    ldata[((ly*2+1)*8+(lx*2  ))*3+c] = input[((y*2+1)*width+(x*2  ))*3+c];
    ldata[((ly*2+1)*8+(lx*2+1))*3+c] = input[((y*2+1)*width+(x*2+1))*3+c];
  }

  // wait for all local data to be loaded
  barrier(CLK_LOCAL_MEM_FENCE);

  /**
   * calculate average color in one block area
   */
  for(int c = 0; c < 3; c++){
    ldata[((ly * 2    ) * 8 + (lx * 2    )) * 3 + c] = (ldata[((ly * 2    ) * 8 + (lx * 2    )) * 3 + c] +
                                                        ldata[((ly * 2    ) * 8 + (lx * 2 + 1)) * 3 + c] +
                                                        ldata[((ly * 2 + 1) * 8 + (lx * 2    )) * 3 + c] +
                                                        ldata[((ly * 2 + 1) * 8 + (lx * 2 + 1)) * 3 + c]) / 4;
  }

  // wait for local calculations to be over before output
  barrier(CLK_LOCAL_MEM_FENCE);

  /**
   * fill one color in a block area
   */
  /*
  output[((y*2  )*width+(x*2  ))*4  ] = ldata[0];
  output[((y*2  )*width+(x*2  ))*4+1] = ldata[1];
  output[((y*2  )*width+(x*2  ))*4+2] = ldata[2];
  output[((y*2  )*width+(x*2  ))*4+3] = 0xff;
  output[((y*2  )*width+(x*2+1))*4  ] = ldata[0];
  output[((y*2  )*width+(x*2+1))*4+1] = ldata[1];
  output[((y*2  )*width+(x*2+1))*4+2] = ldata[2];
  output[((y*2  )*width+(x*2+1))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2  ))*4  ] = ldata[0];
  output[((y*2+1)*width+(x*2  ))*4+1] = ldata[1];
  output[((y*2+1)*width+(x*2  ))*4+2] = ldata[2];
  output[((y*2+1)*width+(x*2  ))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2+1))*4  ] = ldata[0];
  output[((y*2+1)*width+(x*2+1))*4+1] = ldata[1];
  output[((y*2+1)*width+(x*2+1))*4+2] = ldata[2];
  output[((y*2+1)*width+(x*2+1))*4+3] = 0xff;
  */
  for(int c=0; c<3; c++){
    output[((y*2  )*width+(x*2  ))*4+c] = ldata[c];
    output[((y*2  )*width+(x*2+1))*4+c] = ldata[c];
    output[((y*2+1)*width+(x*2  ))*4+c] = ldata[c];
    output[((y*2+1)*width+(x*2+1))*4+c] = ldata[c];
  }
  output[((y*2  )*width+(x*2  ))*4+3] = 0xff;
  output[((y*2  )*width+(x*2+1))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2  ))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2+1))*4+3] = 0xff;

  /* just through process which means that there is no data change
  output[((y*2  )*width+(x*2  ))*4  ] = input[((y*2  )*width+(x*2  ))*3  ];
  output[((y*2  )*width+(x*2  ))*4+1] = input[((y*2  )*width+(x*2  ))*3+1];
  output[((y*2  )*width+(x*2  ))*4+2] = input[((y*2  )*width+(x*2  ))*3+2];
  output[((y*2  )*width+(x*2  ))*4+3] = 0xff;
  output[((y*2  )*width+(x*2+1))*4  ] = input[((y*2  )*width+(x*2+1))*3  ];
  output[((y*2  )*width+(x*2+1))*4+1] = input[((y*2  )*width+(x*2+1))*3+1];
  output[((y*2  )*width+(x*2+1))*4+2] = input[((y*2  )*width+(x*2+1))*3+2];
  output[((y*2  )*width+(x*2+1))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2  ))*4  ] = input[((y*2+1)*width+(x*2  ))*3  ];
  output[((y*2+1)*width+(x*2  ))*4+1] = input[((y*2+1)*width+(x*2  ))*3+1];
  output[((y*2+1)*width+(x*2  ))*4+2] = input[((y*2+1)*width+(x*2  ))*3+2];
  output[((y*2+1)*width+(x*2  ))*4+3] = 0xff;
  output[((y*2+1)*width+(x*2+1))*4  ] = input[((y*2+1)*width+(x*2+1))*3  ];
  output[((y*2+1)*width+(x*2+1))*4+1] = input[((y*2+1)*width+(x*2+1))*3+1];
  output[((y*2+1)*width+(x*2+1))*4+2] = input[((y*2+1)*width+(x*2+1))*3+2];
  output[((y*2+1)*width+(x*2+1))*4+3] = 0xff;
  */
}
