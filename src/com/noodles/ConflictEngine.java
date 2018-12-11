package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.VisUtil;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * created by yanmeng 2018/12/11
 * 模型引擎：构建模型与渲染视图，控制model和view交互
 */
public class ConflictEngine {

    private ConflictModel model;
    private ConflictFrame frame;
    private boolean isAnimated = true;
    public static int DELAY = 1500;

    public ConflictEngine(double sceneWidth, double sceneHeight) {

        /* 初始化数据层(model) */
        model = new ConflictModel(Config.SIZE, Config.SIZE);

        /* 初始化视图层(将GUI组件放入事件分发线程) */
        EventQueue.invokeLater(() -> {
            frame = new ConflictFrame("CONFLICT", sceneWidth, sceneHeight);
            frame.addKeyListener(new ConflictKeyListener());
            new Thread(() -> {
                run();
            }).start();
        });
    }

    private void run() {
        for (int i = 0; i < Config.ITERATOR_NUMBER; i ++) {
            /* 渲染 */
            frame.render(model);
            VisUtil.pause(DELAY);

            /* 逻辑 */
            if (isAnimated) {
                Agent[][] nextAgents = model.nextAgents();
                model.setCurrentAgents(nextAgents);
                double[][] currentState = model.currentState();
                model.setCurrentState(currentState);
                System.out.println("Iterator No." + model.getStepCount());
            }
        }
    }

    /* 添加键盘监听事件 */
    private class ConflictKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent event) {
            if(event.getKeyChar() == ' ') {
                isAnimated = !isAnimated;
            }
        }
    }
}
