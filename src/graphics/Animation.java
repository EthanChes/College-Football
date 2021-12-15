package graphics;

import gameplay.Timer;

public class Animation {
    private Texture[] frames;
    private int pointer;

    private double elapsedTime;
    private double currentTime;
    private double lastTime;
    private double fps;

    public Animation(int amount, int fps, String filename) { // amount is total frames, fps is fps of animation, filename is name of file
        this.pointer = 0;
        this.elapsedTime = 0;
        this.currentTime = 0;
        this.lastTime = Timer.getTime();
        this.fps = 1.0/(double)fps;

        this.frames = new Texture[amount];
        for (int count = 0; count < amount; count++) {
            this.frames[count] = new Texture("animation/" + filename + "/_" + count + ".png");
        }
    }

    public void bind() { bind(0); }

    public void bind(int sampler) {
        this.currentTime = Timer.getTime();
        this.elapsedTime += currentTime-lastTime;

        if (elapsedTime >= fps) {
            elapsedTime = 0;
            pointer++;
        }

        if (pointer >= frames.length) pointer = 0;

        this.lastTime = currentTime;

        frames[pointer].bind(sampler);
    }

}
