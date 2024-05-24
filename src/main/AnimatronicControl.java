package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/** Controls the actions of the animatronics*/
public class AnimatronicControl {
	
    UI ui;

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> graph = new HashMap<>();
    public Animatronic freddy, freddy1, freddy2, freddy3;
    public ArrayList<ArrayList<Integer>> connectedNodes;

    /** Stop the animatronic timers*/
    public void resetAnimatronics() {

        freddy.moveTimer.stop();
        freddy.freddyVentTimer.stop();

        freddy1.moveTimer.stop();
        freddy1.freddyVentTimer.stop();

        freddy2.moveTimer.stop();
        freddy2.freddyVentTimer.stop();

        freddy3.moveTimer.stop();
        freddy3.freddyVentTimer.stop();
    }

    /**
     * Loads resources that animatronics will use and connects with UI
     * @param ui is the UI
     */
    public AnimatronicControl(UI ui) {

        this.ui = ui;

        createGraph();
        createSound(7);
        createAnimatronics();
        System.out.println(connectedNodes);
    }

    /** Creates all the animatronics and randomizes their starting room*/
    public void createAnimatronics() {

        Random r = new Random();

        freddy = new Animatronic(this, r.nextInt(13) + 1, 1);
        freddy1 = new Animatronic(this, r.nextInt(13) + 1, 2);
        freddy2 = new Animatronic(this, r.nextInt(13) + 1, 3);
        freddy3 = new Animatronic(this, r.nextInt(13) + 1, 4);
    }

    /** Depending on what night, it determines the number of animatronics in the facility*/
    public void startAnimatronics() {

        if (ui.gm.night > 1) {
            freddy.movementStart();
            if (ui.gm.night > 2) {
                freddy1.movementStart();
                if (ui.gm.night > 3) {
                    freddy2.movementStart();
                    if (ui.gm.night > 4) {
                        freddy3.movementStart();
                    }
                }
            }
        }
    }

    /** Creates weighted graph through having rooms point to the rooms that can be travelled too and gives then percentage of traveling their*/
    public void createGraph() {

        ArrayList<Integer> weightList1 = new ArrayList<>(Arrays.asList(0, 33));
        ArrayList<Integer> weightList2 = new ArrayList<>(Arrays.asList(2, 66));
        ArrayList<Integer> weightList3 = new ArrayList<>(Arrays.asList(3, 99));
        ArrayList<ArrayList<Integer>> roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(1, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 50));
        weightList2 = new ArrayList<>(Arrays.asList(4, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.put(2, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 33));
        weightList2 = new ArrayList<>(Arrays.asList(4, 66));
        weightList3 = new ArrayList<>(Arrays.asList(6, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(3, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(2, 33));
        weightList2 = new ArrayList<>(Arrays.asList(3, 66));
        weightList3 = new ArrayList<>(Arrays.asList(5, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(4, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(4, 50));
        weightList2 = new ArrayList<>(Arrays.asList(6, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.put(5, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(3, 33));
        weightList2 = new ArrayList<>(Arrays.asList(5, 66));
        weightList3 = new ArrayList<>(Arrays.asList(7, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(6, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(6, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.put(7, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(0, 33));
        weightList2 = new ArrayList<>(Arrays.asList(9, 66));
        weightList3 = new ArrayList<>(Arrays.asList(10, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(8, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 50));
        weightList2 = new ArrayList<>(Arrays.asList(11, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.put(9, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 33));
        weightList2 = new ArrayList<>(Arrays.asList(11, 66));
        weightList3 = new ArrayList<>(Arrays.asList(13, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(10, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(9, 33));
        weightList2 = new ArrayList<>(Arrays.asList(10, 66));
        weightList3 = new ArrayList<>(Arrays.asList(12, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(11, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(11, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.put(12, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(7, 33));
        weightList2 = new ArrayList<>(Arrays.asList(10, 66));
        weightList3 = new ArrayList<>(Arrays.asList(12, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.put(13, roomList);
    }

    /** Resets graph to default chances*/
    public void resetGraph() {

        ArrayList<Integer> weightList1 = new ArrayList<>(Arrays.asList(0, 33));
        ArrayList<Integer> weightList2 = new ArrayList<>(Arrays.asList(2, 66));
        ArrayList<Integer> weightList3 = new ArrayList<>(Arrays.asList(3, 99));
        ArrayList<ArrayList<Integer>> roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(1, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 50));
        weightList2 = new ArrayList<>(Arrays.asList(4, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(2, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 33));
        weightList2 = new ArrayList<>(Arrays.asList(4, 66));
        weightList3 = new ArrayList<>(Arrays.asList(6, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(3, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(2, 33));
        weightList2 = new ArrayList<>(Arrays.asList(3, 66));
        weightList3 = new ArrayList<>(Arrays.asList(5, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(4, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(4, 50));
        weightList2 = new ArrayList<>(Arrays.asList(6, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(5, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(3, 33));
        weightList2 = new ArrayList<>(Arrays.asList(5, 66));
        weightList3 = new ArrayList<>(Arrays.asList(7, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(6, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(6, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(7, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(0, 33));
        weightList2 = new ArrayList<>(Arrays.asList(9, 66));
        weightList3 = new ArrayList<>(Arrays.asList(10, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(8, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 50));
        weightList2 = new ArrayList<>(Arrays.asList(11, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(9, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 33));
        weightList2 = new ArrayList<>(Arrays.asList(11, 66));
        weightList3 = new ArrayList<>(Arrays.asList(13, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(10, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(9, 33));
        weightList2 = new ArrayList<>(Arrays.asList(10, 66));
        weightList3 = new ArrayList<>(Arrays.asList(12, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(11, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(11, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(12, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(7, 33));
        weightList2 = new ArrayList<>(Arrays.asList(10, 66));
        weightList3 = new ArrayList<>(Arrays.asList(12, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(13, roomList);
    }

    /** Increase chance of traveling to room closer to your room*/
    public void createTaskGraph() {
    	
        ArrayList<Integer> weightList1 = new ArrayList<>(Arrays.asList(0, 50));
        ArrayList<Integer> weightList2 = new ArrayList<>(Arrays.asList(2, 75));
        ArrayList<Integer> weightList3 = new ArrayList<>(Arrays.asList(3, 100));
        ArrayList<ArrayList<Integer>> roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(1, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 60));
        weightList2 = new ArrayList<>(Arrays.asList(4, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(2, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(1, 40));
        weightList2 = new ArrayList<>(Arrays.asList(4, 70));
        weightList3 = new ArrayList<>(Arrays.asList(6, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(3, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(2, 35));
        weightList2 = new ArrayList<>(Arrays.asList(3, 70));
        weightList3 = new ArrayList<>(Arrays.asList(5, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(4, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(4, 50));
        weightList2 = new ArrayList<>(Arrays.asList(6, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(5, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(3, 33));
        weightList2 = new ArrayList<>(Arrays.asList(5, 66));
        weightList3 = new ArrayList<>(Arrays.asList(7, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(6, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(6, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(7, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(0, 50));
        weightList2 = new ArrayList<>(Arrays.asList(9, 75));
        weightList3 = new ArrayList<>(Arrays.asList(10, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(8, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 60));
        weightList2 = new ArrayList<>(Arrays.asList(11, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(9, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(8, 40));
        weightList2 = new ArrayList<>(Arrays.asList(11, 70));
        weightList3 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(10, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(9, 35));
        weightList2 = new ArrayList<>(Arrays.asList(10, 70));
        weightList3 = new ArrayList<>(Arrays.asList(12, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(11, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(11, 50));
        weightList2 = new ArrayList<>(Arrays.asList(13, 100));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2));
        graph.replace(12, roomList);

        weightList1 = new ArrayList<>(Arrays.asList(7, 33));
        weightList2 = new ArrayList<>(Arrays.asList(10, 66));
        weightList3 = new ArrayList<>(Arrays.asList(12, 99));
        roomList = new ArrayList<>(Arrays.asList(weightList1, weightList2, weightList3));
        graph.replace(13, roomList);
    }

    /**
     * Create a decoy sound to distract animatronics (increased chance of traveling towards the room with the audio)
     * @param node is the room where the distraction sound is put
     */
    public void createSound(int node) {
    	
    	connectedNodes = graph.get(node);
    }
}
