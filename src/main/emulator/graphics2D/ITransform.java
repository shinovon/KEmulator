package emulator.graphics2D;

public interface ITransform {
	ITransform newTransform(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5);

	void transform(final ITransform p0);

	void transform(final float[] p0, final int p1, final float[] p2, final int p3, final int p4);
}
