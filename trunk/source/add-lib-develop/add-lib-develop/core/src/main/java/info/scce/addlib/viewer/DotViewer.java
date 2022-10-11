/* Copyright (c) 2017-2022, TU Dortmund University
 * This file is part of ADD-Lib, https://add-lib.scce.info/.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the TU Dortmund University nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package info.scce.addlib.viewer;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;

@SuppressWarnings("PMD.SystemPrintln")
public class DotViewer<D extends RegularDD<?, D>> {

    private static final String DEFAULT_VIEWER_NAME = "ADD-Lib Viewer";

    private final String viewerName;

    private final Lock lock = new ReentrantLock();
    private final Condition allFramesClosed = lock.newCondition();
    private final List<DotViewerFrame<D>> frames = new ArrayList<>();

    public DotViewer() {
        this(DEFAULT_VIEWER_NAME);
    }

    public DotViewer(String viewerName) {
        this.viewerName = viewerName;
    }

    public void view(D root, String label) {
        view(new LabelledRegularDD<>(root, label));
    }

    public void view(LabelledRegularDD<D> root) {
        view(Collections.singletonList(root));
    }

    public void view(List<LabelledRegularDD<D>> roots) {
        DotViewerFrame<D> f = new DotViewerFrame<>(viewerName);
        f.view(roots);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                lock.lock();
                try {
                    frames.remove(e.getWindow());
                    if (frames.isEmpty()) {
                        allFramesClosed.signal();
                    }
                } finally {
                    lock.unlock();
                }
            }
        });
        lock.lock();
        try {
            frames.add(f);
        } finally {
            lock.unlock();
        }
    }

    public void waitUntilAllClosed() {
        lock.lock();
        try {
            synchronized (this) {
                while (!frames.isEmpty()) {
                    allFramesClosed.await();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void closeAll() {
        lock.lock();
        try {
            for (Frame f : frames) {
                SwingUtilities.invokeLater(() -> {
                    WindowEvent e = new WindowEvent(f, WindowEvent.WINDOW_CLOSING);
                    f.dispatchEvent(e);
                });
            }
        } finally {
            lock.unlock();
        }
    }
}


