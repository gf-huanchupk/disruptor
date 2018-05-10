package com.gf.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;
/**
 * 很明显的是：但用一个简单队列来发布事件的时候回牵涉更多的细节，这是因为事件对象还需要预先创建。发布事件需要两步：
 *          获取下一个事件槽不发布事件（发布事件要使用try/finally保证事件一定会被发布）。
 *          如果我们使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。如果不能发布事件，那么就会引起Disruptor状态的混乱。
 *          尤其是在多个事件生产者的情况下会导致事件消费者失速，从而不得不重启才能恢复。
 * 
 * @author huanchu
 *
 */
public class LongEventProducer {

	private final RingBuffer<LongEvent> ringBuffer;

	public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	
	public void onData(ByteBuffer bb){
		
		//可以把ringBuffer看做一个事件队列，那个next就是得到下一个事件槽
		long sequence = ringBuffer.next();
		
		try {
			//用上面的索引取出一个空闲事件用于填充
			LongEvent event = ringBuffer.get(sequence);
			event.setValue(bb.getLong(0));
		} finally {
			//发布事件
			ringBuffer.publish(sequence);
		}
		
	}
	
}
