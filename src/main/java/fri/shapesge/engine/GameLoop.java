package fri.shapesge.engine;

class GameLoop implements Runnable {
    private final GameFPSCaps fpsCaps;
    private final GameFPSCounter fpsCounter;
    private final GameWindow gameWindow;
    private final GameTimerProcessor timerProcessor;
    private final GameEventDispatcher eventDispatcher;
    private volatile Thread thread;
    private boolean preventiveStop;

    GameLoop(GameWindow gameWindow, GameTimerProcessor timerProcessor, GameEventDispatcher eventDispatcher, GameFPSCounter fpsCounter, GameConfig gameConfig) {
        this.gameWindow = gameWindow;
        this.timerProcessor = timerProcessor;
        this.eventDispatcher = eventDispatcher;
        this.fpsCounter = fpsCounter;

        this.fpsCaps = new GameFPSCaps(gameConfig.getInt(GameConfig.WINDOW_SECTION, GameConfig.FPS));
    }

    @Override
    public void run() {
        while (!this.preventiveStop) {
            this.fpsCounter.countFrame();

            this.timerProcessor.processTimers();
            this.eventDispatcher.doEvents();

            try {
                this.gameWindow.redraw();
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

            this.fpsCaps.doWait();
        }
    }

    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.thread = null;
        this.preventiveStop = true;
    }
}
