package com.example.fitnesstracker;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private View vMap;
    private MapView mapView;
    private GoogleMap mMap;
    private double lat, lon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = vMap.findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
        return vMap;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String address = ((Home)getActivity()).getUser().getAddress();;
        //String address = "56 college way Deakin Hall";
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                lat = addresses.get(0).getLatitude();
                lon = addresses.get(0).getLongitude();
            }
            //
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            // Add a marker in Sydney and move the camera

            LatLng home = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(home).title("Home").snippet(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            //parks
            GetNearByPlacesAsyncTask getNearByPlacesAsyncTask = new GetNearByPlacesAsyncTask();
            try {
                String nearByPlaces = getNearByPlacesAsyncTask.execute(Double.toString(lat), Double.toString(lon)).get();
                JSONArray jsonArray = new JSONObject(nearByPlaces).getJSONArray("results");
                //Log.i("nearby places", nearByPlaces);
                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    double latitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double longitude = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    String name = obj.getString("name");
                    LatLng park = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(park).title(name).icon(bitmapDescriptor));
                }
            }catch (JSONException e){
            }catch (ExecutionException e){
            }catch (InterruptedException e){
            }

        } catch(IOException e){

        }
    }

    private class GetNearByPlacesAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return RestClient.readUrl(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
        }
    }
}
