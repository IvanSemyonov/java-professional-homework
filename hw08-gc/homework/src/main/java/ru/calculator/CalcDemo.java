package ru.calculator;


/*
-Xms256m
-Xmx256m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./logs/heapdump.hprof
-XX:+UseG1GC
-Xlog:gc=debug:file=./logs/gc-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m
*/

/*
До оптимизаций

|------------------------------------------|
| Heap Size | spend sec | spend msec       |
|-----------|-----------|------------------|
| 256M      | 12        |                  |
|-----------|-----------|------------------|
| 512M      | 8         | 8232             |
|-----------|-----------|------------------|
| 1024M     | 7         | 7713, 7928       |
|-----------|-----------|------------------|
| 2048M     | 7         | 7001, 6873, 7062 |
|-----------|-----------|------------------|
| 4096M     | 6         | 6842, 6859, 6829 |
|-----------|-----------|------------------|
| 8192M     | 7         | 7100, 7130, 7093 |
|-----------|-----------|------------------|

Оптимальный размер хипа: 4096M
 */


import java.time.LocalDateTime;

public class CalcDemo {
    public static void main(String[] args) {
        long counter = 100_000_000;
        var summator = new Summator();
        long startTime = System.currentTimeMillis();

        for (var idx = 0; idx < counter; idx++) {
            var data = new Data(idx);
            summator.calc(data);

            if (idx % 10_000_000 == 0) {
                System.out.println(LocalDateTime.now() + " current idx:" + idx);
            }
        }

        long delta = System.currentTimeMillis() - startTime;
        System.out.println(summator.getPrevValue());
        System.out.println(summator.getPrevPrevValue());
        System.out.println(summator.getSumLastThreeValues());
        System.out.println(summator.getSomeValue());
        System.out.println(summator.getSum());
        System.out.println("spend msec:" + delta + ", sec:" + (delta / 1000));
    }
}
