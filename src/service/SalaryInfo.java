package service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SalaryInfo {

    private String personName;
    private String personalCode;
    private BigDecimal salary;

    public SalaryInfo() {
    }

    public SalaryInfo(String personName, String personalCode, BigDecimal salary) {
        this.personName = personName;
        this.personalCode = personalCode;
        this.salary = salary.setScale(2, RoundingMode.HALF_UP);
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return personName + "," + personalCode + "," + salary + "\n";
    }


}
