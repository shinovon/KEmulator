package javax.microedition.m3g;

import java.util.Vector;

public abstract class Object3D implements Cloneable {
	int userID = 0;
	Object userObject = null;
	Vector animationTracks = new Vector();
	Vector references = new Vector();

	public final int animate(int time) {
		int var2 = this.animation(time);

		for (Object reference : this.references) {
			var2 = Math.min(var2, ((Object3D) reference).animate(time));
		}

		return var2;
	}

	protected final int animation(int time) {
		int var2 = Integer.MAX_VALUE;
		int var3 = 0;

		while (var3 < this.animationTracks.size()) {
			AnimationTrack var4;
			int var5 = (var4 = (AnimationTrack) this.animationTracks.elementAt(var3)).getTargetProperty();
			float[] var6 = new float[var4.getKeyframeSequence().getComponentCount()];
			float[] var7 = new float[2];
			float var8 = 0.0F;

			do {
				var4.getContribution(time, var6, var7);
				var8 += var7[0];
				var2 = Math.min(var2, (int) var7[1]);
				++var3;
			} while (var3 != this.animationTracks.size() && (var4 = (AnimationTrack) this.animationTracks.elementAt(var3)).getTargetProperty() == var5);

			if (var8 > 0.0F) {
				this.updateProperty(var5, var6);
			}
		}

		return var2;
	}

	protected void updateProperty(int var1, float[] var2) {
		throw new Error("Invalid animation target property!");
	}

	public final Object3D duplicate() {
		Object3D var1 = this.duplicateObject();
		if (this instanceof Node) {
			Node var2;
			(var2 = (Node) this).updateAlignReferences();
			var2.clearAlignReferences();
		}

		return var1;
	}

	protected Object3D duplicateObject() {
		Object3D var1 = null;

		try {
			(var1 = (Object3D) this.clone()).references = (Vector) this.references.clone();
			var1.animationTracks = (Vector) this.animationTracks.clone();
		} catch (Exception ignored) {}

		return var1;
	}

	public Object3D find(int var1) {
		if (this.userID == var1) {
			return this;
		} else {
			Object3D var2 = null;

			for (int var3 = 0; var3 < this.references.size() && (var2 = ((Object3D) this.references.get(var3)).find(var1)) == null; ++var3) {
				;
			}

			return var2;
		}
	}

	public int getReferences(Object3D[] var1) {
		if (var1 != null && var1.length < this.getReferences((Object3D[]) null)) {
			throw new IllegalArgumentException();
		} else {
			if (var1 != null) {
				for (int var2 = 0; var2 < this.references.size(); ++var2) {
					var1[var2] = (Object3D) this.references.get(var2);
				}
			}

			return this.references.size();
		}
	}

	public void setUserID(int var1) {
		this.userID = var1;
	}

	public int getUserID() {
		return this.userID;
	}

	public void setUserObject(Object var1) {
		this.userObject = var1;
	}

	public Object getUserObject() {
		return this.userObject;
	}

	public void addAnimationTrack(AnimationTrack var1) {
		if (var1 == null) {
			throw new NullPointerException();
		} else if (!this.animationTracks.contains(var1) && var1.checkCompatible(this)) {
			int var2 = var1.getKeyframeSequence().getComponentCount();
			int var3 = var1.getTargetProperty();

			for (int var4 = 0; var4 < this.animationTracks.size(); ++var4) {
				AnimationTrack var5;
				if ((var5 = (AnimationTrack) this.animationTracks.get(var4)).getTargetProperty() > var3) {
					this.animationTracks.insertElementAt(var1, var4);
					this.addReference(var1);
					return;
				}

				if (var5.getTargetProperty() == var3 && var5.getKeyframeSequence().getComponentCount() != var2) {
					throw new IllegalArgumentException();
				}
			}

			this.animationTracks.addElement(var1);
			this.addReference(var1);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public AnimationTrack getAnimationTrack(int var1) {
		if (var1 >= 0 && var1 < this.animationTracks.size()) {
			return (AnimationTrack) this.animationTracks.elementAt(var1);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void removeAnimationTrack(AnimationTrack var1) {
		if (this.animationTracks.contains(var1)) {
			this.animationTracks.remove(var1);
			this.removeReference(var1);
		}

	}

	public int getAnimationTrackCount() {
		return this.animationTracks.size();
	}

	protected void addReference(Object3D var1) {
		if (var1 != null) {
			this.references.add(var1);
		}
	}

	protected void removeReference(Object3D var1) {
		if (var1 != null) {
			this.references.remove(var1);
		}
	}
}
