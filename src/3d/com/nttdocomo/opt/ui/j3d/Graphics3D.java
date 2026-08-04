package com.nttdocomo.opt.ui.j3d;

public abstract interface Graphics3D
{
  public abstract void setViewTrans(AffineTrans paramAffineTrans);
  
  public abstract void setScreenScale(int paramInt1, int paramInt2);
  
  public abstract void setScreenCenter(int paramInt1, int paramInt2);
  
  public abstract void drawFigure(Figure paramFigure);
  
  public abstract void setSphereTexture(Texture paramTexture);
  
  public abstract void enableLight(boolean paramBoolean);
  
  public abstract void enableSphereMap(boolean paramBoolean);
  
  public abstract void setAmbientLight(int paramInt);
  
  public abstract void setDirectionLight(Vector3D paramVector3D, int paramInt);
  
  public abstract void enableSemiTransparent(boolean paramBoolean);
  
  public abstract void setClipRect3D(int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  void setPerspective(int n1, int n2, int n3);

  void executeCommandList(int[] a);
}
