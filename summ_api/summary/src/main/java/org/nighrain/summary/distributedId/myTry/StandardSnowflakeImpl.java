package org.nighrain.summary.distributedId.myTry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 *    
 * Title         [ 生成唯一id]
 * Author:       [..]
 * CreateDate:   [2019-04-21--17:56]  @_@ ~~
 * Version:      [v1.0]
 * Description:  [根据雪花算法改写的]
 * <p>
 *     去除了数据中心id
 * </p>
 *  
 */
public class StandardSnowflakeImpl implements IdWorker {

    private static final Logger log = LoggerFactory.getLogger(StandardSnowflakeImpl.class);
    private static final String lineSp = System.getProperty("line.separator", "\r\n");

    //机器编号
    private long machineId ;

    public static void main(String[] args) {
        //1254840016441383
        long start = System.currentTimeMillis();
        IdWorker idWorker = StandardSnowflakeImpl.create(0);
        for (int i = 0; i < 1000; i++) {
            System.out.println(i+"\t\t"+idWorker.nextId());
        }
        System.out.println((System.currentTimeMillis()-start)+"ms");

    }

    /**
     * @param machineId 机器编号 [0,32]
     */
    public static IdWorker create (long machineId){
        return new StandardSnowflakeImpl(machineId);
    }

    /**
     * @param machineId 机器编号 [0,32]
     */
    private StandardSnowflakeImpl(long machineId) {
        //check for machineId
        if(machineId > maxMachineId || machineId < 0){
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0, but got %d",maxMachineId , machineId));
        }
        System.out.printf(lineSp+"[StandardSnowflakeImpl] worker starting. timestamp left shift %d, machineId id bits %d, sequence bits %d, machineId %d"+lineSp,
                timestampLeftShift, machineIdBits, sequenceBits, machineId);
        this.machineId = machineId;
    }

    private static final long timeStart = 1546272000000L;  //时间戳的参考值  //1546272000000L对应的是 2019-01-01
    private long lastTimestamp = -1L;  //上次最新时间戳
    private long sequence = 0L;  //当前序列号

    /**
     * 二进制中占的位数
     */
    private static final long machineIdBits = 5L; //机器标识符所占的位数
    private static final long sequenceBits = 12L; //序列号所占的位数

    /**
     * 最大值
     */
    private long maxMachineId = -1L ^ (-1L << machineIdBits);         //机器标识最大值
    private long sequenceMask = -1L ^ (-1L << sequenceBits);           // -1L^(-1L << 12L) //4095 同一毫秒内可使用的最大的队列数

    /**
     * 偏移量
     */
    private long machineIdShift = sequenceBits;                         // 机器标识 要左移 12
    private long timestampLeftShift = machineIdBits + sequenceBits;    // 时间戳 要左移 12+5=17

    //获得机器id
    public long getMachineId() {
        return machineId;
    }

    //获得当前时间戳
    public long timeGen(){
        return System.currentTimeMillis();
    }

    @Override
    public long nextId() {
        return getNextId();
    }

    //同步方法 上锁
    private synchronized long getNextId(){
        long timeStamp = timeGen();

        if(timeStamp < lastTimestamp){
            //时钟回滚了
            log.error(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timeStamp));
        }

        if(timeStamp == lastTimestamp){
            //在同一毫秒数内 则队列数 +1 用  (sequence + 1) & sequenceMask 来保证 队列位数不会溢出
            sequence = (sequence +1) & sequenceMask;
            if(sequence == 0){
                //等于0 就是位数溢出了 则等待下一毫秒
                timeStamp = waitNextMillis(lastTimestamp);
            }
        }else{
            //在当前毫秒数是新的毫秒 则对列从 0 开始
            sequence = 0;
        }

        // 为下一次取id做毫秒数的判断 留下参考
        lastTimestamp = timeStamp;
        return ((timeStamp - timeStart) << timestampLeftShift) |
                (machineId << machineIdShift) |
                sequence;
    }

    //等待下一秒
    private long waitNextMillis(long lastTimestamp) {
        long timeStamp = timeGen();
        while (timeStamp <= lastTimestamp){
            timeStamp = timeGen();
        }
        return timeStamp;
    }


}
