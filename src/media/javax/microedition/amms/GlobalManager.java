package javax.microedition.amms;

import javax.microedition.amms.control.audio3d.CommitControl;
import javax.microedition.amms.control.audio3d.DopplerControl;
import javax.microedition.amms.control.audio3d.LocationControl;
import javax.microedition.amms.control.audio3d.OrientationControl;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;

public class GlobalManager {
	private static final String UNSUPPORTED = "Unsupported";
	private static final String defaultPackage = "javax.microedition.media.control";
	private static final String audioEffectPackage = "javax.microedition.amms.control.audioeffect";
	private static Spectator spectator;

	public static EffectModule createEffectModule() throws MediaException {
		throw new MediaException("Unsupported");
	}

	public static MediaProcessor createMediaProcessor(String inputType) throws MediaException {
		throw new MediaException("Unsupported");
	}

	public static SoundSource3D createSoundSource3D() throws MediaException {
		throw new MediaException("Unsupported");
	}

	public static Control getControl(String s) {
		String pkgName = "javax.microedition.media.control";
		if (s == null) {
			throw new IllegalArgumentException();
		}
		if (s.startsWith("javax.microedition.media.control")) {
			s = s.substring("javax.microedition.media.control".length() + 1);
		} else if (s.startsWith("javax.microedition.amms.control.audioeffect")) {
			s = s.substring("javax.microedition.amms.control.audioeffect".length() + 1);
			pkgName = "javax.microedition.amms.control.audioeffect";
		}
		if (s.contains("CommitControl")) {
			return new CommitControl() {

				@Override
				public void commit() {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean isDeferred() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setDeferred(boolean paramBoolean) {
					// TODO Auto-generated method stub

				}

			};
		}
		return null;
	}

	public static Control[] getControls() {
		return new Control[0];
	}

	public static Spectator getSpectator() throws MediaException {
		if (spectator == null) spectator = new Spectator(new Controllable() {

			@Override
			public Control getControl(String s) {
				if (s.contains("LocationControl")) {
					return new LocationControl() {

						@Override
						public int[] getCartesian() {
							// TODO Auto-generated method stub
							return new int[3];
						}

						@Override
						public void setCartesian(int paramInt1, int paramInt2, int paramInt3) {
							// TODO Auto-generated method stub

						}

						@Override
						public void setSpherical(int paramInt1, int paramInt2, int paramInt3) {
							// TODO Auto-generated method stub

						}

					};
				}
				if (s.contains("OrientationControl")) {
					return new OrientationControl() {

						@Override
						public int[] getOrientationVectors() {
							// TODO Auto-generated method stub
							return new int[3];
						}

						@Override
						public void setOrientation(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
								throws IllegalArgumentException {
							// TODO Auto-generated method stub

						}

						@Override
						public void setOrientation(int paramInt1, int paramInt2, int paramInt3) {
							// TODO Auto-generated method stub

						}

					};
				}
				if (s.contains("DopplerControl")) {
					return new DopplerControl() {

						@Override
						public int[] getVelocityCartesian() {
							// TODO Auto-generated method stub
							return new int[3];
						}

						@Override
						public boolean isEnabled() {
							// TODO Auto-generated method stub
							return false;
						}

						@Override
						public void setEnabled(boolean paramBoolean) {
							// TODO Auto-generated method stub

						}

						@Override
						public void setVelocityCartesian(int paramInt1, int paramInt2, int paramInt3) {
							// TODO Auto-generated method stub

						}

						@Override
						public void setVelocitySpherical(int paramInt1, int paramInt2, int paramInt3) {
							// TODO Auto-generated method stub

						}

					};
				}
				return null;
			}

			@Override
			public Control[] getControls() {
				return new Control[0];
			}

		});
		return spectator;
	}

	public static String[] getSupportedMediaProcessorInputTypes() {
		return new String[0];
	}

	public static String[] getSupportedSoundSource3DPlayerTypes() {
		return new String[0];
	}
}
