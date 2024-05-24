package main;

/** Detects if animation started or completed*/
public interface AnimationListener {

    void animationStarted(SceneAnimator aPane);
    void animationCompleted(SceneAnimator aPane);
}
