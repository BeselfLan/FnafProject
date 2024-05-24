package main;

/** Detect when fade animation starts and ends*/
public interface FadeListener {

    void fadeStarted(FadePane pane);
    void fadeCompleted(FadePane pane);
}
