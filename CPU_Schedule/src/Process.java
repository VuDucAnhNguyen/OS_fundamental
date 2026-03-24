import java.lang.System;

public class Process implements Comparable<Process> {
    private String processID;
    private int burstTime;
    private int arrivalTime;
    private int remainTime;
    private String readyQueue;
    private int waitTime = 0;
    private int responseTime = 0;
    private int turnAroundTime = 0;

    public Process (String processID, int burstTime, int arrivalTime, String readyQueue) {
        this.processID = processID;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.readyQueue = readyQueue;
        this.remainTime = burstTime;
    }

    public String getProcessID() {
        return processID;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public String getReadyQueue() {
        return readyQueue;
    }

    public void minusRemainTime() {
        this.remainTime--;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void plusWaitTime() {
        this.waitTime++;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    @Override
    public int compareTo(Process o) {
        return Integer.compare(this.remainTime, o.remainTime);
    }

    public void printProcess () {
        String s = "Process ID: " + processID
                    + ", wait time: " + waitTime
                    + ", response time: " + responseTime
                    + ", turnaround time: " + turnAroundTime;
        System.out.println(s);
    }
}
