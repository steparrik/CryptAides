package steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeNameCoin;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NameRemoveCoin {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserService userService;
    private final AddingOptionService addingOptionService;
    private final CoinService coinService;


    @Autowired
    public NameRemoveCoin(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserService userService, AddingOptionService addingOptionService, CoinService coinService) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userService = userService;
        this.addingOptionService = addingOptionService;
        this.coinService = coinService;
    }

    public BotApiMethod<?> answerMessage(Message message, Bot bot, User user) {
        Long chatId = message.getChatId();
        String messageText = message.getText();

        if(message.getText().contains("полностью")) {
            coinService.deleteAllCoin(user);
            userService.save(user);

            user = userService.findByChatId(chatId + "").get();
            addingOptionService.deleteAdding(chatId, user);
            userService.save(user);

            return methodFactory.getSendMessage(chatId,"*Все токены были удалены из порфеля🛑\nХотите добавить новые?*",
                    keyboardFactory.getReplayKeyboardMarkup(
                            List.of("Добавить монету", "Портфель"),
                            List.of(1,1)), null);
        }


        Optional<Coin> coinForDelete = coinService.findByNameAndUSer(messageText, user);
        if(coinForDelete.isEmpty()){
            return methodFactory.getSendMessage(chatId, "*🛑Токенов с данным названием нет в вашем портфеле\n" +
                    "Проверьте введенные данные*", null, null);
        }

        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding(messageText).user(user).build()));
        user.getAddingOptions().add(addingOptionService.save(AddingOptions.builder().adding(coinForDelete.get().getCount()+"").user(user).build()));


        return methodFactory.getSendMessage(chatId, "*Введите количество монет для удаления*\n" +
                "*Вы имеете " + coinForDelete.get().getCount() +" " + coinForDelete.get().getName()+"*" ,
                keyboardFactory.getReplayKeyboardMarkup(List.of("Удалить все "+coinForDelete.get().getCount() + " токен(а-ов)", "Портфель"),
                        List.of(1,1)), null);
    }
}
