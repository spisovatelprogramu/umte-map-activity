package cz.vhrb.myfirstumtemapapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

	private GoogleMap mMap;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// callback pro zavolání on ready po načtení
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.
//		LatLng sydney = new LatLng(-34, 151);
//		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

		mMap.setOnMapLongClickListener(this);

		db.collection("markers")
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (!task.isSuccessful()) {
							return;
						}

						for (QueryDocumentSnapshot document : task.getResult()) {
							LatLng latLng = new LatLng(document.getDouble("lat"), document.getDouble("lng"));
							mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
						}
					}
				});
	}

	@Override
	public void onMapLongClick(LatLng latLng) {

		// Add a marker on clic point
		mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

		Map<String, Object> user = new HashMap<>();
		user.put("lat", latLng.latitude);
		user.put("lng", latLng.longitude);

		db.collection("markers").add(user);
	}
}
