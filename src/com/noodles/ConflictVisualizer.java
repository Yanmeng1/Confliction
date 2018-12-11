package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.VisUtil;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConflictVisualizer {

    public static int DELAY = 1500;
    private ConflictModel model;
    private ConflictFrame frame;
    private boolean isAnimated = true;

    public ConflictVisualizer(double sceneWidth, double sceneHeight) {

        /** 初始化数据层: 1.agent初始化，2.装载邻居 **/
        model = new ConflictModel(Config.SIZE, Config.SIZE);

        /** 初始化视图层：将GUI组件放入事件分发线程 **/
        EventQueue.invokeLater(() -> {
            frame = new ConflictFrame("conflict", sceneWidth, sceneHeight);
            frame.addKeyListener(new ConflictKeyListener());
            new Thread(() -> {
                run();
            }).start();
        });
    }

    private void run() {
        for (int i = 0; i < Config.ITERATOR_NUMBER; i ++) {
            /** 渲染 -> paintComponent中 **/
            double[][] currentState = model.getCurrentState();

            frame.render(model);
            VisUtil.pause(DELAY);

            /** 逻辑 **/
            if (isAnimated) {
                Agent[][] nextAgents = model.nextAgents();
                model.setCurrentAgents(nextAgents);
                currentState = model.currentState();
                model.setCurrentState(currentState);
                System.out.println("Iterator No." + model.getStepCount());
            }
        }
    }


    /** 添加键盘监听事件 **/
    private class ConflictKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent event) {
            if(event.getKeyChar() == ' ') {
                isAnimated = !isAnimated;
            }
        }
    }
}
