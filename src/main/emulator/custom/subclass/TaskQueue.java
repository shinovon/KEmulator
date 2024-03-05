package emulator.custom.subclass;

class TaskQueue {
    private SubTimerTask[] queue;
    private int size;
    boolean newTasksMayBeScheduled;

    TaskQueue() {
        super();
        this.queue = new SubTimerTask[4];
        this.size = 0;
        this.newTasksMayBeScheduled = true;
    }

    void add(final SubTimerTask subTimerTask) {
        if (++this.size == this.queue.length) {
            final SubTimerTask[] queue = new SubTimerTask[2 * this.queue.length];
            System.arraycopy(this.queue, 0, queue, 0, this.size);
            this.queue = queue;
        }
        this.queue[this.size] = subTimerTask;
        this.fixUp(this.size);
    }

    SubTimerTask getMin() {
        return this.queue[1];
    }

    void removeMin() {
        this.queue[1] = this.queue[this.size];
        this.queue[this.size--] = null;
        this.fixDown(1);
    }

    void rescheduleMin(final long nextExecutionTime) {
        this.queue[1].nextExecutionTime = nextExecutionTime;
        this.fixDown(1);
    }

    boolean isEmpty() {
        return this.size == 0;
    }

    void clear() {
        for (int i = 1; i <= this.size; ++i) {
            this.queue[i] = null;
        }
        this.size = 0;
    }

    private void fixUp(int i) {
        while (i > 1) {
            final int n = i >> 1;
            if (this.queue[n].nextExecutionTime <= this.queue[i].nextExecutionTime) {
                return;
            }
            final SubTimerTask subTimerTask = this.queue[n];
            this.queue[n] = this.queue[i];
            this.queue[i] = subTimerTask;
            i = n;
        }
    }

    private void fixDown(int n) {
        int n2;
        while ((n2 = n << 1) <= this.size) {
            if (n2 < this.size && this.queue[n2].nextExecutionTime > this.queue[n2 + 1].nextExecutionTime) {
                ++n2;
            }
            if (this.queue[n].nextExecutionTime <= this.queue[n2].nextExecutionTime) {
                return;
            }
            final SubTimerTask subTimerTask = this.queue[n2];
            this.queue[n2] = this.queue[n];
            this.queue[n] = subTimerTask;
            n = n2;
        }
    }
}
