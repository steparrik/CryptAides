package steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeStartCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.Coin;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.AddingOptionService;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.portfolio.PortfolioManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StartRemoveCoin {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final AddingOptionService addingOptionService;


    @Autowired
    public StartRemoveCoin(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory,  AddingOptionService addingOptionService) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.addingOptionService = addingOptionService;

    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        if(user.getPortfolio().isEmpty()){
            try {
                bot.execute(methodFactory.getSendMessage(message.getChatId(), "*Ваш портфель пуст\nНет токенов для удаления*", null, null));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding("изменить").user(user).build()));

        List<String> userCoinNames = user.getPortfolio().stream().map(Coin::getName).collect(Collectors.toList());


        List<Integer> order = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < userCoinNames.size(); i++) {
            if (count == 4) {
                order.add(count);
                count = 0;
            }
            count++;
        }
        order.add(count);
        order.add(2);
        System.out.println(order);
        userCoinNames.add("Портфель");
        userCoinNames.add("Очистить полностью");

        return methodFactory.getSendMessage(message.getChatId(), "*Введите тикер монеты которую хотите удалить*"
                , keyboardFactory.getReplayKeyboardMarkup(
                        userCoinNames,
                        order
                ), null);
    }

    public BotApiMethod<?> answerMessage(CallbackQuery callbackQuery, Bot bot, User user) {
        if(user.getPortfolio().isEmpty()){
            return methodFactory.getAnswerCallbackQuery(callbackQuery.getId(), "Ваш портфель пуст");
        }


        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding("изменить").user(user).build()));

        List<String> userCoinNames = user.getPortfolio().stream().map(Coin::getName).collect(Collectors.toList());


        List<Integer> order = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < userCoinNames.size(); i++) {
            if (count == 4) {
                order.add(count);
                count = 0;
            }
            count++;
        }
        order.add(count);
        order.add(2);
        System.out.println(order);
        userCoinNames.add("Портфель");
        userCoinNames.add("Очистить полностью");


        return methodFactory.getEditMessageText(callbackQuery, "*Введите тикер монеты которую хотите удалить*"
                , keyboardFactory.getInlineKeyboard(
                        userCoinNames,
                        order, userCoinNames
                ));
    }
}

