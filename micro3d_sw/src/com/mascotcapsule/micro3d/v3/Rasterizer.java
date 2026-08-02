//MIT License
//Copyright (c) 2026 Roman Lahin
//WARNING!!! 
//THIS CODE WAS GENERATED AUTOMATICALLY
//SEE codegen DIRECTORY
package com.mascotcapsule.micro3d.v3;

public class Rasterizer {

	static final int fp = 12, FP = 1 << fp;
	private static final int shadeFpShift = fp - 8;

	private Rasterizer() {
	}

	final static int blendPixel(int src, int dst, int mode) {
		switch(mode) {
			default:
				src = 0xff000000 | ((dst & src) + (((dst ^ src) >> 1) & 0x7F7F7F));
				return src;
			case 2: {
				{
					int tmp = (((dst + src - ((dst ^ src) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					src = 0xff000000 | (dst + src - tmp) | tmp;
				}
				return src;
			}
			case 3: {
				{
					int tmp = (((~dst + src - ((~dst ^ src) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					src = 0xff000000 | ((dst | tmp) - (src | tmp));
				}
				return src;
			}
		}
	}

	static void drawLine(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int x0, int y0, int x1, int y1,
			int color, int blendMode) {
		int dx = x1 - x0;
		if(dx < 0) dx = -dx;
		int dy = y1 - y0;
		if(dy < 0) dy = -dy;
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		int err = dx - dy;
		while(true) {
			if(x0 >= clipX1 && x0 < clipX2 && y0 >= clipY1 && y0 < clipY2) {
				int tmpCol = color;
				int fbPos = y0 * fbWidth + x0;
				if(blendMode != 0)
					tmpCol = blendPixel(tmpCol, frameBuffer[fbPos], blendMode);
				frameBuffer[fbPos] = tmpCol;
			}
			if(x0 == x1 && y0 == y1) break;
			int e2 = 2 * err;
			if(e2 > -dy) {
				err -= dy;
				x0 += sx;
			}
			if(e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
	//Possible defines: 
	// TEX (textured triangle) 
	// LIGHT (smooth lighting)
	// ENVMAP (environment mapping)
	//Textured triangles
	final static void fillTriangleAffineT(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int au, int av, int bu, int bv, int cu, int cv,
			Texture tex, boolean useColorKey,
			int shade,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;


		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;
			t = bu;
			bu = cu;
			cu = t;
			t = bv;
			bv = cv;
			cv = t;


		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;


		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		int shadeOffset = (shade >> (fp - 8)) & 0xff00;
		boolean fastPath = !useColorKey;
		fastPath &= ((shade >> fp) == 31 && tex.palette.length > 256) || (shade == 0 && tex.palette.length == 256);
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;
		final int du_start = (cu - au) / tempI;
		final int dv_start = (cv - av) / tempI;



		int dx_end = 0;
		int du_end = 0;
		int dv_end = 0;


		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;
			du_end = (bu - au) / tempI;
			dv_end = (bv - av) / tempI;



		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;
		int u_start = au + du_start * tempI;
		int v_start = av + dv_start * tempI;



		x_end = bx << fp;
		int u_end = bu;
		int v_end = bv;



		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;
		final int du = (u_start - u_end) / tempI;
		final int dv = (v_start - v_end) / tempI;



		x_end = x_start = ax << fp;
		u_end = u_start = au;
		v_end = v_start = av;



		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;
				int u;
				int v;


				int dx_left, dx_right;
				int du_left;
				int dv_left;


				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;
					u_start -= du_start * tempI;
					v_start -= dv_start * tempI;



					x_end -= dx_end * tempI;
					u_end -= du_end * tempI;
					v_end -= dv_end * tempI;



					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;
					u = u_start;
					v = v_start;
					du_left = du_start;
					dv_left = dv_start;






					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;
					u = u_end;
					v = v_end;
					du_left = du_end;
					dv_left = dv_end;






					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						if(fastPath) {
							fillTriangleAffineT_replaceFast(
									frameBuffer, fbWidth,
									clipX1, clipX2,
									y_start, y_end_draw,
									u, du_left, du, v, dv_left, dv,
									tex,
									x1, dx_left, x2, dx_right);
							break;
						} else {
							fillTriangleAffineT_replace(
									frameBuffer, fbWidth,
									clipX1, clipX2,
									y_start, y_end_draw,
									u, du_left, du, v, dv_left, dv,
									tex, useColorKey,
									shadeOffset,
									x1, dx_left, x2, dx_right);
							break;
						}
					case 1:
						fillTriangleAffineT_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineT_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineT_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;
			u_start = au + du_start * tempI;
			v_start = av + dv_start * tempI;



			x_end = bx << fp;
			u_end = bu;
			v_end = bv;



			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;
			du_end = (cu - bu) / tempI;
			dv_end = (cv - bv) / tempI;



			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineT_replaceFast(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.origPalette;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;



		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);



			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;



				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			//Slight speedup for most used functions
			while(x2 - x1 >= 6) {
				frameBuffer[x1] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				frameBuffer[x1 + 1] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				frameBuffer[x1 + 2] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				frameBuffer[x1 + 3] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				frameBuffer[x1 + 4] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				frameBuffer[x1 + 5] = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];
				u += du;
				v += dv;
				x1 += 6;
			}
			for(; x1 < x2;
					u += du, v += dv, x1++) {
				int color = texPal[texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask] & 0xFF];

				frameBuffer[x1] = color;
			}
		}
	}

	private final static void fillTriangleAffineT_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;



		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);



			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;



				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			//Slight speedup for most used functions
			while(x2 - x1 >= 6) {
				int texIdx = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx2 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx3 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx4 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx5 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx6 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				if(texIdx != colorKeyIdx) {
					frameBuffer[x1] = texPal[shadeOffset | (texIdx & 0xFF)];
				}
				if(texIdx2 != colorKeyIdx) {
					frameBuffer[x1 + 1] = texPal[shadeOffset | (texIdx2 & 0xFF)];
				}
				if(texIdx3 != colorKeyIdx) {
					frameBuffer[x1 + 2] = texPal[shadeOffset | (texIdx3 & 0xFF)];
				}
				if(texIdx4 != colorKeyIdx) {
					frameBuffer[x1 + 3] = texPal[shadeOffset | (texIdx4 & 0xFF)];
				}
				if(texIdx5 != colorKeyIdx) {
					frameBuffer[x1 + 4] = texPal[shadeOffset | (texIdx5 & 0xFF)];
				}
				if(texIdx6 != colorKeyIdx) {
					frameBuffer[x1 + 5] = texPal[shadeOffset | (texIdx6 & 0xFF)];
				}
				x1 += 6;
			}
			for(; x1 < x2;
					u += du, v += dv, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];

					frameBuffer[x1] = color;
				}
			}
		}
	}

	private final static void fillTriangleAffineT_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;



		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);



			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;



				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			//Slight speedup for most used functions
			while(x2 - x1 >= 6) {
				int texIdx = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx2 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx3 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx4 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx5 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx6 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				if(texIdx != colorKeyIdx) {
					texIdx = texPal[shadeOffset | (texIdx & 0xFF)];
					int dst = frameBuffer[x1];
					frameBuffer[x1] = 0xff000000 | ((dst & texIdx) + (((dst ^ texIdx) >> 1) & 0x7F7F7F));
				}
				if(texIdx2 != colorKeyIdx) {
					texIdx2 = texPal[shadeOffset | (texIdx2 & 0xFF)];
					int dst = frameBuffer[x1 + 1];
					frameBuffer[x1 + 1] = 0xff000000 | ((dst & texIdx2) + (((dst ^ texIdx2) >> 1) & 0x7F7F7F));
				}
				if(texIdx3 != colorKeyIdx) {
					texIdx3 = texPal[shadeOffset | (texIdx3 & 0xFF)];
					int dst = frameBuffer[x1 + 2];
					frameBuffer[x1 + 2] = 0xff000000 | ((dst & texIdx3) + (((dst ^ texIdx3) >> 1) & 0x7F7F7F));
				}
				if(texIdx4 != colorKeyIdx) {
					texIdx4 = texPal[shadeOffset | (texIdx4 & 0xFF)];
					int dst = frameBuffer[x1 + 3];
					frameBuffer[x1 + 3] = 0xff000000 | ((dst & texIdx4) + (((dst ^ texIdx4) >> 1) & 0x7F7F7F));
				}
				if(texIdx5 != colorKeyIdx) {
					texIdx5 = texPal[shadeOffset | (texIdx5 & 0xFF)];
					int dst = frameBuffer[x1 + 4];
					frameBuffer[x1 + 4] = 0xff000000 | ((dst & texIdx5) + (((dst ^ texIdx5) >> 1) & 0x7F7F7F));
				}
				if(texIdx6 != colorKeyIdx) {
					texIdx6 = texPal[shadeOffset | (texIdx6 & 0xFF)];
					int dst = frameBuffer[x1 + 5];
					frameBuffer[x1 + 5] = 0xff000000 | ((dst & texIdx6) + (((dst ^ texIdx6) >> 1) & 0x7F7F7F));
				}
				x1 += 6;
			}
			for(; x1 < x2;
					u += du, v += dv, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];

					int dst = frameBuffer[x1];
					frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
				}
			}
		}
	}

	private final static void fillTriangleAffineT_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;



		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);



			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;



				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			//Slight speedup for most used functions
			while(x2 - x1 >= 6) {
				int texIdx = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx2 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx3 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx4 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx5 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx6 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				if(texIdx != colorKeyIdx) {
					texIdx = texPal[shadeOffset | (texIdx & 0xFF)];
					int dst = frameBuffer[x1];
					{
						int tmp = (((dst + texIdx - ((dst ^ texIdx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | (dst + texIdx - tmp) | tmp;
					}
				}
				if(texIdx2 != colorKeyIdx) {
					texIdx2 = texPal[shadeOffset | (texIdx2 & 0xFF)];
					int dst = frameBuffer[x1 + 1];
					{
						int tmp = (((dst + texIdx2 - ((dst ^ texIdx2) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 1] = 0xff000000 | (dst + texIdx2 - tmp) | tmp;
					}
				}
				if(texIdx3 != colorKeyIdx) {
					texIdx3 = texPal[shadeOffset | (texIdx3 & 0xFF)];
					int dst = frameBuffer[x1 + 2];
					{
						int tmp = (((dst + texIdx3 - ((dst ^ texIdx3) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 2] = 0xff000000 | (dst + texIdx3 - tmp) | tmp;
					}
				}
				if(texIdx4 != colorKeyIdx) {
					texIdx4 = texPal[shadeOffset | (texIdx4 & 0xFF)];
					int dst = frameBuffer[x1 + 3];
					{
						int tmp = (((dst + texIdx4 - ((dst ^ texIdx4) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 3] = 0xff000000 | (dst + texIdx4 - tmp) | tmp;
					}
				}
				if(texIdx5 != colorKeyIdx) {
					texIdx5 = texPal[shadeOffset | (texIdx5 & 0xFF)];
					int dst = frameBuffer[x1 + 4];
					{
						int tmp = (((dst + texIdx5 - ((dst ^ texIdx5) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 4] = 0xff000000 | (dst + texIdx5 - tmp) | tmp;
					}
				}
				if(texIdx6 != colorKeyIdx) {
					texIdx6 = texPal[shadeOffset | (texIdx6 & 0xFF)];
					int dst = frameBuffer[x1 + 5];
					{
						int tmp = (((dst + texIdx6 - ((dst ^ texIdx6) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 5] = 0xff000000 | (dst + texIdx6 - tmp) | tmp;
					}
				}
				x1 += 6;
			}
			for(; x1 < x2;
					u += du, v += dv, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];

					int dst = frameBuffer[x1];
					{
						int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
					}
				}
			}
		}
	}

	private final static void fillTriangleAffineT_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;



		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);



			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;



				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			//Slight speedup for most used functions
			while(x2 - x1 >= 6) {
				int texIdx = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx2 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx3 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx4 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx5 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				int texIdx6 = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				u += du;
				v += dv;
				if(texIdx != colorKeyIdx) {
					texIdx = texPal[shadeOffset | (texIdx & 0xFF)];
					int dst = frameBuffer[x1];
					{
						int tmp = (((~dst + texIdx - ((~dst ^ texIdx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (texIdx | tmp));
					}
				}
				if(texIdx2 != colorKeyIdx) {
					texIdx2 = texPal[shadeOffset | (texIdx2 & 0xFF)];
					int dst = frameBuffer[x1 + 1];
					{
						int tmp = (((~dst + texIdx2 - ((~dst ^ texIdx2) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 1] = 0xff000000 | ((dst | tmp) - (texIdx2 | tmp));
					}
				}
				if(texIdx3 != colorKeyIdx) {
					texIdx3 = texPal[shadeOffset | (texIdx3 & 0xFF)];
					int dst = frameBuffer[x1 + 2];
					{
						int tmp = (((~dst + texIdx3 - ((~dst ^ texIdx3) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 2] = 0xff000000 | ((dst | tmp) - (texIdx3 | tmp));
					}
				}
				if(texIdx4 != colorKeyIdx) {
					texIdx4 = texPal[shadeOffset | (texIdx4 & 0xFF)];
					int dst = frameBuffer[x1 + 3];
					{
						int tmp = (((~dst + texIdx4 - ((~dst ^ texIdx4) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 3] = 0xff000000 | ((dst | tmp) - (texIdx4 | tmp));
					}
				}
				if(texIdx5 != colorKeyIdx) {
					texIdx5 = texPal[shadeOffset | (texIdx5 & 0xFF)];
					int dst = frameBuffer[x1 + 4];
					{
						int tmp = (((~dst + texIdx5 - ((~dst ^ texIdx5) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 4] = 0xff000000 | ((dst | tmp) - (texIdx5 | tmp));
					}
				}
				if(texIdx6 != colorKeyIdx) {
					texIdx6 = texPal[shadeOffset | (texIdx6 & 0xFF)];
					int dst = frameBuffer[x1 + 5];
					{
						int tmp = (((~dst + texIdx6 - ((~dst ^ texIdx6) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1 + 5] = 0xff000000 | ((dst | tmp) - (texIdx6 | tmp));
					}
				}
				x1 += 6;
			}
			for(; x1 < x2;
					u += du, v += dv, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];

					int dst = frameBuffer[x1];
					{
						int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
					}
				}
			}
		}
	}

	final static void fillTriangleAffineTL(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int au, int av, int bu, int bv, int cu, int cv,
			Texture tex, boolean useColorKey,
			int as, int bs, int cs,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;
			t = as;
			as = bs;
			bs = t;

		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;
			t = bu;
			bu = cu;
			cu = t;
			t = bv;
			bv = cv;
			cv = t;
			t = bs;
			bs = cs;
			cs = t;

		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;
			t = as;
			as = bs;
			bs = t;

		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;
		final int du_start = (cu - au) / tempI;
		final int dv_start = (cv - av) / tempI;
		final int ds_start = (cs - as) / tempI;


		int dx_end = 0;
		int du_end = 0;
		int dv_end = 0;
		int ds_end = 0;

		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;
			du_end = (bu - au) / tempI;
			dv_end = (bv - av) / tempI;
			ds_end = (bs - as) / tempI;


		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;
		int u_start = au + du_start * tempI;
		int v_start = av + dv_start * tempI;
		int s_start = as + ds_start * tempI;


		x_end = bx << fp;
		int u_end = bu;
		int v_end = bv;
		int s_end = bs;


		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;
		final int du = (u_start - u_end) / tempI;
		final int dv = (v_start - v_end) / tempI;
		final int ds = (s_start - s_end) / tempI;


		x_end = x_start = ax << fp;
		u_end = u_start = au;
		v_end = v_start = av;
		s_end = s_start = as;


		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;
				int u;
				int v;
				int s;

				int dx_left, dx_right;
				int du_left;
				int dv_left;
				int ds_left;

				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;
					u_start -= du_start * tempI;
					v_start -= dv_start * tempI;
					s_start -= ds_start * tempI;


					x_end -= dx_end * tempI;
					u_end -= du_end * tempI;
					v_end -= dv_end * tempI;
					s_end -= ds_end * tempI;


					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;
					u = u_start;
					v = v_start;
					du_left = du_start;
					dv_left = dv_start;
					s = s_start;
					ds_left = ds_start;




					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;
					u = u_end;
					v = v_end;
					du_left = du_end;
					dv_left = dv_end;
					s = s_end;
					ds_left = ds_end;




					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineTL_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineTL_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineTL_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineTL_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;
			u_start = au + du_start * tempI;
			v_start = av + dv_start * tempI;
			s_start = as + ds_start * tempI;


			x_end = bx << fp;
			u_end = bu;
			v_end = bv;
			s_end = bs;


			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;
			du_end = (cu - bu) / tempI;
			dv_end = (cv - bv) / tempI;
			ds_end = (cs - bs) / tempI;


			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineTL_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];

					frameBuffer[x1] = color;
				}
			}
		}
	}

	private final static void fillTriangleAffineTL_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];

					int dst = frameBuffer[x1];
					frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
				}
			}
		}
	}

	private final static void fillTriangleAffineTL_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];

					int dst = frameBuffer[x1];
					{
						int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
					}
				}
			}
		}
	}

	private final static void fillTriangleAffineTL_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];

					int dst = frameBuffer[x1];
					{
						int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
					}
				}
			}
		}
	}

	final static void fillTriangleAffineTE(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int au, int av, int bu, int bv, int cu, int cv,
			Texture tex, boolean useColorKey,
			int shade,
			int aeu, int aev, int beu, int bev, int ceu, int cev,
			Texture envMap,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;

			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;
			t = bu;
			bu = cu;
			cu = t;
			t = bv;
			bv = cv;
			cv = t;

			t = beu;
			beu = ceu;
			ceu = t;
			t = bev;
			bev = cev;
			cev = t;
		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;

			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		int shadeOffset = (shade >> (fp - 8)) & 0xff00;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;
		final int du_start = (cu - au) / tempI;
		final int dv_start = (cv - av) / tempI;

		final int deu_start = (ceu - aeu) / tempI;
		final int dev_start = (cev - aev) / tempI;
		int dx_end = 0;
		int du_end = 0;
		int dv_end = 0;

		int deu_end = 0;
		int dev_end = 0;
		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;
			du_end = (bu - au) / tempI;
			dv_end = (bv - av) / tempI;

			deu_end = (beu - aeu) / tempI;
			dev_end = (bev - aev) / tempI;
		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;
		int u_start = au + du_start * tempI;
		int v_start = av + dv_start * tempI;

		int eu_start = aeu + deu_start * tempI;
		int ev_start = aev + dev_start * tempI;
		x_end = bx << fp;
		int u_end = bu;
		int v_end = bv;

		int eu_end = beu;
		int ev_end = bev;
		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;
		final int du = (u_start - u_end) / tempI;
		final int dv = (v_start - v_end) / tempI;

		final int deu = (eu_start - eu_end) / tempI;
		final int dev = (ev_start - ev_end) / tempI;
		x_end = x_start = ax << fp;
		u_end = u_start = au;
		v_end = v_start = av;

		eu_end = eu_start = aeu;
		ev_end = ev_start = aev;
		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;
				int u;
				int v;

				int eu;
				int ev;
				int dx_left, dx_right;
				int du_left;
				int dv_left;

				int deu_left;
				int dev_left;
				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;
					u_start -= du_start * tempI;
					v_start -= dv_start * tempI;

					eu_start -= deu_start * tempI;
					ev_start -= dev_start * tempI;
					x_end -= dx_end * tempI;
					u_end -= du_end * tempI;
					v_end -= dv_end * tempI;

					eu_end -= deu_end * tempI;
					ev_end -= dev_end * tempI;
					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;
					u = u_start;
					v = v_start;
					du_left = du_start;
					dv_left = dv_start;


					eu = eu_start;
					ev = ev_start;
					deu_left = deu_start;
					dev_left = dev_start;
					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;
					u = u_end;
					v = v_end;
					du_left = du_end;
					dv_left = dv_end;


					eu = eu_end;
					ev = ev_end;
					deu_left = deu_end;
					dev_left = dev_end;
					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineTE_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineTE_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineTE_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineTE_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								shadeOffset,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;
			u_start = au + du_start * tempI;
			v_start = av + dv_start * tempI;

			eu_start = aeu + deu_start * tempI;
			ev_start = aev + dev_start * tempI;
			x_end = bx << fp;
			u_end = bu;
			v_end = bv;

			eu_end = beu;
			ev_end = bev;
			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;
			du_end = (cu - bu) / tempI;
			dv_end = (cv - bv) / tempI;

			deu_end = (ceu - beu) / tempI;
			dev_end = (cev - bev) / tempI;
			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineTE_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;

		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);

			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;

				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					frameBuffer[x1] = color;
				}
			}
		}
	}

	private final static void fillTriangleAffineTE_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;

		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);

			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;

				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
				}
			}
		}
	}

	private final static void fillTriangleAffineTE_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;

		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);

			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;

				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					{
						int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
					}
				}
			}
		}
	}

	private final static void fillTriangleAffineTE_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int shadeOffset,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;

		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);

			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;

				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[shadeOffset | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					{
						int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
					}
				}
			}
		}
	}

	final static void fillTriangleAffineTLE(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int au, int av, int bu, int bv, int cu, int cv,
			Texture tex, boolean useColorKey,
			int as, int bs, int cs,
			int aeu, int aev, int beu, int bev, int ceu, int cev,
			Texture envMap,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;
			t = as;
			as = bs;
			bs = t;
			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;
			t = bu;
			bu = cu;
			cu = t;
			t = bv;
			bv = cv;
			cv = t;
			t = bs;
			bs = cs;
			cs = t;
			t = beu;
			beu = ceu;
			ceu = t;
			t = bev;
			bev = cev;
			cev = t;
		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;
			t = au;
			au = bu;
			bu = t;
			t = av;
			av = bv;
			bv = t;
			t = as;
			as = bs;
			bs = t;
			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;
		final int du_start = (cu - au) / tempI;
		final int dv_start = (cv - av) / tempI;
		final int ds_start = (cs - as) / tempI;
		final int deu_start = (ceu - aeu) / tempI;
		final int dev_start = (cev - aev) / tempI;
		int dx_end = 0;
		int du_end = 0;
		int dv_end = 0;
		int ds_end = 0;
		int deu_end = 0;
		int dev_end = 0;
		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;
			du_end = (bu - au) / tempI;
			dv_end = (bv - av) / tempI;
			ds_end = (bs - as) / tempI;
			deu_end = (beu - aeu) / tempI;
			dev_end = (bev - aev) / tempI;
		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;
		int u_start = au + du_start * tempI;
		int v_start = av + dv_start * tempI;
		int s_start = as + ds_start * tempI;
		int eu_start = aeu + deu_start * tempI;
		int ev_start = aev + dev_start * tempI;
		x_end = bx << fp;
		int u_end = bu;
		int v_end = bv;
		int s_end = bs;
		int eu_end = beu;
		int ev_end = bev;
		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;
		final int du = (u_start - u_end) / tempI;
		final int dv = (v_start - v_end) / tempI;
		final int ds = (s_start - s_end) / tempI;
		final int deu = (eu_start - eu_end) / tempI;
		final int dev = (ev_start - ev_end) / tempI;
		x_end = x_start = ax << fp;
		u_end = u_start = au;
		v_end = v_start = av;
		s_end = s_start = as;
		eu_end = eu_start = aeu;
		ev_end = ev_start = aev;
		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;
				int u;
				int v;
				int s;
				int eu;
				int ev;
				int dx_left, dx_right;
				int du_left;
				int dv_left;
				int ds_left;
				int deu_left;
				int dev_left;
				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;
					u_start -= du_start * tempI;
					v_start -= dv_start * tempI;
					s_start -= ds_start * tempI;
					eu_start -= deu_start * tempI;
					ev_start -= dev_start * tempI;
					x_end -= dx_end * tempI;
					u_end -= du_end * tempI;
					v_end -= dv_end * tempI;
					s_end -= ds_end * tempI;
					eu_end -= deu_end * tempI;
					ev_end -= dev_end * tempI;
					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;
					u = u_start;
					v = v_start;
					du_left = du_start;
					dv_left = dv_start;
					s = s_start;
					ds_left = ds_start;
					eu = eu_start;
					ev = ev_start;
					deu_left = deu_start;
					dev_left = dev_start;
					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;
					u = u_end;
					v = v_end;
					du_left = du_end;
					dv_left = dv_end;
					s = s_end;
					ds_left = ds_end;
					eu = eu_end;
					ev = ev_end;
					deu_left = deu_end;
					dev_left = dev_end;
					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineTLE_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineTLE_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineTLE_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineTLE_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								u, du_left, du, v, dv_left, dv,
								tex, useColorKey,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;
			u_start = au + du_start * tempI;
			v_start = av + dv_start * tempI;
			s_start = as + ds_start * tempI;
			eu_start = aeu + deu_start * tempI;
			ev_start = aev + dev_start * tempI;
			x_end = bx << fp;
			u_end = bu;
			v_end = bv;
			s_end = bs;
			eu_end = beu;
			ev_end = bev;
			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;
			du_end = (cu - bu) / tempI;
			dv_end = (cv - bv) / tempI;
			ds_end = (cs - bs) / tempI;
			deu_end = (ceu - beu) / tempI;
			dev_end = (cev - bev) / tempI;
			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineTLE_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					frameBuffer[x1] = color;
				}
			}
		}
	}

	private final static void fillTriangleAffineTLE_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
				}
			}
		}
	}

	private final static void fillTriangleAffineTLE_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					{
						int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
					}
				}
			}
		}
	}

	private final static void fillTriangleAffineTLE_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int u_start, int du_start, int du, int v_start, int dv_start, int dv,
			Texture tex,
			boolean useColorKey,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final byte[] texBitmap = tex.bitmapData;
		final int texWBit = tex.widthBit;
		final int texLenMask = texBitmap.length - 1;
		final int[] texPal = tex.palette;
		int colorKeyIdx = useColorKey ? 0 : 256;
		if(tex.firstColorIsBlack) colorKeyIdx = 0;
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams
		if(du_start != 0) u_start += du_start > 0 ? 1 : -1;
		if(dv_start != 0) v_start += dv_start > 0 ? 1 : -1;
		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, u_start += du_start, v_start += dv_start, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));
			int u = u_start + ((du * tempI) >> fp);
			int v = v_start + ((dv * tempI) >> fp);
			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;
				u -= du * tempI;
				v -= dv * tempI;
				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					u += du, v += dv, s += ds, eu += deu, ev += dev, x1++) {
				int color = texBitmap[(((v >> fp) << texWBit) | (u >> fp)) & texLenMask];
				if(color != colorKeyIdx) {
					color = texPal[((s >> shadeFpShift) & 0x1f00) | (color & 0xFF)];
					int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
					if(envPx != 0) {
						int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						color = 0xff000000 | (color + envPx - tmp) | tmp;
					}
					int dst = frameBuffer[x1];
					{
						int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
						frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
					}
				}
			}
		}
	}
	//Color triangles
	final static void fillTriangleAffineC(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int polyColor,
			int shade,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;



		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;



		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;



		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		shade >>= fp;
		int colorRGB = 0xff000000;
		colorRGB |= (((polyColor & 0xff0000) * (shade + 1)) >> 5) & 0xff0000;
		colorRGB |= (((polyColor & 0x00ff00) * (shade + 1)) >> 5) & 0x00ff00;
		colorRGB |= (((polyColor & 0x0000ff) * (shade + 1)) >> 5) & 0x0000ff;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;





		int dx_end = 0;



		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;





		}
		int x_start, x_end;
		x_end = x_start = ax << fp;





		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;



				int dx_left, dx_right;



				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;





					x_end -= dx_end * tempI;





					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;










					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;










					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineC_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineC_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineC_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineC_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;





			x_end = bx << fp;





			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;





			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineC_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int x_start, int dx_start, int x_end, int dx_end) {
		//Corrected rounding to avoid texture seams





		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));





			if(x1 < clipX1) {
				tempI = x1 - clipX1;





				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					x1++) {
				int color = colorRGB;

				frameBuffer[x1] = color;
			}
		}
	}

	private final static void fillTriangleAffineC_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int x_start, int dx_start, int x_end, int dx_end) {
		//Corrected rounding to avoid texture seams





		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));





			if(x1 < clipX1) {
				tempI = x1 - clipX1;





				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					x1++) {
				int color = colorRGB;

				int dst = frameBuffer[x1];
				frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
			}
		}
	}

	private final static void fillTriangleAffineC_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int x_start, int dx_start, int x_end, int dx_end) {
		//Corrected rounding to avoid texture seams





		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));





			if(x1 < clipX1) {
				tempI = x1 - clipX1;





				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					x1++) {
				int color = colorRGB;

				int dst = frameBuffer[x1];
				{
					int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
				}
			}
		}
	}

	private final static void fillTriangleAffineC_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int x_start, int dx_start, int x_end, int dx_end) {
		//Corrected rounding to avoid texture seams





		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));





			if(x1 < clipX1) {
				tempI = x1 - clipX1;





				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					x1++) {
				int color = colorRGB;

				int dst = frameBuffer[x1];
				{
					int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
				}
			}
		}
	}

	final static void fillTriangleAffineCL(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int polyColor,
			int as, int bs, int cs,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;

			t = as;
			as = bs;
			bs = t;

		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;

			t = bs;
			bs = cs;
			cs = t;

		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;

			t = as;
			as = bs;
			bs = t;

		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		final int colorRB = polyColor & 0x00ff00ff;
		final int colorG = polyColor & 0x0000ff00;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;


		final int ds_start = (cs - as) / tempI;


		int dx_end = 0;

		int ds_end = 0;

		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;


			ds_end = (bs - as) / tempI;


		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;


		int s_start = as + ds_start * tempI;


		x_end = bx << fp;


		int s_end = bs;


		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;


		final int ds = (s_start - s_end) / tempI;


		x_end = x_start = ax << fp;


		s_end = s_start = as;


		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;

				int s;

				int dx_left, dx_right;

				int ds_left;

				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;


					s_start -= ds_start * tempI;


					x_end -= dx_end * tempI;


					s_end -= ds_end * tempI;


					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;




					s = s_start;
					ds_left = ds_start;




					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;




					s = s_end;
					ds_left = ds_end;




					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineCL_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineCL_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineCL_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineCL_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;


			s_start = as + ds_start * tempI;


			x_end = bx << fp;


			s_end = bs;


			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;


			ds_end = (cs - bs) / tempI;


			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineCL_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);

				frameBuffer[x1] = color;
			}
		}
	}

	private final static void fillTriangleAffineCL_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);

				int dst = frameBuffer[x1];
				frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
			}
		}
	}

	private final static void fillTriangleAffineCL_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);

				int dst = frameBuffer[x1];
				{
					int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
				}
			}
		}
	}

	private final static void fillTriangleAffineCL_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;


		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);


			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;


				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);

				int dst = frameBuffer[x1];
				{
					int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
				}
			}
		}
	}

	final static void fillTriangleAffineCE(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int polyColor,
			int shade,
			int aeu, int aev, int beu, int bev, int ceu, int cev,
			Texture envMap,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;


			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;


			t = beu;
			beu = ceu;
			ceu = t;
			t = bev;
			bev = cev;
			cev = t;
		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;


			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		shade >>= fp;
		int colorRGB = 0xff000000;
		colorRGB |= (((polyColor & 0xff0000) * (shade + 1)) >> 5) & 0xff0000;
		colorRGB |= (((polyColor & 0x00ff00) * (shade + 1)) >> 5) & 0x00ff00;
		colorRGB |= (((polyColor & 0x0000ff) * (shade + 1)) >> 5) & 0x0000ff;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;



		final int deu_start = (ceu - aeu) / tempI;
		final int dev_start = (cev - aev) / tempI;
		int dx_end = 0;


		int deu_end = 0;
		int dev_end = 0;
		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;



			deu_end = (beu - aeu) / tempI;
			dev_end = (bev - aev) / tempI;
		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;



		int eu_start = aeu + deu_start * tempI;
		int ev_start = aev + dev_start * tempI;
		x_end = bx << fp;



		int eu_end = beu;
		int ev_end = bev;
		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;



		final int deu = (eu_start - eu_end) / tempI;
		final int dev = (ev_start - ev_end) / tempI;
		x_end = x_start = ax << fp;



		eu_end = eu_start = aeu;
		ev_end = ev_start = aev;
		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;


				int eu;
				int ev;
				int dx_left, dx_right;


				int deu_left;
				int dev_left;
				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;



					eu_start -= deu_start * tempI;
					ev_start -= dev_start * tempI;
					x_end -= dx_end * tempI;



					eu_end -= deu_end * tempI;
					ev_end -= dev_end * tempI;
					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;






					eu = eu_start;
					ev = ev_start;
					deu_left = deu_start;
					dev_left = dev_start;
					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;






					eu = eu_end;
					ev = ev_end;
					deu_left = deu_end;
					dev_left = dev_end;
					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineCE_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineCE_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineCE_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineCE_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRGB,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;



			eu_start = aeu + deu_start * tempI;
			ev_start = aev + dev_start * tempI;
			x_end = bx << fp;



			eu_end = beu;
			ev_end = bev;
			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;



			deu_end = (ceu - beu) / tempI;
			dev_end = (cev - bev) / tempI;
			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineCE_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams



		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));



			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;



				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					eu += deu, ev += dev, x1++) {
				int color = colorRGB;
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				frameBuffer[x1] = color;
			}
		}
	}

	private final static void fillTriangleAffineCE_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams



		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));



			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;



				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					eu += deu, ev += dev, x1++) {
				int color = colorRGB;
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
			}
		}
	}

	private final static void fillTriangleAffineCE_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams



		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));



			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;



				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					eu += deu, ev += dev, x1++) {
				int color = colorRGB;
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				{
					int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
				}
			}
		}
	}

	private final static void fillTriangleAffineCE_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRGB,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		//Corrected rounding to avoid texture seams



		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));



			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;



				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					eu += deu, ev += dev, x1++) {
				int color = colorRGB;
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				{
					int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
				}
			}
		}
	}

	final static void fillTriangleAffineCLE(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipY1, int clipX2, int clipY2,
			int ax, int ay,
			int bx, int by,
			int cx, int cy,
			int polyColor,
			int as, int bs, int cs,
			int aeu, int aev, int beu, int bev, int ceu, int cev,
			Texture envMap,
			int blendMode) {
		//Sorting vertices
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;

			t = as;
			as = bs;
			bs = t;
			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(by > cy) {
			int t = bx;
			bx = cx;
			cx = t;
			t = by;
			by = cy;
			cy = t;

			t = bs;
			bs = cs;
			cs = t;
			t = beu;
			beu = ceu;
			ceu = t;
			t = bev;
			bev = cev;
			cev = t;
		}
		if(ay > by) {
			int t = ax;
			ax = bx;
			bx = t;
			t = ay;
			ay = by;
			by = t;

			t = as;
			as = bs;
			bs = t;
			t = aeu;
			aeu = beu;
			beu = t;
			t = aev;
			aev = bev;
			bev = t;
		}
		if(cy == ay) return;
		if(cy <= clipY1 || ay >= clipY2) return;
		final int colorRB = polyColor & 0x00ff00ff;
		final int colorG = polyColor & 0x0000ff00;
		int tempI = cy - ay;
		final int dx_start = ((cx - ax) << fp) / tempI;


		final int ds_start = (cs - as) / tempI;
		final int deu_start = (ceu - aeu) / tempI;
		final int dev_start = (cev - aev) / tempI;
		int dx_end = 0;

		int ds_end = 0;
		int deu_end = 0;
		int dev_end = 0;
		if(by != ay) {
			tempI = by - ay;
			dx_end = ((bx - ax) << fp) / tempI;


			ds_end = (bs - as) / tempI;
			deu_end = (beu - aeu) / tempI;
			dev_end = (bev - aev) / tempI;
		}
		int x_start, x_end;
		//Calculate scanline derivatives
		tempI = by - ay;
		x_start = (ax << fp) + dx_start * tempI;


		int s_start = as + ds_start * tempI;
		int eu_start = aeu + deu_start * tempI;
		int ev_start = aev + dev_start * tempI;
		x_end = bx << fp;


		int s_end = bs;
		int eu_end = beu;
		int ev_end = bev;
		tempI = (x_start - x_end) >> fp;
		//Symmetric ceil
		if(tempI < 0) tempI--;
		else tempI++;


		final int ds = (s_start - s_end) / tempI;
		final int deu = (eu_start - eu_end) / tempI;
		final int dev = (ev_start - ev_end) / tempI;
		x_end = x_start = ax << fp;


		s_end = s_start = as;
		eu_end = eu_start = aeu;
		ev_end = ev_start = aev;
		int y_start = ay;
		int y_end = by;
		for(int i = 0; i < 2; i++) {
			if(y_end > clipY1) {
				int x1, x2;

				int s;
				int eu;
				int ev;
				int dx_left, dx_right;

				int ds_left;
				int deu_left;
				int dev_left;
				if(y_start < clipY1) {
					tempI = y_start - clipY1;
					x_start -= dx_start * tempI;


					s_start -= ds_start * tempI;
					eu_start -= deu_start * tempI;
					ev_start -= dev_start * tempI;
					x_end -= dx_end * tempI;


					s_end -= ds_end * tempI;
					eu_end -= deu_end * tempI;
					ev_end -= dev_end * tempI;
					y_start = clipY1;
				}
				if(x_start < x_end || (x_start == x_end && dx_start < dx_end)) {
					x1 = x_start;
					dx_left = dx_start;




					s = s_start;
					ds_left = ds_start;
					eu = eu_start;
					ev = ev_start;
					deu_left = deu_start;
					dev_left = dev_start;
					x2 = x_end;
					dx_right = dx_end;
				} else {
					x1 = x_end;
					dx_left = dx_end;




					s = s_end;
					ds_left = ds_end;
					eu = eu_end;
					ev = ev_end;
					deu_left = deu_end;
					dev_left = dev_end;
					x2 = x_start;
					dx_right = dx_start;
				}
				final int y_end_draw = y_end < clipY2 ? y_end : clipY2;
				switch(blendMode) {
					default:
						fillTriangleAffineCLE_replace(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 1:
						fillTriangleAffineCLE_half(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 2:
						fillTriangleAffineCLE_add(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
					case 3:
						fillTriangleAffineCLE_sub(
								frameBuffer, fbWidth,
								clipX1, clipX2,
								y_start, y_end_draw,
								colorRB, colorG,
								s, ds_left, ds,
								eu, deu_left, deu, ev, dev_left, dev,
								envMap,
								x1, dx_left, x2, dx_right);
						break;
				}
			}
			if(i == 1) return;
			if(by >= clipY2) return;
			if(cy == by) return;
			tempI = by - ay;
			x_start = (ax << fp) + dx_start * tempI;


			s_start = as + ds_start * tempI;
			eu_start = aeu + deu_start * tempI;
			ev_start = aev + dev_start * tempI;
			x_end = bx << fp;


			s_end = bs;
			eu_end = beu;
			ev_end = bev;
			tempI = cy - by;
			dx_end = ((cx - bx) << fp) / tempI;


			ds_end = (cs - bs) / tempI;
			deu_end = (ceu - beu) / tempI;
			dev_end = (cev - bev) / tempI;
			y_start = by;
			y_end = cy;
		}
	}

	private final static void fillTriangleAffineCLE_replace(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, eu += deu, ev += dev, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				frameBuffer[x1] = color;
			}
		}
	}

	private final static void fillTriangleAffineCLE_half(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, eu += deu, ev += dev, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				frameBuffer[x1] = 0xff000000 | ((dst & color) + (((dst ^ color) >> 1) & 0x7F7F7F));
			}
		}
	}

	private final static void fillTriangleAffineCLE_add(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, eu += deu, ev += dev, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				{
					int tmp = (((dst + color - ((dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | (dst + color - tmp) | tmp;
				}
			}
		}
	}

	private final static void fillTriangleAffineCLE_sub(
			int[] frameBuffer, int fbWidth,
			int clipX1, int clipX2,
			int y_start, int y_end,
			int colorRB, int colorG,
			int s_start, int ds_start, int ds,
			int eu_start, int deu_start, int deu, int ev_start, int dev_start, int dev,
			Texture envMap,
			int x_start, int dx_start, int x_end, int dx_end) {
		final int[] envTexBitmap = envMap.envmapData;
		final int envTexWBit = envMap.widthBit;
		final int envTexLenMask = envTexBitmap.length - 1;
		final int rbMask = 0x00ff00ff << 5;
		final int gMask = 0x0000ff00 << 5;
		//Corrected rounding to avoid texture seams


		if(ds_start != 0) s_start += ds_start > 0 ? 1 : -1;
		if(deu_start != 0) eu_start += deu_start > 0 ? 1 : -1;
		if(dev_start != 0) ev_start += dev_start > 0 ? 1 : -1;
		y_start *= fbWidth;
		y_end *= fbWidth;
		for(; y_start < y_end;
				y_start += fbWidth, s_start += ds_start, eu_start += deu_start, ev_start += dev_start, x_start += dx_start, x_end += dx_end) {
			int x1 = x_start >> fp;
			int x2 = x_end >> fp;
			//Subpixel precision, ceil rounding
			int tempI = FP - (x_start & (FP - 1));


			int s = s_start + ((ds * tempI) >> fp);
			int eu = eu_start + ((deu * tempI) >> fp);
			int ev = ev_start + ((dev * tempI) >> fp);
			if(x1 < clipX1) {
				tempI = x1 - clipX1;


				s -= ds * tempI;
				eu -= deu * tempI;
				ev -= dev * tempI;
				x1 = clipX1;
			}
			if(x2 > clipX2) x2 = clipX2;
			x1 += y_start;
			x2 += y_start;
			for(; x1 < x2;
					s += ds, eu += deu, ev += dev, x1++) {
				int shadeLevel = s >> fp;
				int color = 0xff000000 | ((((colorRB * shadeLevel) & rbMask) | ((colorG * shadeLevel) & gMask)) >> 5);
				int envPx = envTexBitmap[(((ev >> fp) << envTexWBit) | (eu >> fp)) & envTexLenMask];
				if(envPx != 0) {
					int tmp = (((color + envPx - ((color ^ envPx) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					color = 0xff000000 | (color + envPx - tmp) | tmp;
				}
				int dst = frameBuffer[x1];
				{
					int tmp = (((~dst + color - ((~dst ^ color) & 0x01010101)) >> 8) & 0x010101) * 0xff;
					frameBuffer[x1] = 0xff000000 | ((dst | tmp) - (color | tmp));
				}
			}
		}
	}
}
