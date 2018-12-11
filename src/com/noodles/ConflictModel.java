package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.DataGene;
import com.noodles.util.Location;

/**
 * created by yanmeng 2018/12/09
 * 模型的主要流程：
 *      1. 初始化agent对象及全局信息；
 *      2. 根据agent对象属性信息，确定agent身份状态
 *      3. agent相互作用，相互影响(asset,culture,economicDissatisfy,culturalDifferent)
 */
public class ConflictModel {

    private int columns;
    private int rows;
    private Agent[][] currentAgents;        // 全局Agent集合，外部set注入
    private double[][] currentState;        // display信息 左 -> (0-1满意度 / <0暴乱) 右 -> (0-1资产分布)，外部set注入
    private int stepCount;

    private double governmentFactor = Config.FACTOR_GOVERNMENT;
    private double opponentFactor = Config.FACTOR_OPPONENT;

    /** 1. 初始化 **/
    public ConflictModel(int rows, int columns) {
        this.columns = columns;
        this.rows = rows;
        this.currentState = new double[rows][2*columns];
        this.currentAgents = new Agent[rows][columns];

        for (int i = 0; i < rows; i ++) {
            for (int j = 0; j < columns; j ++) {
                /* 创建agent(location, neighborLocation, asset, culture, isRioter) */
                currentAgents[i][j] = new Agent(new Location(i, j));
                currentAgents[i][j].loadNeighborLocation(rows, columns);
            }
        }
        /* 加载agent(neighbors, economicDissatisfy, cultureDifferent)属性信息 */
        loadAgentAttribute(currentAgents);
    }

    /** 2 核心逻辑: 捕获Agent当前状态  **/
    public double[][] currentState() {

        double[][] nextState = new double[currentState.length][currentState[0].length];
        double maxAsset = Double.MIN_VALUE;
        double minAsset = Double.MAX_VALUE;

        for (int row = 0; row < rows; row ++) {
            for (int col = 0; col < columns; col ++) {
                Agent agent = currentAgents[row][col];
                if (agent.isRioter()) {
                    double random = DataGene.uniform();
                    /* 政府有效镇压，更新agent身份、state和敌对势力诱导因子 */
                    if ((random < governmentFactor)) {
                        agent.setRioter(false);
                        nextState[row][col] = -currentState[row][col];
                        opponentFactor = (1 - Config.FACTOR_OPPONENT_TRANSFER) * opponentFactor;
                    } else {
                        nextState[row][col] = currentState[row][col];
                    }
                } else {
                    /* agent恐怖主义激活: 1.局部恐怖分子诱导 2.不满意冲破阈值，文化和外部敌对势力综合作用agent */
                    if (agent.microRioterLure() || agent.microCulturalLure(opponentFactor)) {
                        nextState[row][col] = -agent.getEconomicDissatisfy();
                        opponentFactor = (1 + Config.FACTOR_OPPONENT_TRANSFER) * opponentFactor;
                    } else {
                        nextState[row][col] = agent.getEconomicDissatisfy();
                    }
                }
                double asset = agent.getAsset();
                maxAsset = maxAsset > asset ? maxAsset : asset;
                minAsset = minAsset < asset ? minAsset : asset;
            }
        }

        /* 资产分布归一化 */
        for (int row = 0; row < rows; row ++) {
            for (int col = columns; col < 2 * columns; col ++) {
                Agent agent = currentAgents[row][col - columns];
                double assetValue = (agent.getAsset() - minAsset) / (maxAsset - minAsset);
                nextState[row][col] = assetValue;
            }
        }

        return nextState;
    }

    /** 3. 核心逻辑: 产生下一代 **/
    public Agent[][] nextAgents() {
        /* 3.1 创建下一代agent(location, neighborLocation) */
        Agent[][] nextAgents = new Agent[rows][columns];
        for (int row = 0; row < nextAgents.length; row ++) {
            for (int col = 0; col < nextAgents[row].length; col ++) {
                nextAgents[row][col] = (Agent) currentAgents[row][col].clone();
            }
        }
        /* 3.2 加载邻居信息(neighbors) */
        for (int row = 0; row < nextAgents.length; row ++) {
            for (int col = 0; col < nextAgents[row].length; col ++) {
                Agent agent = nextAgents[row][col];
                loadNeighbour(agent, nextAgents);
            }
        }

        /* 3.3 更新 asset  */
        for (int row = 0; row < nextAgents.length; row ++) {
            for (int col = 0; col < nextAgents[row].length; col ++) {
                Agent nextAgent = nextAgents[row][col];
                Agent[] currentNeighbors = currentAgents[row][col].getNeighbors();
                nextAgent.microEconomicEffect(currentNeighbors);
            }
        }

        /* 3.4 更新 asset, culture  */
        for (int row = 0; row < nextAgents.length; row ++) {
            for (int col = 0; col < nextAgents[row].length; col ++) {
                Agent nextAgent = nextAgents[row][col];
                nextAgent.macroEconomicEffect(Config.GDP);
                nextAgent.culturalIntegration();
            }
        }

        /* 3.5 基于asset和culture，更新economicDissatisfy, cultureDifferent  */
        for (int row = 0; row < nextAgents.length; row ++) {
            for (int col = 0; col < nextAgents[row].length; col ++) {
                Agent nextAgent = nextAgents[row][col];
                nextAgent.loadEconomicDissatisfy();
                nextAgent.loadCulturalDifferent();
            }
        }

        stepCount ++;
        return nextAgents;
    }


    /* 加载Agent属性信息  neighbors \ economicDissatisfy \ culturalDifferent  */
    private void loadAgentAttribute(Agent[][] agents) {
        int rows = agents.length;
        int columns = agents[0].length;
        for (int row = 0; row < rows; row ++) {
            for (int col = 0; col < columns; col ++) {
                Agent agent = agents[row][col];
                loadNeighbour(agent, agents);
                agent.loadEconomicDissatisfy();
                agent.loadCulturalDifferent();
            }
        }
    }

    /* 根据neighborsLocation直接获取neighbors */
    private void loadNeighbour(Agent agent, Agent[][] agents) {
        Location[] locations = agent.getNeighborsLocation();
        Agent[] neighbors = new Agent[locations.length];
        for (int i = 0; i < locations.length; i ++) {
            int row = locations[i].getRow();
            int col = locations[i].getCol();
            neighbors[i] = agents[row][col];
        }
        agent.setNeighbors(neighbors);
    }

    public void setCurrentAgents(Agent[][] agents) {
        currentAgents = agents;
    }

    public void setCurrentState(double[][] state) {
        currentState = state;
    }

    public double[][] getCurrentState() {
        return currentState;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getRows() {
        return rows;
    }
    public int getColumns() {
        return columns;
    }
}
