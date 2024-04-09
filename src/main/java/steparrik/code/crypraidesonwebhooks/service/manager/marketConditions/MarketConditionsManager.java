package steparrik.code.crypraidesonwebhooks.service.manager.marketConditions;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketConditionsManager extends AbstractManager {
    private final Api api;
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;

    @Autowired
    public MarketConditionsManager(Api api, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.api = api;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        List<Double> prices = new ArrayList<>();
        List<String> names = new ArrayList<>();
        names.add("btc");
        names.add("eth");
        names.parallelStream().forEach(e ->
                prices.add(api.getPriceOnNameByUSDT(e))
        );
        double BTCPrice;
        double ETHPrice;

        if(prices.get(0)>prices.get(1)){
            BTCPrice = prices.get(0);
            ETHPrice = prices.get(1);
        }else {
             BTCPrice = prices.get(1);
             ETHPrice = prices.get(0);
        }


        String response = "*Ситуация на рынке на данный момент:*\n\n"
                +"*#BTC = *" + BTCPrice  + " $ | [График ](https://ru.tradingview.com/chart/?symbol=btcusdt)\n"
                +"*#ETH = *" + ETHPrice + " $ | [График ](https://ru.tradingview.com/chart/?symbol=ethusdt)";

        return methodFactory.getSendMessage(message.getChatId(), response,
                keyboardFactory.getReplayKeyboardMarkup(
                        List.of("Добавить монету", "Портфель"),
                        List.of(1,1)
                ), null);
    }

    @Override
    public BotApiMethod<?> answerCallBackQuery(CallbackQuery callbackQuery, Bot bot) {
        List<Double> prices = new ArrayList<>();
        List<String> names = new ArrayList<>();
        names.add("btc");
        names.add("eth");
        names.parallelStream().forEach(e ->
                prices.add(api.getPriceOnNameByUSDT(e))
        );
        double BTCPrice;
        double ETHPrice;

        if(prices.get(0)>prices.get(1)){
            BTCPrice = prices.get(0);
            ETHPrice = prices.get(1);
        }else {
            BTCPrice = prices.get(1);
            ETHPrice = prices.get(0);
        }


        String response = "*Ситуация на рынке на данный момент:*\n\n"
                +"*#BTC = *" + BTCPrice  + " $ | [График ](https://ru.tradingview.com/chart/?symbol=btcusdt)\n"
                +"*#ETH = *" + ETHPrice + " $ | [График ](https://ru.tradingview.com/chart/?symbol=ethusdt)";


        return methodFactory.getSendMessage(callbackQuery.getMessage().getChatId(), response,
                keyboardFactory.getReplayKeyboardMarkup(
                        List.of("Добавить монету", "Портфель"),
                        List.of(1,1)
                ), null);
    }
}
