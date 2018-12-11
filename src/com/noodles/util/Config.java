package com.noodles.util;

public class Config {

    public final static int ITERATOR_NUMBER = 1000;
    public final static int SIZE = 40;

    public final static double GDP = 0.08;
    /** 随机数变异系数 **/
    public final static double CV = 0.02;
    public final static double ASSET = 50.0;

    /** 财富值转换量 **/
    public final static double ASSET_TRANSFER_RATE = 0.1;
    /** 社会机制：
     *  穷人-30%的可能性将差距财富的0.1从富人手里拿到
     *  富人-70%从可能性将差距财富的0.1从富人手里拿到
     **/
    public final static double ASSET_RICH2POOR_PROBABILITY = 0.3;
    public final static double ASSET_POOR2RICH_PROBABILITY = 0.7;

    /** 不满意度阈值(周围人) **/
    public final static double DISSATISFY_THRESHOLD = 0.6;
    /** 恐怖分子转变阈值（周围恐怖分子数量比例） **/
    public final static double RIOTER_TRANSFER_THRESHOLD = 0.7;
    /** 截然不同的文化阈值 **/
    public final static double CULTURAL_DIFFERENT_THRESHOLD = 0.6;

    /** 文化分布比例 **/
    public final static double CULTURAL_ONE2TWO_RATE = 0.7;

    /** 文化值 **/
    public final static double CULTURAL_ONE = 1.0;
    public final static double CULTURAL_MIDDLE = 1.5;
    public final static double CULTURAL_TWO = 2.0;



    /** 文化转换率 **/
    public final static double CULTURAL_ONE2TWO_TRANSFER = 0.05;
    public final static double CULTURAL_ONE2MIDDLE_TRANSFER = 0.2;
    public final static double CULTURAL_ONE2ONE_TRANSFER = 0.75;
    public final static double CULTURAL_TWO2ONE_TRANSFER = 0.05;
    public final static double CULTURAL_TWO2MIDDLE_TRANSFER = 0.2;
    public final static double CULTURAL_TWO2TWO_TRANSFER = 0.75;

    /** 政府有效镇压因子 **/
    public final static double FACTOR_GOVERNMENT = 0.8;
    /** 敌对势力诱导因子 **/
    public final static double FACTOR_OPPONENT = 0.01;
    /** 每一次暴乱敌对势力诱导因子的增长幅度，每一次镇压政府镇压有效因子敌对势力诱导因子的减小幅度 **/
    public final static double FACTOR_OPPONENT_TRANSFER = 0.05;



}
