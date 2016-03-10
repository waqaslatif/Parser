package com.stella.utils;

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

            final CcdaSectionExtractor extractor = new CcdaSectionExtractor();
            extractor.extract(filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
