package steparrik.code.crypraidesonwebhooks.service.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import javax.swing.text.html.HTML;

@Component
public class AnswerMethodFactory {

    public SendMessage getSendMessage(Long chatId,
                                      String text,
                                      ReplyKeyboard replyKeyboard,
                                      InlineKeyboardMarkup inlineKeyboardMarkup){
//        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
//        remove.setRemoveKeyboard(true);
//        remove.setSelective(true);


         SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .disableWebPagePreview(true)
                 .parseMode("Markdown")
                 .replyMarkup(replyKeyboard)
                 .build();




         return sendMessage;
    }


    public EditMessageText getEditMessageText(CallbackQuery callBackQuery,
                                              String text,
                                              InlineKeyboardMarkup keyboard){
        return EditMessageText.builder()
                .chatId(callBackQuery.getMessage().getChatId())
                .text(text)
                .messageId(callBackQuery.getMessage().getMessageId())
                .replyMarkup(keyboard)
                .parseMode("Markdown")
                .disableWebPagePreview(true)
                .build();
    }

    public DeleteMessage getDeleteMessage(Long chatId,
                                          Integer messageId){
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    public AnswerCallbackQuery getAnswerCallbackQuery(String callbackQueryId,
                                                      String text){
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .build();
    }




}
