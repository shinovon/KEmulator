package javax.microedition.m3g;

public class World extends Group {
	private Background activeBg = null;
	private Camera activeCam = null;

	public void setBackground(Background bg) {
		removeReference(activeBg);
		activeBg = bg;
		addReference(activeBg);
	}

	public Background getBackground() {
		return activeBg;
	}

	public void setActiveCamera(Camera cam) {
		if (cam == null) {
			throw new NullPointerException();
		} else {
			removeReference(activeCam);
			activeCam = cam;
			addReference(activeCam);
		}
	}

	public Camera getActiveCamera() {
		return activeCam;
	}

	protected void updateAlignReferences() {
		super.updateAlignReferences();
		if (activeCam != null && activeCam.m_duplicatedNode != null && activeCam.m_duplicatedNode.isDescendantOf(super.m_duplicatedNode)) {
			((World) super.m_duplicatedNode).activeCam = (Camera) activeCam.m_duplicatedNode;
		}

	}
}
