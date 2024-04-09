package steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addNameCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.AddingOptionService;
import steparrik.code.crypraidesonwebhooks.service.entityService.CoinService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;
import steparrik.code.crypraidesonwebhooks.util.BadRequestException;

import java.util.ArrayList;
import java.util.List;

import static steparrik.code.crypraidesonwebhooks.service.data.CallbackData.PORTFOLIO;

@Component
public class NameCoin  {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final AddingOptionService addingOptionService;
    private final Api api;

    @Autowired
    public NameCoin(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, AddingOptionService addingOptionService, Api api) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.addingOptionService = addingOptionService;
        this.api = api;
    }


    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user){
        String nameCoin = message.getText().toUpperCase();
        double price = 0.0;
        try {
            price = api.getPriceOnNameByUSDT(nameCoin);
        } catch (BadRequestException e) {
            return methodFactory.getSendMessage(message.getChatId(),
                    "🛑*Криптовалюты <" + nameCoin + "> не существует\n" +
                            "Возможно вы ошиблись, введите еще раз*",
                    null, null);

        }

        user.getAddingOptions().add(addingOptionService.save(
                AddingOptions.builder()
                        .adding(message.getText().toUpperCase())
                        .user(user)
                        .build()));

        return methodFactory.getSendMessage(message.getChatId(),
                "*Введите цену покупки за 1 токен:*\n\n" +
                        "Текущая цена [" + nameCoin + "](https://ru.tradingview.com/chart/?symbol=" + nameCoin + "usdt) = "
                        + Math.round(price* 1000.0) / 1000.0 + "$",

                keyboardFactory.getReplayKeyboardMarkup(List.of("Маркет","Портфель"),
                        List.of(1,1)
                                ), null);


    }
}
