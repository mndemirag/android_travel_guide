package androidapp.uturn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class TF {

    private String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private String mainUrl = url;

    private String origin = null;
    private String destination = null;

    private String key = "AIzaSyAAHn2GZ2EufUkY5GysfTrVWlMTJVRSPk4";
    private String keyMatrix = "AIzaSyAAHn2GZ2EufUkY5GysfTrVWlMTJVRSPk4";

    private String urlMatrix = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private String urlMatrixCopy = urlMatrix;

    private List<String> list = null;
    private long[][] times = null;
    private String mode;

    private LinkedList<String[]> html_instructions_list = new LinkedList<>();

    public void setOrigin(String origin){
        this.origin = origin;
    }

    public void setDestination(String destination){
        this.destination = destination;
    }

    public void setListMatrix(List<String> list){
        this.list = list;
    }

    public void setMode(String mode){
        this.mode = mode;
    }

    public long[][] fetchMatrixResult() {

        urlMatrix = urlMatrixCopy;
        urlMatrix += "origins=";

        int length = list.size();
        times = new long[length][length];
        for(int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                times[i][j] = 0;
            }
        }

        if(length == 1) {
            urlMatrix += list.get(0);
        } else {
            for(int i = 0; i < length - 1; i++) {
                urlMatrix += list.get(i) + "|";
            }

            urlMatrix += list.get(length - 1);
        }
        urlMatrix += "&destinations=";

        if(length == 1) {
            urlMatrix += list.get(0);
        }
        else {
            for(int i = 0; i < length - 1; i++){
                urlMatrix += list.get(i) + "|";
            }
            urlMatrix += list.get(length - 1);
        }

        urlMatrix += "&mode=" + mode;
        urlMatrix += "&key=" + keyMatrix;
        System.out.println(urlMatrix);

        String json = retrieveURLData();
        System.out.println("JSON: ");
        System.out.println(json);
        String[][] matrix = buildTimeMatrix(json, length);
        for(String[] row: matrix){
            System.out.print("| ");
            for(String element: row) {
                System.out.print(element + " | ");
            }

            System.out.println();
        }

        System.out.println("\nTIME MATRIX\n");
        for(long[] row: times){
            System.out.print("| ");
            for(long time: row){
                System.out.print(time+" | ");
            }

            System.out.println();
        }
        System.out.println("\n\n");

        return times;
    }

    private String retrieveURLData() {

        try {

            int read;
            char[] chars = new char[1024];
            URL urlPath = new URL(urlMatrix);
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlPath.openStream()));
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();

        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        return null;
    }

    private String[][] buildTimeMatrix(String json, int length){

        System.out.println("\n\n");
        String[][] matrix = new String[length + 1][length + 1];

        try{
            JSONObject jObj = new JSONObject(json);
            JSONArray jArray = (JSONArray) jObj.get("destination_addresses");
            for(int i = 1; i < length + 1; i++) {

                matrix[0][i] = jArray.getString(i - 1);
                matrix[i][0] = jArray.getString(i - 1);
            }

            jArray = (JSONArray) jObj.get("rows");
            for(int i = 0; i < jArray.length(); i++) {

                JSONObject row = (JSONObject)jArray.get(i);
                JSONArray elements = (JSONArray) row.get("elements");

                for(int j = 0; j < elements.length(); j++) {
                    String text;
                    long timeValue;

                    JSONObject element = (JSONObject) elements.get(j);
                    JSONObject duration = (JSONObject) element.get("duration");
                    text = (String) duration.get("text");
                    timeValue = duration.getLong("value");

                    matrix[i+1][j+1] = text;
                    times[i][j] = timeValue;
                }
            }
        } catch (JSONException je){
            je.printStackTrace();
        }

        return matrix;
    }

    public String[] getHtmlInstructions(int index){

        return html_instructions_list.get(index);
    }

    public String fetchResult() {

        url = mainUrl;
        url += "origin=" + origin;
        url += "&destination=" + destination;
        url += "&mode=" + mode;
        url += "&key=" + key;

        try{

            System.out.println(url);

            int read;
            URL urlPath = new URL(url);
            char[] chars = new char[1024];
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlPath.openStream()));
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            JSONObject json = new JSONObject(buffer.toString());
            System.out.println(buffer.toString());

            JSONArray array = (JSONArray) json.get("routes");
            if(array.length() == 0){
                return "NO ROUTE";
            }

            JSONObject obj = (JSONObject) array.get(0);

            JSONArray array1 = (JSONArray) obj.get("legs");
            JSONObject obj2 = (JSONObject) array1.get(0);
            String origin = obj2.getString("start_address");
            String destination = obj2.getString("end_address");
            String header = origin +" --> "+destination+"\n";
            JSONArray array2 = (JSONArray) obj2.get("steps");
            String[] html_instructions = new String[array2.length()+1];
            html_instructions[0] = header;
            for(int i = 0; i < array2.length(); i++){
                JSONObject obj2a = (JSONObject) array2.get(i);
                String html = obj2a.getString("html_instructions");
                html_instructions[i+1] = html;
            }
            html_instructions_list.addLast(html_instructions);

            JSONObject obj3 = (JSONObject) obj.get("overview_polyline");
            return obj3.getString("points");

        }  catch(IOException | JSONException ex){

            ex.printStackTrace();
        }

        return null;
    }
}
