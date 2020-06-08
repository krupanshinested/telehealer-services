package flavor.GoogleFit.Interface;

import java.util.ArrayList;

import flavor.GoogleFit.Models.GoogleFitData;


public interface GoogleFitResultFetcher {
    void didFinishFetch(ArrayList<GoogleFitData> fitData);
    void didFailedToFetch(Exception e);
}
