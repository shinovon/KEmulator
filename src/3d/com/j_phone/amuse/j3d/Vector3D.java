/*
 * Copyright 2022-2023 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.j_phone.amuse.j3d;

public class Vector3D extends com.jblend.graphics.j3d.Vector3D {
	public Vector3D() {}

	public Vector3D(int x, int y, int z) {
		super(x, y, z);
	}

	public int innerProduct(Vector3D v) {
		if (v == null) {
			throw new NullPointerException();
		}
		return x * v.x + y * v.y + z * v.z;
	}

	/** @noinspection unused*/
	public static int innerProduct(Vector3D v1, Vector3D v2) {
		if (v1 == null) {
			throw new NullPointerException();
		}
		return v1.innerProduct(v2);
	}

	/** @noinspection unused*/
	public void outerProduct(Vector3D v) {
		if (v == null) {
			throw new NullPointerException();
		}
		int x = this.x;
		int y = this.y;
		int z = this.z;
		this.x = y * v.z - z * v.y;
		this.y = z * v.x - x * v.z;
		this.z = x * v.y - y * v.x;
	}

	public static Vector3D outerProduct(Vector3D v1, Vector3D v2) {
		if (v1 == null || v2 == null) {
			throw new NullPointerException();
		}
		int x = v1.y * v2.z - v1.z * v2.y;
		int y = v1.z * v2.x - v1.x * v2.z;
		int z = v1.x * v2.y - v1.y * v2.x;
		return new Vector3D(x, y, z);
	}
}