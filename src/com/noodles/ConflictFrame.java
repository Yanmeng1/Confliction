package com.noodles;

import com.noodles.util.VisUtil;

import javax.swing.*;
import java.awt.*;

public class ConflictFrame extends JFrame {

    private int canvasWidth;
    private int canvasHeight;
    private ConflictModel model;

    public ConflictFrame(String title, int canvasWidth, int canvasHeight) {
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

    public int getCanvasWidth() {return canvasWidth;}
    public int getCanvasHeight() {return canvasHeight;}

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

            /** 模型渲染 **/
            VisUtil.setStrokeWidth(g2d, 3);
            VisUtil.setColor(g2d, VisUtil.Blue);
            double[][] state = model.getCurrentState();

            double squareHeight = canvasHeight / model.getRows();
            double squareWidth = canvasWidth / (model.getColumns() * 2);

            for (int i = 0; i < model.getRows(); i++) {
                for (int j = 0; j < model.getColumns() * 2; j ++) {
                    int alpha = (int) ((1 - state[i][j]) * 255);
                    if (alpha < 0) alpha = 0;
                    if (alpha > 255) alpha = 255;
                    g2d.setColor(new Color(161, 23, 21, alpha));

                    VisUtil.fillRectangle(g2d, i * squareHeight, j * squareWidth, squareWidth, squareHeight);
//                    g2d.setColor(new Color(0, 139, 69, alpha));
//                    VisUtil.fillRectangle(g2d, i * squareHeight, (j + model.getColumns()) * squareWidth, squareWidth, squareHeight);
//                    if (j > model.getColumns()) System.out.print(state[i][i] + " ");
                }
                System.out.println();
            }
            VisUtil.drawText(g2d, String.valueOf(model.getRows()), canvasWidth / 4, canvasHeight / 4);
            VisUtil.drawText(g2d, String.valueOf(model.getColumns() * 2), canvasWidth * 3 / 4, canvasHeight / 4);
            VisUtil.drawText(g2d, String.valueOf(model.currentState().length), canvasWidth / 4, canvasHeight / 2);
            VisUtil.drawText(g2d, String.valueOf(model.currentState()[0].length), canvasWidth * 3 / 4, canvasHeight / 2);


        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(canvasWidth, canvasHeight);
        }
    }
}
