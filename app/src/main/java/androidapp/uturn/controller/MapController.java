package androidapp.uturn.controller;

import androidapp.uturn.model.TF;
import androidapp.uturn.model.TSP;
import androidapp.uturn.model.PolyLine;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by admin on 2016-02-01
 */
public class MapController {

    private int nof = 0;
    private TF tf = new TF();
    private TSP tsp = new TSP();
    private PolyLine polyLine = new PolyLine();

    public void setListMatrix(List<String> list) {

        nof = list.size();
        tf.setListMatrix(list);
    }

    public void setMode(String mode) {

        tf.setMode(mode);
    }

    public int[][] calculateTimeResult() {

        long[][] times = tf.fetchMatrixResult();
        tsp.setNof(nof);
        return tsp.startMinimization(times);
    }

    public List<LatLng> decodePoly(String encodedString) {

        return polyLine.decodePoly(encodedString);
    }

    public String[] getHTML(int index) {

        return tf.getHtmlInstructions(index);
    }

    public void setDirectionOrigin(String origin) {

        tf.setOrigin(origin);
    }

    public void setDirectionDestination(String destination) {

        tf.setDestination(destination);
    }

    public String fetchPolyLine() {

        return tf.fetchResult();
    }
}