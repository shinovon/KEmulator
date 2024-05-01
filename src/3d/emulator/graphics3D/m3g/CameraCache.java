package emulator.graphics3D.m3g;

import emulator.graphics3D.Transform3D;

import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Transform;

public final class CameraCache {
    public static Camera camera;

    private static Transform camTrans = new Transform();
    public static Transform invCam = new Transform();

    /*private static final float[][] frustum = new float[6][16];
    private static final float[] invCamf = new float[16], proj = new float[16], clip = new float[16];*/

    public static void setCamera(Camera cam, Transform transform) {
        if (transform != null) {
            camTrans.set(transform);
            invCam.set(transform);
            ((Transform3D) invCam.getImpl()).invert();
        } else {
            camTrans.setIdentity();
            invCam.setIdentity();
        }

        camera = cam;
        //setFrustum();
    }

    public static Camera getCamera(Transform transform) {
        if (transform != null) {
            transform.set(camTrans);
        }

        return camera;
    }

    /*private static void setFrustum() {
        camTrans.get(invCamf);
        camera.getProjection(proj);

        //Combine the two matrices (multiply projection by modelview)
        //Combine the two matrices (multiply projection by modelview)
        clip[0] = invCamf[0] * proj[0] + invCamf[1] * proj[4] + invCamf[2] * proj[8] + invCamf[3] * proj[12];
        clip[1] = invCamf[0] * proj[1] + invCamf[1] * proj[5] + invCamf[2] * proj[9] + invCamf[3] * proj[13];
        clip[2] = invCamf[0] * proj[2] + invCamf[1] * proj[6] + invCamf[2] * proj[10] + invCamf[3] * proj[14];
        clip[3] = invCamf[0] * proj[3] + invCamf[1] * proj[7] + invCamf[2] * proj[11] + invCamf[3] * proj[15];

        clip[4] = invCamf[4] * proj[0] + invCamf[5] * proj[4] + invCamf[6] * proj[8] + invCamf[7] * proj[12];
        clip[5] = invCamf[4] * proj[1] + invCamf[5] * proj[5] + invCamf[6] * proj[9] + invCamf[7] * proj[13];
        clip[6] = invCamf[4] * proj[2] + invCamf[5] * proj[6] + invCamf[6] * proj[10] + invCamf[7] * proj[14];
        clip[7] = invCamf[4] * proj[3] + invCamf[5] * proj[7] + invCamf[6] * proj[11] + invCamf[7] * proj[15];

        clip[8] = invCamf[8] * proj[0] + invCamf[9] * proj[4] + invCamf[10] * proj[8] + invCamf[11] * proj[12];
        clip[9] = invCamf[8] * proj[1] + invCamf[9] * proj[5] + invCamf[10] * proj[9] + invCamf[11] * proj[13];
        clip[10] = invCamf[8] * proj[2] + invCamf[9] * proj[6] + invCamf[10] * proj[10] + invCamf[11] * proj[14];
        clip[11] = invCamf[8] * proj[3] + invCamf[9] * proj[7] + invCamf[10] * proj[11] + invCamf[11] * proj[15];

        clip[12] = invCamf[12] * proj[0] + invCamf[13] * proj[4] + invCamf[14] * proj[8] + invCamf[15] * proj[12];
        clip[13] = invCamf[12] * proj[1] + invCamf[13] * proj[5] + invCamf[14] * proj[9] + invCamf[15] * proj[13];
        clip[14] = invCamf[12] * proj[2] + invCamf[13] * proj[6] + invCamf[14] * proj[10] + invCamf[15] * proj[14];
        clip[15] = invCamf[12] * proj[3] + invCamf[13] * proj[7] + invCamf[14] * proj[11] + invCamf[15] * proj[15];

        //Extract the numbers for the RIGHT plane
        frustum[0][0] = clip[3] - clip[0];
        frustum[0][1] = clip[7] - clip[4];
        frustum[0][2] = clip[11] - clip[8];
        frustum[0][3] = clip[15] - clip[12];

        //Normalize the result
        float t = (float) Math.sqrt(
                frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]
        );
        frustum[0][0] /= t;
        frustum[0][1] /= t;
        frustum[0][2] /= t;
        frustum[0][3] /= t;

        //Extract the numbers for the LEFT plane
        frustum[1][0] = clip[3] + clip[0];
        frustum[1][1] = clip[7] + clip[4];
        frustum[1][2] = clip[11] + clip[8];
        frustum[1][3] = clip[15] + clip[12];

        //Normalize the result
        t = (float) Math.sqrt(
                frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]
        );
        frustum[1][0] /= t;
        frustum[1][1] /= t;
        frustum[1][2] /= t;
        frustum[1][3] /= t;

        //Extract the BOTTOM plane
        frustum[2][0] = clip[3] + clip[1];
        frustum[2][1] = clip[7] + clip[5];
        frustum[2][2] = clip[11] + clip[9];
        frustum[2][3] = clip[15] + clip[13];

        //Normalize the result
        t = (float) Math.sqrt(
                frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]
        );
        frustum[2][0] /= t;
        frustum[2][1] /= t;
        frustum[2][2] /= t;
        frustum[2][3] /= t;

        //Extract the TOP plane
        frustum[3][0] = clip[3] - clip[1];
        frustum[3][1] = clip[7] - clip[5];
        frustum[3][2] = clip[11] - clip[9];
        frustum[3][3] = clip[15] - clip[13];

        //Normalize the result
        t = (float) Math.sqrt(
                frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]
        );
        frustum[3][0] /= t;
        frustum[3][1] /= t;
        frustum[3][2] /= t;
        frustum[3][3] /= t;

        //Extract the FAR plane
        frustum[4][0] = clip[3] - clip[2];
        frustum[4][1] = clip[7] - clip[6];
        frustum[4][2] = clip[11] - clip[10];
        frustum[4][3] = clip[15] - clip[14];

        //Normalize the result
        t = (float) Math.sqrt(
                frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]
        );
        frustum[4][0] /= t;
        frustum[4][1] /= t;
        frustum[4][2] /= t;
        frustum[4][3] /= t;

        //Extract the NEAR plane
        frustum[5][0] = clip[3] + clip[2];
        frustum[5][1] = clip[7] + clip[6];
        frustum[5][2] = clip[11] + clip[10];
        frustum[5][3] = clip[15] + clip[14];

        //Normalize the result
        t = (float) Math.sqrt(
                frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2]
        );
        frustum[5][0] /= t;
        frustum[5][1] /= t;
        frustum[5][2] /= t;
        frustum[5][3] /= t;
    }

    public static boolean visible(float[] aabb) {
        //тестируем 6 плоскостей фрустума
        float minx = aabb[0], miny = aabb[1], minz = aabb[3];
        float maxx = aabb[4], maxy = aabb[5], maxz = aabb[6];

        for(int i = 0; i < 6; i++) {
            //находим ближайшую к плоскости вершину
            //проверяем, если она находится за плоскостью, то объект вне фрустума

            float[] frustum = CameraCache.frustum[i];
            float frustum0 = frustum[0],
                    frustum1 = frustum[1],
                    frustum2 = frustum[2];

            float minxf = minx * frustum0,
                    maxxf = maxx * frustum0,
                    minyf = miny * frustum1,
                    maxyf = maxy * frustum1,
                    minzf = minz * frustum2,
                    maxzf = maxz * frustum2;

            float d = (minxf > maxxf ? minxf : maxxf)
                    + (minyf > maxyf ? minyf : maxyf)
                    + (minzf > maxzf ? minzf : maxzf)
                    + frustum[3];

            if(d <= 0) return false;
        }

        //если не нашли разделяющей плоскости, считаем объект видим
        return true;
    }*/
}
