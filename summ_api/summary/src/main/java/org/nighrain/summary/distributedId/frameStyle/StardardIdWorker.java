package org.nighrain.summary.distributedId.frameStyle;

/**
 * StardardIdWorker
 *
 * @author mayanjun(5/1/16)
 * <p>
 *     IDworker的实现类
 * </p>
 */
public class StardardIdWorker implements IdWorker {

    private IdWorkerHandler handler;

    public StardardIdWorker(int ... indexes) {
        handler = new IdWorkerHandler(indexes);
    }

    public int getMaxIndex() {
        return IdWorkerHandler.MAX_WORKER_INDEX;
    }


    @Override
    public long nextId() {
        return this.handler.nextId();
    }
}
