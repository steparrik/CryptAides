package steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addPriceCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.AddingOptionService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.ArrayList;
import java.util.List;

@Component
public class PriceCoin {
    private final Api api;
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final AddingOptionService addingOptionService;


    @Autowired
    public PriceCoin(Api api, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, AddingOptionService addingOptionService) {
        this.api = api;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.addingOptionService = addingOptionService;
    }


    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        String messageText = message.getText();
        Long chatId = message.getChatId();
        double buyPrice = 0;
        if (!messageText.toLowerCase().contains("маркет")) {
            try {
                buyPrice = Double.parseDouble(messageText);
                if (buyPrice <= 0) {
                    return methodFactory.getSendMessage(chatId, "*Введите число больше нуля*", null, null);
                }
                user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding(buyPrice+"").user(user).build()));

            } catch (IllegalArgumentException e) {
                return methodFactory.getSendMessage(chatId, "*Введите число*", null, null);

            }
        }else {
            buyPrice = api.getPriceOnNameByUSDT(user.getAddingOptions().get(1).getAdding());
            user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding(buyPrice+"").user(user).build()));
        }


        return methodFactory.getSendMessage(chatId, "*Введите количество монет*",
                keyboardFactory.getReplayKeyboardMarkup(
                List.of("1", "10", "100", "Портфель"),
                List.of(3, 1)), null);
    }
}
