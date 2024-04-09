package steparrik.code.crypraidesonwebhooks.service.handler;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.addCoin.AddCoinManager;
import steparrik.code.crypraidesonwebhooks.service.manager.marketConditions.MarketConditionsManager;
import steparrik.code.crypraidesonwebhooks.service.manager.portfolio.PortfolioManager;
import steparrik.code.crypraidesonwebhooks.service.manager.removeCoin.RemoveCoinManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;

import static steparrik.code.crypraidesonwebhooks.service.data.CallbackData.*;

@Service
public class CallBackQueryHandler {
    private final AnswerMethodFactory methodFactory;
    private final PortfolioManager portfolioManager;
    private final AddCoinManager addCoinManager;
    private final MarketConditionsManager marketConditionsManager;
    private final RemoveCoinManager removeCoinManager;
    private final UserService userService;

    public CallBackQueryHandler(AnswerMethodFactory methodFactory, PortfolioManager portfolioManager, AddCoinManager addCoinManager, MarketConditionsManager marketConditionsManager, RemoveCoinManager removeCoinManager, UserService userService) {
        this.methodFactory = methodFactory;
        this.portfolioManager = portfolioManager;
        this.addCoinManager = addCoinManager;
        this.marketConditionsManager = marketConditionsManager;
        this.removeCoinManager = removeCoinManager;
        this.userService = userService;
    }

    public BotApiMethod<?> answer(CallbackQuery callBackQuery, Bot bot){
        User user = userService.findByChatId(callBackQuery.getMessage().getChatId()+"").orElse(null);

        List<AddingOptions> addingOptions = user.getAddingOptions();
        String data = callBackQuery.getData();

        if((data.equals(UPDATE_PORTFOLIO) && addingOptions.isEmpty())||data.equals(PORTFOLIO) || data.equals("Портфель")){
            return portfolioManager.answerCallBackQuery(callBackQuery, bot);
        }
        if(data.equals(CONDITIONS) && addingOptions.isEmpty()){
            return marketConditionsManager.answerCallBackQuery(callBackQuery, bot);
        }
        if(data.equals(REMOVE)){
            return removeCoinManager.answerCallBackQuery(callBackQuery, bot);
        }
        if(data.equals(ADD)){
            return addCoinManager.answerCallBackQuery(callBackQuery, bot);
        }

        return null;
    }
}
