package com.stella.ccd;

import com.stella.ccd.utils.ParserConfig;

/**
 * @author Shamsi
 * @author ali
 * @author Waqas
 */
public class App {
    public static void main(String[] args) {

        try {
            final ParserConfig config = ParserConfig.getInstance();
            final String filePath = config.get(ParserConfig.DATASET_DIR_PATH);

            final CCDSQLScriptBuilder extractor = new CCDSQLScriptBuilder();
            extractor.build(filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
