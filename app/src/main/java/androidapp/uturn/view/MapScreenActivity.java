package androidapp.uturn.view;

// Android Libraries
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

// Google API and Map client
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

// Java Libraries
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

// Application-specific Libraries
import androidapp.uturn.controller.MapController;
import androidapp.uturn.helper.SearchInputs;
import androidapp.uturn.model.SphericalUtil;
import layout.SaveRouteFragment;

/**
 * Activity that is responsible for rendering the Map screen
 */
public class MapScreenActivity extends NavigationSliderActivity implements SaveRouteFragment.saveRouteDialogListener,
                OnMapReadyCallback, OnCameraChangeListener, OnStreetViewPanoramaReadyCallback, OnStreetViewPanoramaChangeListener,
                ConnectionCallbacks, OnStreetViewPanoramaCameraChangeListener, StreetViewPanorama.OnStreetViewPanoramaClickListener,
                OnMapClickListener, OnPolylineClickListener, LocationListener, OnConnectionFailedListener, GoogleMap.OnMapLoadedCallback,
				GoogleMap.OnMarkerClickListener, RadioGroup.OnCheckedChangeListener {

    // Variable that holds the map instance
    private GoogleMap m_routeMap;

    //private GoogleApiClient client;
    private LocationRequest locationRequest;
    private StreetViewPanorama streetViewPanorama;
    private SupportStreetViewPanoramaFragment streetViewPanoramaFragment;

    // Variable that stores the name of the route to be saved into the DB
    private int nof = 0;
    private List<String> list;
    private Marker[] markers;
    private int markerIndex = 0;
    private String m_RouteName;

    private String mode = "driving";
    private MapController objController = new MapController();
    private LinkedList<List<LatLng>> listPolylines = new LinkedList<>();
    private LinkedList<Polyline> polyLines = new LinkedList<>();

    // Variables to store email and name of the user
    private SearchInputs m_objSearchInputs;

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    int index = 0;
    private TextView textView;
    private boolean viewPolylineInfo = false;
    private int polyLineID = -1;

    // My location
    Location myLocation = null;

    // Animation
    private float bearing = 0;
    private int currentPositionIndex = 0;
    private boolean playSubroute = false;
    private boolean reset = false;
    private int polyLineIdIndex = 0;
    private boolean playRoute = false;
    private boolean waypoint = false;
    private int SV_current_index = 0;
    private float bearingSV;
    private boolean bearingFinished = false;
    boolean showStreetView = false;



    // Variables for route follow functionality
    boolean polyLinesDrawn = false;
    TextView currentLL;
    TextView currentB;
    TextView distanceTo;
    TextView bearingTo;
    Button beginRoute;
    boolean following = false;
    int bearingToPoint;
    int distanceToPoint;
    int polylineIndex = 0;
    int pointIndex = 0;
    LatLng currentLatLng;
    float currentBearing;

    CheckBox checkBox;
    private HashMap<Integer, Integer> markerIndexToPolylineIndex = new HashMap<>();
    boolean markerClicked = false;
    private HashMap<Integer, Boolean> subrouteMarked = new HashMap<>();
    private RadioGroup rgroup;
    private List<String> routeInfoTexts = new ArrayList<>();
    private boolean clearRB = false;
    private LinearLayout navigationButtonBar;
    private Button showTextInfo;
    private Button showStreetViewButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * Adding map screen layout to the frame layout of the parent Navigation Slider class.
         */
       getLayoutInflater().inflate(R.layout.activity_map_screen, mContentLayout);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            // Retrieve the user mail id and display name
            m_strUserMailID = extras.getString(RegistrationScreenActivity.KEY_MAIL_ID);
            m_strUserDispName = extras.getString(RegistrationScreenActivity.KEY_NAME);

            // Retrieve the user search inputs
            m_objSearchInputs = extras.getParcelable("user_search_inputs");
            list = m_objSearchInputs.m_DestinationList;
            markers = new Marker[list.size()];

            String commute = m_objSearchInputs.m_commuteMode.toString();
            ImageButton button = null;
            if(commute.equals("WALK")) {
                button = (ImageButton)findViewById(R.id.btnByWalk);
				mode = "walking";
			}
            else if(commute.equals("PUBLIC_TRANSPORT")) {
                button = (ImageButton)findViewById(R.id.btnByPublicTransport);
				mode = "transit";
        	}
			else if(commute.equals("CAR")) {
                button = (ImageButton)findViewById(R.id.btnByCar);
				mode = "driving";
			}
            else if(commute.equals("BIKE")) {
                button = (ImageButton)findViewById(R.id.btnByBike);
				mode = "bicycling";
			}
			objController.setMode(mode);
            if(button != null) {
                button.setBackgroundColor(Color.YELLOW);
            }

            System.out.println("COMMUTE: "+m_objSearchInputs.m_commuteMode.toString());
        }
        setInputsForSearch();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapFragment);
        mapFragment.getMapAsync(this);

        rgroup = (RadioGroup)findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(this);

        navigationButtonBar = (LinearLayout) findViewById(R.id.navigationButtonBar);

        beginRoute = (Button) findViewById(R.id.btnStartNavigation);
        beginRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int size = polyLines.get(polyLineID).getPoints().size();
                LatLng latLng = polyLines.get(polyLineID).getPoints().get(size - 1);

                double lat = latLng.latitude;
                double lon = latLng.longitude;
                String m = null;
                if (mode.equals("transit")) {
                    System.out.println("TRANSIT");
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f&dirflg=r", lat, lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                } else {
                    if (mode.equals("driving")) m = "d";
                    if (mode.equals("bicycling")) m = "b";
                    if (mode.equals("walking")) m = "w";
                    String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f&mode=%s", lat, lon, m);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }

            }
        });

        textView = (TextView) findViewById(R.id.polylineInfo);
        textView.setMovementMethod(new ScrollingMovementMethod());

        showTextInfo = (Button)findViewById(R.id.routeInfoText);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(200);

        // Google Street View
        streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetviewFragment);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        streetViewPanorama = panorama;
                        streetViewPanorama.setOnStreetViewPanoramaChangeListener(MapScreenActivity.this);
                        streetViewPanorama.setOnStreetViewPanoramaCameraChangeListener(MapScreenActivity.this);
                        streetViewPanorama.setOnStreetViewPanoramaClickListener(MapScreenActivity.this);

                        if (savedInstanceState == null) {

                            streetViewPanorama.setPosition(SYDNEY);
                        }
                    }
                });

        hideSV();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Display a screen for user to enter the route name to save
        if (id == R.id.saveRouteButton) {

            displaySaveRouteDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySaveRouteDialog() {

        FragmentManager fm = getSupportFragmentManager();
        SaveRouteFragment frag = new SaveRouteFragment();
        frag.show(fm, "fragment_save_route");
    }

    @Override
    public void onFinishInputDialog(String strRouteName) {

    }

    /**
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Get the map and then start working on the map
        m_routeMap = googleMap;

        m_routeMap.setBuildingsEnabled(true);
        m_routeMap.setOnMapClickListener(this);
        m_routeMap.setOnPolylineClickListener(this);
        m_routeMap.setOnCameraChangeListener(this);
        m_routeMap.setOnMapLoadedCallback(this);
        m_routeMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            System.out.println("MY LOCATION ENABLED");
            m_routeMap.setMyLocationEnabled(true);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //Criteria criteria = new Criteria();
            //String provider = locationManager.getBestProvider(criteria, true);
            //String provider = LocationManager.GPS_PROVIDER;
            //myLocation = locationManager.getLastKnownLocation(provider);
            //List<String> providers = locationManager.getAllProviders();

            List<String> providers = locationManager.getProviders(true);
            for(String prv: providers) {
                System.out.println("| " + prv);
                myLocation = locationManager.getLastKnownLocation(prv);
                if(myLocation != null) {
                    System.out.println("MYLOCATION: " + myLocation);
                    break;
                }
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }

        int i = 0;
        for(String location: list) {

            List<Address> addresses = null;
            Geocoder geocoder = new Geocoder(this);
            try{
                if(location.equals("CURRENT_LOCATION")) {

                    addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                    String lat = myLocation.getLatitude()+"";
                    String lon = myLocation.getLongitude()+"";
                    list.set(i, new String(lat + "," + lon));
                } else {
                    addresses = geocoder.getFromLocationName(location, 1);
                }
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

            if(addresses != null) {

                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                markers[i++] = m_routeMap.addMarker(new MarkerOptions().position(latLng).title(location));
            }
        }

        System.out.println("Current Location is : " + myLocation);

        // Start the fetch route operation in a separate thread
        startCalculateThread();
    }

    @Override
    public void onMapLoaded() {

        onProceed();
        showAllPolyLines();
        loadRadioButtons();
    }

    private void loadRadioButtons() {

        for(String routeInfo: routeInfoTexts) {
            RadioButton rb = new RadioButton(this);
            rb.setText(routeInfo);
            rgroup.addView(rb);
        }
    }

    /**
     * Method which is called when street view is ready to use
     * @param panorama - streetview instance
     */
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {

        long duration = 20;
        float tilt = 30;
        float bearing = 90;

        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                .zoom(panorama.getPanoramaCamera().zoom)
                .bearing(bearing)
                .tilt(tilt)
                .build();

        panorama.setPosition(new LatLng(52.208818, 0.090587));
        panorama.setStreetNamesEnabled(false);
        panorama.setZoomGesturesEnabled(false);
        panorama.animateTo(camera, duration);
    }

    /**
     * Method which is invoked when map is clicked
     * @param point - Point on map
     */
    @Override
    public void onMapClick(LatLng point) {

        if(viewPolylineInfo) {

            viewPolylineInfo = false;
        }

        for(Polyline p: polyLines) {

            p.setColor(Color.BLACK);
            p.setVisible(true);
        }

        if(markerClicked) markerClicked = false;
        //System.out.println(viewPolylineInfo);
        clearRB = true;
        //beginRoute.setVisibility(View.INVISIBLE);
        navigationButtonBar.setVisibility(View.INVISIBLE);
        rgroup.clearCheck();
    }

    /**
     * Method invoked when polyline is clicked
     * @param polyline - Specific line that is clicked
     */
    @Override
    public void onPolylineClick(Polyline polyline) {

        //Button playSubRoute = (Button)findViewById(R.id.play_subroute);
        //clickedLatLng = polyline.getPoints().get(polyline.getPoints().size()/2);

        int id = Integer.parseInt(polyline.getId().substring(2,3));
        String[] html_instructions = objController.getHTML(id);

        System.out.println("\nROUTE INSTRUCTION: ");
        String message = "";
        for(String html_route: html_instructions){
            html_route = html_route.replaceAll( "<[^>]*>", "" );
            System.out.println(html_route);
            message += "-" + html_route + "\n";
        }

        System.out.println("\n");
        if(viewPolylineInfo) {

            if(id != polyLineID) {

                for(Polyline p: polyLines) {
                    p.setColor(Color.BLACK);
                }
                polyline.setColor(Color.CYAN);
                textView.setText(message);
            } else {

                for(Polyline p: polyLines) {
                    p.setColor(Color.BLACK);
                }

                viewPolylineInfo = false;
                textView.setVisibility(View.INVISIBLE);
                beginRoute.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                // playSubRoute.setVisibility(View.INVISIBLE);
            }
        } else {

            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
            polyline.setColor(Color.CYAN);
            viewPolylineInfo = true;
            if(id == 0 || (id > 0 && subrouteMarked.get(id-1))) {
                beginRoute.setVisibility(View.VISIBLE);
                checkBox.performClick();
                checkBox.setVisibility(View.VISIBLE);
            }
            // playSubRoute.setVisibility(View.VISIBLE);
        }

        polyLineID = id;
    }

    public void viewPolylineInfo(View view){

        Button b = (Button) view;


        if(viewPolylineInfo) {

            viewPolylineInfo = false;
            b.setTextColor(Color.BLACK);
            viewPolylineInfo = false;
            textView.setVisibility(View.INVISIBLE);
        } else {

            viewPolylineInfo = true;
            String[] html_instructions = objController.getHTML(polyLineID);

            System.out.println("\nROUTE INSTRUCTION: ");
            String message = "";
            for(String html_route: html_instructions){
                html_route = html_route.replaceAll( "<[^>]*>", "" );
                System.out.println(html_route);
                message+="-"+html_route+"\n";
            }

            System.out.println("\n");
            b.setTextColor(Color.YELLOW);
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);

            /*
            polyline.setColor(Color.CYAN);
            viewPolylineInfo = true;
            if(id == 0 || (id > 0 && subrouteMarked.get(id-1))) {
                beginRoute.setVisibility(View.VISIBLE);
                checkBox.performClick();
                checkBox.setVisibility(View.VISIBLE);
            }
            // playSubRoute.setVisibility(View.VISIBLE);
            */
        }

        // polyLineID = id;
    }

    /**
     * Show/Hide streetview button
     * @param view - Button to show/hide street view
     */
    public void onShowStreetView(View view) {

        if(showStreetView){
            showStreetView = false;
            hideSV();
        }
        else{
            showStreetView = true;
            showSV();
        }
    }

    private void showSV() {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(streetViewPanoramaFragment)
                .commit();
    }

    private void hideSV() {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(streetViewPanoramaFragment)
                .commit();
    }

    /**
     * Animate route button
     * @param view - Animate button
     */
    public void onPlayRoute(View view) {

        polyLineID = polyLineIdIndex;
        Button button = (Button)view;

        if(playRoute){
            // m_routeMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            button.setText(">>");
            playRoute = false;
            reset = true;

        } else {
            // m_routeMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            streetViewPanorama.setPosition(polyLines.get(0).getPoints().get(0));
            button.setText("||");
            playRoute = true;
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }

            animateRoute();
            //animateStreetView();
        }
    }

    /**
     * Animate subroute button
     * @param view - Sub route animation button
     */
    public void onPlaySubRoute(View view){
        Button button = (Button) view;
        //Button button1 = (Button)findViewById(R.id.play_route);

        if(!playSubroute) {
            button.setText("||");
            playSubroute = true;
            //button1.setVisibility(View.INVISIBLE);
            animateRoute();

        } else {
            button.setText(">");
            playSubroute = false;
            // button1.setVisibility(View.VISIBLE);
            reset = true;
        }
    }

    private void animateRoute() {

        Polyline polyline = polyLines.get(polyLineID);
        List<LatLng> points = polyline.getPoints();

        if(currentPositionIndex+1 < points.size()) {

            LatLng current = points.get(currentPositionIndex);
            currentPositionIndex++;
            LatLng next = points.get(currentPositionIndex);
            bearing = (float) SphericalUtil.computeHeading(current, next);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(current)      // Sets the center of the map to Mountain View
                                .zoom(19)                   // Sets the zoom
                                .bearing(bearing)                // Sets the orientation of the camera to east
                                .tilt(67)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder

            m_routeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            System.out.println(current + " -> " + next);

        } else {
            if(playRoute) {
                waypoint = true;
                polyline.setColor(Color.GREEN);
                if(polyLineID < polyLines.size() - 1) {
                    polyLines.get(polyLineID + 1).setColor(Color.BLUE);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(points.get(currentPositionIndex))      // Sets the center of the map to last point
                        .zoom(19)                   // Sets the zoom
                        .bearing(bearing)                // Sets the orientation of the camera to east
                        .tilt(67)                   // Sets the tilt of the camera to 67 degrees
                        .build();                   // Creates a CameraPosition from the builder

                currentPositionIndex = 0;
                m_routeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {

                //Button playSubRouteButton = (Button)findViewById(R.id.play_subroute);
                //playSubRouteButton.setText(">");

                playSubroute = false;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(points.get(currentPositionIndex))      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder

                currentPositionIndex = 0;
                m_routeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private void animateStreetView() {

        long duration = 500;
        float tilt = 0;

        LatLng from = polyLines.get(0).getPoints().get(SV_current_index);
        LatLng to = polyLines.get(0).getPoints().get(SV_current_index+1);
        bearingSV = (float) SphericalUtil.computeHeading(from, to);

        StreetViewPanoramaCamera streetViewPanoramaCamera = new StreetViewPanoramaCamera.Builder()
                                                                    .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                                                                    .bearing(bearingSV)
                                                                    .tilt(tilt)
                                                                    .build();

        // SV_current_index = 1;
        streetViewPanorama.animateTo(streetViewPanoramaCamera, duration);
        streetViewPanorama.setPosition(to);

        /*
        if(SV_current_index > 0)
            streetViewPanorama.setPosition(polyLines.get(0).getPoints().get(SV_current_index));
        else {
            LatLng from = polyLines.get(0).getPoints().get(0);
            streetViewPanorama.setPosition(from);

            LatLng to = polyLines.get(0).getPoints().get(1);
            bearingSV = (float) SphericalUtil.computeHeading(from, to);
            System.out.println("bearingSV = "+(360+bearingSV));
            StreetViewPanoramaCamera streetViewPanoramaCamera = new StreetViewPanoramaCamera.Builder()

                    .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                    .bearing(bearingSV)
                    .tilt(tilt)
                    .build();
            SV_current_index = 1;
            streetViewPanorama.animateTo(streetViewPanoramaCamera, duration);
        }
        */
    }

    private void zoomOut(LatLng target) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(target)
                                            .zoom(17)                   // Sets the zoom
                                            .bearing(90)                // Sets the orientation of the camera to east
                                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                            .build();                   // Creates a CameraPosition from the builder
        currentPositionIndex = 0;
        m_routeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Zoom out enough to cover all markers on the map
     */
    private void showAllMarkers() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker: markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);

        m_routeMap.animateCamera(cu);
    }

    /**
     * Zoom out enough to cover all points on the map
     */
    private void showAllPolyLines() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Polyline polyline: polyLines) {

            List<LatLng> latLngs = polyline.getPoints();
            for(LatLng latLng: latLngs) {

                builder.include(latLng);
            }
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 170);
        m_routeMap.animateCamera(cu);
    }

    /**
     * Method invoked when camera position on map is changed.
     * When animating route, this method calls the animate-method which this method is the
     * callback for. Route animation is then built upon several back-and-forth method calls
     * @param cameraPosition - new Camera position
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        //if(infoLine != null)
        //  infoLine.remove();

        //if(viewPolylineInfo)
        //  infoLine = m_routeMap.addPolyline(new PolylineOptions().add(cameraPosition.target).add(clickedLatLng));

        if(playSubroute || playRoute) {
            if(waypoint) {
                waypoint = false;
                polyLineID++;

                if(polyLineID == polyLines.size()) {
                    playRoute = false;

                    // Button button = (Button) findViewById(R.id.play_route);
                    // button.setText(">>");

                    polyLineID = 0;
                    showAllMarkers();
                }
                else
                    animateRoute();
            }
            else
                animateRoute();
        }
        else{
            if(reset) {
                reset = false;
                zoomOut(cameraPosition.target);
            }
        }
    }

    /**
     * Streetview callback method when location changed
     * @param streetViewPanoramaLocation - new Location
     */
    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

        /*StreetViewPanoramaLink[] streetViewPanoramaLinks = streetViewPanoramaLocation.links;
        for(StreetViewPanoramaLink streetViewPanoramaLink: streetViewPanoramaLinks){
            System.out.println("LINK: " + streetViewPanoramaLink.toString());
        }
        */

        if(SV_current_index < 10 && playRoute) {

            SV_current_index++;
            System.out.println(SV_current_index);
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ie){
                ie.printStackTrace();
            }

            animateStreetView();
        }
    }

    /**
     * Streetview callback method when map is clicked
     * @param orientation - angle
     */
    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {

        Point point = streetViewPanorama.orientationToPoint(orientation);
        System.out.println("CLICK: " + point.toString());

        streetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder()
                        .orientation(orientation)
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .build(), 1000);
    }

    /**
     * Streetview callback method when camera view is changed
     * @param streetViewPanoramaCamera - new camera view
     */
    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {

        System.out.println("CAMERA CHANGE: "+streetViewPanoramaCamera.bearing);
        if((int)(360 + bearingSV) == (int)streetViewPanoramaCamera.bearing && !bearingFinished) {
            bearingFinished = true;
            animateStreetView();
        }

        //if(SV_current_index < 10 && playRoute)
        //SV_current_index++;
        //animateStreetView();
    }

    /**
     * When my current location is changed, this method is invoked
     * @param location - new location
     */
    @Override
    public void onLocationChanged(Location location) {

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentBearing = location.getBearing();
        currentLL.setText("Current LatLng: "+currentLatLng.latitude+","+currentLatLng.longitude);
        currentB.setText("Current Bearing: "+(int)currentBearing);
        if(myLocation == null) {
            System.out.println("FIRST TIME CHANGED");
        }

        System.out.println("LOCATION CHANGED: " + location.toString());

        if(polyLinesDrawn) {
            LatLng latLng = polyLines.get(polylineIndex).getPoints().get(pointIndex);
            float[] result = new float[3];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), latLng.latitude, latLng.longitude, result);
            if(result.length > 0){
                distanceToPoint = (int)result[0];
                if(result.length > 1){
                    bearingToPoint = (result[1] < 0) ? (int)(360 + result[1]) : (int)result[1];
                    bearingTo.setText("Bearing: "+bearingToPoint);
                }
                distanceTo.setText("Distance: "+distanceToPoint+" m");
            }
        }

        if(following) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)      // Sets the center of the map to Mountain View
                    .zoom(19)                   // Sets the zoom
                    .bearing(bearing)                // Sets the orientation of the camera to east
                    .tilt(67)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder

            m_routeMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if(distanceToPoint < 5) {
                pointIndex++;
            }
        }
    }

    // NOT IN USE
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        System.out.println("STATUS CHANGED");
    }

    // NOT IN USE
    @Override
    public void onProviderEnabled(String provider) {

        System.out.println("PROVIDER " + provider + " ENABLED");
    }

    // NOT IN USE
    @Override
    public void onProviderDisabled(String provider) {

        System.out.println("PROVIDER " + provider + " DISABLED");
    }

    @Override
    public void onConnected(Bundle bundle) {

        if ( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(!markerClicked) {

            int id = 0;
            System.out.println(marker.getId());
            System.out.println(marker.getTitle());
            for (int i = 0; i < markers.length; i++) {

                if (markers[i].getId().equals(marker.getId())) {
                    id = i;
                }
            }

            int polyLineIndex = markerIndexToPolylineIndex.get(id);
            for (int i = 0; i < polyLines.size(); i++) {

                if (i != polyLineIndex) {
                    polyLines.get(i).setVisible(false);
                    polyLines.get(i).setClickable(false);
                }
            }

            markerClicked = true;
        }

        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        if(clearRB){
            if(checkedId == -1){
                clearRB = false;
            }
        }
        else{
            //beginRoute.setVisibility(View.VISIBLE);
            navigationButtonBar.setVisibility(View.VISIBLE);
            if(checkedId != -1){
                polyLineID = checkedId - 1;
                for (int i = 0; i < polyLines.size(); i++) {
                    if (i != polyLineID) {
                        //polyLines.get(i).setClickable(false);
                        polyLines.get(i).setVisible(false);
                        polyLines.get(i).setColor(Color.BLACK);
                    } else {
                        polyLines.get(i).setColor(Color.BLUE);
                        polyLines.get(i).setVisible(true);
                    }
                }
            }
            showAllMarkers();
        }
    }

    /**
     * Inner class which runs a separate thread that calculates the route
     * Calls MapController to fetch proper result and run algorithm
     */
    private class calculateThread extends Thread {

        public calculateThread(){}

        @Override
        public void run(){

            int count = 0;
            routeInfoTexts.clear();
            int[][] optimizedRoute = objController.calculateTimeResult();
            for(String s: list){

                System.out.println("LIST: "+s);
            }

            for(int[] partRoute: optimizedRoute) {

                int fromIndex = partRoute[0];
                int toIndex = partRoute[1];
                if(fromIndex == toIndex) {
                    break;
                }

                markerIndexToPolylineIndex.put(toIndex, count);
                subrouteMarked.put(count, false);
                System.out.println("F: "+fromIndex + "  T: "+toIndex);

                String origin = list.get(fromIndex);
                String destination = list.get(toIndex);
                System.out.println(origin+" -> "+destination);

                objController.setDirectionOrigin(origin);
                objController.setDirectionDestination(destination);

                String polyLine = objController.fetchPolyLine();
                if(polyLine.equals("NO ROUTE")){
                    Toast.makeText(getApplicationContext(), "Mode not supported", Toast.LENGTH_LONG).show();
                    System.out.println("ERROR: "+ polyLine);
                    count++;
                    if(count == nof){
                        break;
                    }

                    continue;
                }

                List<LatLng> listPolyLine = objController.decodePoly(polyLine);
                listPolylines.addLast(listPolyLine);

                count++;
                if(count == nof){
                    break;
                }
            }
        }
    }

    private void setInputsForSearch() {

        objController.setListMatrix(list);
    }

    private void onProceed() {

        index = 0;
        markerIndex = 0;
        int colorValue;
        int width;

        colorValue = Color.GREEN;
        width = 20;

        // Button for animation
       /* Button playRoute = (Button)findViewById(R.id.play_route);
        playRoute.setVisibility(View.VISIBLE); */

        // List is being initialized in the calculate thread
        System.out.println("SIZE: " + listPolylines.size());
        for(int i = 0; i < listPolylines.size(); i++) {
            Polyline p = m_routeMap.addPolyline(new PolylineOptions().addAll(listPolylines.get(i)).width(22).color(Color.parseColor("#aab1ff")).geodesic(true).clickable(true));
            polyLines.add(p);
        }

        polyLinesDrawn = true;

        /*
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker: markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);

        m_routeMap.animateCamera(cu);
        */
    }

    private void clearPolyLines() {

        for(Polyline p: polyLines){
            p.remove();
        }

        polyLines.clear();
        listPolylines.clear();
    }

    private void startCalculateThread() {

        // Start the thread that calculates the route acc. to the mode
        Thread thread = new calculateThread();
        thread.start();

        // Wait for the thread to complete and display the result
        try{
            thread.join();
        } catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }

    // Transport mode buttons click handler method
    public void traceRouteByMode(View v) {

        clearPolyLines();

        // Setting a different color to the selected button
        ImageButton b1 = (ImageButton) findViewById(R.id.btnByWalk);
        ImageButton b2 = (ImageButton) findViewById(R.id.btnByPublicTransport);
        ImageButton b3 = (ImageButton) findViewById(R.id.btnByCar);
        ImageButton b4 = (ImageButton) findViewById(R.id.btnByBike);
        b1.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
        b2.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
        b3.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
        b4.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.primary));

		ImageButton button = (ImageButton) v;
        button.setBackgroundColor(Color.YELLOW);

        if(v.getId() == R.id.btnByWalk) {
            mode = "walking";

        } else if(v.getId() == R.id.btnByPublicTransport) {
            mode = "transit";

        } else if(v.getId() == R.id.btnByCar) {
            mode = "driving";

        } else if(v.getId() == R.id.btnByBike) {
            mode = "bicycling";
        }

        objController.setMode(mode);
        markerIndexToPolylineIndex.clear();

        startCalculateThread();
        for(int i = 0 ; i < routeInfoTexts.size(); i++){
            RadioButton radioButton = (RadioButton)rgroup.getChildAt(i);
            radioButton.setText(routeInfoTexts.get(i));
        }
        rgroup.clearCheck();

        onProceed();
    }
}
