package main;

/** Controls computer task actions*/
public class ComputerHandler {

    UI ui;

    private int tasksCompleted = 0;
    private final int TOTALTASKS = 2;

    /**
     * Connects ComputerHandler to UI
     * @param ui is the UI
     */
    public ComputerHandler(UI ui ) {

        this.ui = ui;
    }

    /**
     * keeps track of how many tasks are completed
     * @param command determine the action to update computer
     */
    public void updateComputer(String command) {

        switch(command) {
            case "taskComplete": tasksCompleted++; break;
            case "taskCancel": ui.taskTimer.stop(); ui.resetTaskTimer(); break;

        }
        if (tasksCompleted == TOTALTASKS) {
        	ui.allTasksComplete = true;
        }
    }

    /** Reset number of tasks completed*/
    public void resetTasks() {
        tasksCompleted = 0;
        ui.allTasksComplete = false;
        ui.resetTaskTimer();
    }
}