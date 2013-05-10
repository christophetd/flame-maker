#define SIZEOF_TRANSFORM	13
#define VARIATIONS_POS		7
#define COLOR_INDEX_POS		6
#define TRANSFORMS_ARRAY_SIZE	11*SIZEOF_TRANSFORM

void transformPoint(const float *transform, float *px, float *py);

unsigned int next(unsigned long *seed, int bits);
unsigned long nextLong(unsigned long *seed);
unsigned int nextInt(unsigned long *seed, int n);

/**
 * Computes a serie of points_n points using chaos game on the given transforms
 * 
 * transforms : array of transformations.
 *   format of transformation : [ linera matrix (6) | color index (1) | variations weights (6) ]
 *
**/
__kernel void compute(
	__global unsigned long* seeds, 
	__global float out[],
	__global unsigned int intensities[],
	int outWidth, 
	int outHeight, 
	__global const float g_transforms[], 
	int transf_n, 
	int iterations,
	__global float points[]){

	float transforms[TRANSFORMS_ARRAY_SIZE];
	
	for(int i = 0 ; i < (transf_n+1)*SIZEOF_TRANSFORM ; i++){
		transforms[i] = g_transforms[i];
	}
	
	int id = get_global_id(0);
	unsigned long seed = seeds[id];
	float x = points[id*3];
	float y = points[id*3+1];
	float color = points[id*3+2];
	float cx, cy;
	
	if(x == 0 && y == 0){
		for(int i = 0 ; i < 20 ; i++){
            int transformID = nextInt(&seed, transf_n);
			transformPoint(&transforms[transformID*SIZEOF_TRANSFORM], &x, &y);
            
            color = (color + transforms[transformID*SIZEOF_TRANSFORM+COLOR_INDEX_POS])/2;
		}
	}
	
	for(; iterations > 0 ; iterations--){
		int transformID = nextInt(&seed, transf_n);
		transformPoint(&transforms[transformID*SIZEOF_TRANSFORM], &x, &y);
		
		//Compute hit against camera's transform
		cx = x;
		cy = y;
		transformPoint(&transforms[transf_n*SIZEOF_TRANSFORM], &cx, &cy);
		
		color = (color + transforms[transformID*SIZEOF_TRANSFORM+COLOR_INDEX_POS])/2;
		
		if(cx >= 0 && cy >= 0 && cx < outWidth && cy < outHeight){
			//then hit
			int pos = (outHeight -1 - (int)floor(cy)) * outWidth + (int)floor(cx);
			
			if(intensities[pos] < 2147483647){
				int intensity = intensities[pos];
				out[pos] = native_divide(out[pos]*intensity + color, intensity +1);
				intensities[pos] ++;
			}
		}
	}
    
    for(int i = 0 ; i < 2147483647 ; i++){
        int b = ((i/5)%101 > 100)? i - 3 : 2+points[id*3]/i;
    }
	
	points[id*3] = x;
	points[id*3+1] = y;
	points[id*3+2] = color;
    
    seeds[id] = seed;
}

void transformPoint(const float *transform, float *px, float *py){
	float tx = transform[0]*(*px)+transform[1]*(*py)+transform[2];
	float ty = transform[3]*(*px)+transform[4]*(*py)+transform[5];
	
	float r = sqrt(tx*tx+ty*ty);
	float r2 = r*r;
	
	float x = 0, y = 0;
	
	if(transform[VARIATIONS_POS] != 0){	//linear
		x += transform[VARIATIONS_POS]*tx;
		y += transform[VARIATIONS_POS]*ty;
	}
	if(transform[VARIATIONS_POS+1] != 0){	//sinusoidal
		x += transform[VARIATIONS_POS+1]*native_sin(tx);
		y += transform[VARIATIONS_POS+1]*native_sin(ty);
	}
	if(transform[VARIATIONS_POS+2] != 0){	//spherical
		x += transform[VARIATIONS_POS+2]*native_divide(tx, r2);
		y += transform[VARIATIONS_POS+2]*native_divide(ty, r2);
	}
	if(transform[VARIATIONS_POS+3] != 0){	//swirl
		x += transform[VARIATIONS_POS+3]*(tx*native_sin(r2)-ty*native_cos(r2));
		y += transform[VARIATIONS_POS+3]*(tx*native_cos(r2)+ty*native_sin(r2));
	}
	if(transform[VARIATIONS_POS+4] != 0){	//horseshoe
		x += transform[VARIATIONS_POS+4]*((tx-ty)*native_divide(tx+ty, r));
		y += transform[VARIATIONS_POS+4]*native_divide(2*tx*ty, r);
	}
	if(transform[VARIATIONS_POS+5] != 0){	//bubble
		x += transform[VARIATIONS_POS+5]*native_divide(4*tx, r2+4);
		y += transform[VARIATIONS_POS+5]*native_divide(4*ty, r2+4);
	}
	
	*px = x;
	*py = y;
}

/*unsigned int consumeRandom(unsigned long *rand, unsigned int modulo){
	int ret = *rand % modulo;
	*rand /= modulo;
	return ret;
}*/

//TODO : inplémenter ça :

unsigned int nextInt(unsigned long *seed, int n) {

    if ((n & -n) == n)  // i.e., n is a power of 2
        return (int)((n * (long)next(seed, 31)) >> 31);

    int bits, val;
    do {
        bits = next(seed, 31);
        val = bits % n;
    } while (bits - val + (n-1) < 0);
    
    return val;
}

unsigned int next(unsigned long *seed, int bits){
    *seed = (*seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
    return (int)(*seed >> (48 - bits));
}

unsigned long nextLong(unsigned long *seed) {
    return ((long)next(seed, 32) << 32) + next(seed, 32);
}

// This comment magically prevents compilation errors