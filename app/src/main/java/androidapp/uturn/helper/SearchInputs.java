package androidapp.uturn.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rakesh on 2/20/2016
 */
public class SearchInputs implements Parcelable {

    public enum CommuteMode {
        WALK,
        BIKE,
        CAR,
        PUBLIC_TRANSIT
    }

    public boolean m_bRoundTrip;
    public CommuteMode m_commuteMode;
    public String m_strStartingPoint;
    public String m_strFinishPoint;
    public List<String> m_DestinationList;

    public SearchInputs() {

        m_bRoundTrip = false;
        m_strStartingPoint = m_strFinishPoint = "";
        m_DestinationList = new ArrayList<>();
    }

    public SearchInputs(Parcel parcel) {

        m_bRoundTrip = (parcel.readInt() == 1);
        m_commuteMode = (CommuteMode) (parcel.readValue(CommuteMode.class.getClassLoader()));
        m_strStartingPoint = parcel.readString();
        m_strFinishPoint = parcel.readString();
        if(m_DestinationList == null) {

            m_DestinationList = new ArrayList<>();
        }
        parcel.readStringList(m_DestinationList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write the custom class object to Parcel
    @Override
    public void writeToParcel(Parcel outObject, int flags) {
        outObject.writeInt(m_bRoundTrip ? 1 : 0);
        outObject.writeValue(m_commuteMode);
        outObject.writeString(m_strStartingPoint);
        outObject.writeString(m_strFinishPoint);
        outObject.writeStringList(m_DestinationList);
    }

    // Required method to recreate the custom object from a Parcel
    public static Creator<SearchInputs> CREATOR = new Creator<SearchInputs>() {

        @Override
        public SearchInputs createFromParcel(Parcel inObject) {
            return new SearchInputs(inObject);
        }

        @Override
        public SearchInputs[] newArray(int size) {
            return new SearchInputs[size];
        }
    };
}