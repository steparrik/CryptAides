package steparrik.code.crypraidesonwebhooks.service.manager.addCoin;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.UserRepository;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addCountCoin.CountCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addNameCoin.NameCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addPriceCoin.PriceCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.startAddCoin.StartAddCoin;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;

import static steparrik.code.crypraidesonwebhooks.service.data.CallbackData.ADD;

@Component
public class AddCoinManager extends AbstractManager {
    private final UserRepository userRepository;
    private final CountCoin countCoin;
    private final PriceCoin priceCoin;
    private final NameCoin nameCoin;
    private final StartAddCoin startAddCoin;

    public AddCoinManager(UserRepository userRepository, CountCoin countCoin, PriceCoin priceCoin, NameCoin nameCoin, StartAddCoin startAddCoin) {
        this.userRepository = userRepository;
        this.countCoin = countCoin;
        this.priceCoin = priceCoin;
        this.nameCoin = nameCoin;
        this.startAddCoin = startAddCoin;
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

        if(addingOptions.isEmpty() && message.getText().contains("Добавить")){
            return startAddCoin.answerMessage(message, bot, user);
        }
        if(addingOptions.size()==1 && addingOptions.get(0).getAdding().equals("да")){
            return nameCoin.answerMessage(message, bot, user);
        }
        if(addingOptions.size()==2 && addingOptions.get(0).getAdding().equals("да")){
            return priceCoin.answerMessage(message, bot, user);
        }
        if(addingOptions.size()==3 && addingOptions.get(0).getAdding().equals("да")){
            return countCoin.answerMessage(message, bot, user);
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallBackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        User user = userRepository.findByChatId(chatId+"").orElseThrow();

        List<AddingOptions> addingOptions = user.getAddingOptions();

        if(addingOptions.isEmpty() && callbackQuery.getData().equals(ADD)){
            return startAddCoin.answerMessage(callbackQuery, bot, user);
        }
        return null;
    }
}
