import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SystemOS {
    String mode;
    private int numProcess;
    private int numResource;

    private int[][] allocation;
    private int[][] demand;
    private int[] available;
    private int[] request;
    private int[][] need;

    public static final String MODE_BANKER = "BANKER";
    public static final String MODE_DETECTION = "DETECTION";

    private void readFile (String filePath) {
        try {
            File inputFile = new File (filePath);
            Scanner reader = new Scanner(inputFile);

            if (reader.hasNextLine()){
                mode = reader.next();
            }

            if (reader.hasNextLine()){
                this.numProcess = reader.nextInt();
                this.numResource = reader.nextInt();
            }

            this.allocation = new int[numProcess][numResource];
            this.demand = new int[numProcess][numResource];
            this.available = new int[numResource];
            this.request = new int[numResource + 1];

            for (int j = 0; j < numResource; j++){
                available[j] = reader.nextInt();
            }

            for (int i = 0; i < numProcess; i++){
                for (int j = 0; j < numResource; j++){
                    demand[i][j] = reader.nextInt();
                }
            }

            for (int i = 0; i < numProcess; i++) {
                for (int j = 0; j < numResource; j++) {
                    allocation[i][j] = reader.nextInt();
                }
            }

            if (mode.equals(MODE_BANKER)) {
                for (int j = 0; j <= numResource; j++){
                    request[j] = reader.nextInt();
                }
            }

            if (mode.equals(MODE_BANKER)) {
                this.need = new int[numProcess][numResource];
                calculateNeed();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            System.exit(1);
        } catch (Exception e){
            System.out.println("Invalid input format!");
            System.exit(1);
        }
    }

    private void calculateNeed() {
        for (int i = 0; i < numProcess; i++) {
            for (int j = 0; j < numResource; j++) {
                need[i][j] = demand[i][j] - allocation[i][j];
            }
        }
    }

    private boolean safeAlgorithm() {
        ArrayList<Integer> safe = new ArrayList<>();
        int[] work = available.clone();

        boolean[] finish = new boolean[numProcess];
        for (int i = 0; i < numProcess; i++) {
            finish[i] = false;
        }

        while (true) {
            boolean found = false;

            for (int i = 0 ; i < numProcess; i++) {
                if (!finish[i]) {
                    boolean runnable = true;
                    for (int j = 0; j < numResource; j++) {
                        if (need[i][j] > work[j]){
                            runnable = false;
                            break;
                        }
                    }

                    if (runnable) {
                        for (int j = 0; j < numResource; j++) {
                            work[j] += allocation[i][j];
                        }

                        finish[i] = true;
                        safe.add(i);
                        found = true;
                    }
                }
            }

            if (!found) {
                break;
            }
        }

        if (safe.size() != numProcess) {
            System.out.println("System is unsafe");
            return false;
        } else {
            System.out.println("System is safe: " + safe);
            return true;
        }
    }

    private boolean requestAlgorithm() {
        for (int j = 0; j < numResource; j++){
            if (request[j] > need[request[numResource]][j]) {
                System.out.println("Request more than max!");
                return false;
            }
        }

        for (int j = 0; j < numResource; j++){
            if (request[j] > available[j]) {
                System.out.println("Resources not ready!");
                return false;
            }
        }

        for (int j = 0; j < numResource; j++){
            available[j] -= request[j];
            allocation[request[numResource]][j] += request[j];
            need[request[numResource]][j] -= request[j];
        }
        return true;
    }

    private void Banker(){
        System.out.println("Banker algorithm:");

        if (requestAlgorithm()){
            if (safeAlgorithm()) {
                System.out.println("Request approved!");
            } else {
                System.out.println("Request denied!");
                for (int j = 0; j < numResource; j++){
                    available[j] += request[j];
                    allocation[request[numResource]][j] -= request[j];
                    need[request[numResource]][j] += request[j];
                }
            }
        }
    }

    private void Detection() {
        System.out.println("Deadlock detection algorithm:");

        ArrayList<Integer> result = new ArrayList<>();
        int[] work = available.clone();

        boolean[] finish = new boolean[numProcess];
        for (int i = 0; i < numProcess; i++) {
            finish[i] = false;
        }

        while (true) {
            boolean found = false;

            for (int i = 0; i < numProcess; i++) {
                if (!finish[i]) {
                    boolean runnable = true;
                    for (int j = 0; j < numResource; j++) {
                        if (demand[i][j] > work[j]) {
                            runnable = false;
                            break;
                        }
                    }

                    if (runnable) {
                        for (int j = 0; j < numResource; j++) {
                            work[j] += allocation[i][j];
                        }

                        finish[i] = true;
                        result.add(i);
                        found = true;
                    }
                }
            }

            if (!found) {
                break;
            }
        }

        if (result.size() == numProcess) {
            System.out.println("System is safe: " + result);
        } else {
            System.out.println("System is deadlock at:");
            for (int i = 0; i < numProcess; i++) {
                if (!finish[i]){
                    for (int j = 0; j < numResource; j++) {
                        if (allocation[i][j] != 0) {
                            System.out.println("Process " + i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void Run (String filePath) {
        readFile(filePath);

        if (mode.equals(MODE_BANKER)) {
            Banker();
        } else if (mode.equals(MODE_DETECTION)) {
            Detection();
        }
    }
}
