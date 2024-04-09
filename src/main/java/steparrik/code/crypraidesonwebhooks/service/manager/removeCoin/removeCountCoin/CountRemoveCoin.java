package steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeCountCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import java.util.Optional;

@Component
public class CountRemoveCoin {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserService userService;
    private final AddingOptionService addingOptionService;
    private final CoinService coinService;


    @Autowired
    public CountRemoveCoin(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserService userService, AddingOptionService addingOptionService, CoinService coinService) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userService = userService;
        this.addingOptionService = addingOptionService;
        this.coinService = coinService;
    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        Long chatId = message.getChatId();
        String messageText = message.getText();
        String nameCoin = user.getAddingOptions().get(1).getAdding();

        double count = 0.0;
        if (!messageText.toLowerCase().contains("удалить все ")) {
            try {
                count  = Double.parseDouble(messageText);
                if (count <= 0) {
                    return methodFactory.getSendMessage(chatId, "Нужно ввести число больше нуля!", null, null);
                }if(count >= Double.parseDouble(user.getAddingOptions().get(2).getAdding())){
                    return methodFactory.getSendMessage(chatId, "Вы не можете удалить больше " + Double.parseDouble(user.getAddingOptions().get(2).getAdding()) +" токенов", null, null);

                }
                addingOptionService.deleteAdding(chatId, user);
                coinService.deletePartCoin(nameCoin, user, count);

                return methodFactory.getSendMessage(chatId, "*" + count +" "+nameCoin+" были удалены из порфеля🛑\nХотите добавить новый токен?*",
                        keyboardFactory.getReplayKeyboardMarkup(List.of("Добавить монету", "Портфель"),
                                List.of(1,1)), null);
            } catch (IllegalArgumentException e) {
                return methodFactory.getSendMessage(chatId, "*Нужно ввести число*", null,null);

            }
        } else {
            addingOptionService.deleteAdding(chatId, user);
            coinService.deleteCoin(nameCoin, user);
            return methodFactory.getSendMessage(chatId, "*Все токены "+nameCoin+" были удалены из порфеля🛑\nХотите добавить новый токен?*",
                    keyboardFactory.getReplayKeyboardMarkup(List.of("Добавить монету", "Портфель"),
                            List.of(1,1)), null);

        }
    }
}
