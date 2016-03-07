/**
 * @author karim jana
 * Program Description:
 A Maze with the Maze Objects: Goal, Obstacles, Ball, Wall, Floor

 * Program Requirements Compliance:
 UFOPostionServiceImpl.java: A Service to poll for alien invasion updates
 
 MainActivity.java: An Activity to display the map, ship icons, and the paths the ships have taken
 
 UFOPosition.java: A Parcelable representation of a UFO position
 
 UFOPOsition.aidl: AIDL file that describe the UFO Position.
 
 UFOPositionReport.aidl: AIDL file that provides a reporter interface that the service can use to communicate status updates to the
 activity (it will pass UFOPosition objects).

 AlienService.aidl: AIDL file that provides the alien service’s interface (allows add/remove of the reporter)
 
 UFOPositionServiceImpl.java: A Service to poll for alien invasion updates.
 */

package jana.karim.hw5;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	GoogleMap googleMap = null;
	BitmapDescriptor redUfo = null;
	LatLngBounds.Builder bounds = null;
	List<Marker> previousReportList = new ArrayList<Marker>();
	List<Marker> currentMarkerList = new ArrayList<Marker>();
	private static final int GOOGLE_PLAY_SETUP = 42;
	private AlienService ufoPositionService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStop() {
		Log.d("Main", "onStop");
		try {
			ufoPositionService.remove(reporter);
		} catch (RemoteException e) {
			Log.e("MainActivity4", "addReporter", e);
		}
		unbindService(serviceConnection);
		super.onStop();
	}

	@Override
	protected void onStart() {
		Log.d("Main", "onStart");
		Intent intent = new Intent("jana.karim.hw5.UFOPositionServiceImpl");
		// Next line of code is a fix for the following error I was getting on
		// my Android Lollipop phone
		// java.lang.IllegalArgumentException: Service Intent must be explicit
		// The code of the video lectures did not suggested this.
		intent.setPackage("jana.karim.hw5");
		if (!bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE))
			Toast.makeText(getBaseContext(), "Could not bind to service",
					Toast.LENGTH_LONG).show();
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			ufoPositionService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ufoPositionService = AlienService.Stub.asInterface(service);
			try {
				Log.e("UFO_SERVICE_CONNECTION", "SERVICE CONNECTED");
				ufoPositionService.add(reporter);
			} catch (RemoteException e) {
				Log.e("MainActivity", "addReporter", e);
			}
		}
	};

	protected void onResume() {
		super.onResume();

		int googlePlayServicesAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		switch (googlePlayServicesAvailable) {
		case ConnectionResult.SUCCESS:
			break;
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
			GooglePlayServicesUtil.getErrorDialog(googlePlayServicesAvailable,
					this, GOOGLE_PLAY_SETUP).show();
			return;
		default:
			throw new RuntimeException(
					"Unexpected result code from isGooglePlayServicesAvailable: "
							+ googlePlayServicesAvailable
							+ " ("
							+ GooglePlayServicesUtil
									.getErrorString(googlePlayServicesAvailable)
							+ ")");

		}

		MapFragment map = (MapFragment) getFragmentManager().findFragmentById(
				R.id.map);
		googleMap = map.getMap();
		googleMap.setMyLocationEnabled(true);
		bounds = new LatLngBounds.Builder();

	}

	/**
	 * updateBounds
	 * 
	 * @param latLng
	 *            Adds Lat Lng Coordinates to bounds global object
	 */
	private void updateBounds(LatLng latLng) {
		bounds.include(latLng);
	}

	/**
	 * updataeMapCamera Updates Map Camera View by reading in the current state
	 * of the bounds object
	 */
	private void updateMapCamera() {

		int map_camera_padding = (int) getResources().getDimension(
				R.dimen.map_camera_padding);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
				bounds.build(), map_camera_padding));

	}

	/**
	 * addUFOMarker
	 * 
	 * @param latitude
	 * @param longitude
	 * @param shipNumber
	 * @return ufoMarker Creates a ufo marker and adds it to the map. Returns a
	 *         reference to the added marker.
	 */
	private Marker addUFOMarker(double latitude, double longitude,
			int shipNumber) {
		redUfo = BitmapDescriptorFactory.fromResource(R.drawable.red_ufo);
		Marker ufoMarker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude)).icon(redUfo)
				.title(Integer.toString(shipNumber)));
		return ufoMarker;

	}

	/**
	 * updateMarkerPosition
	 * 
	 * @param currentMarkerList
	 * @param ufoPositionObj
	 *            Finds the marker that needs to be update and sets it to the
	 *            most recent position with its path.
	 */
	private void updateMarkerPosition(List<Marker> currentMarkerList,
			UFOPosition ufoPositionObj) {
		for (Marker ufoPositionMarker : currentMarkerList) {
			if (Integer.parseInt(ufoPositionMarker.getTitle()) == ufoPositionObj
					.getShipNumber()) {
				LatLng previousPosition = ufoPositionMarker.getPosition();
				LatLng recentPosition = new LatLng(ufoPositionObj.getLat(),
						ufoPositionObj.getLon());
				// sets most recent marker position
				ufoPositionMarker.setPosition(recentPosition);
				// draws ufo path
				addPolylineUFOPath(previousPosition, recentPosition);

			}
		}
	}

	/**
	 * newUFOMarker
	 * 
	 * @param currentUfoReportList
	 * @param newShip
	 * @return boolean Checks if a position represents a new ship.
	 */
	private boolean newUFOMarker(List<Marker> currentMarkerList,
			UFOPosition newShip) {

		for (Marker ufoPositionMarker : currentMarkerList) {
			if (Integer.parseInt(ufoPositionMarker.getTitle()) == newShip
					.getShipNumber())
				return false;
		}
		return true;
	}

	/**
	 * addPolylineUFOPath
	 * 
	 * @param previousPosition
	 * @param recentPosition
	 *            Configures and draws a polyline in the map between two points
	 */
	private void addPolylineUFOPath(LatLng previousPosition,
			LatLng recentPosition) {
		PolylineOptions ufoPath = new PolylineOptions().add(previousPosition,
				recentPosition);
		Polyline polyline = googleMap.addPolyline(ufoPath);

	}

	/**
	 * filterOutOfDateUFOMarkers
	 * 
	 * @param currentReportList
	 * @param previousReportList
	 * @return outOfDateMarkerList compares a most up to date UFO Position list
	 *         versus old marker list and returns the out of date markers
	 */
	private List<Marker> filterOutOfDateUFOMarkers(
			List<UFOPosition> currentReportList, List<Marker> previousReportList) {
		// loop through each new reported ship and remove
		// from Marker List markers not found in current report list
		List<Marker> outOfDateMarkerList = new ArrayList<Marker>();
		for (UFOPosition ufoPositionObj : currentReportList) {
			for (Marker marker : previousReportList)
				if (ufoPositionObj.getShipNumber() != Integer.parseInt(marker
						.getTitle())) {
					outOfDateMarkerList.add(marker);
				}
		}
		return outOfDateMarkerList;
	}

	/**
	 * updateUFOMarkersList
	 * 
	 * @param currentReportList
	 * @param previousReportList
	 * @return updatedMarkerList compares a most up to date UFO Position list
	 *         versus old marker list and returns up to date marker list
	 */
	private List<Marker> updateUFOMarkersList(
			List<UFOPosition> currentReportList, List<Marker> previousReportList) {
		// loop through each new reported ship and remove
		// from Marker List markers not found in current report list
		List<Marker> updatedMarkerList = new ArrayList<Marker>();
		for (UFOPosition ufoPositionObj : currentReportList) {
			for (Marker marker : previousReportList)
				if (ufoPositionObj.getShipNumber() == Integer.parseInt(marker
						.getTitle())) {
					updatedMarkerList.add(marker);
				}
		}
		return updatedMarkerList;
	}

	/**
	 * removeMapMarkers
	 * 
	 * @param outOfDateMarkers
	 *            remove out of date Marker from map
	 */
	private void removeMapMarkers(List<Marker> outOfDateMarkers) {
		for (Marker marker : outOfDateMarkers) {
			// remove from map
			marker.remove();
		}
	}

	private UFOPositionReporter reporter = new UFOPositionReporter.Stub() {
		@Override
		public void report(final List<UFOPosition> ufoPositionList)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					List<Marker> outOfDateMarkerList = filterOutOfDateUFOMarkers(
							ufoPositionList, previousReportList);
					currentMarkerList = updateUFOMarkersList(ufoPositionList,
							previousReportList);
					// if a ship was previously reported but is not in the
					// current report removes marker
					removeMapMarkers(outOfDateMarkerList);

					for (UFOPosition ufoPositionObj : ufoPositionList) {

						if (newUFOMarker(currentMarkerList, ufoPositionObj)) {
							Marker marker = addUFOMarker(
									ufoPositionObj.getLat(),
									ufoPositionObj.getLon(),
									ufoPositionObj.getShipNumber());

							currentMarkerList.add(marker);
						} else {
							// position represents existing ship
							updateMarkerPosition(currentMarkerList,
									ufoPositionObj);

						}
						// Log.d("UFO_Obj", ufoPositionObj.getShipNumber()+"");
						// Log.d("UFO_Obj", ufoPositionObj.getLat()+"");
						// Log.d("UFO_Obj", ufoPositionObj.getLon()+"");
						updateBounds(new LatLng(ufoPositionObj.getLat(),
								ufoPositionObj.getLon()));
					}
					previousReportList = new ArrayList<Marker>(
							currentMarkerList);
					updateMapCamera();

				}
			});

		}
	};

}
