package service;

import java.math.BigDecimal;
import java.util.Objects;

public class SalaryTableExerciseResponse {

    private String biggestSalaryName;
    private BigDecimal biggestSalary;
    private String mostAverageSalaryName;
    private BigDecimal mostAverageSalary;
    private BigDecimal averageSalary;

    public SalaryTableExerciseResponse() {

    }


    public String getBiggestSalaryName() {
        return biggestSalaryName;
    }

    public BigDecimal getBiggestSalary() {
        return biggestSalary;
    }

    public String getMostAverageSalaryName() {
        return mostAverageSalaryName;
    }

    public BigDecimal getMostAverageSalary() {
        return mostAverageSalary;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public SalaryTableExerciseResponse(String biggestSalaryName, BigDecimal biggestSalary, String mostAverageSalaryName, BigDecimal mostAverageSalary, BigDecimal averageSalary) {
        this.biggestSalaryName = biggestSalaryName;
        this.biggestSalary = biggestSalary;
        this.mostAverageSalaryName = mostAverageSalaryName;
        this.mostAverageSalary = mostAverageSalary;
        this.averageSalary = averageSalary;
    }

    public void setBiggestSalaryName(String biggestSalaryName) {
        this.biggestSalaryName = biggestSalaryName;
    }

    public void setBiggestSalary(BigDecimal biggestSalary) {
        this.biggestSalary = biggestSalary;
    }

    public void setMostAverageSalaryName(String mostAverageSalaryName) {
        this.mostAverageSalaryName = mostAverageSalaryName;
    }

    public void setMostAverageSalary(BigDecimal mostAverageSalary) {
        this.mostAverageSalary = mostAverageSalary;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalaryTableExerciseResponse that = (SalaryTableExerciseResponse) o;
        return Objects.equals(biggestSalaryName, that.biggestSalaryName) && Objects.equals(biggestSalary, that.biggestSalary) && Objects.equals(mostAverageSalaryName, that.mostAverageSalaryName) && Objects.equals(mostAverageSalary, that.mostAverageSalary) && Objects.equals(averageSalary, that.averageSalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(biggestSalaryName, biggestSalary, mostAverageSalaryName, mostAverageSalary, averageSalary);
    }

    @Override
    public String toString() {
        return "SalaryTableExerciseResponse{" +
                "biggestSalaryName='" + biggestSalaryName + '\'' +
                ", biggestSalary=" + biggestSalary +
                ", mostAverageSalaryName='" + mostAverageSalaryName + '\'' +
                ", mostAverageSalary=" + mostAverageSalary +
                ", averageSalary=" + averageSalary +
                '}';
    }
}
