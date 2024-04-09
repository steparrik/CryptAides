package steparrik.code.crypraidesonwebhooks.service.manager.addCoin.startAddCoin;

import org.aspectj.weaver.ast.Call;
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
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static steparrik.code.crypraidesonwebhooks.service.data.CallbackData.PORTFOLIO;

@Component
public class StartAddCoin {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final AddingOptionService addingOptionService;

    public StartAddCoin( AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, AddingOptionService addingOptionService) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.addingOptionService = addingOptionService;
    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding("да").user(user).build()));
        List<String> userCoinNames = user.getPortfolio().stream().map(Coin::getName).collect(Collectors.toList());
        userCoinNames.add("Портфель");

        List<Integer> order = new ArrayList<>();
        int count = 0;
        for(int i =0;i<userCoinNames.size();i++){
            if(count==4){
                order.add(count);
                count=0;
            }
            count++;
        }
        order.add(count-1);
        order.add(1);

        return methodFactory.getSendMessage(message.getChatId(), "*Введите тикер монеты которую хотите добавить*"
        , keyboardFactory.getReplayKeyboardMarkup(
                userCoinNames,
                        order
                ), null);
    }

    public BotApiMethod<?> answerMessage(CallbackQuery callbackQuery, Bot bot, User user) {
//
//            try {
//                bot.execute(methodFactory.getDeleteMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId()));
//            } catch (TelegramApiException e) {
//                System.out.println("SUKA");
//                return null;
//            }

        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding("да").user(user).build()));
        List<String> userCoinNames = user.getPortfolio().stream().map(Coin::getName).collect(Collectors.toList());
        userCoinNames.add("Портфель");


        List<Integer> order = new ArrayList<>();
        int count = 0;
        for(int i =0;i<userCoinNames.size();i++){
            if(count==4){
                order.add(count);
                count=0;
            }
            count++;
        }
        order.add(count-1);
        order.add(1);


        return methodFactory.getEditMessageText(callbackQuery, "*Введите тикер монеты которую хотите добавить*"
                , keyboardFactory.getInlineKeyboard(
                        userCoinNames,
                        order,
                        userCoinNames
                ));
    }
}
