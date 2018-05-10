package com.gf.base;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class LongEventMain {

	public static void main(String[] args) {
		producer();
		producer2();
		producer3();
	}

	public static void producer() {
		// 创建缓冲池
		ExecutorService executor = Executors.newCachedThreadPool();
		// 创建工厂
		LongEventFactory factory = new LongEventFactory();
		// 创建bufferSize，也就是RingBuffer大小，必须是2的N次方
		int ringBufferSize = 1024 * 1024;

		/**
		 *
		 * //BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各个不同部署环境中能更加一致的性能表现
		 * WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
		 * //SleepingWaitStrategy
		 * 的性能表现跟BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者的影响最小，适合用于异步日志类似的场景
		 * WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
		 * //YieldingWaitStrategy
		 * 的性能最好，适用于低延迟系统，在要求极高性能且事件处理线程数小于CPU逻辑核心数的场景中，推荐使用此此策略；例如，CPU开启超线程的特性
		 * WaitStrategy YIELDING_WAIT = new YieldingWaitStrategy();
		 * 
		 */

		// 创建disruptor
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ringBufferSize, executor,
				ProducerType.SINGLE, new YieldingWaitStrategy());

		// 连接消费disuptor
		disruptor.handleEventsWith(new LongEventHandler());

		// 启动
		disruptor.start();

		// Disruptor 的事件发布过程是一个两阶段提交的过程：
		// 发布事件
		RingBuffer ringBuffer = disruptor.getRingBuffer();

		// LongEventProducer producer = new LongEventProducer(ringBuffer);
		LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);

		for (long l = 0; l < 100; l++) {
			byteBuffer.putLong(0, l);
			producer.onData(byteBuffer);
			// Thread.sleep(1000);
		}
	}

	public static void producer2() {
		LongEventFactory factory = new LongEventFactory();
		int ringBufferSize = 1024 * 1024;
		ThreadFactory threadFactory = new ThreadFactory() {

			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		};
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ringBufferSize, threadFactory);
		disruptor.handleEventsWith(new LongEventHandler());
		disruptor.start();

		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);

		for (long l = 0; l < 100; l++) {
			byteBuffer.putLong(0, l);
			producer.onData(byteBuffer);
			// Thread.sleep(1000);
		}
	}
	
	public static void producer3() {
		LongEventFactory factory = new LongEventFactory();
		int ringBufferSize = 1024 * 1024;
		ThreadFactory threadFactory = new ThreadFactory() {

			public Thread newThread(Runnable r) {
				return new Thread(r);
			}
		};
		Disruptor<LongEvent> disruptor = new Disruptor<LongEvent>(factory, ringBufferSize, threadFactory,ProducerType.SINGLE, new YieldingWaitStrategy());
		disruptor.handleEventsWith(new LongEventHandler());
		disruptor.start();

		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);

		ByteBuffer byteBuffer = ByteBuffer.allocate(8);

		for (long l = 0; l < 100; l++) {
			byteBuffer.putLong(0, l);
			producer.onData(byteBuffer);
			// Thread.sleep(1000);
		}
		
	}

}
