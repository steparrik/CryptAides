package steparrik.code.crypraidesonwebhooks.service.manager.removeCoin;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.UserRepository;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addCountCoin.CountCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeCountCoin.CountRemoveCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeNameCoin.NameRemoveCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.removeStartCoin.StartRemoveCoin;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;

import static steparrik.code.crypraidesonwebhooks.service.data.CallbackData.REMOVE;

@Component
public class RemoveCoinManager extends AbstractManager {
    private final UserRepository userRepository;
    private final StartRemoveCoin startRemoveCoin;
    private final NameRemoveCoin nameRemoveCoin;
    private final CountRemoveCoin countRemoveCoin;

    public RemoveCoinManager(UserRepository userRepository, StartRemoveCoin startRemoveCoin, NameRemoveCoin nameRemoveCoin, CountRemoveCoin countRemoveCoin) {
        this.userRepository = userRepository;
        this.startRemoveCoin = startRemoveCoin;
        this.nameRemoveCoin = nameRemoveCoin;
        this.countRemoveCoin = countRemoveCoin;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        User user = userRepository.findByChatId(chatId+"").orElseThrow();
        String text = message.getText();
        List<AddingOptions> addingOptions = user.getAddingOptions();

        if(addingOptions.isEmpty() && message.getText().contains("Изменить")){
            return startRemoveCoin.answerMessage(message, bot, user);
        }
        if (addingOptions.size()==1 && addingOptions.get(0).getAdding().equals("изменить")){
            return nameRemoveCoin.answerMessage(message, bot, user);
        }
        if(addingOptions.size()==3 && addingOptions.get(0).getAdding().equals("изменить")){
            return countRemoveCoin.answerMessage(message, bot, user);
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallBackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        User user = userRepository.findByChatId(chatId+"").orElseThrow();
        List<AddingOptions> addingOptions = user.getAddingOptions();

        if(addingOptions.isEmpty() && callbackQuery.getData().equals(REMOVE)){
            return startRemoveCoin.answerMessage(callbackQuery, bot, user);
        }
        return null;
    }
}
