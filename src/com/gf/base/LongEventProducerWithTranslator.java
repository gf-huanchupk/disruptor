package com.gf.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * Disruptor3.0提供了lambda式的API。这样可以把一些复杂的操作放在Ring Buffer。
 * 所以在Disruptor3.0之后的版本做好使用EventPublisher或者Event Tanslator
 * 
 * @author huanchu
 *
 */
public class LongEventProducerWithTranslator {

	//一个translator可以看做一个事件初始化器，publicEvent方法会调用它
	//填充Event
	private static final EventTranslatorOneArg<LongEvent,ByteBuffer> TRANSLATOR = 
			
			new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
				
				@Override
				public void translateTo(LongEvent event, long sequence, ByteBuffer bb) {
					event.setValue(bb.getLong(0));
				}
			};
	
    private final RingBuffer<LongEvent> ringBuffer;

	public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	
	public void onData(ByteBuffer bb){
		ringBuffer.publishEvent(TRANSLATOR,bb);
	}
    
    
			
}
