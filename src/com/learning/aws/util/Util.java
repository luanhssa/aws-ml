package com.learning.aws.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.opencsv.CSVReader;

public class Util {
	private static final String NUMERIC = "NUMERIC";
	private static final String CATEGORICAL = "CATEGORICAL";
	private static final String BINARY = "BINARY";
	private static final String TEXT = "TEXT";
	
    /**
     * Reads an entire file
     * @param filename local file to read
     * @return String with entire contents of file
     * @throws IOException 
     */
    public static String loadFile(String filename) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        try {
            String strline;
            StringBuffer output = new StringBuffer();
            while((strline=br.readLine())!=null)
            {
                output.append(strline);
            }
            return output.toString();
        } finally {
            br.close();
            fr.close();
        }
    }

    @SuppressWarnings("resource")
	public static void createDataSchema(String data, String schemaFilename) {
		try {
			String[] csv = new CSVReader(new FileReader(data)).readNext();
			JSONObject jSchema = new JSONObject();
			JSONArray attributes = new JSONArray();
			jSchema.put("version", "1.0");
			jSchema.put("rowId", "ID");
			jSchema.put("targetAttributeName", "CLASS");
			jSchema.put("dataFormat", "CSV");
			jSchema.put("dataFileContainsHeader", true);
			for(int i = 0; i < 102; i++) {
				JSONObject att = new JSONObject();
				att.put("attributeName", getHeaderByLocation(csv, i));
				att.put("attributeType", NUMERIC);	
				attributes.put(att);
			}
			JSONObject att = new JSONObject();	
			att.put("attributeName", "ID");
			att.put("attributeType", CATEGORICAL);	
			attributes.put(att);
			att = new JSONObject();	
			att.put("attributeName", "CLASS");
			att.put("attributeType", BINARY);	
			attributes.put(att);
			jSchema.put("attributes", attributes);
			
			FileWriter file = new FileWriter(schemaFilename);
			file.write(jSchema.toString());
			file.close();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}
    
	public static String getHeaderByLocation(String[] headers, int column) {
		   return Arrays.asList(headers).get(column);
	}
}
