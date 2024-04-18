package jpabook.jpashop.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LogbackConfig {


    @PostConstruct
    public void init() {
        // Configure Logback programmatically
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAndStopAllAppenders();

        // Create encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("[%d{yyyy.MM.dd HH:mm:ss.SSS}] - [%-5level] - [%X{request_id}] - [%logger{5}] - %msg%n");
        encoder.setContext(loggerContext);
        encoder.start();

        // Create appender
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(encoder);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();

        // Add appender to the root logger
        rootLogger.addAppender(consoleAppender);
    }


}
