package javax.microedition.m3g;

import java.util.Vector;

public abstract class Object3D implements Cloneable {
	int userID = 0;
	Object userObject = null;
	Vector animationTracks = new Vector();
	Vector references = new Vector();

	public final int animate(int time) {
		int validity = this.animation(time);

		for (Object reference : this.references) {
			validity = Math.min(validity, ((Object3D) reference).animate(time));
		}

		return validity;
	}

	protected final int animation(int time) {
		int validity = Integer.MAX_VALUE;
		int trackIndex = 0;

		while (trackIndex < this.animationTracks.size()) {
			AnimationTrack track;
			int property = (track = (AnimationTrack) this.animationTracks.elementAt(trackIndex)).getTargetProperty();
			float[] value = new float[track.getKeyframeSequence().getComponentCount()];
			float[] contribution = new float[2];
			float weightSum = 0.0F;

			do {
				track.getContribution(time, value, contribution);
				weightSum += contribution[0];
				validity = Math.min(validity, (int) contribution[1]);
				++trackIndex;
			} while (trackIndex != this.animationTracks.size() && (track = (AnimationTrack) this.animationTracks.elementAt(trackIndex)).getTargetProperty() == property);

			if (weightSum > 0.0F) {
				this.updateProperty(property, value);
			}
		}

		return validity;
	}

	protected void updateProperty(int property, float[] value) {
		throw new Error("Invalid animation target property!");
	}

	public final Object3D duplicate() {
		Object3D copy = this.duplicateObject();
		if (this instanceof Node) {
			Node node;
			(node = (Node) this).updateAlignReferences();
			node.clearAlignReferences();
		}

		return copy;
	}

	protected Object3D duplicateObject() {
		Object3D copy = null;

		try {
			(copy = (Object3D) this.clone()).references = (Vector) this.references.clone();
			copy.animationTracks = (Vector) this.animationTracks.clone();
		} catch (Exception ignored) {}

		return copy;
	}

	public Object3D find(int userID) {
		if (this.userID == userID) {
			return this;
		} else {
			Object3D found = null;

			for (int i = 0; i < this.references.size() && (found = ((Object3D) this.references.get(i)).find(userID)) == null; ++i) {
				;
			}

			return found;
		}
	}

	public int getReferences(Object3D[] references) {
		if (references != null && references.length < this.getReferences((Object3D[]) null)) {
			throw new IllegalArgumentException();
		} else {
			if (references != null) {
				for (int i = 0; i < this.references.size(); ++i) {
					references[i] = (Object3D) this.references.get(i);
				}
			}

			return this.references.size();
		}
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getUserID() {
		return this.userID;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public Object getUserObject() {
		return this.userObject;
	}

	public void addAnimationTrack(AnimationTrack track) {
		if (track == null) {
			throw new NullPointerException();
		} else if (!this.animationTracks.contains(track) && track.checkCompatible(this)) {
			int componentCount = track.getKeyframeSequence().getComponentCount();
			int property = track.getTargetProperty();

			for (int i = 0; i < this.animationTracks.size(); ++i) {
				AnimationTrack existing;
				if ((existing = (AnimationTrack) this.animationTracks.get(i)).getTargetProperty() > property) {
					this.animationTracks.insertElementAt(track, i);
					this.addReference(track);
					return;
				}

				if (existing.getTargetProperty() == property && existing.getKeyframeSequence().getComponentCount() != componentCount) {
					throw new IllegalArgumentException();
				}
			}

			this.animationTracks.addElement(track);
			this.addReference(track);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public AnimationTrack getAnimationTrack(int index) {
		if (index >= 0 && index < this.animationTracks.size()) {
			return (AnimationTrack) this.animationTracks.elementAt(index);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public void removeAnimationTrack(AnimationTrack track) {
		if (this.animationTracks.contains(track)) {
			this.animationTracks.remove(track);
			this.removeReference(track);
		}

	}

	public int getAnimationTrackCount() {
		return this.animationTracks.size();
	}

	protected void addReference(Object3D reference) {
		if (reference != null) {
			this.references.add(reference);
		}
	}

	protected void removeReference(Object3D reference) {
		if (reference != null) {
			this.references.remove(reference);
		}
	}
}
