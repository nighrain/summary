package org.nighrain.summary.distributedId.frameStyle;

/**
 * IdWorkerBuilder
 *
 * @author mayanjun(5/1/16)
 * <p>工厂</p>
 */
public class IdWorkerFactory {

    public static IdWorker create(int ... indexes) {
        return new StardardIdWorker(indexes);
    }
}
