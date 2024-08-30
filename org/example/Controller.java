package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller {
    private List<Double> floatValues = new ArrayList<>();
    private List<Long> intValues = new ArrayList<>();
    private List<String> textValues = new ArrayList<>();
    private List<Path> pathToRead = new ArrayList<>();
    private String pathToSave = "./";
    private String prefixName = "";
    private boolean addMode = false;
    private boolean shortStat = false;
    private boolean fullStat = false;

    public void runProgramm(String[] args) {
        parseCommandLine(args);
        for (Path pathToFile : pathToRead) {
            readFile(pathToFile);
        }
        printStat();
        try {
            saveToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printStat() {
        if (shortStat) {
            System.out.println("Number of real numbers written:\t\t" + floatValues.size());
            System.out.println("Integers were written:\t\t\t\t" + intValues.size());
            System.out.println("String were written:\t\t\t\t" + textValues.size());
        }
        if (fullStat) {
            System.out.println("---------------------------------------------------------------");
            if (intValues != null && !intValues.isEmpty()) {
                System.out.println("Integer min value:\t\t" + Collections.min(intValues));
                System.out.println("Integer max value:\t\t" + Collections.max(intValues));
                long sum = 0;
                for (long i : intValues) {
                    sum += i;
                }
                System.out.println("Sum of integers:\t\t" + sum);
                System.out.println("Average of integers:\t" + (long) (sum / intValues.size()));
            }
            if (floatValues != null && !floatValues.isEmpty()) {
                System.out.println("---------------------------------------------------------------");
                System.out.println("Real number min value:\t\t" + Collections.min(floatValues));
                System.out.println("Real number max value:\t\t" + Collections.max(floatValues));
                double sum = 0.0;
                for (double i : floatValues) {
                    sum += i;
                }
                System.out.println("Sum of real numbers:\t\t" + sum);
                System.out.println("Average of real numbers:\t" + (double) (sum / floatValues.size()));
            }
            if (textValues != null && !textValues.isEmpty()) {
                System.out.println("---------------------------------------------------------------");
                String min = textValues.getFirst();
                String max = textValues.getFirst();
                for (int i = 1; i < textValues.size(); i++) {
                    if (textValues.get(i).length() < min.length()) {
                        min = textValues.get(i);
                    }
                    if (textValues.get(i).length() > max.length()) {
                        max = textValues.get(i);
                    }
                }
                System.out.println("Shortest string length:\t" + min.length() + "\tValue:\t" + min);
                System.out.println("Longest string length:\t" + max.length() + "\tValue:\t" + max);
            }
        }
    }

    private void parseCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    i++;
                    pathToSave = args[i];
                    break;
                case "-p":
                    i++;
                    Pattern patternPrefix = Pattern.compile("[\\\\/:*?\"<>|]+");
                    if (patternPrefix.matcher(args[i]).find()) {
                        System.err.println("The file name prefix contains system reserved characters. The prefix will not be applied to the file name.");
                    } else {
                        prefixName = args[i];
                    }
                    break;
                case "-a":
                    addMode = true;
                    break;
                case "-s":
                    shortStat = true;
                    break;
                case "-f":
                    fullStat = true;
                    shortStat = true;
                    break;
                default:
                    pathToRead.add(Paths.get(args[i]));
                    break;
            }
        }
    }

    private void readFile(Path pathToFile) {
        try {
            FileReader fr = new FileReader(pathToFile.toFile());
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                parseInput(line);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.err.println(pathToFile.toString() + " could not be read");
        }

    }


    private void saveToFile() throws IOException {
        if (Files.isDirectory(Path.of(pathToSave))) {
            if (!textValues.isEmpty()) {
                Path textPath = Paths.get(pathToSave, prefixName + "strings.txt");
                if (Files.exists(textPath) && addMode) {
                    Files.write(textPath, textValues, StandardOpenOption.APPEND);
                } else {
                    Files.deleteIfExists(textPath);
                    Files.write(textPath, textValues, StandardOpenOption.CREATE);
                }
            }
            if (!floatValues.isEmpty()) {
                Path floatPath = Paths.get(pathToSave, prefixName + "floats.txt");
                String floatString = floatValues.stream().map(String::valueOf).collect(Collectors.joining("\n"));
                if (Files.exists(floatPath) && addMode) {
                    Files.writeString(floatPath, "\n" + floatString, StandardOpenOption.APPEND);
                } else {
                    Files.deleteIfExists(floatPath);
                    Files.writeString(floatPath, floatString, StandardOpenOption.CREATE);
                }
            }
            if (!intValues.isEmpty()) {
                Path integerPath = Paths.get(pathToSave, prefixName + "integers.txt");
                String integerString = intValues.stream().map(String::valueOf).collect(Collectors.joining("\n"));
                if (Files.exists(integerPath) && addMode) {
                    Files.writeString(integerPath, "\n" + integerString, StandardOpenOption.APPEND);
                } else {
                    Files.deleteIfExists(integerPath);
                    Files.writeString(integerPath, integerString, StandardOpenOption.CREATE);
                }
            }
        } else {
            System.err.println("Invalid path to save files. Files will be saved in the default directory.");
            pathToSave = "./";
            System.out.println(Path.of(pathToSave));
            saveToFile();
        }
    }

    private void parseInput(String line) {
        Pattern patternFloat = Pattern.compile("^-?\\d+\\.\\d+(E-\\d+)?$");
        Pattern patternInt = Pattern.compile("^-?\\d+$");
        if (patternFloat.matcher(line).find()) {
            floatValues.add(Double.parseDouble(line));
        } else if (patternInt.matcher(line).find()) {
            intValues.add(Long.parseLong(line));
        } else {
            textValues.add(line);
        }
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.runProgramm(args);
    }
}
