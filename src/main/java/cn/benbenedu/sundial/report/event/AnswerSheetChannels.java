package cn.benbenedu.sundial.report.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface AnswerSheetChannels {

    @Input("inboundAnswerSheetFinished")
    SubscribableChannel answerSheetFinished();
}
