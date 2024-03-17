import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TgBot extends TelegramLongPollingBot {

    HttpRequsts req = new HttpRequsts();
    boolean is_selected = false;
    private static boolean isAdmin = false;
    String coctail_now = "";
    private static final String adminName = "Tashbash59";

    @Override
    public void onUpdateReceived(Update update) {
        String username =  update.getMessage().getFrom().getUserName();
        req.doPostRequest("user/postUser","{\"user_name\":\"" + username + "\",\"is_admin\":false}");
//        setUser(username);
        isAdmin = username.equals(adminName);
        Message message = update.getMessage();
        //Tashbash59
        // получаем записи у пользователя и вставляем их в массивы

        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    BackToMenu(message, "Выбери нужную команду",isAdmin);
                    break;
                case "Выбрать коктейль":
                    BackToMenu(message, "Выбрать коктейль",isAdmin);
                    break;
                case "Посмотреть историю":
                    BackToMenu(message, "Посмотреть историю",isAdmin);
                    break;
                case "Посмотреть рецепт":
                    BackToMenu(message, "Посмотреть рецепт",isAdmin);
                    break;
                default:
                    if (is_selected) {
                        BackToMenu(message, "",isAdmin);
                    } else {
                        BackToMenu(message, "Вы ввели неправильные данные, попробуйте еще раз",isAdmin);
                    }
                    break;
            }
        }
    }

    public void getAllCoctails(String jsonCoctails, Message message) {

        is_selected = true;
        String[] items = jsonCoctails.split(",");

        // Создаем список для хранения значений
        List<String> names = new ArrayList<>();
        for (String item : items) {
            // Удаляем лишние пробелы в начале и конце элемента
            String trimmedItem = item.trim();
            names.add(trimmedItem);
        }

        StringBuilder text = new StringBuilder("");

        for (int i = 0; i < names.size(); i++) {
            text.append(names.get(i) + "\n");
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(String.valueOf(text));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showHistory(String response, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(response);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void BackToMenu (Message message, String text,Boolean isAdmin) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new
                ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Выбрать коктейль");
//        keyboardFirstRow.add("Список моих записей");
//        keyboardFirstRow.add("Удалить запись на стирку");

        KeyboardRow keyboardSecondRow = new KeyboardRow();


        if (isAdmin) {
            keyboardSecondRow.add("Добавить коктейль");
//            keyboardSecondRow.add("Закрыть возможность записи");
        }


        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());

        if (text == "Выбрать коктейль") {
            getAllCoctails(req.doGetRequset("coctails/getCoctails"), message);
        }
        else if (is_selected && text == "Посмотреть историю") {
            is_selected = false;
            String encodedText = null;
            try {
                encodedText = URLEncoder.encode(coctail_now, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            showHistory(req.doGetRequset("coctails/getHistory/" + encodedText),message);
            System.out.println("coctails/getHistory/" + coctail_now);
        } else if (is_selected && text == "Посмотреть рецепт") {
            is_selected = false;
            String encodedText = null;
            try {
                encodedText = URLEncoder.encode(coctail_now, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            showHistory(req.doGetRequset("coctails/getRecipe/" + encodedText),message);
            System.out.println("coctails/getHistory/" + coctail_now);
        } else if (is_selected) {
            coctail_now = message.getText();
            sendMessage.setText("Выберите следующие действия");
            keyboard.remove(0);
            keyboardFirstRow.add("Посмотреть историю");
            keyboardFirstRow.add("Посмотреть рецепт");
            keyboard.add(keyboardFirstRow);
        } else {
            sendMessage.setText("Такой кнопки не существует");
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getBotUsername() {
        return "Coctails_Bar_Bot";
    }

    @Override
    public String getBotToken() {
        return "6919061982:AAFjM5_-GCkScdj0PF81YjHcDZTy0BdAOA8";
    }
}
