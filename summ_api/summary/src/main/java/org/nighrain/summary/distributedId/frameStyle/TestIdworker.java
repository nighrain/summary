package org.nighrain.summary.distributedId.frameStyle;

public class TestIdworker {
    public static void main(String[] args) {

        IdWorker idWorker = IdWorkerFactory.create(1,2,14);
        for (int i = 0; i < 10; i++) {
            long l = idWorker.nextId();
            System.out.println(l);
        }
    }
}
