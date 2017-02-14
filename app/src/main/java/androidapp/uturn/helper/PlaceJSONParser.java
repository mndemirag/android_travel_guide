package androidapp.uturn.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joakim on 2016-02-29
 */
public class PlaceJSONParser {

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {

        JSONArray jPlaces = null;

        try{
            jPlaces = jsonObject.getJSONArray("predictions");
        } catch (JSONException jsone){
            jsone.printStackTrace();
        }

        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {

        int placesCount = jPlaces.length();
        HashMap<String, String> place;
        List<HashMap<String, String>> placesList = new ArrayList<>();

        for(int i = 0; i < placesCount; i++) {

            try{
                place = getPlace((JSONObject)jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException jsone){

                jsone.printStackTrace();
            }
        }

        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jPlace){

        String id, reference, description;
        HashMap<String, String> place = new HashMap<>();

        try {

            description = jPlace.getString("description");
            id = jPlace.getString("id");
            reference = jPlace.getString("reference");

            place.put("description", description);
            place.put("_id", id);
            place.put("reference", reference);

        } catch (JSONException jsone) {

            jsone.printStackTrace();
        }

        return place;
    }
}
