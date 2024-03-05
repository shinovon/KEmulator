package emulator.custom.subclass;

import java.util.*;

public class Timer {
    private TaskQueue queue;
    private TimerThread thread;

    public Timer() {
        super();
        this.queue = new TaskQueue();
    }

    public void schedule(final SubTimerTask subTimerTask, final long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        }
        this.sched(subTimerTask, System.currentTimeMillis() + n, 0L);
    }

    public void schedule(final SubTimerTask subTimerTask, final Date date) {
        this.sched(subTimerTask, date.getTime(), 0L);
    }

    public void schedule(final SubTimerTask subTimerTask, final long n, final long n2) {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        }
        if (n2 <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.sched(subTimerTask, System.currentTimeMillis() + n, -n2);
    }

    public void schedule(final SubTimerTask subTimerTask, final Date date, final long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.sched(subTimerTask, date.getTime(), -n);
    }

    public void scheduleAtFixedRate(final SubTimerTask subTimerTask, final long n, final long n2) {
        if (n < 0L) {
            throw new IllegalArgumentException("Negative delay.");
        }
        if (n2 <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.sched(subTimerTask, System.currentTimeMillis() + n, n2);
    }

    public void scheduleAtFixedRate(final SubTimerTask subTimerTask, final Date date, final long n) {
        if (n <= 0L) {
            throw new IllegalArgumentException("Non-positive period.");
        }
        this.sched(subTimerTask, date.getTime(), n);
    }

    private void sched(final SubTimerTask subTimerTask, final long nextExecutionTime, final long period) {
        if (nextExecutionTime < 0L) {
            throw new IllegalArgumentException("Illegal execution time.");
        }
        synchronized (this.queue) {
            if (!this.queue.newTasksMayBeScheduled) {
                throw new IllegalStateException("Timer already cancelled.");
            }
            if (this.thread == null || !this.thread.isAlive()) {
                (this.thread = new TimerThread(this.queue)).start();
            }
            synchronized (subTimerTask.lock) {
                if (subTimerTask.state != 0) {
                    throw new IllegalStateException("Task already scheduled or cancelled");
                }
                subTimerTask.nextExecutionTime = nextExecutionTime;
                subTimerTask.period = period;
                subTimerTask.state = 1;
            }
            this.queue.add(subTimerTask);
            if (this.queue.getMin() == subTimerTask) {
                this.queue.notify();
            }
        }
    }

    public void cancel() {
        synchronized (this.queue) {
            this.queue.newTasksMayBeScheduled = false;
            this.queue.clear();
            this.queue.notify();
        }
    }
}
