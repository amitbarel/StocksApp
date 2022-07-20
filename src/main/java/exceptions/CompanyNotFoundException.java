package exceptions;

public class CompanyNotFoundException extends Exception{
    private static final String msg = "CompanyNotFoundException: ";
    public CompanyNotFoundException() {
        super(msg + "Company stocks not found");
    }

    public CompanyNotFoundException(int companyID) {
        super(msg + "Company ID:" + companyID + " stocks not found");
    }
}
