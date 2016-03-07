package jana.karim.hw5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class UFOPositionServiceImpl extends Service {

	private List<UFOPositionReporter> reporters = new ArrayList<UFOPositionReporter>();
	private int id = 0;
	private ServiceThread serviceThread;
	private String HTTPResultString = null;

	private class ServiceThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {
				id++;
				List<UFOPositionReporter> targets;
				List<UFOPosition> ufoPositionObjects = new ArrayList<UFOPosition>();

				Log.d("UFOPositionServiceImpl", "GET REST PATH ID = " + id);
				try {
					getUFOServerUpdate(id);
					parseJSONResult(ufoPositionObjects);
					sleep(1000);
					// Log.d("JSON_URI","Continue");

				} catch (InterruptedException e) {
					// Log.d("JSON_URI","INTERRUPTED");
					interrupt();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (reporters) {
					targets = new ArrayList<UFOPositionReporter>(reporters);
				}
				for (UFOPositionReporter reporter : targets) {
					try {
						reporter.report(ufoPositionObjects);
					} catch (RemoteException e) {
						Log.e("UFOPositionServiceImpl", "report", e);
					}
				}

			}
		}
	}

	/**
	 * parseJSONResult
	 * 
	 * @param ufoPositionList
	 * @throws JSONException
	 *             parses the HTTPResultString from the REST Server. Creates and
	 *             add to global ufoPositionList a UFOPosition object for each
	 *             UFOPosition JSON string.
	 */
	public void parseJSONResult(List<UFOPosition> ufoPositionList)
			throws JSONException {

		JSONArray ufoPositionJsonArray = new JSONArray(HTTPResultString);
		int jsonArrayLength = ufoPositionJsonArray.length();
		for (int i = 0; i < jsonArrayLength; i++) {
			JSONObject ufoPositionJson = ufoPositionJsonArray.getJSONObject(i);
			// Log.d("JSON_UFO", ufoPositionJson.toString());
			int ship = Integer.parseInt(ufoPositionJson.opt("ship").toString());
			double lat = Double.parseDouble(ufoPositionJson.opt("lat")
					.toString());
			double lon = Double.parseDouble(ufoPositionJson.opt("lon")
					.toString());
			UFOPosition ufoPositionObj = new UFOPosition(ship, lat, lon);
			ufoPositionList.add(ufoPositionObj);
		}
	}

	/**
	 * setGETRequest
	 * 
	 * @param id
	 * @return request response Allows to change the id for server request to be
	 *         made.
	 * 
	 */
	public HttpUriRequest setGETRequest(int id) {
		HttpUriRequest request = null;
		String uri = "http://javadude.com/aliens/" + Integer.toString(id)
				+ ".json";
		Log.d("JSON_URI", uri);
		request = new HttpGet(uri);
		return request;
	}

	/**
	 * getServerResponse
	 * 
	 * @param request
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpException
	 *             Makes an HTTP Request and handles the response.
	 * 
	 */
	public void getServerResponse(HttpUriRequest request)
			throws ClientProtocolException, IOException, HttpException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(request);
		StatusLine statusLine = response.getStatusLine();

		HttpEntity entity = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		// Log.d("JSON_URI_STATUS", Integer.toString(statusCode));

		if (entity != null && statusCode != HttpStatus.SC_NOT_FOUND) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			entity.writeTo(out);
			out.close();
			HTTPResultString = out.toString();
			// Log.d("JSON_SUCCESS", "RESULT SUCCESSFUL STRING: " +
			// HTTPResultString );
		} else {
			HTTPResultString = statusLine.getReasonPhrase();
			// Log.d("JSON_FAILED", "RESULT FAILED CODE: " + HTTPResultString );
			throw new HttpException(HTTPResultString);
		}
	}

	/**
	 * getUFOServerUpdate
	 * 
	 * @param id
	 * @throws InterruptedException
	 * @throws ClientProtocolException
	 * @throws IOException
	 *             Reads in the new id to build the HTTP request. If it detects
	 *             an HTTPException proceeds to interrupt the process.
	 */
	public void getUFOServerUpdate(int id) throws InterruptedException,
			ClientProtocolException, IOException {

		HttpUriRequest request = setGETRequest(id);
		try {
			getServerResponse(request);
		} catch (HttpException e) {
			// Log.d("JSON_URI_EXCEPTION", e +"");
			throw new InterruptedException();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		serviceThread = new ServiceThread();
		serviceThread.start();
		return binder;

	}

	@Override
	public void onDestroy() {
		if (serviceThread != null) {
			serviceThread.interrupt();
			serviceThread = null;
		}
		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
		// Log.d("UFOPositionServiceImpl", "onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.d("UFOPositionServiceImpl", "onStartCommand(" + intent + ", " +
		// flags + ", " + startId + ")");
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		// Log.d("UFOPositionServiceImpl", "onCreate");
	}

	private AlienService.Stub binder = new AlienService.Stub() {

		@Override
		public void add(UFOPositionReporter reporter) throws RemoteException {
			// TODO Auto-generated method stub
			synchronized (reporters) {
				reporters.add(reporter);
			}
		}

		@Override
		public void remove(UFOPositionReporter reporter) throws RemoteException {
			// TODO Auto-generated method stub
			synchronized (reporters) {
				reporters.remove(reporter);
			}
		}

	};

}
