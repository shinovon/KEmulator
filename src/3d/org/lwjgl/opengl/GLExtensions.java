package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;

import java.nio.ShortBuffer;

public class GLExtensions {
	public static void glNormalPointer(int stride, ShortBuffer pointer) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glNormalPointer;
		BufferChecks.checkFunctionAddress(function_pointer);
		GLChecks.ensureArrayVBOdisabled(caps);
		BufferChecks.checkDirect(pointer);
		if (LWJGLUtil.CHECKS) {
			StateTracker.getReferences(caps).GL11_glNormalPointer_pointer = pointer;
		}
		GL11.nglNormalPointer(GL11.GL_SHORT, stride, MemoryUtil.getAddress(pointer), function_pointer);
	}
}
