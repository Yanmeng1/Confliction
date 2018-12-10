package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.VisUtil;

import java.awt.*;

public class ConflictVisualizer {

    public static int DELAY = 800;
    private ConflictModel model;
    private ConflictFrame frame;

    public ConflictVisualizer(int sceneWidth, int sceneHeight) {

        /** 初始化数据层: 1.agent初始化，2.装载邻居 **/
        model = new ConflictModel(Config.SIZE, Config.SIZE);

        /** 初始化视图层：将GUI组件放入事件分发线程 **/
        EventQueue.invokeLater(() -> {
            frame = new ConflictFrame("conflict", sceneWidth, sceneHeight);
            new Thread(() -> {
                run();
            }).start();
        });
    }

    private void run() {
        for (int i = 0; i < Config.ITERATOR_NUMBER; i ++) {
            /** 渲染 -> paintComponent中 **/
            double[][] currentState = model.currentState();
            model.setCurrentState(currentState);

            frame.render(model);
            VisUtil.pause(DELAY);

            /** 逻辑 **/
            Agent[][] nextAgents = model.nextAgents();
            model.setCurrentAgents(nextAgents);
        }
    }


}
