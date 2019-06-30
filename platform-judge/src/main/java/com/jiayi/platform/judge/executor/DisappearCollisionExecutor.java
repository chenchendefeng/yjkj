package com.jiayi.platform.judge.executor;

import com.jiayi.platform.judge.request.AppearCollisionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DisappearCollisionExecutor extends AppearCollisionExecutor {

    @Override
    protected Pair<Long, Long> getRefBeginAndEndDate(AppearCollisionRequest request) {
        long refBeginDate = request.getAnalyzeEndDate() + (long) request.getBufferTime() * 3600L * 1000L;
        long refEndDate = request.getAnalyzeEndDate() + ((long) request.getRefDuration() * 24L + (long) request.getBufferTime()) * 3600L * 1000L;
        return Pair.of(refBeginDate, refEndDate);
    }
}
