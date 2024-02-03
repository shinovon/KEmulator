/*
 * Created on 13/02/2006
 *
 * Copyright (c) 2005, Fundação Des. Paulo Feitoza. All rights reserved.
 * 
 */
package javax.microedition.m3g;

import java.util.Vector;

/**
 * [Description for RecTransform]
 *
 * @author Rodrigo Cal (rcal@fpf.br) 
 */
class RecTransform {

    static final int REF_MULT = 0;
    static final int POS_MULT = 1;
    int mode = REF_MULT;             
    int weight;
    Object id;
    Transform boneTransform;
    Transform transform;        
    int index;         
    Vector vet;   

}
