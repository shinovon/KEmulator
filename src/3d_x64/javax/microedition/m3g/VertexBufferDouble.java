/*
 * Created on 23/01/2006
 *
 * Copyright (c) 2005, Fundação Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

/**
 * [Description for VertexBufferDouble]
 *
 * @author Rodrigo Cal (rcal@fpf.br) 
 */
class VertexBufferDouble extends VertexBuffer 
{
    double[] positionsExtras = null;
    double[] normalsExtras  = null;
    
    
    public VertexBufferDouble(VertexBuffer vb)
    {
        VertexBufferDouble dup  = this;
        dup.defaultColor        = vb.defaultColor;
        dup.colors              = vb.colors;
        dup.positions           = vb.positions;
        dup.normals             = vb.normals;
        dup.positionScale       = vb.positionScale;
        
        int arrayLenght         = vb.positions.numComponents * vb.positions.numVertices;        
        dup.positionsExtras     = new double[arrayLenght];
        setArray(dup.positionsExtras, vb.positions);
        
        if (vb.normals != null)
        {
            dup.normalsExtras   = new double[arrayLenght];
            setArray(dup.normalsExtras,   vb.normals  );
        }
 
        System.arraycopy(vb.positionBias, 0, dup.positionBias,0, 3);
        for(int i = 0; i < vb.textureArrays.size(); i++){
            TextureCoordinate tc = (TextureCoordinate) vb.textureArrays.elementAt(i);
            dup.textureArrays.addElement(tc == null ? null : new TextureCoordinate(
                    tc.vertices, tc.scale, tc.bias));
        }
        vb._duplicate(dup);
    }
    
    private void setArray(double vet[], VertexArray va)
    {
        switch (va.componentSize)
        {
            case 1:
            {
                for (int i = 0; i < vet.length; i++)
                {
                    vet[i] = va.data1[i];
                }
                break;
            }
            case 2:
            {
                for (int i = 0; i < vet.length; i++)
                {
                    vet[i] = va.data2[i];
                }
                break;
            }                
        }
    }
}
