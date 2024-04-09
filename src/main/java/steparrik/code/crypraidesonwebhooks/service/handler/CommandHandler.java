package steparrik.code.crypraidesonwebhooks.service.handler;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import steparrik.code.crypraidesonwebhooks.service.manager.start.StartManager;
import steparrik.code.crypraidesonwebhooks.telegram.Bot;

import static steparrik.code.crypraidesonwebhooks.service.data.Command.*;


@Service
public class CommandHandler {
    private final StartManager startManager;

    @Autowired
    public CommandHandler(StartManager startManager) {
        this.startManager = startManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot){
        String command = message.getText();
        switch (command){
            case START -> {
               return startManager.answerCommand(message, bot);
            }
//            case FEEDBACK_COMMAND -> {
//                return feedbackManager.answerCommand(message, bot);
//            }
//            case HELP_COMMAND -> {
//                return helpManager.answerCommand(message, bot);
//            }
//            case TIMETABLE -> {
//                return timeTableManager.answerCommand(message, bot);
//            }
//            case TASK ->{
//                return taskManager.answerCommand(message, bot);
//            }
//            case PROGRESS ->{
//                return progressControlManager.answerCommand(message, bot);
//            }
//            case PROFILE -> {
//                return profileManager.answerCommand(message, bot);
//            }
//            case SEARCH -> {
//                return searchManager.answerCommand(message, bot);
//            }
            default -> {
                return null;
            }
        }
    }

}