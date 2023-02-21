package ru.gil.bottest.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.gil.bottest.configuration.BotConfiguration;
import ru.gil.bottest.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfiguration botConfiguration;

    private final MessageUtils messageUtils;

    public TelegramBot(BotConfiguration botConfiguration, MessageUtils messageUtils) {
        super(botConfiguration.getToken());
        this.botConfiguration = botConfiguration;
        this.messageUtils = messageUtils;
    }
    @Override
    public String getBotUsername() {
        return botConfiguration.getName();
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error register bot", e);
        }
        createMenu();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        System.out.println(text);
        SendMessage message = messageUtils.generateSendMessageWithText(update, "Привет " +
                update.getMessage().getChat().getFirstName());
        sendAnswerMessage(message);

    }



    public ReplyKeyboardMarkup setButtons() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setSelective(false);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<KeyboardRow>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Текст 1");
        keyboardFirstRow.add("Текст 2");

        KeyboardRow keyboard2Row = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboard2Row.add("Текст 3");
        keyboard2Row.add("Текст 4");
        keyboard2Row.add("Текст 5");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboard2Row);
        // добавляем список клавиатуре
        markup.setKeyboard(keyboard);
        return markup;
    }

    private void createMenu() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "begin work"));
        commandList.add(new BotCommand("/menu", "begin menu"));
        try {
            execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error command ", e);
        }
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error send message", e);
            }
        }
    }
}
