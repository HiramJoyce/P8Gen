package com.tools.gen;

import com.tools.gen.entity.P8TradeInfo;
import com.tools.gen.utils.GenUtils;
import com.tools.gen.utils.Logger;
import com.tools.gen.utils.P8TradeInfoReader;
import sun.rmi.runtime.Log;

public class Main {
    // generate java code base package
    public static final String basePackage = "com.ccb.test";
    // trade source
    public static final String fileName = "P8TradeInfo.txt";

    public static void main(String[] args) throws Exception {

        // obtain trade info
        P8TradeInfo tradeInfo = P8TradeInfoReader.read("resources/" + fileName);
        Logger.info(tradeInfo);

        // generate java
        // TODO
        Logger.info(GenUtils.generate(tradeInfo.getInVo()));

        // generate xml
        // TODO
        Logger.info(GenUtils.generate(tradeInfo.getOutVo()));
    }
}
