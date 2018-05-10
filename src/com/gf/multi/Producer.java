package com.gf.multi;

import com.lmax.disruptor.RingBuffer;

public class Producer {

 	private final RingBuffer<Order> ringBuffer;

	public Producer(RingBuffer<Order> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
 	
	/**
	 * onData用来发布事件，没调用一次就发布一次事件
	 * 它的参数用事件传递给消费者
	 */
	public void onData(String data){
		//可以把ringBuffer 看做一个队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充(获取该序号对应的时间对象)
			Order order = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			order.setId(data);
		} finally {
			//发布事件
			ringBuffer.publish(sequence);
		}
	}
 	
	
}
