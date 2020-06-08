package flavor.GoogleFit.Models;

import java.io.Serializable;

public class GoogleFitSource implements Serializable,Comparable<GoogleFitSource> {
    private String appName;
    private String bundleId;
    private boolean isSelected;

    public GoogleFitSource(String appName, String bundleId, boolean isSelected) {
        this.appName = appName;
        this.bundleId = bundleId;
        this.isSelected = isSelected;
    }

    public String getAppName() {
        return appName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(GoogleFitSource o) {
        return o.bundleId.compareTo(bundleId);
    }
}
