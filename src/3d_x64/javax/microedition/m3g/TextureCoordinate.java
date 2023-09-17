package javax.microedition.m3g;

public class TextureCoordinate {

        VertexArray vertices;
        float scale;
        float[] bias = new float[3];
        
        TextureCoordinate(VertexArray va, float scale, float[] bias){
            this.vertices = va;
            this.scale = scale;
            if(bias != null){
            		System.arraycopy(bias, 0, this.bias, 0, 3);
            }
        }
        
        VertexArray get(float[] scaleBias){
            if(scaleBias != null){
                scaleBias[0] = scale;
                for(int i = 0; i < Math.min(bias.length, scaleBias.length-1); i++){
                    scaleBias[i+1] = bias[i];
                }
            }
            return vertices;

        }
      
        
}
