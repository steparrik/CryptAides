package steparrik.code.crypraidesonwebhooks.service.manager.dailyNotificationManager;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.entity.Coin;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.service.entityService.AddingOptionService;
import steparrik.code.crypraidesonwebhooks.service.entityService.CoinService;
import steparrik.code.crypraidesonwebhooks.service.entityService.PortfolioService;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;
import java.util.Set;

@Component
public class DailyNotification {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final PortfolioService portfolioService;
    private final UserService userService;
    public  final CoinService coinService;
    private final AddingOptionService addingOptionService;


    public DailyNotification(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, PortfolioService portfolioService, UserService userService, CoinService coinService, AddingOptionService addingOptionService) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.portfolioService = portfolioService;
        this.userService = userService;
        this.coinService = coinService;
        this.addingOptionService = addingOptionService;

    }

    public BotApiMethod<?> answerMessage(String chatId, Bot bot) {
        User user = userService.findByChatId(chatId).get();
        addingOptionService.deleteAdding(Long.parseLong(chatId), user);
        Set<Coin> updateCoins = coinService.updatePriceByCoin(user.getPortfolio());


        StringBuilder response = new StringBuilder();


        double allPrice = 0.0;
        double allBuyPrice = 0.0;

        for (Coin coin : updateCoins) {
            allPrice += coin.getCurrentPrice();
            allBuyPrice += coin.getBuyPrice();
        }
        response.append("*🟣Оповещение о текущем состоянии портфеля*\n\n");



        response.append("*Портфель:* " + Math.round(allPrice * 1000.0) / 1000.0 + " *$*\n");
        String sticker;
        if (allPrice - allBuyPrice > 0) {
            sticker = "📈";
        } else if (allPrice - allBuyPrice == 0) {
            sticker = "";
        } else {
            sticker = "📉";
        }

        response.append("*Прибыль за все время:* " + Math.round((allPrice - allBuyPrice) * 1000.0) / 1000.0 + " *$* " + sticker + "\n\n");
        for (Coin coin : updateCoins) {
            double oneTokenCurrentPrice = Math.round(coin.getCurrentPrice() / coin.getCount() * 1000) / 1000.0;
            double differentPrices = Math.round((coin.getCurrentPrice() - coin.getBuyPrice()) * 1000) / 1000.0;

            String util = portfolioService.getUtil(differentPrices);
            String stickerF = portfolioService.getStickerForCoin(differentPrices);

            double percent = portfolioService.getPercent(coin.getCurrentPrice(), coin.getBuyPrice());

            percent = Math.round(percent * 1000) / 1000.0;
            double avgPrice = Math.round(coin.getBuyPrice() / coin.getCount() * 1000.0) / 1000.0;
            response.append(stickerF + " [" + coin.getName() + "](https://ru.tradingview.com/chart/?symbol=" + coin.getName().toLowerCase() + "usdt) :* " + oneTokenCurrentPrice + " $*\n" +
                    "           *Ср. цена покупки: " + avgPrice + " $\n            " +
                    "Количество токенов: " + coin.getCount() +
                    "\n            Расч. стоимость: " + coin.getBuyPrice() + " $\n            " +
                    "Расч. значение: " + coin.getCurrentPrice() + " $\n            " +
                    "PNL: " + util + percent + " % | " + util + differentPrices + " $*\n\n\n");

        }


        return methodFactory.getSendMessage(Long.parseLong(chatId), response.toString(),
                keyboardFactory.getReplayKeyboardMarkup(List.of("Добавить монету", "Состояние рынка",
                                "Обновить данные", "Изменить портфель"),
                        List.of(2, 2))
                , null
        );
    }
}
