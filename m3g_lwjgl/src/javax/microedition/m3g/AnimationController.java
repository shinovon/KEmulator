package javax.microedition.m3g;

public class AnimationController extends Object3D {
	private int activeIntervalStart = 0;
	private int activeIntervalEnd = 0;

	private int refWorldTime = 0;
	private float refSequenceTime = 0.0F;

	private float weight = 1.0F, speed = 1.0F;

	public void setActiveInterval(int start, int end) {
		if (start > end) {
			throw new IllegalArgumentException();
		} else {
			activeIntervalStart = start;
			activeIntervalEnd = end;
		}
	}

	public int getActiveIntervalStart() {
		return activeIntervalStart;
	}

	public int getActiveIntervalEnd() {
		return activeIntervalEnd;
	}

	public void setSpeed(float speed, int worldTime) {
		refSequenceTime = getPosition(worldTime);
		refWorldTime = worldTime;
		this.speed = speed;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setPosition(float sequenceTime, int worldTime) {
		refSequenceTime = sequenceTime;
		refWorldTime = worldTime;
	}

	public float getPosition(int worldTime) {
		return refSequenceTime + speed * (float) (worldTime - refWorldTime);
	}

	public int getRefWorldTime() {
		return refWorldTime;
	}

	public void setWeight(float weight) {
		if (weight < 0.0F) {
			throw new IllegalArgumentException();
		} else {
			this.weight = weight;
		}
	}

	public float getWeight() {
		return this.weight;
	}

	protected boolean isActive(int worldTime) {
		return activeIntervalStart == activeIntervalEnd ? true : worldTime >= activeIntervalStart && worldTime < activeIntervalEnd;
	}

	protected int timeToActivation(int worldTime) {
		return worldTime < activeIntervalStart ? activeIntervalStart - worldTime : (worldTime >= activeIntervalEnd ? Integer.MAX_VALUE : 0);
	}

	protected int timeToDeactivation(int worldTime) {
		return worldTime < activeIntervalEnd ? activeIntervalEnd - worldTime : Integer.MAX_VALUE;
	}
}
