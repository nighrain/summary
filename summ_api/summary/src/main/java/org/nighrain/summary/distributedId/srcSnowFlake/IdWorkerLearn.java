package org.nighrain.summary.distributedId.srcSnowFlake;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *    
 * Title         [学习理解雪花算法]
 * Author:       [nighrain]
 * CreateDate:   [2019-04-21--15:01]  @_@ ~~
 * Version:      [v1.0]
 * Description:  [学习理解雪花算法]
 * <p>
 *     根据 : https://blog.csdn.net/cj2580/article/details/80980459
 * </p>
 *  
 */
public class IdWorkerLearn {

    //---测试---
    public static void main(String[] args) {
        core();
    }
/*
此算法中用到的知识点
    二进制的位运算 <<  &  |
 */
/**
 * 用mask防止溢出
 *      sequence = (sequence + 1) & sequenceMask;
 */
/*
long seqMask = -1L ^ (-1L << 12L); //计算12位能耐存储的最大正整数，相当于：2^12-1 = 4095
System.out.println("seqMask: "+seqMask);       //  seqMask: 4095
System.out.println(1L & seqMask);              //  1
System.out.println(2L & seqMask);              //  2
System.out.println(3L & seqMask);              //  3
System.out.println(4L & seqMask);              //  4
System.out.println(4095L & seqMask);           //  4095
System.out.println(4096L & seqMask);           //  0
System.out.println(4097L & seqMask);           //  1
System.out.println(4098L & seqMask);           //  2
 */
/**
 * 用位运算汇总结果
 *      return ((timestamp - twepoch) << timestampLeftShift) |
 *         (datacenterId << datacenterIdShift) |
 *         (workerId << workerIdShift) |
 *         sequence;
 */

/*
设：timestamp  = 1505914988849，twepoch = 1288834974657
1505914988849 - 1288834974657 = 217080014192 (timestamp相对于起始时间的毫秒偏移量)，其(a)二进制左移22位计算过程如下：

                        |<--这里开始左右22位                            ‭
00000000 00000000 000000|00 00110010 10001010 11111010 00100101 01110000 // a = 217080014192
00001100 10100010 10111110 10001001 01011100 00|000000 00000000 00000000 // a左移22位后的值(la)
                                               |<--这里后面的位补0

*/

/*

设：datacenterId  = 17，其（b）二进制左移17位计算过程如下：

                   |<--这里开始左移17位
00000000 00000000 0|0000000 ‭00000000 00000000 00000000 00000000 00010001 // b = 17
0000000‭0 00000000 00000000 00000000 00000000 0010001|0 00000000 00000000 // b左移17位后的值(lb)
                                                    |<--这里后面的位补0

 */

/*
设：workerId  = 25，其（c）二进制左移12位计算过程如下：

             |<--这里开始左移12位
‭00000000 0000|0000 00000000 00000000 00000000 00000000 00000000 00011001‬ // c = 25
00000000 00000000 00000000 00000000 00000000 00000001 1001|0000 00000000‬ // c左移12位后的值(lc)
                                                          |<--这里后面的位补0
 */

/*
设：sequence = 0，其二进制如下：

00000000 00000000 00000000 00000000 00000000 00000000 0000‭0000 00000000‬ // sequence = 0
 */

/*
return ((timestamp - 1288834974657) << 22) |
        (datacenterId << 17) |
        (workerId << 12) |
        sequence;
-----------------------------
           |
           |简化
          \|/
-----------------------------
return (la) |
        (lb) |
        (lc) |
        sequence;
 */

/*
1  |                    41                        |  5  |   5  |     12

   0|0001100 10100010 10111110 10001001 01011100 00|00000|0 0000|0000 00000000 //la
   0|000000‭0 00000000 00000000 00000000 00000000 00|10001|0 0000|0000 00000000 //lb
   0|0000000 00000000 00000000 00000000 00000000 00|00000|1 1001|0000 00000000 //lc
or 0|0000000 00000000 00000000 00000000 00000000 00|00000|0 0000|‭0000 00000000‬ //sequence
------------------------------------------------------------------------------------------
   0|0001100 10100010 10111110 10001001 01011100 00|10001|1 1001|‭0000 00000000‬ //结果：910499571847892992
 */

/*
观察
1  |                    41                        |  5  |   5  |     12

   0|0001100 10100010 10111110 10001001 01011100 00|     |      |              //la
   0|                                              |10001|      |              //lb
   0|                                              |     |1 1001|              //lc
or 0|                                              |     |      |‭0000 00000000‬ //sequence
------------------------------------------------------------------------------------------
   0|0001100 10100010 10111110 10001001 01011100 00|10001|1 1001|‭0000 00000000‬ //结果：910499571847892992
 */

/*
上面的64位我按1、41、5、5、12的位数截开了，方便观察。

纵向观察发现:

在41位那一段，除了la一行有值，其它行（lb、lc、sequence）都是0，（其他的省略了）
在左起第一个5位那一段，除了lb一行有值，其它行都是0
在左起第二个5位那一段，除了lc一行有值，其它行都是0
按照这规律，如果sequence是0以外的其它值，12位那段也会有值的，其它行都是0
横向观察发现:

在la行，由于左移了5+5+12位，5、5、12这三段都补0了，所以la行除了41那段外，其它肯定都是0
同理，lb、lc、sequnece行也以此类推
正因为左移的操作，使四个不同的值移到了SnowFlake理论上相应的位置，然后四行做位或运算（只要有1结果就是1），就把4段的二进制数合并成一个二进制数。
 */

/**
 * 结论:
 * 左移运算是为了将数值移动到对应的段(41、5、5，12那段因为本来就在最右，因此不用左移)。
 *
 * 然后对每个左移后的值(la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数。
 *
 * 最后转换成10进制，就是最终生成的id
 */
/**
 * 扩展
 * 在理解了这个算法之后，其实还有一些扩展的事情可以做：
 *
 *      根据自己业务修改每个位段存储的信息。算法是通用的，可以根据自己需求适当调整每段的大小以及存储的信息。
 *      解密id，由于id的每段都保存了特定的信息，所以拿到一个id，
 *          应该可以尝试反推出原始的每个段的信息。反推出的信息可以帮助我们分析。
 *          比如作为订单，可以知道该订单的生成日期，负责处理的数据中心等等。
 */
    //核心部分-组装最后的id
    public static void core(){

        long timestamp = 1505914988849L;
        long twepoch = 1288834974657L;
        long datacenterId = 17L;
        long workerId = 25L;
        long sequence = 0L;

        System.out.printf("\ntimestamp: %d \n",timestamp);
        System.out.printf("twepoch: %d \n",twepoch);
        System.out.printf("datacenterId: %d \n",datacenterId);
        System.out.printf("workerId: %d \n",workerId);
        System.out.printf("sequence: %d \n",sequence);
        System.out.println();
        System.out.printf("(timestamp - twepoch): %d \n",(timestamp - twepoch));
        System.out.printf("((timestamp - twepoch) << 22L): %d \n",((timestamp - twepoch) << 22L));
        System.out.printf("(datacenterId << 17L): %d \n" ,(datacenterId << 17L));
        System.out.printf("(workerId << 12L): %d \n",(workerId << 12L));
        System.out.printf("sequence: %d \n",sequence);

        long result = (timestamp-twepoch)<<22L |
                datacenterId << 17L |
                workerId << 12L |
                sequence;

        System.out.println(result);
        /*
            timestamp: 1505914988849
            twepoch: 1288834974657
            datacenterId: 17
            workerId: 25
            sequence: 0

            (timestamp - twepoch): 217080014192
            ((timestamp - twepoch) << 22L): 910499571845562368
            (datacenterId << 17L): 2228224
            (workerId << 12L): 102400
            sequence: 0
            910499571847892992
         */
    }

    //设置初始时间戳
    public static void timestamp(){
        System.out.println(System.currentTimeMillis());

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");
            System.out.println(date.getTime());  //1546272000000L
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
