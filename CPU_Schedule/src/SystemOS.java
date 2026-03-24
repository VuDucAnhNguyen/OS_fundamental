import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class SystemOS {
    private int time = 0;
    private final ArrayList<Process> jobQueue = new ArrayList<>();
    private final ArrayList<Process> finishQueue = new ArrayList<>();
    private final Dispatcher dispatcher;

    public SystemOS (){
        dispatcher = new Dispatcher();
    }

    private void readFile (String filePath) {
        try {
            File inputFile = new File (filePath);
            Scanner reader = new Scanner(inputFile);

            while (reader.hasNext()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                if (parts.length != 4) {
                    System.out.println("Invalid input format!");
                    System.exit(1);
                }

                try {
                    String processID = parts[0];

                    int burstTime = Integer.parseInt(parts[1]);
                    int arrivalTime = Integer.parseInt(parts[2]);
                    String readyQueue = parts[3];

                    if ((!readyQueue.equals(Dispatcher.FCFS) && !readyQueue.equals(Dispatcher.PSJF)
                            && !readyQueue.equals(Dispatcher.RR))
                        || burstTime <= 0 || arrivalTime < 0) {
                        System.out.println("Invalid input value!");
                        System.exit(1);
                    }

                    jobQueue.add(new Process(processID, burstTime, arrivalTime, readyQueue));

                } catch (NumberFormatException e) {
                    System.out.println("Invalid input format!");
                    System.exit(1);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            System.exit(1);
        }
    }

    private void printResult (){
        double meanWaitTime = 0;
        double meanResponseTime = 0;
        double meanTurnAroundTime = 0;
        double numProcess = finishQueue.size();

        for(Process process : finishQueue) {
            process.printProcess();
            meanWaitTime += process.getWaitTime();
            meanResponseTime += process.getResponseTime();
            meanTurnAroundTime += process.getTurnAroundTime();
        }

        System.out.println("Mean wait time: " + meanWaitTime/numProcess
                            + ", Mean response time: " + meanResponseTime/numProcess
                            + ", Mean turn around time: " + meanTurnAroundTime/numProcess
                            + ", context switch: " + dispatcher.getContextSwitch());
    }

    public void Run (String inputPath) {
        readFile(inputPath);

        while (!jobQueue.isEmpty() || dispatcher.isBusy()) {
            Iterator<Process> it = jobQueue.iterator();

            while (it.hasNext()) {
                Process process = it.next();
                if (process.getArrivalTime() <= time) {
                    if (process.getReadyQueue().equals(Dispatcher.FCFS)) {
                        dispatcher.addFCFSReadyQueue(process);
                    } else if (process.getReadyQueue().equals(Dispatcher.PSJF)) {
                        dispatcher.addPreemptiveSJFReadyQueue(process);
                    } else {
                        dispatcher.addRobinReadyQueue(process);
                    }
                    it.remove();
                }
            }

            Process finished = dispatcher.Schedule(time);

            if (finished != null) {
                finishQueue.add(finished);
            }

            time++;
        }
        printResult();
    }
}
