package steparrik.code.crypraidesonwebhooks.service.manager.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import steparrik.code.crypraidesonwebhooks.api.Api;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.UserRepository;
import steparrik.code.crypraidesonwebhooks.service.entityService.UserService;
import steparrik.code.crypraidesonwebhooks.service.factory.AnswerMethodFactory;
import steparrik.code.crypraidesonwebhooks.service.factory.KeyboardFactory;
import steparrik.code.crypraidesonwebhooks.service.manager.AbstractManager;
import steparrik.code.crypraidesonwebhooks.service.manager.portfolio.PortfolioManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import java.util.List;
import java.util.Optional;

@Component
public class StartManager extends AbstractManager {
    private final AnswerMethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserService userService;
    private final PortfolioManager portfolioManager;


    @Autowired
    public StartManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserService userService, PortfolioManager portfolioManager) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userService = userService;
        this.portfolioManager = portfolioManager;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Optional<User> user = userService.findByChatId(message.getChatId()+"");
        String name;
        if(!message.getChat().getFirstName().isEmpty()){
            name = message.getChat().getFirstName();
        }else{
            name = message.getChat().getUserName();
        }

        if(user.isEmpty()){
            userService.save(User.builder().chatId(message.getChatId()+"").name(name).build());
            return methodFactory.getSendMessage(message.getChatId(),
                    "Здравтсвуйте, " + name +  ", рад встрече!\n" +
                            "Ваш торговый портфель пуст, хотите добавить в него токены для отслеживания?",
                    keyboardFactory.getReplayKeyboardMarkup(
                            List.of("Добавить монету"),
                            List.of(1)
                    ), null);

        }else{
            try {
                bot.execute(methodFactory.getSendMessage(message.getChatId(),
                       "Здравтсвуйте, " + name +  ", рад встрече!\n",
                       null, null));
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            return portfolioManager.answerMessage(message, bot);
        }

    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallBackQuery(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }
}
