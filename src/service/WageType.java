package service;

public enum WageType {

    EMPLOYER_EXPENSE("tooandja kulu"),
    GROSS("brutopalk"),
    NET("netopalk");

    private final String label;

    WageType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


