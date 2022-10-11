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
package info.scce.addlib.dd.xdd.ringlikedd.example;

import java.awt.Color;

import info.scce.addlib.backend.ADDBackend;
import info.scce.addlib.backend.BackendProvider;
import info.scce.addlib.dd.xdd.ringlikedd.SemiringDDManager;

public class ColorDDManager extends SemiringDDManager<Color> {

    private static final int COLOR_RANGE = 255;
    private static final double RED_GRAY_PROPORTION = 0.2126;
    private static final double GREEN_GRAY_PROPORTION = 0.7152;
    private static final double BLUE_GRAY_PROPORTION = 0.0722;

    public ColorDDManager() {
        this(BackendProvider.getADDBackend());
    }

    public ColorDDManager(ADDBackend backend) {
        super(backend);
    }

    @Override
    protected Color zeroElement() {
        return new Color(0, 0, 0);
    }

    @Override
    protected Color oneElement() {
        return new Color(COLOR_RANGE, COLOR_RANGE, COLOR_RANGE);
    }

    @Override
    protected Color mult(Color color0, Color color1) {
        int r = (color0.getRed() * color1.getRed()) / COLOR_RANGE;
        int g = (color0.getGreen() * color1.getGreen()) / COLOR_RANGE;
        int b = (color0.getBlue() * color1.getBlue()) / COLOR_RANGE;
        return new Color(r, g, b);
    }

    @Override
    protected Color add(Color color0, Color color1) {
        int r = Math.min(color0.getRed() + color1.getRed(), COLOR_RANGE);
        int g = Math.min(color0.getGreen() + color1.getGreen(), COLOR_RANGE);
        int b = Math.min(color0.getBlue() + color1.getBlue(), COLOR_RANGE);
        return new Color(r, g, b);
    }

    public static Color avg(Color color0, Color color1) {
        int r = (color0.getRed() + color1.getRed()) / 2;
        int g = (color0.getGreen() + color1.getGreen()) / 2;
        int b = (color0.getBlue() + color1.getBlue()) / 2;
        return new Color(r, g, b);
    }

    public static Color grayscale(Color color) {
        int gray = (int) Math.round(RED_GRAY_PROPORTION * color.getRed() + GREEN_GRAY_PROPORTION * color.getGreen()
                                    + BLUE_GRAY_PROPORTION * color.getBlue());
        return new Color(gray, gray, gray);
    }
}

