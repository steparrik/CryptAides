package steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addCountCoin;

import org.aspectj.weaver.ast.Call;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.Coin;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.AddingOptionService;
import steparrik.code.crypraidesonwebhooks.service.entityService.CoinService;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CountCoin {
    private final Api api;
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserService userService;
    private final AddingOptionService addingOptionService;
    private final CoinService coinService;

    @Autowired
    public CountCoin(Api api, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserService userService, AddingOptionService addingOptionService, CoinService coinService) {
        this.api = api;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userService = userService;
        this.addingOptionService = addingOptionService;
        this.coinService = coinService;
    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        String messageText = message.getText();
        Long chatId = message.getChatId();
        double count = 0;
            try {
                count = Double.parseDouble(messageText);
                if (count <= 0) {
                    return methodFactory.getSendMessage(chatId, "*Введите число больше нуля*", null, null);
                }
            } catch (IllegalArgumentException e) {
                return methodFactory.getSendMessage(chatId, "*Введите число*", null, null);
            }

        Coin coinBeforeSave = Coin.builder()
                .count(count).buyPrice(Math.round(Double.parseDouble(user.getAddingOptions().get(2).getAdding()) * count * 1000.0) / 1000.0)
                .currentPrice(Math.round(api.getPriceOnNameByUSDT(user.getAddingOptions().get(1).getAdding()) * count * 1000.0) / 1000.0)
                .name(user.getAddingOptions().get(1).getAdding().toUpperCase())
                .user(user)
                .build();
        Coin coinAfterSave = coinService.save(coinBeforeSave,  user);


        coinAfterSave.setUser(user);
        user.removeCoin(coinBeforeSave);
        user.getPortfolio().add(coinAfterSave);
        userService.save(user);

        String coinName = coinBeforeSave.getName();


        List<String> list = user.getAddingOptions().stream().map(AddingOptions::getAdding).toList();
        addingOptionService.deleteAdding(chatId, user);

        return methodFactory.getSendMessage(chatId,"*"+ coinBeforeSave.getCount() + "  #" + coinName + " добавлено в портфель!*\n\n" +
                "*Курс покупки:* " + coinName + " = " + Math.round(Double.parseDouble(list.get(2)) * 1000.0) / 1000.0 + " $\n" +
                "*Сумма покупки:* " + coinBeforeSave.getBuyPrice() + " $" + "\n\n" ,keyboardFactory.getReplayKeyboardMarkup(
                        List.of("Добавить еще", "Портфель"),
                List.of(2)
        ), null);

    }
}
