package com.alibaba.intl.bertforabsa;

import java.util.List;

class AEPrediction {
    public AEPrediction() {
    }

    public List<List<List<Double>>> logits;
    public List<List<String>> raw_X;
    public List<List<Integer>> idx_map;
}
