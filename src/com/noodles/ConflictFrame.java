package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.VisUtil;

import javax.swing.*;
import java.awt.*;

/**
 * created by yanmeng 2018/12/10
 */
public class ConflictFrame extends JFrame {

    private double canvasWidth;
    private double canvasHeight;
    private ConflictModel model;

    public ConflictFrame(String title, double canvasWidth, double canvasHeight) {
        super(title);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        ConCanvas canvas = new ConCanvas();
        setContentPane(canvas);
        pack();

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public ConflictFrame(String title) {
        this(title, 1024, 768);
    }

    public void render(ConflictModel model) {
        this.model = model;
        repaint();
    }

    public double getCanvasWidth() {return canvasWidth;}
    public double getCanvasHeight() {return canvasHeight;}

    private class ConCanvas extends JPanel {
        public ConCanvas() {
            super(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.addRenderingHints(hints);

            /* 模型渲染 */
            double[][] state = model.getCurrentState();
            double squareHeight = canvasHeight / model.getRows();
            double squareWidth = canvasWidth / ((model.getColumns() * 2));

            for (int i = 0; i < model.getRows(); i++) {
                for (int j = 0; j < model.getColumns(); j ++) {
                    if (state[i][j] < 0) {
                        g2d.setColor(VisUtil.Black);
                    } else {
                        int alpha;
                        if (state[i][j] < Config.DISSATISFY_THRESHOLD) {
                            double lower2One = state[i][j] / Config.DISSATISFY_THRESHOLD;
                            alpha = (int)((1 - lower2One) * 255);
                            g2d.setColor(new Color(0, 139, 69, alpha));
                        } else {
                            double higher2One = (state[i][j] - Config.DISSATISFY_THRESHOLD) / (1 - Config.DISSATISFY_THRESHOLD);
                            alpha = (int)(higher2One * 255);
                            g2d.setColor(new Color(161, 23, 21, alpha));
                        }
                    }
                    VisUtil.fillRectangle(g2d, j * squareWidth, i * squareHeight, squareWidth, squareHeight);
                }
            }

            for (int i = 0; i < model.getRows(); i++) {
                for (int j = model.getColumns(); j < model.getColumns() * 2; j ++) {
                    int alpha = (int) (state[i][j] * 255);
                    g2d.setColor(new Color(0, 0, 139, alpha));
                    VisUtil.fillRectangle(g2d, j * squareWidth,i * squareHeight, squareWidth, squareHeight);
                }
            }


        }

        @Override
        public Dimension getPreferredSize() {
            Dimension dimension = new Dimension();
            dimension.setSize(canvasWidth, canvasHeight);
            return dimension;
        }
    }
}
