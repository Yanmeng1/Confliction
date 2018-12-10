package com.noodles;

import com.noodles.util.Config;
import com.noodles.util.DataGene;
import com.noodles.util.Location;

import java.util.ArrayList;

/**
 * agent主要流程：
 *  1.创建之初初始化财富值
 *  2.加载周围邻居(后期的3.获取不满意程度，4.微观经济影响是基于邻居的)
 *  3.获取不满意程度
 *  4.微观经济影响
 *  5.宏观经济影响
 */
public class Agent implements Cloneable {

    private Location location;
    private double asset;
    private double culture;
    private double economicDissatisfy; // [0,1]
    private double cultureDifferent;   // [0,1]
    private boolean isRioter;
    private Agent[] neighbors;

    /** 在agent.clone()虽然会对neighborsLocation进行浅拷贝，因为位置信息是固定的，所以不会影响结果 **/
    private Location[] neighborsLocation;

    public Agent(Location location) {
        this.location = location;
        this.asset = DataGene.Normal(Config.ASSET, Config.CV);
        if (DataGene.uniform() < Config.CULTURAL_ONE2TWO_RATE) {
            this.culture = Config.CULTURAL_ONE;
        } else {
            this.culture = Config.CULTURAL_TWO;
        }
        this.isRioter = false;
    }

    /** 作用1 ：局部贫富差距异性 **/
    public void microEconomicEffect(Agent[] neighbors) {

        for (int i = 0; i < neighbors.length; i ++) {
            double gapAsset = neighbors[i].getAsset() - asset;
            double microAsset = Config.ASSET_TRANSFER_RATE * gapAsset;
            double prob = DataGene.uniform();
            /** 穷人:小概率从富人那里拿到财富0.3 **/
            if (gapAsset > 0) {
                if (prob < Config.ASSET_RICH2POOR_PROBABILITY) {
                    asset += microAsset;
                    neighbors[i].setAsset(neighbors[i].getAsset() - microAsset);
                } else {
                    /** 大概率更穷 **/
                    asset -= microAsset;
                    neighbors[i].setAsset(neighbors[i].getAsset() + microAsset);
                }
            } else {
                /** 富人:大概率0.7更富 **/
                microAsset = -microAsset;
                if (prob < Config.ASSET_POOR2RICH_PROBABILITY) {
                    asset += microAsset;
                    neighbors[i].setAsset(neighbors[i].getAsset() - microAsset);
                } else {
                    asset -= microAsset;
                    neighbors[i].setAsset(neighbors[i].getAsset() + microAsset);
                }

            }
        }
    }

    /** 作用2 ：国民经济影响 **/
    public void macroEconomicEffect (double GDP) {
        double macroAsset = GDP * asset;
        asset += macroAsset;
    }

    /** 作用3 ： 文化交融 **/
    public void culturalIntegration() {
        if (Config.CULTURAL_MIDDLE - culture < 0.01) {
            return;
        }
        double random = DataGene.uniform();
        if ((Config.CULTURAL_ONE - culture) < 0.01) {
            if (random < Config.CULTURAL_ONE2TWO_TRANSFER) {
                culture = Config.CULTURAL_TWO;
            } else if (random < Config.CULTURAL_ONE2MIDDLE_TRANSFER) {
                culture = Config.CULTURAL_MIDDLE;
            } else {
                culture = Config.CULTURAL_ONE;
            }
        } else {
            if (random < Config.CULTURAL_TWO2ONE_TRANSFER) {
                culture = Config.CULTURAL_ONE;
            } else if (random < Config.CULTURAL_TWO2MIDDLE_TRANSFER) {
                culture = Config.CULTURAL_MIDDLE;
            } else {
                culture = Config.CULTURAL_TWO;
            }
        }
    }

    /** 诱变1 ：局部恐怖势力诱变 **/
    public boolean microRioterLure() {
        int rioterNumber = 0;
        for (int i = 0; i < neighbors.length; i ++) {
            if (neighbors[i].isRioter()) {
                rioterNumber ++;
            }
        }
        double rioterFactor = ((double) rioterNumber) / neighbors.length;
        /** RIOTER_TRANSFER_THRESHOLD = 70% **/
        if (rioterFactor > Config.RIOTER_TRANSFER_THRESHOLD) {
            isRioter = true;
        }
        return isRioter;
    }

    /** 诱变2 ：不满意度达到阈值，文化差异与敌对势力综合影响激活 opponentFactor 0.2  **/
    public boolean microCulturalLure(double opponentFactor) {
        if (economicDissatisfy > Config.DISSATISFY_THRESHOLD) {
            double cultureLure = cultureDifferent * opponentFactor;
            double prob = DataGene.uniform();
            if (prob < cultureLure) {
                isRioter = true;
            }
        }
        return isRioter;
    }

    /** 基于邻居，经济差异所造成的不满意度[0,1] **/
    public void loadEconomicDissatisfy() {
        int disCount = 0;
        for (int i = 0; i < neighbors.length; i ++) {
            if (neighbors[i].getAsset() > asset) {
                disCount ++;
            }
        }
        economicDissatisfy = ((double) disCount) / neighbors.length;
    }

    /** 基于邻居，文化差异[0,1] **/
    public void loadCulturalDifferent() {
        int difCount = 0;
        for (int i = 0; i < neighbors.length; i ++) {
            double cultureDifferent = neighbors[i].getCulture() - culture;
            if (cultureDifferent < 0) cultureDifferent = - cultureDifferent;
            if (cultureDifferent > Config.CULTURAL_DIFFERENT_THRESHOLD) {
                difCount ++;
            }
        }
        cultureDifferent = ((double) difCount) / neighbors.length;
    }

    public void loadNeighborLocation(int rows, int columns) {
        ArrayList<Location> locationList = new ArrayList<>();
        for (Location loc : Location.adjacent) {
            if (loc.getRow() == 0 && loc.getCol() == 0) {
                continue;
            }
            int row = location.getRow() + loc.getRow();
            int col = location.getCol() + loc.getCol();
            if (row >= 0 && row < rows && col >= 0 && col < columns) {
                locationList.add(new Location(row, col));
            }
        }
        this.neighborsLocation = locationList.toArray(new Location[locationList.size()]);
        this.neighbors = new Agent[locationList.size()];
    }

    @Override
    public Object clone() {
        Object cloneObj = null;
        try {
            cloneObj = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloneObj;
    }

    public double getAsset() {
        return asset;
    }
    public void setAsset(double asset) {
        this.asset = asset;
    }
    public void setNeighbors(Agent[] neighbors) { this.neighbors = neighbors; }
    public Agent[] getNeighbors() {
        return neighbors;
    }
    public double getCulture() {
        return culture;
    }
    public Location[] getNeighborsLocation() {
        return neighborsLocation;
    }
    public boolean isRioter() {
        return isRioter;
    }
    public void setRioter(boolean rioter) {
        isRioter = rioter;
    }

    public double getEconomicDissatisfy() {
        return economicDissatisfy;
    }
    public double getCultureDifferent() {
        return cultureDifferent;
    }

    @Override
    public String toString() {
        return super.toString() + " >> location ("+ location.getRow() + ","+ location.getCol() +");  " +
                "neighbors.size() " + neighborsLocation.length + ", asset : " + asset + ";  culture : " + culture;
    }
}
