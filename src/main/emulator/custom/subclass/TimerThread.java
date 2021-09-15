package emulator.custom.subclass;

class TimerThread extends Thread
{
    private TaskQueue queue;
    private static final long THREAD_TIMEOUT = 30000L;
    
    TimerThread(final TaskQueue queue) {
        super();
        this.queue = queue;
    }
    
    public void run() {
        try {
            this.mainLoop();
        }
        catch (Throwable t) {
            synchronized (this.queue) {
                this.queue.newTasksMayBeScheduled = false;
                this.queue.clear();
            }
        }
    }
    
    private void mainLoop() {
        while (true) {
            try {
                while (true) {
                    final SubTimerTask min;
                    final boolean b;
                    synchronized (this.queue) {
                        while (this.queue.isEmpty() && this.queue.newTasksMayBeScheduled) {
                            this.queue.wait(30000L);
                            if (this.queue.isEmpty()) {
                                break;
                            }
                        }
                        if (this.queue.isEmpty()) {
                            return;
                        }
                        final SubTimerTask subTimerTask = min = this.queue.getMin();
                        final long currentTimeMillis;
                        final long nextExecutionTime;
                        synchronized (subTimerTask.lock) {
                            if (min.state == 3) {
                                this.queue.removeMin();
                                continue;
                            }
                            currentTimeMillis = System.currentTimeMillis();
                            if (b = ((nextExecutionTime = min.nextExecutionTime) <= currentTimeMillis)) {
                                if (min.period == 0L) {
                                    this.queue.removeMin();
                                    min.state = 2;
                                }
                                else {
                                    this.queue.rescheduleMin((min.period < 0L) ? (currentTimeMillis - min.period) : (nextExecutionTime + min.period));
                                }
                            }
                        }
                        if (!b) {
                            this.queue.wait(nextExecutionTime - currentTimeMillis);
                        }
                    }
                    if (b) {
                        try {
                            min.run();
                        }
                        catch (Exception ex) {
                            min.cancel();
                        }
                    }
                }
            }
            catch (InterruptedException ex2) {
                continue;
            }
        }
    }
}
