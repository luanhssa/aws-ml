package com.learning.aws.ml;

public class Identifiers {
	private final static String BASE62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    private static String generateEntityId(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("-");
        // 62^11 -> 65 random bits, which is sufficient to avoid collisions
        // until customer has been creating 1 object per second for 200+ years.
        for(int i=0; i<11; i++) {
            int rand = (int) (Math.random() * BASE62_CHARS.length());
            sb.append( BASE62_CHARS.charAt(rand) );
        }
        return sb.toString();
    }
    
    public static String newDataSourceId() {
        return generateEntityId("ds");
    }

    public static String newMLModelId() {
        return generateEntityId("ml");
    }

    public static String newEvaluationId() {
        return generateEntityId("ev");
    }

    public static String newBatchPredictionId() {
        return generateEntityId("bp");
    }
}
