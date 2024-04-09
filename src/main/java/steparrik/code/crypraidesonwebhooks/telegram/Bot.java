package steparrik.code.crypraidesonwebhooks.telegram;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.UpdateDispatcher;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.manager.dailyNotificationManager.DailyNotification;

import java.util.List;

@Component
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bot extends TelegramWebhookBot {
    private  final UserService userService;
    private final DailyNotification dailyNotification;
    final TelegramProperties telegramProperties;
    final UpdateDispatcher updateDispatcher;

    @Autowired
    public Bot(UserService userService, DailyNotification dailyNotification, TelegramProperties telegramProperties, UpdateDispatcher updateDispatcher) {
        super(telegramProperties.getToken());
        this.userService = userService;
        this.dailyNotification = dailyNotification;
        this.telegramProperties = telegramProperties;
        this.updateDispatcher = updateDispatcher;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return updateDispatcher.distribute(update, this);
    }

    @Override
    public String getBotPath() {
        return telegramProperties.getPath();
    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getUsername();
    }


    @Scheduled(fixedRate = 18000000)
    private void everyTwelveHourSend(){
        List<User> users = userService.findAll();
        for(User user:users){
            try {
                this.execute(dailyNotification.answerMessage(user.getChatId(), this));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
