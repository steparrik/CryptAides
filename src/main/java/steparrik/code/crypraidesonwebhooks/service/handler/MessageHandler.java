package steparrik.code.crypraidesonwebhooks.service.handler;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.AddCoinManager;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addCountCoin.CountCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addNameCoin.NameCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.addPriceCoin.PriceCoin;
import steparrik.code.crypraidesonwebhooks.service.manager.marketConditions.MarketConditionsManager;
import steparrik.code.crypraidesonwebhooks.service.manager.portfolio.PortfolioManager;
import steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.RemoveCoinManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageHandler {
    private final MarketConditionsManager marketConditionsManager;
    private final PortfolioManager portfolioManager;
    private final UserService userService;
    private final AddCoinManager addCoinManager;
    private final RemoveCoinManager removeCoinManager;

    public MessageHandler(MarketConditionsManager marketConditionsManager, PortfolioManager portfolioManager, UserService userService, NameCoin nameCoin, CountCoin countCoin, PriceCoin priceCoin, AddCoinManager addCoinManager, RemoveCoinManager removeCoinManager) {
        this.marketConditionsManager = marketConditionsManager;
        this.portfolioManager = portfolioManager;
        this.userService = userService;
        this.addCoinManager = addCoinManager;
        this.removeCoinManager = removeCoinManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot){
        User user = userService.findByChatId(message.getChatId()+"").orElse(null);
        if(user==null){
            return defaultAnswer(message);
        }
        List<AddingOptions> addingOptions = user.getAddingOptions();

        String text = message.getText();
        if (text.equalsIgnoreCase("состояние рынка")) {
            return marketConditionsManager.answerMessage(message, bot);
        }
        if(text.equalsIgnoreCase("портфель")||text.contains("Обновить")){
            return portfolioManager.answerMessage(message, bot);
        }if((addingOptions.isEmpty() && text.contains("Изменить")) ||
                (!addingOptions.isEmpty() && addingOptions.get(0).getAdding().equals("изменить"))){
            return removeCoinManager.answerMessage(message, bot);
        }
        return addCoinManager.answerMessage(message, bot);

    }


    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Для авторизации введите /start")
                .build();
    }


}
