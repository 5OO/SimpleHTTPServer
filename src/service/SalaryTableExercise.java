package service;

import domain.NetSalary;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import static java.util.Calendar.*;

public class SalaryTableExercise {

    public static void main(String[] args) throws IOException {
        File file = new File("palgad.txt");
        getOutputResponse(file);
        getOutputResponseBytes(file);
    }


    public static SalaryTableExerciseResponse getResponse(File file) throws IOException {
        SalaryTableExerciseResponse response = new SalaryTableExerciseResponse();
        List<SalaryInfo> grossSalaryInfoList = getGrossSalaryInfoList(createListFromFile(file));
        response.setBiggestSalary(getBiggestSalary(grossSalaryInfoList).getSalary());
        response.setBiggestSalaryName(getBiggestSalary(grossSalaryInfoList).getPersonName());
        response.setAverageSalary(getAverageSalary(grossSalaryInfoList));
        response.setMostAverageSalaryName(getPersonWithMostAverageSalary(grossSalaryInfoList).getPersonName());
        response.setMostAverageSalary(getPersonWithMostAverageSalary(grossSalaryInfoList).getSalary());
        return response;
    }

    public static void getOutputResponse(File file) throws IOException {
        List<SalaryInfo> list = getGrossSalaryInfoList(createListFromFile(file));
        int dotIndex = file.getName().indexOf(".");
        createFileFromList(getSortedSalaryInfo(list), file.getName().substring(0, dotIndex) + "Output");
    }

    public static byte[] getOutputResponseBytes(File file) throws IOException {
        getOutputResponse(file);
        Path filePath = file.toPath();
        BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
        FileTime fileTime = attributes.creationTime();
        System.out.println(fileTime);
//
//        Basi  cFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
//
//        System.out.println("creationTime: " + attr.creationTime());
//        System.out.println("lastAccessTime: " + attr.lastAccessTime());
//        System.out.println("lastModifiedTime: " + attr.lastModifiedTime());
//
//        System.out.println("isDirectory: " + attr.isDirectory());
//        System.out.println("isOther: " + attr.isOther());
//        System.out.println("isRegularFile: " + attr.isRegularFile());
//        System.out.println("isSymbolicLink: " + attr.isSymbolicLink());
//        System.out.println("size: " + attr.size());


        int dotIndex = file.getName().indexOf(".");
        InputStream in = new FileInputStream(file.getName().substring(0,dotIndex)+"Output.txt");
        byte[] bytes = in.readAllBytes();
        in.close();
        return bytes;
    }

    private static List <SalaryInfo>  getSortedSalaryInfo(List<SalaryInfo> list) {
        list.sort(Comparator.comparing(SalaryInfo::getSalary));
        return list;
    }

    private static void createFileFromList(List<SalaryInfo> list, String fileName) throws IOException {
        OutputStream out = new FileOutputStream(fileName + ".txt");
        for (SalaryInfo salaryInfo : list) {
            out.write(salaryInfo.toString().getBytes("ISO-8859-15"));
        }
        out.close();
    }

    private static List<SalaryInfo> createListFromFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        byte[] bytes = in.readAllBytes();
        in.close();
        String[] persons = new String(bytes, "ISO-8859-15").split("\\n");
        return getSalaryInfoList(persons);
    }

    private static List<SalaryInfo> getSalaryInfoList(String[] persons) {
        List<SalaryInfo> salaryInfoList = new ArrayList<>();
        for (String person : persons) {
            StringBuilder personStringBuilder = new StringBuilder(person);
            int firstCommaIndex = personStringBuilder.indexOf(",");
            int secondCommaIndex = personStringBuilder.indexOf(",", firstCommaIndex + 1);
            String personName = personStringBuilder.substring(0, firstCommaIndex);
            String personalCode = personStringBuilder.substring(firstCommaIndex + 1, secondCommaIndex);
            BigDecimal salary = new BigDecimal(personStringBuilder.substring(secondCommaIndex + 1));
            SalaryInfo salaryInfo = new SalaryInfo(personName, personalCode, salary);
            salaryInfoList.add(salaryInfo);
        }
        return salaryInfoList;
    }

    private static Calendar getDateOfBirth(String personalCode) {
        StringBuilder personalCodeString = new StringBuilder(personalCode);
        char centuryControl = personalCodeString.charAt(0);
        int year = 0;
        int yearLastTwoDigits = Integer.parseInt(personalCodeString.substring(1, 3));
        if (centuryControl == '3' || centuryControl == '4') {
            year = 1900 + yearLastTwoDigits;
        } else {
            year = 2000 + yearLastTwoDigits;
        }
        int month = Integer.parseInt(personalCodeString.substring(3, 5));
        int day = Integer.parseInt(personalCodeString.substring(5, 7));
        Builder builder = new Builder();
        return builder.setDate(year, month, day).build();
    }

    private static Boolean isOverThirtyYearsOld(SalaryInfo salaryInfo) {
        Calendar dateOfBirth = getDateOfBirth(salaryInfo.getPersonalCode());
        Calendar dateThirtyYearsAgo = getInstance();
        dateThirtyYearsAgo.set(dateThirtyYearsAgo.get(YEAR) - 30,
                dateThirtyYearsAgo.get(MONTH), dateThirtyYearsAgo.get(DAY_OF_MONTH));
        return dateThirtyYearsAgo.compareTo(dateOfBirth) > 0;
    }


    private static List<SalaryInfo> getGrossSalaryInfoList(List<SalaryInfo> list) {
        for (SalaryInfo salaryInfo : list) {
            if (!isOverThirtyYearsOld(salaryInfo)) {
                salaryInfo.setSalary(SalaryCalculatorService.
                        getSalaryInformation(new NetSalary(salaryInfo.getSalary())).getGrossSalary());
            }
        }
        return list;
    }

    private static SalaryInfo getBiggestSalary(List<SalaryInfo> list) {
        BigDecimal biggestSalary = BigDecimal.ZERO;
        SalaryInfo response = new SalaryInfo();
        for (SalaryInfo salaryInfo : list) {
            if (salaryInfo.getSalary().compareTo(biggestSalary) > 0) {
                biggestSalary = salaryInfo.getSalary();
                response.setPersonName(salaryInfo.getPersonName());
                response.setPersonalCode(salaryInfo.getPersonalCode());
                response.setSalary(biggestSalary);
            }
        }
        return response;
    }


    private static BigDecimal getAverageSalary(List<SalaryInfo> list) {
        BigDecimal sum = BigDecimal.ZERO;
        for (SalaryInfo salaryInfo : list) {
            sum = sum.add(salaryInfo.getSalary());
        }
        return sum.divide(BigDecimal.valueOf(list.size()), 2, RoundingMode.HALF_UP);
    }

    private static SalaryInfo getPersonWithMostAverageSalary(List<SalaryInfo> list) {
        SalaryInfo response = new SalaryInfo();
        BigDecimal averageSalary = getAverageSalary(list);
        BigDecimal smallestDifferenceFromAverageSalary = BigDecimal.valueOf(Integer.MAX_VALUE);
        for (SalaryInfo salaryInfo : list) {
            BigDecimal differenceFromAverageSalary = salaryInfo.getSalary().subtract(averageSalary).abs();
            if (differenceFromAverageSalary.compareTo(smallestDifferenceFromAverageSalary) < 0) {
                smallestDifferenceFromAverageSalary = differenceFromAverageSalary;
                response.setPersonName(salaryInfo.getPersonName());
                response.setPersonalCode(salaryInfo.getPersonalCode());
                response.setSalary(salaryInfo.getSalary());
            }
        }
        return response;
    }
}
