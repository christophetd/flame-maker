#define SIZEOF_TRANSFORM	13
#define VARIATIONS_POS		7
#define COLOR_INDEX_POS		6
#define TRANSFORMS_ARRAY_SIZE	11*SIZEOF_TRANSFORM

void transformPoint(const float *transform, float *px, float *py);

//Consumes the random number providing uniform [0-modulo] number repartition
unsigned int consumeRandom(unsigned long *rand, unsigned int modulo);


/**
 * Computes a serie of points_n points using chaos game on the given transforms
 * 
 * transforms : array of transformations.
 *   format of transformation : [ linera matrix (6) | color index (1) | variations weights (6) ]
 *
**/
__kernel void compute(
	__global const unsigned long* randoms, 
	__global unsigned char out[],
	__global unsigned int intensities[],
	int outWidth, 
	int outHeight, 
	__global const float g_transforms[], 
	int transf_n, 
	int random_life, 		//Defines the number of times the random number can be consumed before expiring
	__global float points[]){

	float transforms[TRANSFORMS_ARRAY_SIZE];
	
	for(int i = 0 ; i < (transf_n+1)*SIZEOF_TRANSFORM ; i++){
		transforms[i] = g_transforms[i];
	}
	
	int id = get_global_id(0);
	unsigned long random = randoms[id];
	float x = points[id*3];
	float y = points[id*3+1];
	float color = points[id*3+2];
	float cx, cy;
	
	if(x == 0 && y == 0){
		for(int i = 0 ; i < 20 && random_life > 0; i++, random_life--){
			transformPoint(&transforms[consumeRandom(&random, transf_n)*SIZEOF_TRANSFORM], &x, &y);
		}
	}
	
	for(; random_life > 0 ; random_life--){
		int transformID = consumeRandom(&random, transf_n);
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
				out[pos] = native_divide(out[pos]*intensity + color*127, intensity +1);
				intensities[pos] ++;
			}
		}
	}
	
	points[id*3] = x;
	points[id*3+1] = y;
	points[id*3+2] = color;
}

__kernel void logarithmize(__global unsigned int intensities[], float divider, int n){
	
	int id = get_global_id(0);
	
	if(id > n)
		return;
		
	intensities[id] = (2147483647*native_divide(native_log((float)intensities[id]+1), divider));
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

unsigned int consumeRandom(unsigned long *rand, unsigned int modulo){
	int ret = *rand % modulo;
	*rand /= modulo;
	return ret;
}
