package steparrik.code.crypraidesonwebhooks.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.handler.CallBackQueryHandler;
import steparrik.code.crypraidesonwebhooks.service.handler.CommandHandler;
import steparrik.code.crypraidesonwebhooks.service.handler.MessageHandler;
import steparrik.code.crypraidesonwebhooks.service.manager.dailyNotificationManager.DailyNotification;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;


@Component
@Slf4j
public class UpdateDispatcher {
    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;
    private final CallBackQueryHandler callBackQueryHandler;




    @Autowired
    public UpdateDispatcher(MessageHandler messageHandler, CommandHandler commandHandler, CallBackQueryHandler callBackQueryHandler) {
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.callBackQueryHandler = callBackQueryHandler;
    }

    public BotApiMethod<?> distribute(Update update, Bot bot) {
        if (update.hasCallbackQuery()) {
            return callBackQueryHandler.answer(update.getCallbackQuery(), bot);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                if (message.getText().charAt(0) == '/') {
                    return commandHandler.answer(message, bot);
                }
                return messageHandler.answer(message, bot);
            }
        }
        log.error("Unsupported update: " + update);
        return null;
    }



}
