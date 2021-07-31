package kr.ac.kumoh.s20140716.cctvmap;

import java.util.Comparator;

class CCTV_weight {
    double cc_latitude;
    double cc_longtitude;
    double cc_weight1;
    double cc_weight2;
    double cc_weight3;

    public CCTV_weight(double cc_latitude, double cc_longitude, int cc_weight1, int cc_weight2, int cc_weight3) {
        this.cc_latitude = cc_latitude;
        this.cc_longtitude = cc_longitude;
        this.cc_weight1 = cc_weight1;
        this.cc_weight2 = cc_weight2;
        this.cc_weight3 = cc_weight3;
    }
    static class MiniComporator1 implements Comparator<CCTV_weight> {
        @Override
        public int compare(CCTV_weight o1, CCTV_weight o2) {
            double firstValue = o1.cc_weight1;
            double secondValue = o2.cc_weight1;

            if (firstValue > secondValue) {
                return 1;
            } else if (firstValue < secondValue) {
                return -1;
            } else
                return 0;
        }
    }

    static class MiniComporator2 implements Comparator<CCTV_weight> {
        @Override
        public int compare(CCTV_weight o1, CCTV_weight o2) {
            double firstValue = o1.cc_weight2;
            double secondValue = o2.cc_weight2;

            if (firstValue > secondValue) {
                return 1;
            } else if (firstValue < secondValue) {
                return -1;
            } else
                return 0;
        }
    }

    static class MiniComporator3 implements Comparator<CCTV_weight> {
        @Override
        public int compare(CCTV_weight o1, CCTV_weight o2) {
            double firstValue = o1.cc_weight3;
            double secondValue = o2.cc_weight3;

            if (firstValue > secondValue) {
                return 1;
            } else if (firstValue < secondValue) {
                return -1;
            } else
                return 0;
        }
    }
}
