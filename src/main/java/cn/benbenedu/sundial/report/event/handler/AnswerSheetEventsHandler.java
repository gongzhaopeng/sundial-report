package cn.benbenedu.sundial.report.event.handler;

import cn.benbenedu.sundial.report.event.AnswerSheetChannels;
import cn.benbenedu.sundial.report.event.model.AnswerSheetFinishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(AnswerSheetChannels.class)
@Slf4j
public class AnswerSheetEventsHandler {

    @StreamListener("inboundAnswerSheetFinished")
    public void answerSheetFinished(
            AnswerSheetFinishedEvent answerSheetFinishedEvent) {

        log.info(
                "Receive a AnswerSheet-Finished event: {}",
                answerSheetFinishedEvent);

        // TODO to process the finished answer-sheet here.
    }
}
