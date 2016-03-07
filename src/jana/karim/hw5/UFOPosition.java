package jana.karim.hw5;

import android.os.Parcel;
import android.os.Parcelable;

public class UFOPosition implements Parcelable {

	private int shipNumber;
	private double lat;
	private double lon;

	/**
	 * Constructor to use when re-constructing object from a parcel
	 * 
	 * @param in
	 *            a parcel from which to read this object
	 */
	public UFOPosition(Parcel in) {
		readFromParcel(in);
	}

	/* Standard constructor for non-parcel creation */
	public UFOPosition(int shipNumber, double lat, double lon) {
		super();
		this.shipNumber = shipNumber;
		this.lat = lat;
		this.lon = lon;
	}

	public int getShipNumber() {
		return shipNumber;
	}

	public void setShipNumber(int shipNumber) {
		this.shipNumber = shipNumber;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// We write the each field into the parcel.
		dest.writeInt(shipNumber);
		dest.writeDouble(lat);
		dest.writeDouble(lon);
	}

	private void readFromParcel(Parcel in) {
		// We just need to read back each field in the order
		// that it was written to the parcel
		shipNumber = in.readInt();
		lat = in.readDouble();
		lon = in.readDouble();
	}

	public static final Parcelable.Creator<UFOPosition> CREATOR = new Parcelable.Creator<UFOPosition>() {
		public UFOPosition createFromParcel(Parcel in) {
			return new UFOPosition(in);
		}

		public UFOPosition[] newArray(int size) {
			return new UFOPosition[size];
		}
	};
}
