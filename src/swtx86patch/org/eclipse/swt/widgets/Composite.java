//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.internal.win32.BP_PAINTPARAMS;
import org.eclipse.swt.internal.win32.LRESULT;
import org.eclipse.swt.internal.win32.MSG;
import org.eclipse.swt.internal.win32.NMHDR;
import org.eclipse.swt.internal.win32.NMTTDISPINFO;
import org.eclipse.swt.internal.win32.NMTTDISPINFOA;
import org.eclipse.swt.internal.win32.NMTTDISPINFOW;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.PAINTSTRUCT;
import org.eclipse.swt.internal.win32.POINT;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.internal.win32.WINDOWPOS;

public class Composite extends Scrollable {
    Layout layout;
    WINDOWPOS[] lpwp;
    Control[] tabList;
    int layoutCount;
    int backgroundMode;
    static final int TOOLTIP_LIMIT = 4096;

    Composite() {
    }

    public Composite(Composite var1, int var2) {
        super(var1, var2);
    }

    Control[] _getChildren() {
        int var1 = 0;
        int var2 = OS.GetWindow(this.handle, 5);
        if (var2 == 0) {
            return new Control[0];
        } else {
            while(var2 != 0) {
                ++var1;
                var2 = OS.GetWindow(var2, 2);
            }

            Control[] var3 = new Control[var1];
            int var4 = 0;

            for(var2 = OS.GetWindow(this.handle, 5); var2 != 0; var2 = OS.GetWindow(var2, 2)) {
                Control var5 = this.display.getControl(var2);
                if (var5 != null && var5 != this) {
                    var3[var4++] = var5;
                }
            }

            if (var1 == var4) {
                return var3;
            } else {
                Control[] var6 = new Control[var4];
                System.arraycopy(var3, 0, var6, 0, var4);
                return var6;
            }
        }
    }

    Control[] _getTabList() {
        if (this.tabList == null) {
            return this.tabList;
        } else {
            int var1 = 0;

            for(int var2 = 0; var2 < this.tabList.length; ++var2) {
                if (!this.tabList[var2].isDisposed()) {
                    ++var1;
                }
            }

            if (var1 == this.tabList.length) {
                return this.tabList;
            } else {
                Control[] var5 = new Control[var1];
                int var3 = 0;

                for(int var4 = 0; var4 < this.tabList.length; ++var4) {
                    if (!this.tabList[var4].isDisposed()) {
                        var5[var3++] = this.tabList[var4];
                    }
                }

                this.tabList = var5;
                return this.tabList;
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public void changed(Control[] var1) {
        this.layout(var1, 4);
    }

    void checkBuffered() {
        if (OS.IsWinCE || (this.state & 2) == 0) {
            super.checkBuffered();
        }

    }

    void checkComposited() {
        if ((this.state & 2) != 0 && (this.style & 1073741824) != 0) {
            int var1 = this.parent.handle;
            int var2 = OS.GetWindowLong(var1, -20);
            var2 |= 33554432;
            OS.SetWindowLong(var1, -20, var2);
        }

    }

    protected void checkSubclass() {
    }

    Widget[] computeTabList() {
        Widget[] var1 = super.computeTabList();
        if (var1.length == 0) {
            return var1;
        } else {
            Control[] var2 = this.tabList != null ? this._getTabList() : this._getChildren();

            for(int var3 = 0; var3 < var2.length; ++var3) {
                Control var4 = var2[var3];
                Widget[] var5 = var4.computeTabList();
                if (var5.length != 0) {
                    Widget[] var6 = new Widget[var1.length + var5.length];
                    System.arraycopy(var1, 0, var6, 0, var1.length);
                    System.arraycopy(var5, 0, var6, var1.length, var5.length);
                    var1 = var6;
                }
            }

            return var1;
        }
    }

    Point computeSizeInPixels(int var1, int var2, boolean var3) {
        this.display.runSkin();
        Point var4;
        if (this.layout != null) {
            if (var1 != -1 && var2 != -1) {
                var4 = new Point(var1, var2);
            } else {
                var3 |= (this.state & 64) != 0;
                this.state &= -65;
                var4 = DPIUtil.autoScaleUp(this.layout.computeSize(this, DPIUtil.autoScaleDown(var1), DPIUtil.autoScaleDown(var2), var3));
            }
        } else {
            var4 = this.minimumSize(var1, var2, var3);
            if (var4.x == 0) {
                var4.x = 64;
            }

            if (var4.y == 0) {
                var4.y = 64;
            }
        }

        if (var1 != -1) {
            var4.x = var1;
        }

        if (var2 != -1) {
            var4.y = var2;
        }

        Rectangle var5 = DPIUtil.autoScaleUp(this.computeTrim(0, 0, DPIUtil.autoScaleDown(var4.x), DPIUtil.autoScaleDown(var4.y)));
        return new Point(var5.width, var5.height);
    }

    void copyArea(GC var1, int var2, int var3, int var4, int var5) {
        this.checkWidget();
        if (var1 == null) {
            this.error(4);
        }

        if (var1.isDisposed()) {
            this.error(5);
        }

        int var6 = var1.handle;
        int var7 = OS.SaveDC(var6);
        OS.IntersectClipRect(var6, 0, 0, var4, var5);
        POINT var8 = new POINT();
        int var9 = OS.GetParent(this.handle);
        OS.MapWindowPoints(this.handle, var9, var8, 1);
        RECT var10 = new RECT();
        OS.GetWindowRect(this.handle, var10);
        POINT var11 = new POINT();
        POINT var12 = new POINT();
        var2 += var8.x - var10.left;
        var3 += var8.y - var10.top;
        OS.SetWindowOrgEx(var6, var2, var3, var11);
        OS.SetBrushOrgEx(var6, var2, var3, var12);
        int var13 = OS.GetWindowLong(this.handle, -16);
        if ((var13 & 268435456) == 0) {
            OS.DefWindowProc(this.handle, 11, 1, 0);
        }

        OS.RedrawWindow(this.handle, (RECT)null, 0, 384);
        OS.PrintWindow(this.handle, var6, 0);
        if ((var13 & 268435456) == 0) {
            OS.DefWindowProc(this.handle, 11, 0, 0);
        }

        OS.RestoreDC(var6, var7);
    }

    void createHandle() {
        super.createHandle();
        this.state |= 2;
        if ((this.style & 768) == 0 || this.findThemeControl() == this.parent) {
            this.state |= 256;
        }

        if ((this.style & 1073741824) != 0) {
            int var1 = OS.GetWindowLong(this.handle, -20);
            var1 |= 32;
            OS.SetWindowLong(this.handle, -20, var1);
        }

    }

    int applyThemeBackground() {
        return this.backgroundAlpha != 0 && (this.style & 768) != 0 && this.findThemeControl() != this.parent ? 0 : 1;
    }

    public void drawBackground(GC var1, int var2, int var3, int var4, int var5, int var6, int var7) {
        this.checkWidget();
        var2 = DPIUtil.autoScaleUp(var2);
        var3 = DPIUtil.autoScaleUp(var3);
        var4 = DPIUtil.autoScaleUp(var4);
        var5 = DPIUtil.autoScaleUp(var5);
        var6 = DPIUtil.autoScaleUp(var6);
        var7 = DPIUtil.autoScaleUp(var7);
        this.drawBackgroundInPixels(var1, var2, var3, var4, var5, var6, var7);
    }

    void drawBackgroundInPixels(GC var1, int var2, int var3, int var4, int var5, int var6, int var7) {
        if (var1 == null) {
            this.error(4);
        }

        if (var1.isDisposed()) {
            this.error(5);
        }

        RECT var8 = new RECT();
        OS.SetRect(var8, var2, var3, var2 + var4, var3 + var5);
        int var9 = var1.handle;
        int var10 = this.background == -1 ? var1.getBackground().handle : -1;
        this.drawBackground(var9, var8, var10, var6, var7);
    }

    Composite findDeferredControl() {
        return this.layoutCount > 0 ? this : this.parent.findDeferredControl();
    }

    Menu[] findMenus(Control var1) {
        if (var1 == this) {
            return new Menu[0];
        } else {
            Menu[] var2 = super.findMenus(var1);
            Control[] var3 = this._getChildren();

            for(int var4 = 0; var4 < var3.length; ++var4) {
                Control var5 = var3[var4];
                Menu[] var6 = var5.findMenus(var1);
                if (var6.length != 0) {
                    Menu[] var7 = new Menu[var2.length + var6.length];
                    System.arraycopy(var2, 0, var7, 0, var2.length);
                    System.arraycopy(var6, 0, var7, var2.length, var6.length);
                    var2 = var7;
                }
            }

            return var2;
        }
    }

    void fixChildren(Shell var1, Shell var2, Decorations var3, Decorations var4, Menu[] var5) {
        super.fixChildren(var1, var2, var3, var4, var5);
        Control[] var6 = this._getChildren();

        for(int var7 = 0; var7 < var6.length; ++var7) {
            var6[var7].fixChildren(var1, var2, var3, var4, var5);
        }

    }

    void fixTabList(Control var1) {
        if (this.tabList != null) {
            int var2 = 0;

            for(int var3 = 0; var3 < this.tabList.length; ++var3) {
                if (this.tabList[var3] == var1) {
                    ++var2;
                }
            }

            if (var2 != 0) {
                Control[] var7 = null;
                int var4 = this.tabList.length - var2;
                if (var4 != 0) {
                    var7 = new Control[var4];
                    int var5 = 0;

                    for(int var6 = 0; var6 < this.tabList.length; ++var6) {
                        if (this.tabList[var6] != var1) {
                            var7[var5++] = this.tabList[var6];
                        }
                    }
                }

                this.tabList = var7;
            }
        }
    }

    public int getBackgroundMode() {
        this.checkWidget();
        return this.backgroundMode;
    }

    public Control[] getChildren() {
        this.checkWidget();
        return this._getChildren();
    }

    int getChildrenCount() {
        int var1 = 0;

        for(int var2 = OS.GetWindow(this.handle, 5); var2 != 0; var2 = OS.GetWindow(var2, 2)) {
            ++var1;
        }

        return var1;
    }

    public Layout getLayout() {
        this.checkWidget();
        return this.layout;
    }

    public Control[] getTabList() {
        this.checkWidget();
        Control[] var1 = this._getTabList();
        if (var1 == null) {
            int var2 = 0;
            Control[] var3 = this._getChildren();

            int var4;
            for(var4 = 0; var4 < var3.length; ++var4) {
                if (var3[var4].isTabGroup()) {
                    ++var2;
                }
            }

            var1 = new Control[var2];
            var4 = 0;

            for(int var5 = 0; var5 < var3.length; ++var5) {
                if (var3[var5].isTabGroup()) {
                    var1[var4++] = var3[var5];
                }
            }
        }

        return var1;
    }

    boolean hooksKeys() {
        return this.hooks(1) || this.hooks(2);
    }

    public boolean getLayoutDeferred() {
        this.checkWidget();
        return this.layoutCount > 0;
    }

    public boolean isLayoutDeferred() {
        this.checkWidget();
        return this.findDeferredControl() != null;
    }

    public void layout() {
        this.checkWidget();
        this.layout(true);
    }

    public void layout(boolean var1) {
        this.checkWidget();
        if (this.layout != null) {
            this.layout(var1, false);
        }
    }

    public void layout(boolean var1, boolean var2) {
        this.checkWidget();
        if (this.layout != null || var2) {
            this.markLayout(var1, var2);
            this.updateLayout(var2);
        }
    }

    public void layout(Control[] var1) {
        this.checkWidget();
        if (var1 == null) {
            this.error(5);
        }

        this.layout(var1, 0);
    }

    public void layout(Control[] var1, int var2) {
        this.checkWidget();
        if (var1 != null) {
            int var3;
            for(var3 = 0; var3 < var1.length; ++var3) {
                Control var4 = var1[var3];
                if (var4 == null) {
                    this.error(5);
                }

                if (var4.isDisposed()) {
                    this.error(5);
                }

                boolean var5 = false;

                for(Composite var6 = var4.parent; var6 != null; var6 = var6.parent) {
                    var5 = var6 == this;
                    if (var5) {
                        break;
                    }
                }

                if (!var5) {
                    this.error(32);
                }
            }

            var3 = 0;
            Composite[] var9 = new Composite[16];

            int var10;
            for(var10 = 0; var10 < var1.length; ++var10) {
                Object var11 = var1[var10];
                Composite var7 = ((Control)var11).parent;
                ((Control)var11).markLayout(false, false);

                while(var11 != this) {
                    if (var7.layout != null) {
                        var7.state |= 32;
                        if (!var7.layout.flushCache((Control)var11)) {
                            var7.state |= 64;
                        }
                    }

                    if (var3 == var9.length) {
                        Composite[] var8 = new Composite[var9.length + 16];
                        System.arraycopy(var9, 0, var8, 0, var9.length);
                        var9 = var8;
                    }

                    var11 = var9[var3++] = var7;
                    var7 = ((Control)var11).parent;
                }
            }

            if ((var2 & 4) != 0) {
                this.setLayoutDeferred(true);
                this.display.addLayoutDeferred(this);
            }

            for(var10 = var3 - 1; var10 >= 0; --var10) {
                var9[var10].updateLayout(false);
            }
        } else {
            if (this.layout == null && (var2 & 1) == 0) {
                return;
            }

            this.markLayout((var2 & 2) != 0, (var2 & 1) != 0);
            if ((var2 & 4) != 0) {
                this.setLayoutDeferred(true);
                this.display.addLayoutDeferred(this);
            }

            this.updateLayout((var2 & 1) != 0);
        }

    }

    void markLayout(boolean var1, boolean var2) {
        if (this.layout != null) {
            this.state |= 32;
            if (var1) {
                this.state |= 64;
            }
        }

        if (var2) {
            Control[] var3 = this._getChildren();

            for(int var4 = 0; var4 < var3.length; ++var4) {
                var3[var4].markLayout(var1, var2);
            }
        }

    }

    Point minimumSize(int var1, int var2, boolean var3) {
        Control[] var4 = this._getChildren();
        Rectangle var5 = DPIUtil.autoScaleUp(this.getClientArea());
        int var6 = 0;
        int var7 = 0;

        for(int var8 = 0; var8 < var4.length; ++var8) {
            Rectangle var9 = DPIUtil.autoScaleUp(var4[var8].getBounds());
            var6 = Math.max(var6, var9.x - var5.x + var9.width);
            var7 = Math.max(var7, var9.y - var5.y + var9.height);
        }

        return new Point(var6, var7);
    }

    boolean redrawChildren() {
        if (!super.redrawChildren()) {
            return false;
        } else {
            Control[] var1 = this._getChildren();

            for(int var2 = 0; var2 < var1.length; ++var2) {
                var1[var2].redrawChildren();
            }

            return true;
        }
    }

    void releaseParent() {
        super.releaseParent();
        if ((this.state & 2) != 0 && (this.style & 1073741824) != 0) {
            int var1 = this.parent.handle;

            int var3;
            for(int var2 = OS.GetWindow(var1, 5); var2 != 0; var2 = OS.GetWindow(var2, 2)) {
                if (var2 != this.handle) {
                    var3 = OS.GetWindowLong(var1, -20);
                    if ((var3 & 32) != 0) {
                        return;
                    }
                }
            }

            var3 = OS.GetWindowLong(var1, -20);
            var3 &= -33554433;
            OS.SetWindowLong(var1, -20, var3);
        }

    }

    void releaseChildren(boolean var1) {
        Control[] var2 = this._getChildren();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            Control var4 = var2[var3];
            if (var4 != null && !var4.isDisposed()) {
                var4.release(false);
            }
        }

        super.releaseChildren(var1);
    }

    void releaseWidget() {
        super.releaseWidget();
        if ((this.state & 2) != 0 && (this.style & 16777216) != 0) {
            int var1 = OS.GetWindow(this.handle, 5);
            if (var1 != 0) {
                int var2 = OS.GetWindowThreadProcessId(var1, (int[])null);
                if (var2 != OS.GetCurrentThreadId()) {
                    OS.ShowWindow(var1, 0);
                    OS.SetParent(var1, 0);
                }
            }
        }

        this.layout = null;
        this.tabList = null;
        this.lpwp = null;
    }

    void removeControl(Control var1) {
        this.fixTabList(var1);
        this.resizeChildren();
    }

    void reskinChildren(int var1) {
        super.reskinChildren(var1);
        Control[] var2 = this._getChildren();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            Control var4 = var2[var3];
            if (var4 != null) {
                var4.reskin(var1);
            }
        }

    }

    void resizeChildren() {
        if (this.lpwp != null) {
            do {
                WINDOWPOS[] var1 = this.lpwp;
                this.lpwp = null;
                if (!this.resizeChildren(true, var1)) {
                    this.resizeChildren(false, var1);
                }
            } while(this.lpwp != null);

        }
    }

    boolean resizeChildren(boolean var1, WINDOWPOS[] var2) {
        if (var2 == null) {
            return true;
        } else {
            int var3 = 0;
            if (var1) {
                var3 = OS.BeginDeferWindowPos(var2.length);
                if (var3 == 0) {
                    return false;
                }
            }

            for(int var4 = 0; var4 < var2.length; ++var4) {
                WINDOWPOS var5 = var2[var4];
                if (var5 != null) {
                    if (var1) {
                        var3 = this.DeferWindowPos(var3, var5.hwnd, 0, var5.x, var5.y, var5.cx, var5.cy, var5.flags);
                        if (var3 == 0) {
                            return false;
                        }
                    } else {
                        this.SetWindowPos(var5.hwnd, 0, var5.x, var5.y, var5.cx, var5.cy, var5.flags);
                    }
                }
            }

            if (var1) {
                return OS.EndDeferWindowPos(var3);
            } else {
                return true;
            }
        }
    }

    void resizeEmbeddedHandle(int var1, int var2, int var3) {
        if (var1 != 0) {
            int[] var4 = new int[1];
            int var5 = OS.GetWindowThreadProcessId(var1, var4);
            if (var5 != OS.GetCurrentThreadId()) {
                if (var4[0] == OS.GetCurrentProcessId() && this.display.msgHook == 0 && !OS.IsWinCE) {
                    this.display.getMsgCallback = new Callback(this.display, "getMsgProc", 3);
                    this.display.getMsgProc = this.display.getMsgCallback.getAddress();
                    if (this.display.getMsgProc == 0) {
                        this.error(3);
                    }

                    this.display.msgHook = OS.SetWindowsHookEx(3, this.display.getMsgProc, OS.GetLibraryHandle(), var5);
                    OS.PostThreadMessage(var5, 0, 0, 0);
                }

                short var6 = 16436;
                OS.SetWindowPos(var1, 0, 0, 0, var2, var3, var6);
            }

        }
    }

    void sendResize() {
        this.setResizeChildren(false);
        super.sendResize();
        if (!this.isDisposed()) {
            if (this.layout != null) {
                this.markLayout(false, false);
                this.updateLayout(false, false);
            }

            this.setResizeChildren(true);
        }
    }

    public void setBackgroundMode(int var1) {
        this.checkWidget();
        this.backgroundMode = var1;
        Control[] var2 = this._getChildren();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3].updateBackgroundMode();
        }

    }

    void setBoundsInPixels(int var1, int var2, int var3, int var4, int var5, boolean var6) {
        if (this.display.resizeCount > 4) {
            var6 = false;
        }

        if (!var6 && (this.state & 2) != 0) {
            this.state &= -327681;
            this.state |= 655360;
        }

        super.setBoundsInPixels(var1, var2, var3, var4, var5, var6);
        if (!var6 && (this.state & 2) != 0) {
            boolean var7 = (this.state & 65536) != 0;
            boolean var8 = (this.state & 262144) != 0;
            this.state &= -655361;
            if (var7 && !this.isDisposed()) {
                this.sendMove();
            }

            if (var8 && !this.isDisposed()) {
                this.sendResize();
            }
        }

    }

    public boolean setFocus() {
        this.checkWidget();
        Control[] var1 = this._getChildren();

        int var2;
        Control var3;
        for(var2 = 0; var2 < var1.length; ++var2) {
            var3 = var1[var2];
            if (var3.setRadioFocus(false)) {
                return true;
            }
        }

        for(var2 = 0; var2 < var1.length; ++var2) {
            var3 = var1[var2];
            if (var3.setFocus()) {
                return true;
            }
        }

        return super.setFocus();
    }

    public void setLayout(Layout var1) {
        this.checkWidget();
        this.layout = var1;
    }

    public void setLayoutDeferred(boolean var1) {
        this.checkWidget();
        if (!var1) {
            if (--this.layoutCount == 0 && ((this.state & 128) != 0 || (this.state & 32) != 0)) {
                this.updateLayout(true);
            }
        } else {
            ++this.layoutCount;
        }

    }

    public void setTabList(Control[] var1) {
        this.checkWidget();
        if (var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
                Control var3 = var1[var2];
                if (var3 == null) {
                    this.error(5);
                }

                if (var3.isDisposed()) {
                    this.error(5);
                }

                if (var3.parent != this) {
                    this.error(32);
                }
            }

            Control[] var4 = new Control[var1.length];
            System.arraycopy(var1, 0, var4, 0, var1.length);
            var1 = var4;
        }

        this.tabList = var1;
    }

    void setResizeChildren(boolean var1) {
        if (var1) {
            this.resizeChildren();
        } else {
            if (this.display.resizeCount > 4) {
                return;
            }

            int var2 = this.getChildrenCount();
            if (var2 > 1 && this.lpwp == null) {
                this.lpwp = new WINDOWPOS[var2];
            }
        }

    }

    boolean setTabGroupFocus() {
        if (this.isTabItem()) {
            return this.setTabItemFocus();
        } else {
            boolean var1 = (this.style & 524288) == 0;
            if ((this.state & 2) != 0) {
                var1 = this.hooksKeys();
                if ((this.style & 16777216) != 0) {
                    var1 = true;
                }
            }

            if (var1 && this.setTabItemFocus()) {
                return true;
            } else {
                Control[] var2 = this._getChildren();

                int var3;
                Control var4;
                for(var3 = 0; var3 < var2.length; ++var3) {
                    var4 = var2[var3];
                    if (var4.isTabItem() && var4.setRadioFocus(true)) {
                        return true;
                    }
                }

                for(var3 = 0; var3 < var2.length; ++var3) {
                    var4 = var2[var3];
                    if (var4.isTabItem() && !var4.isTabGroup() && var4.setTabItemFocus()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    boolean updateTextDirection(int var1) {
        super.updateTextDirection(var1);
        Control[] var2 = this._getChildren();
        int var3 = var2.length;

        while(var3-- > 0) {
            if (var2[var3] != null && !var2[var3].isDisposed()) {
                var2[var3].updateTextDirection(var1);
            }
        }

        return true;
    }

    String toolTipText(NMTTDISPINFO var1) {
        Shell var2 = this.getShell();
        if ((var1.uFlags & 1) != 0) {
            var2.setToolTipTitle(var1.hwndFrom, (String)null, 0);
            OS.SendMessage(var1.hwndFrom, 1048, 0, 32767);
            Control var5 = this.display.getControl(var1.idFrom);
            return var5 != null ? var5.toolTipText : null;
        } else {
            String var3 = null;
            ToolTip var4 = var2.findToolTip(var1.idFrom);
            if (var4 != null) {
                var3 = var4.message;
                if (var3 == null || var3.length() == 0) {
                    var3 = " ";
                }

                if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION(6, 0) && var3.length() > 1024) {
                    var3 = this.display.wrapText(var3, this.handle, var4.getWidth());
                }
            }

            return var3;
        }
    }

    boolean translateMnemonic(Event var1, Control var2) {
        if (super.translateMnemonic(var1, var2)) {
            return true;
        } else {
            if (var2 != null) {
                Control[] var3 = this._getChildren();

                for(int var4 = 0; var4 < var3.length; ++var4) {
                    Control var5 = var3[var4];
                    if (var5.translateMnemonic(var1, var2)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    boolean translateTraversal(MSG var1) {
        if ((this.state & 2) != 0) {
            if ((this.style & 16777216) != 0) {
                return false;
            }

            switch (var1.wParam) {
                case 33:
                case 34:
                case 37:
                case 38:
                case 39:
                case 40:
                    int var2 = OS.SendMessage(var1.hwnd, 297, 0, 0);
                    if ((var2 & 1) != 0) {
                        OS.SendMessage(var1.hwnd, 296, OS.MAKEWPARAM(2, 1), 0);
                    }
                case 35:
                case 36:
            }
        }

        return super.translateTraversal(var1);
    }

    void updateBackgroundColor() {
        super.updateBackgroundColor();
        Control[] var1 = this._getChildren();

        for(int var2 = 0; var2 < var1.length; ++var2) {
            if ((var1[var2].state & 1024) != 0) {
                var1[var2].updateBackgroundColor();
            }
        }

    }

    void updateBackgroundImage() {
        super.updateBackgroundImage();
        Control[] var1 = this._getChildren();

        for(int var2 = 0; var2 < var1.length; ++var2) {
            if ((var1[var2].state & 1024) != 0) {
                var1[var2].updateBackgroundImage();
            }
        }

    }

    void updateBackgroundMode() {
        super.updateBackgroundMode();
        Control[] var1 = this._getChildren();

        for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2].updateBackgroundMode();
        }

    }

    void updateFont(Font var1, Font var2) {
        super.updateFont(var1, var2);
        Control[] var3 = this._getChildren();

        for(int var4 = 0; var4 < var3.length; ++var4) {
            Control var5 = var3[var4];
            if (!var5.isDisposed()) {
                var5.updateFont(var1, var2);
            }
        }

    }

    void updateLayout(boolean var1) {
        this.updateLayout(true, var1);
    }

    void updateLayout(boolean var1, boolean var2) {
        Composite var3 = this.findDeferredControl();
        if (var3 != null) {
            var3.state |= 128;
        } else {
            if ((this.state & 32) != 0) {
                boolean var4 = (this.state & 64) != 0;
                this.state &= -97;
                this.display.runSkin();
                if (var1) {
                    this.setResizeChildren(false);
                }

                this.layout.layout(this, var4);
                if (var1) {
                    this.setResizeChildren(true);
                }
            }

            if (var2) {
                this.state &= -129;
                Control[] var6 = this._getChildren();

                for(int var5 = 0; var5 < var6.length; ++var5) {
                    var6[var5].updateLayout(var1, var2);
                }
            }

        }
    }

    void updateOrientation() {
        Control[] var1 = this._getChildren();
        RECT[] var2 = new RECT[var1.length];

        int var3;
        for(var3 = 0; var3 < var1.length; ++var3) {
            Control var4 = var1[var3];
            RECT var5 = var2[var3] = new RECT();
            var4.forceResize();
            OS.GetWindowRect(var4.topHandle(), var5);
            OS.MapWindowPoints(0, this.handle, var5, 2);
        }

        var3 = this.style & 100663296;
        super.updateOrientation();

        for(int var8 = 0; var8 < var1.length; ++var8) {
            Control var9 = var1[var8];
            RECT var6 = var2[var8];
            var9.setOrientation(var3);
            byte var7 = 21;
            this.SetWindowPos(var9.topHandle(), 0, var6.left, var6.top, 0, 0, var7);
        }

    }

    void updateUIState() {
        int var1 = this.getShell().handle;
        int var2 = OS.SendMessage(var1, 297, 0, 0);
        if ((var2 & 1) != 0) {
            OS.SendMessage(var1, 295, OS.MAKEWPARAM(2, 1), 0);
        }

    }

    int widgetStyle() {
        return super.widgetStyle() | 33554432;
    }

    LRESULT WM_ERASEBKGND(int var1, int var2) {
        LRESULT var3 = super.WM_ERASEBKGND(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            return (this.state & 2) != 0 && (this.style & 1074003968) != 0 ? LRESULT.ZERO : var3;
        }
    }

    LRESULT WM_GETDLGCODE(int var1, int var2) {
        LRESULT var3 = super.WM_GETDLGCODE(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            if ((this.state & 2) != 0) {
                int var4 = 0;
                if (this.hooksKeys()) {
                    var4 |= 7;
                }

                if ((this.style & 524288) != 0) {
                    var4 |= 256;
                }

                if (OS.GetWindow(this.handle, 5) != 0) {
                    var4 |= 256;
                }

                if (var4 != 0) {
                    return new LRESULT(var4);
                }
            }

            return var3;
        }
    }

    LRESULT WM_GETFONT(int var1, int var2) {
        LRESULT var3 = super.WM_GETFONT(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            int var4 = this.callWindowProc(this.handle, 49, var1, var2);
            return var4 != 0 ? new LRESULT(var4) : new LRESULT(this.font != null ? this.font.handle : this.defaultFont());
        }
    }

    LRESULT WM_LBUTTONDOWN(int var1, int var2) {
        LRESULT var3 = super.WM_LBUTTONDOWN(var1, var2);
        if (var3 == LRESULT.ZERO) {
            return var3;
        } else {
            if ((this.state & 2) != 0 && (this.style & 524288) == 0 && this.hooksKeys() && OS.GetWindow(this.handle, 5) == 0) {
                this.setFocus();
            }

            return var3;
        }
    }

    LRESULT WM_NCHITTEST(int var1, int var2) {
        LRESULT var3 = super.WM_NCHITTEST(var1, var2);
        if (var3 != null) {
            return var3;
        } else if (!OS.IsWinCE && OS.COMCTL32_MAJOR >= 6 && OS.IsAppThemed() && (this.state & 2) != 0) {
            int var4 = this.callWindowProc(this.handle, 132, var1, var2);
            if (var4 == 1) {
                RECT var5 = new RECT();
                OS.GetClientRect(this.handle, var5);
                POINT var6 = new POINT();
                var6.x = OS.GET_X_LPARAM(var2);
                var6.y = OS.GET_Y_LPARAM(var2);
                OS.MapWindowPoints(0, this.handle, var6, 1);
                if (!OS.PtInRect(var5, var6)) {
                    short var7 = 1025;
                    OS.RedrawWindow(this.handle, (RECT)null, 0, var7);
                }
            }

            return new LRESULT(var4);
        } else {
            return var3;
        }
    }

    LRESULT WM_PARENTNOTIFY(int var1, int var2) {
        if ((this.state & 2) != 0 && (this.style & 16777216) != 0 && OS.LOWORD(var1) == 1) {
            RECT var3 = new RECT();
            OS.GetClientRect(this.handle, var3);
            this.resizeEmbeddedHandle(var2, var3.right - var3.left, var3.bottom - var3.top);
        }

        return super.WM_PARENTNOTIFY(var1, var2);
    }

    LRESULT WM_PAINT(int var1, int var2) {
        if ((this.state & 4096) != 0) {
            return LRESULT.ZERO;
        } else if ((this.state & 2) != 0 && (this.state & 16384) == 0) {
            int var3 = 0;
            int var4 = 0;
            if (!OS.IsWinCE) {
                var3 = OS.GetWindowLong(this.handle, -16);
                var4 = var3 | 67108864 | 33554432;
                if (var4 != var3) {
                    OS.SetWindowLong(this.handle, -16, var4);
                }
            }

            PAINTSTRUCT var5 = new PAINTSTRUCT();
            if (!this.hooks(9) && !this.filters(9)) {
                int var21 = OS.BeginPaint(this.handle, var5);
                if ((this.style & 1074003968) == 0) {
                    RECT var23 = new RECT();
                    OS.SetRect(var23, var5.left, var5.top, var5.right, var5.bottom);
                    this.drawBackground(var21, var23);
                }

                OS.EndPaint(this.handle, var5);
            } else {
                boolean var6 = false;
                if ((this.style & 536870912) != 0 && !OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION(6, 0) && (this.style & 69206016) == 0 && (this.style & 1073741824) == 0) {
                    var6 = true;
                }

                int var9;
                int var11;
                GCData var14;
                if (var6) {
                    int var7 = OS.BeginPaint(this.handle, var5);
                    int var8 = var5.right - var5.left;
                    var9 = var5.bottom - var5.top;
                    if (var8 != 0 && var9 != 0) {
                        int[] var10 = new int[1];
                        var11 = 0;
                        RECT var12 = new RECT();
                        OS.SetRect(var12, var5.left, var5.top, var5.right, var5.bottom);
                        int var13 = OS.BeginBufferedPaint(var7, var12, var11, (BP_PAINTPARAMS)null, var10);
                        var14 = new GCData();
                        var14.device = this.display;
                        var14.foreground = this.getForegroundPixel();
                        Object var15 = this.findBackgroundControl();
                        if (var15 == null) {
                            var15 = this;
                        }

                        var14.background = ((Control)var15).getBackgroundPixel();
                        var14.font = Font.win32_new(this.display, OS.SendMessage(this.handle, 49, 0, 0));
                        var14.uiState = OS.SendMessage(this.handle, 297, 0, 0);
                        if ((this.style & 262144) == 0) {
                            RECT var16 = new RECT();
                            OS.SetRect(var16, var5.left, var5.top, var5.right, var5.bottom);
                            this.drawBackground(var10[0], var16);
                        }

                        GC var33 = GC.win32_new(var10[0], var14);
                        Event var17 = new Event();
                        var17.gc = var33;
                        var17.setBoundsInPixels(new Rectangle(var5.left, var5.top, var8, var9));
                        this.sendEvent(9, var17);
                        if (var14.focusDrawn && !this.isDisposed()) {
                            this.updateUIState();
                        }

                        var33.dispose();
                        OS.EndBufferedPaint(var13, true);
                    }

                    OS.EndPaint(this.handle, var5);
                } else {
                    GCData var22 = new GCData();
                    var22.ps = var5;
                    var22.hwnd = this.handle;
                    GC var24 = GC.win32_new(this, var22);
                    var9 = 0;
                    int var25;
                    if ((this.style & 1610612736) != 0 || (this.style & 2097152) != 0) {
                        var9 = OS.CreateRectRgn(0, 0, 0, 0);
                        if (OS.GetRandomRgn(var24.handle, var9, 4) == 1) {
                            if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION(4, 10) && (OS.GetLayout(var24.handle) & 1) != 0) {
                                var25 = OS.GetRegionData(var9, 0, (int[])null);
                                int[] var27 = new int[var25 / 4];
                                OS.GetRegionData(var9, var25, var27);
                                int var28 = OS.ExtCreateRegion(new float[]{-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F}, var25, var27);
                                OS.DeleteObject(var9);
                                var9 = var28;
                            }

                            if (OS.IsWinNT) {
                                POINT var26 = new POINT();
                                OS.MapWindowPoints(0, this.handle, var26, 1);
                                OS.OffsetRgn(var9, var26.x, var26.y);
                            }
                        }
                    }

                    var25 = var5.right - var5.left;
                    var11 = var5.bottom - var5.top;
                    if (var25 > 0 && var11 > 0) { // there
                        GC var29 = null;
                        Image var30 = null;
                        RECT var32;
                        if ((this.style & 1610612736) != 0) {
                            var30 = new Image(this.display, var25, var11);
                            var29 = var24;
                            var24 = new GC(var30, var24.getStyle() & 67108864);
                            var14 = var24.getGCData();
                            var14.uiState = var22.uiState;
                            var24.setForeground(this.getForeground());
                            var24.setBackground(this.getBackground());
                            var24.setFont(this.getFont());
                            if ((this.style & 1073741824) != 0) {
                                OS.BitBlt(var24.handle, 0, 0, var25, var11, var29.handle, var5.left, var5.top, 13369376);
                            }

                            OS.OffsetRgn(var9, -var5.left, -var5.top);
                            OS.SelectClipRgn(var24.handle, var9);
                            OS.OffsetRgn(var9, var5.left, var5.top);
                            OS.SetMetaRgn(var24.handle);
                            OS.SetWindowOrgEx(var24.handle, var5.left, var5.top, (POINT)null);
                            OS.SetBrushOrgEx(var24.handle, var5.left, var5.top, (POINT)null);
                            if ((this.style & 1074003968) == 0) {
                                var32 = new RECT();
                                OS.SetRect(var32, var5.left, var5.top, var5.right, var5.bottom);
                                this.drawBackground(var24.handle, var32);
                            }
                        }

                        Event var31 = new Event();
                        var31.gc = var24;
                        var32 = null;
                        if ((this.style & 2097152) != 0 && OS.GetRgnBox(var9, var32 = new RECT()) == 3) {
                            int var34 = OS.GetRegionData(var9, 0, (int[])null);
                            int[] var36 = new int[var34 / 4];
                            OS.GetRegionData(var9, var34, var36);
                            int var18 = var36[2];

                            for(int var19 = 0; var19 < var18; ++var19) {
                                int var20 = 8 + (var19 << 2);
                                OS.SetRect(var32, var36[var20], var36[var20 + 1], var36[var20 + 2], var36[var20 + 3]);
                                if ((this.style & 1610874880) == 0) {
                                    this.drawBackground(var24.handle, var32);
                                }

                                var31.setBoundsInPixels(new Rectangle(var32.left, var32.top, var32.right - var32.left, var32.bottom - var32.top));
                                var31.count = var18 - 1 - var19;
                                this.sendEvent(9, var31);
                            }
                        } else {
                            if ((this.style & 1610874880) == 0) {
                                if (var32 == null) {
                                    var32 = new RECT();
                                }

                                OS.SetRect(var32, var5.left, var5.top, var5.right, var5.bottom);
                                this.drawBackground(var24.handle, var32);
                            }

                            var31.setBoundsInPixels(new Rectangle(var5.left, var5.top, var25, var11));
                            this.sendEvent(9, var31);
                        }

                        var31.gc = null;
                        if ((this.style & 1610612736) != 0) {
                            if (!var24.isDisposed()) {
                                GCData var35 = var24.getGCData();
                                if (var35.focusDrawn && !this.isDisposed()) {
                                    this.updateUIState();
                                }
                            }

                            var24.dispose();
                            if (!this.isDisposed()) {
                                var29.drawImage(var30, DPIUtil.autoScaleDown(var5.left), DPIUtil.autoScaleDown(var5.top));
                            }

                            var30.dispose();
                            var24 = var29;
                        }
                    }

                    if (var9 != 0) {
                        OS.DeleteObject(var9);
                    }

                    if (var22.focusDrawn && !this.isDisposed()) {
                        this.updateUIState();
                    }

                    var24.dispose();
                }
            }

            if (!OS.IsWinCE && !this.isDisposed() && var4 != var3 && !this.isDisposed()) {
                OS.SetWindowLong(this.handle, -16, var3);
            }

            return LRESULT.ZERO;
        } else {
            return super.WM_PAINT(var1, var2);
        }
    }

    LRESULT WM_PRINTCLIENT(int var1, int var2) {
        LRESULT var3 = super.WM_PRINTCLIENT(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            if ((this.state & 2) != 0) {
                this.forceResize();
                int var4 = OS.SaveDC(var1);
                RECT var5 = new RECT();
                OS.GetClientRect(this.handle, var5);
                if ((this.style & 1074003968) == 0) {
                    this.drawBackground(var1, var5);
                }

                if (this.hooks(9) || this.filters(9)) {
                    GCData var6 = new GCData();
                    var6.device = this.display;
                    var6.foreground = this.getForegroundPixel();
                    Object var7 = this.findBackgroundControl();
                    if (var7 == null) {
                        var7 = this;
                    }

                    var6.background = ((Control)var7).getBackgroundPixel();
                    var6.font = Font.win32_new(this.display, OS.SendMessage(this.handle, 49, 0, 0));
                    var6.uiState = OS.SendMessage(this.handle, 297, 0, 0);
                    GC var8 = GC.win32_new(var1, var6);
                    Event var9 = new Event();
                    var9.gc = var8;
                    var9.setBoundsInPixels(new Rectangle(var5.left, var5.top, var5.right - var5.left, var5.bottom - var5.top));
                    this.sendEvent(9, var9);
                    var9.gc = null;
                    var8.dispose();
                }

                OS.RestoreDC(var1, var4);
            }

            return var3;
        }
    }

    LRESULT WM_SETFONT(int var1, int var2) {
        if (var2 != 0) {
            OS.InvalidateRect(this.handle, (RECT)null, true);
        }

        return super.WM_SETFONT(var1, var2);
    }

    LRESULT WM_SIZE(int var1, int var2) {
        LRESULT var3 = null;
        if ((this.state & 524288) != 0) {
            var3 = super.WM_SIZE(var1, var2);
        } else {
            this.setResizeChildren(false);
            var3 = super.WM_SIZE(var1, var2);
            if (this.isDisposed()) {
                return var3;
            }

            if (this.layout != null) {
                this.markLayout(false, false);
                this.updateLayout(false, false);
            }

            this.setResizeChildren(true);
        }

        if (OS.IsWindowVisible(this.handle)) {
            if ((this.state & 2) != 0 && (this.style & 1048576) == 0 && this.hooks(9)) {
                OS.InvalidateRect(this.handle, (RECT)null, true);
            }

            if (OS.COMCTL32_MAJOR >= 6 && OS.IsAppThemed() && this.findThemeControl() != null) {
                this.redrawChildren();
            }
        }

        if ((this.state & 2) != 0 && (this.style & 16777216) != 0) {
            this.resizeEmbeddedHandle(OS.GetWindow(this.handle, 5), OS.LOWORD(var2), OS.HIWORD(var2));
        }

        return var3;
    }

    LRESULT WM_SYSCOLORCHANGE(int var1, int var2) {
        LRESULT var3 = super.WM_SYSCOLORCHANGE(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            for(int var4 = OS.GetWindow(this.handle, 5); var4 != 0; var4 = OS.GetWindow(var4, 2)) {
                OS.SendMessage(var4, 21, 0, 0);
            }

            return var3;
        }
    }

    LRESULT WM_SYSCOMMAND(int var1, int var2) {
        LRESULT var3 = super.WM_SYSCOMMAND(var1, var2);
        if (var3 != null) {
            return var3;
        } else if ((var1 & '\uf000') == 0) {
            return var3;
        } else {
            if (!OS.IsWinCE) {
                int var4 = var1 & '\ufff0';
                switch (var4) {
                    case 61552:
                    case 61568:
                        boolean var5 = this.horizontalBar != null && this.horizontalBar.getVisible();
                        boolean var6 = this.verticalBar != null && this.verticalBar.getVisible();
                        int var7 = this.callWindowProc(this.handle, 274, var1, var2);
                        if (var5 != (this.horizontalBar != null && this.horizontalBar.getVisible()) || var6 != (this.verticalBar != null && this.verticalBar.getVisible())) {
                            short var8 = 1281;
                            OS.RedrawWindow(this.handle, (RECT)null, 0, var8);
                        }

                        if (var7 == 0) {
                            return LRESULT.ZERO;
                        }

                        return new LRESULT(var7);
                }
            }

            return var3;
        }
    }

    LRESULT WM_UPDATEUISTATE(int var1, int var2) {
        LRESULT var3 = super.WM_UPDATEUISTATE(var1, var2);
        if (var3 != null) {
            return var3;
        } else {
            if ((this.state & 2) != 0 && this.hooks(9)) {
                OS.InvalidateRect(this.handle, (RECT)null, true);
            }

            return var3;
        }
    }

    LRESULT wmNCPaint(int var1, int var2, int var3) {
        LRESULT var4 = super.wmNCPaint(var1, var2, var3);
        if (var4 != null) {
            return var4;
        } else {
            int var5 = this.borderHandle();
            if (((this.state & 2) != 0 || var1 == var5 && this.handle != var5) && OS.COMCTL32_MAJOR >= 6 && OS.IsAppThemed()) {
                int var6 = OS.GetWindowLong(var1, -20);
                if ((var6 & 512) != 0) {
                    int var7 = 0;
                    int var8 = OS.GetWindowLong(var1, -16);
                    if ((var8 & 3145728) != 0) {
                        var7 = this.callWindowProc(var1, 133, var2, var3);
                    }

                    int var9 = OS.GetWindowDC(var1);
                    RECT var10 = new RECT();
                    OS.GetWindowRect(var1, var10);
                    var10.right -= var10.left;
                    var10.bottom -= var10.top;
                    var10.left = var10.top = 0;
                    int var11 = OS.GetSystemMetrics(45);
                    OS.ExcludeClipRect(var9, var11, var11, var10.right - var11, var10.bottom - var11);
                    OS.DrawThemeBackground(this.display.hEditTheme(), var9, 1, 1, var10, (RECT)null);
                    OS.ReleaseDC(var1, var9);
                    return new LRESULT(var7);
                }
            }

            return var4;
        }
    }

    LRESULT wmNotify(NMHDR var1, int var2, int var3) {
        if (!OS.IsWinCE) {
            switch (var1.code) {
                case -530:
                case -520:
                    Object var12;
                    if (var1.code == -520) {
                        var12 = new NMTTDISPINFOA();
                        OS.MoveMemory((NMTTDISPINFOA)var12, var3, NMTTDISPINFOA.sizeof);
                    } else {
                        var12 = new NMTTDISPINFOW();
                        OS.MoveMemory((NMTTDISPINFOW)var12, var3, NMTTDISPINFOW.sizeof);
                    }

                    String var14 = this.toolTipText((NMTTDISPINFO)var12);
                    if (var14 != null) {
                        Shell var15 = this.getShell();
                        var14 = Display.withCrLf(var14);
                        if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION(6, 0) && var14.length() > 4096) {
                            var14 = var14.substring(0, 4096);
                        }

                        char[] var7 = this.fixMnemonic(var14);
                        Object var8 = null;
                        int var9 = var1.idFrom;
                        if ((((NMTTDISPINFO)var12).uFlags & 1) != 0) {
                            var8 = this.display.getControl(var9);
                        } else if (var1.hwndFrom == var15.toolTipHandle || var1.hwndFrom == var15.balloonTipHandle) {
                            var8 = var15.findToolTip(var1.idFrom);
                        }

                        if (var8 != null) {
                            int var10 = ((Widget)var8).getStyle();
                            int var11 = -2080374784;
                            if ((var10 & var11) != 0 && (var10 & var11) != var11) {
                                ((NMTTDISPINFO)var12).uFlags |= 4;
                            } else {
                                ((NMTTDISPINFO)var12).uFlags &= -5;
                            }
                        }

                        if (var1.code == -520) {
                            byte[] var16 = new byte[var7.length * 2];
                            OS.WideCharToMultiByte(this.getCodePage(), 0, var7, var7.length, var16, var16.length, (byte[])null, (boolean[])null);
                            var15.setToolTipText((NMTTDISPINFO)var12, var16);
                            OS.MoveMemory(var3, (NMTTDISPINFOA)var12, NMTTDISPINFOA.sizeof);
                        } else {
                            var15.setToolTipText((NMTTDISPINFO)var12, var7);
                            OS.MoveMemory(var3, (NMTTDISPINFOW)var12, NMTTDISPINFOW.sizeof);
                        }

                        return LRESULT.ZERO;
                    }
                    break;
                case -522:
                case -521:
                    int var4 = var1.hwndFrom;

                    int var5;
                    do {
                        var4 = OS.GetParent(var4);
                        if (var4 == 0) {
                            break;
                        }

                        var5 = OS.GetWindowLong(var4, -20);
                    } while((var5 & 8) == 0);

                    if (var4 == 0) {
                        if (this.display.getActiveShell() == null) {
                            return LRESULT.ONE;
                        }

                        this.display.lockActiveWindow = true;
                        byte var13 = 19;
                        int var6 = var1.code == -521 ? -1 : -2;
                        this.SetWindowPos(var1.hwndFrom, var6, 0, 0, 0, 0, var13);
                        this.display.lockActiveWindow = false;
                    }
            }
        }

        return super.wmNotify(var1, var2, var3);
    }
}
