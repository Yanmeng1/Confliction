package com.noodles.util;

import java.util.Random;

public class DataGene {

    public static double uniform() {
        Random random = new Random();
        return random.nextDouble();
    }

    public static double uniform(double from, double to) {
        Random random = new Random();
        return random.nextDouble() * (to - from) + from;
    }

    /**
     * 返回一个服从N(mean, (cv*mean)^2)的随机数
     * @param mean
     * @param cv 变异系数 = 标准差 / 平均值
     * @return
     */
    public static double Normal(double mean, double cv) {
        Random random = new Random();
        return cv * mean * random.nextGaussian() + mean;
    }

    public static double Normal() {
        Random random = new Random();
        return random.nextGaussian();
    }

}
