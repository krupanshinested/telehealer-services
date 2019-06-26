package Flavor.GoogleFit.Interface;

import java.util.ArrayList;

import Flavor.GoogleFit.Models.GoogleFitData;


public interface GoogleFitResultFetcher {
    void didFinishFetch(ArrayList<GoogleFitData> fitData);
    void didFailedToFetch(Exception e);
}
