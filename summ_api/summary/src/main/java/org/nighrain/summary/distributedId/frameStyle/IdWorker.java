package org.nighrain.summary.distributedId.frameStyle;

/**
 * Generate ID
 * @author mayanjun
 * @since 1.0.0(Jun 26, 2015)
 * title <p>
 *     生成id的统一接口
 * </p>
 */
public interface IdWorker {

    int MIN_HANDLER_ID = IdWorkerHandler.MIN_WORKER_INDEX;

    int MAX_HANDLER_ID = IdWorkerHandler.MAX_WORKER_INDEX;

    long nextId();
}