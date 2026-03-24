import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dispatcher {
    private final PriorityQueue<Process> PreemptiveSJFReadyQueue = new PriorityQueue<>();
    private final Queue<Process> FCFSReadyQueue = new ArrayDeque<>();
    private final Queue<Process> RobinReadyQueue = new ArrayDeque<>();

    private Process current = null;
    private int contextSwitch = 0;
    private int IDLE = 1;

    private static final int timeQuantum = 20;
    public static final String FCFS = "FCFS";
    public static final String PSJF = "PSJF";
    public static final String RR = "RR";

    public void addFCFSReadyQueue(Process process){
        FCFSReadyQueue.add(process);
    }

    public void addPreemptiveSJFReadyQueue(Process process){
        PreemptiveSJFReadyQueue.add(process);
    }

    public void addRobinReadyQueue (Process process) {
        RobinReadyQueue.add(process);
    }

    public int getContextSwitch() {
        return contextSwitch;
    }

    public boolean isBusy() {
        return !FCFSReadyQueue.isEmpty() || !PreemptiveSJFReadyQueue.isEmpty() ||
                !RobinReadyQueue.isEmpty() || current != null;
    }

    private void changeContext(int time) {
        if (current.getRemainTime() == current.getBurstTime()) {
            current.setResponseTime(time - current.getArrivalTime());
        }

        if (IDLE != 1) {
            contextSwitch++;
        }
        IDLE = 0;
    }

    private Process executeCurrent(int time) {
        current.minusRemainTime();

        for (Process process : RobinReadyQueue) {
            process.plusWaitTime();
        }

        for (Process process : PreemptiveSJFReadyQueue) {
            process.plusWaitTime();
        }

        for (Process process : FCFSReadyQueue) {
            process.plusWaitTime();
        }

        if (current.getRemainTime() == 0) {
            current.setTurnAroundTime(time + 1 - current.getArrivalTime());
            Process finished = current;
            current = null;
            return finished;
        }
        return null;
    }

    public Process Schedule (int time){
        if (current == null){
            if (!RobinReadyQueue.isEmpty()) {
                current = RobinReadyQueue.poll();
                changeContext(time);
            } else if (!PreemptiveSJFReadyQueue.isEmpty()) {
                current = PreemptiveSJFReadyQueue.poll();
                changeContext(time);
            } else if (!FCFSReadyQueue.isEmpty()){
                current = FCFSReadyQueue.poll();
                changeContext(time);
            } else {
                if (IDLE != 0){
                    IDLE = 1;
                }
                return null;
            }
        } else {
            if (current.getReadyQueue().equals(PSJF)){
                if (!RobinReadyQueue.isEmpty()) {
                    PreemptiveSJFReadyQueue.add(current);
                    current = RobinReadyQueue.poll();
                    changeContext(time);
                } else if (!PreemptiveSJFReadyQueue.isEmpty() &&
                        PreemptiveSJFReadyQueue.peek().getRemainTime() < current.getRemainTime()) {
                    PreemptiveSJFReadyQueue.add(current);
                    current = PreemptiveSJFReadyQueue.poll();
                    changeContext(time);
                }
            } else if (current.getReadyQueue().equals(RR)){
                if ((current.getBurstTime() - current.getRemainTime())%timeQuantum == 0){
                    RobinReadyQueue.add(current);
                    current = RobinReadyQueue.poll();
                    changeContext(time);
                }
            }
        }

        return executeCurrent(time);
    }
}
