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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import info.scce.addlib.codegenerator.DotGenerator;
import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("PMD.UnusedFormatParameter")
public class DotViewerFrame<D extends RegularDD<?, D>> extends JFrame {

    public static final int DEFAULT_CANVAS_WIDTH = 1024;
    public static final int DEFAULT_CANVAS_HEIGHT = DEFAULT_CANVAS_WIDTH / 2;

    public static final String FILE_EXTENSION_DOT = "dot";
    public static final String FILE_EXTENSION_PNG = "png";

    public static final double ZOOM_BASE = 1.2;

    public static final Color ERROR_CIRCLES_COLOR = new Color(255, 0, 0, 64);
    public static final int ERROR_CIRCLES_N = 8;
    public static final int ERROR_LINE_HEIGHT = 16;
    public static final String ERROR_MESSAGE_L0 = "Failed to execute dot command";
    public static final String ERROR_MESSAGE_L1 = "https://www.graphviz.org/";

    private final JScrollPane scrollPane;

    private List<LabelledRegularDD<D>> roots;
    private BufferedImage image;
    private double zoomExp;

    public DotViewerFrame(String title) {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /* Build menu bar */
        JMenuItem exportPngItem = new JMenuItem("Export as *.png");
        exportPngItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        exportPngItem.addActionListener(this::onExportPng);
        JMenuItem exportDotItem = new JMenuItem("Export as *.dot");
        exportDotItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        exportDotItem.addActionListener(this::onExportDot);
        JMenuItem zoomInItem = new JMenuItem("Zoom in");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        zoomInItem.addActionListener(e -> {
            zoomExp += 1.0;
            updateCanvas();
        });
        JMenuItem zoomOutItem = new JMenuItem("Zoom out");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        zoomOutItem.addActionListener(e -> {
            zoomExp -= 1.0;
            updateCanvas();
        });
        JMenu menu = new JMenu("Menu");
        menu.add(exportDotItem);
        menu.add(exportPngItem);
        menu.add(zoomInItem);
        menu.add(zoomOutItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        /* Build canvas */
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        scrollPane = new JScrollPane(label);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT));

        /* Compose GUI */
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        pack();
    }

    public void view(List<LabelledRegularDD<D>> roots) {
        this.roots = new ArrayList<>(roots);
        image = createImage(roots);
        updateCanvas();
    }

    public BufferedImage createImage(List<LabelledRegularDD<D>> roots) {
        try {

            /* Setup sub process */
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng")
                    .redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();

            /* Generate and feed dot */
            DotGenerator<D> g = new DotGenerator<>();
            g.generate(p.getOutputStream(), roots);
            p.getOutputStream().close();

            /* Read image */
            try (InputStream in = p.getInputStream()) {
                return ImageIO.read(in);
            }

        } catch (IOException e) {
            return createErrorImage();
        }
    }

    private BufferedImage createErrorImage() {
        BufferedImage img = new BufferedImage(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();

        /* Draw white background */
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT);

        /* Draw random circles */
        g.setColor(ERROR_CIRCLES_COLOR);
        for (int i = 1; i <= ERROR_CIRCLES_N; i++) {
            int d = Math.min(DEFAULT_CANVAS_WIDTH, DEFAULT_CANVAS_HEIGHT) / ERROR_CIRCLES_N * i;
            int x = (int) (Math.random() * (DEFAULT_CANVAS_WIDTH - d));
            int y = (int) (Math.random() * (DEFAULT_CANVAS_HEIGHT - d));
            g.fillOval(x, y, d, d);
        }

        /* Print text */
        g.setColor(Color.BLACK);
        g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), ERROR_LINE_HEIGHT));
        int y = (DEFAULT_CANVAS_HEIGHT - 2 * ERROR_LINE_HEIGHT) / 2;
        g.drawString(ERROR_MESSAGE_L0, ERROR_LINE_HEIGHT, y);
        y += ERROR_LINE_HEIGHT;
        g.drawString(ERROR_MESSAGE_L1, ERROR_LINE_HEIGHT, y);

        return img;
    }

    @SuppressWarnings("nullness") // Only called once everything is initialized
    private void updateCanvas(@UnknownInitialization DotViewerFrame<D> this) {

        /* Update displayed image according to current zoom level */
        double zoomFactor = Math.pow(ZOOM_BASE, zoomExp);
        int width = (int) (image.getWidth() * zoomFactor);
        int height = (int) (image.getHeight() * zoomFactor);
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        JViewport viewport = scrollPane.getViewport();

        scrollPane.setViewportView(new JLabel(new ImageIcon(scaledImage)));
        scrollPane.setViewport(viewport);
    }

    private void onExportDot(ActionEvent e) { //NOPMD - suppressed UnusedFormalParameter
        File targetFile = promptTargetFile(FILE_EXTENSION_DOT);
        if (targetFile != null) {

            /* Use code generator to export dot format */
            DotGenerator<D> g = new DotGenerator<>();
            try {
                g.generateToFileSystem(targetFile, roots);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onExportPng(ActionEvent e) {
        File targetFile = promptTargetFile(FILE_EXTENSION_PNG);
        if (targetFile != null) {

            /* Save original image as png (zoom level has no effect on this) */
            try {
                ImageIO.write(image, "png", targetFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private @Nullable File promptTargetFile(String ensureExt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File targetFile = chooser.getSelectedFile();
            if (targetFile != null) {

                /* Ensure the expected file extension (case insensitive) */
                boolean validExt = targetFile.getName()
                                             .toLowerCase(Locale.ROOT)
                                             .endsWith(ensureExt.toLowerCase(Locale.ROOT));
                if (!validExt) {
                    File parent = targetFile.getParentFile();
                    String extendedName = targetFile.getName() + "." + ensureExt;
                    targetFile = new File(parent, extendedName);
                }
                return targetFile;
            }
        }
        return null;
    }
}
