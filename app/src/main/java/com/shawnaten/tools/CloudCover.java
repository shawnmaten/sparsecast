package com.shawnaten.tools;

public class CloudCover {
    public static final int CLEAR = 0;
    public static final int SCATTERED = 1;
    public static final int BROKEN = 2;
    public static final int OVERCAST = 3;

    public static int getCloudCode(double coverage) {
        if (coverage >= .75) {
            double distToOvercast = 1 - coverage;
            double distToMod = coverage - .75;

            if (distToOvercast < distToMod)
                return OVERCAST;
            else
                return BROKEN;
        } else if (coverage >= .4) {
            double distToBroken = .75 - coverage;
            double distToScattered = coverage - .4;

            if (distToBroken < distToScattered)
                return BROKEN;
            else
                return SCATTERED;
        } else {
            double distToScattered = .4 - coverage;

            if (distToScattered < coverage)
                return SCATTERED;
            else
                return CLEAR;
        }
    }
}
