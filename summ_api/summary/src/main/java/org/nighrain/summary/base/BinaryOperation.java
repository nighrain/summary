package org.nighrain.summary.base;

/**
 * Title         [Java 中二进制运算总结 **binary operation**]
 * Author:       [nighrain]
 * CreateDate:   [2019-04-21--18:45]  @_@ ~~
 * Version:      [v1.0]
 * Description:  [demo]
 * <p>
 * ^    : "^"(异或运算) 针对二进制,相同为0,不同为1;
 *              eg: 3^2 = 0011^0010 = 0001
 * &    : "&"(与运算) 针对二进制,只要有一个为0,就为0;
 *              eg: 3&2 = 0011&0010 = 0010
 * |    : "|"(管道符号) 针对二进制, 含义:x的第n位和y的第n位, 只要有一个是1, 则结果的第n位也为1, 否则为0;
 *              eg: 3|2 = 0011|0010 = 0011
 * <<   :"<<"(向左位移) 针对二进制,转换成二进制后向左移动指定位数位,后面用0补齐;
 *              eg: 3<<5 是把int3转换成二进制数 0000 0000 0000 0011 然后向左移动5位 即 0000 0000 0110 0000
 *                  相当于 3乘2的五次方=96
 * >>   :">>"(向右位移) 针对二进制,转换成二进制后向右移动3位;
 *              eg: 和上面"<<" 相反
 *                  16>>2 = 4
 *                  15>>2 = 3
 * >>>  :">>>"(无符号右移)  无符号右移,忽略符号位，空位都以0补齐;
 *              eg: >>>和>>的唯一不同是: 它的二进制最高位无论是0还是1,都会用0填充;
 *                  正数的 >>> 效果和 >> 完全一致,只有负数有区别;
 *                  比如，byte是8位的, -1表示为byte型是 11111111 (补码表示法)
 *                  byte b = -1; b>>>4 就是无符号右移4位，即 00001111 ,这样结果就是15;
 * </p>
 */
public class BinaryOperation {
    public static void main(String[] args) {
        t2();
    }

    public static void t1(){
        long a = -1L << 5L;// -32 2的5次方
        long b  = -1L ^ (-1L << 5L);//31 2的5次方-1
        System.out.printf("a=%d ,b=%d\r\n",a,b);
        System.out.println("b = " + b);
        System.out.println(3<<3);//3*(2*2*2)=24
        System.out.println(1<<12);//
        System.out.println(4<<3);//3*(2*2*2*2)=24
        System.out.println(4<<2);//3*(2*2)=16
        System.out.println(-1L>>>1L);
        System.out.println("=============");
        System.out.println(1073741824 >>> 1);
        System.out.println(3&2);
        System.out.println(15>>2);
    }
    public static void t2(){
        int a = 3;      //0011
        int b = 1;      //0001
        System.out.println("a>>b = "+(3>>1));
        System.out.println("a<<b = "+(3<<1));
        System.out.println("a&b = "+(3&1));
        System.out.println("a|b = "+(3|1));
        System.out.println("a^b = "+(a^b));
        /*
        结果;
            a>>b = 1
            a<<b = 6
            a&b = 1
            a|b = 3
            a^b = 2
         */

    }

}
