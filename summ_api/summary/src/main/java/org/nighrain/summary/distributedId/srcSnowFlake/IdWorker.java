package org.nighrain.summary.distributedId.srcSnowFlake;

/**
 *    
 * Title         [snowflake 分布式 id 生成]
 * Author:       [https://blog.csdn.net/cj2580/article/details/80980459]
 * CreateDate:   [2019-04-21--14:18]  @_@ ~~
 * Version:      [v1.0]
 * Description:  [学习雪花算法的Java版本]
 * <p>
 *     SnowFlake算法生成id的结果是一个64bit大小的整数, 它的结构如下图：
 *   <image>
 *     [snowflake-64bit]
 *                      41bit-时间戳                                        12bit-序列号
 *       v-------------------------------------------------v              v------------v
 *     0-000 0000 0000 0000 0000 0000 0000 0000 0000 0000 00-00 0000 0000-0000 0000 0000
 *     ^                                                     ^----------^
 *     1bit-不用                                             10bit-工作机器id
 *     可表示的最大的数是 ; 9,223,372,036,854,775,807 共19个位
 *   </image>
 *     
 * 1位, 不用. 二进制中最高位为1的都是负数, 但是我们生成的id一般都使用整数, 所以这个最高位固定是0;
 * 41位, 41位的时间序列, 精确到毫秒级, 41位的长度可以使用69年. 时间位还有一个很重要的作用是可以根据时间进行排序;
 *      如果只用来表示正整数（计算机中正数包含0）, 可以表示的数值范围是：0 至 , 减1是因为可表示的数值范围是从0开始算的, 而不是1. 
 *      也就是说41位可以表示个毫秒的值, 转化成单位年则是年
 *
 * 10位, 10位的机器标识，10位的长度最多支持部署1024个节点;
 *      可以部署在个节点, 包括5位 datacenterId 和5位 workerId
 *      5位（bit）可以表示的最大正整数是, 即可以用0、1、2、3、....31这32个数字, 来表示不同的datecenterId或workerId
 *
 * 12位, 序列号, 用来记录同毫秒内产生的不同id. 
 *      12位（bit）可以表示的最大正整数是, 即可以用0、1、2、3、....4094这4095个数字, 来表示同一机器同一时间截（毫秒)内产生的4095个ID序号
 *
 * 由于在Java中64bit的整数是long类型, 所以在Java中SnowFlake算法生成的id就是long来存储的. 
 * SnowFlake可以保证：
 *      所有生成的id按时间趋势递增
 *      整个分布式系统内不会产生重复id（因为有 datacenterId 和 workerId 来做区分）
 * Talk is cheap, show you the code!
 * </p>
 *  
 */

public class IdWorker{

    private long workerId;
    private long datacenterId;
    private long sequence;

    /**
     *
     * @param workerId   机器标识符
     * @param datacenterId  数据中心id
     * @param sequence   队列的初始值
     */
    public IdWorker(long workerId, long datacenterId, long sequence){
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);

        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
    }

//    private long twepoch = 1288834974657L;                             //起始时间戳，用于用当前时间戳减去这个时间戳，算出偏移量
    private long twepoch = 1546272000000L;                             //起始时间戳，用于用当前时间戳减去这个时间戳，算出偏移量

    private long workerIdBits = 5L;                                    //机器标识占用的位数:5
    private long datacenterIdBits = 5L;                                //数据中心占用的位数:5
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);            //workerId可以使用的最大数值：31
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);    //datacenterId可以使用的最大数值：31
    private long sequenceBits = 12L;                                   //序列号占用的位数：12

    private long workerIdShift = sequenceBits;                         // 要左移 12
    private long datacenterIdShift = sequenceBits + workerIdBits;      // 要左移 12+5=17
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;    // 要左移 12+5+5=22
    private long sequenceMask = -1L ^ (-1L << sequenceBits);           // -1L^(-1L << 12L) //4095 同一毫秒内可使用的最大的队列数

    private long lastTimestamp = -1L;                                   //标识上一毫秒 用来判断当前毫秒是新的毫秒数还是旧的毫秒数

    public long getWorkerId(){
        return workerId;
    }

    public long getDatacenterId(){
        return datacenterId;
    }

    public long getTimestamp(){
        return System.currentTimeMillis();
    }

    //同步代码块 上锁
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            //时钟回滚了
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            //在同一毫秒数内 则队列数 +1 用  (sequence + 1) & sequenceMask 来保证 队列位数不会溢出
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //等于0 就是位数溢出了 则等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //在当前毫秒数是新的毫秒 则对列从 0 开始
            sequence = 0;
        }

        // 为下一次取id做毫秒数的判断 留下参考
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    //等待下一毫秒
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen(){
        return System.currentTimeMillis();
    }

    //---------------测试---------------
    public static void main(String[] args) {
        IdWorker worker = new IdWorker(1,1,1);
        for (int i = 0; i < 30; i++) {
            System.out.println(worker.nextId());
        }
    }

}
