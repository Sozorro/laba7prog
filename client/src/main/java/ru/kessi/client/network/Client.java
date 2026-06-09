package ru.kessi.client.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import org.tinylog.Logger;

import ru.kessi.client.io.Input;
import ru.kessi.common.Request;

public class Client {
    private static final int MAX_MESSAGE_SIZE = 10 * 1024 * 1024;

    private SocketChannel socketChannel;
    private String host;
    private int port;

    private boolean checkAuth = true;
    private String login;
    private String password;

    public Client() {
    }

    public void createClient(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.socket().setSoTimeout(30000); // чтобы не висеть вечно
            Logger.debug("Клиент создан");
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке создать клиента");
        }
    }

    public void closeClient(){
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
                Logger.debug("Клиент удалён");
            }
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке удалить клиента");
        }
    }

    public void run(String host, int port) {
        this.host = host;
        this.port = port;

        Scanner scanner = new Scanner(System.in);
        try {
            Logger.info("Ожидаем подключения сервера ...");
            if (connectRetry()) {
                Logger.info("Сервер {} подключен", socketChannel.getRemoteAddress());
                while (true) {
                    try {
                        Input.initInput(scanner);
                        if(login == null && password == null) {
                            checkAuth = authenticateUser(scanner);
                        }
                        if(checkAuth == true)  request(scanner);
                        break; // Выход, если request() завершился нормально (например, exit)
                    } catch (IOException e) {
                        Logger.warn(e, "Соединение потеряно, переподключение...");
                        if (!connectRetry()) {
                            Logger.error(e, "Не удалось переподключиться. Завершение работы.");
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке подключить сервер");
        } finally {
            closeClient();
        }

    }
    private boolean connectRetry() {
        int maxAttempt = 100;
        int attempt = 0;
                
        Logger.info("Попытка установить связь с сервером...");

        while (attempt < maxAttempt) {
            attempt++;
            
            if (socketChannel != null && socketChannel.isOpen()) {
                closeClient();
            }
            createClient();

            try {
                if (socketChannel.connect(new InetSocketAddress(host, port))) {
                    Logger.info("Подключение установлено", attempt);
                    Logger.debug("Подключение установлено с попытки {}", attempt);
                    return true;
                }
            } catch (ConnectException e) {
                Logger.warn("Попытка {} не удалась: сервер недоступен ({}:{})", attempt, host, port);
            } catch (NoRouteToHostException e) {
                Logger.warn("Попытка {} не удалась: хост недоступен", attempt);
            } catch (IOException e) {
                Logger.warn("Попытка {} не удалась: {}", attempt, e.getMessage());
            } catch (Exception e) {
                Logger.warn("Попытка {} не удалась (неизвестная ошибка): {}", attempt, e.getMessage());
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.error(e, "Поток прерван");
                break;
            }
        }

        Logger.error("Достигнут лимит попыток подключения. Проверьте доступность сервера.");
        return false;
    }


    public void request(Scanner scanner) throws IOException {

        try {
            while (true) {
                Request req = Input.getRequest();
                if (req == null || req.getCommand() == null) {
                    continue;
                }

                Logger.debug("получена команда: {}", req.getCommand().toString());

                if (req.getCommand().getName().equals("help")) {
                    continue;
                }

                req.setLogin(login);
                req.setPassword(password);

                Logger.debug("Передаём данные: {}", req.getCommand().toString());
                if(req.getLabWork() != null) Logger.trace("LabWork: {}", req.getLabWork());
                if(req.getArgs() != null) Logger.trace("Args: {}", req.getArgs());
                
                byte[] serializedObject = Serialize.serializeObject(req);
                ByteBuffer buffer = ByteBuffer.allocate(4 + serializedObject.length);
                buffer.putInt(serializedObject.length);
                buffer.put(serializedObject);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                } 
                buffer.clear();
                if (req.getCommand().getName().equals("exit") || 
                    req.getCommand().getName().equals("stop")) {
                   break;
                }
                String ans = answerServer();
                Logger.info(ans);

            }

        } catch (IOException e) {
            Logger.error(e, "Ошибка ввода-вывода при попытке передать запрос");
            throw e;
        } catch (Exception e) {
            Logger.error(e, "Ошибка при попытке передать запрос");
        }
    }

    public String answerServer() {
        try {
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            while (lengthBuffer.hasRemaining()) {
                int c = socketChannel.read(lengthBuffer);
                if (c == -1) {
                    Logger.warn("Ошибка при попытке чтения заголовка ответа от сервера :(");
                    throw new IOException("Сервер закрыл соединение");
                }   
            }
            lengthBuffer.flip();
            int size = lengthBuffer.getInt();

            if(size <= 0 || size > MAX_MESSAGE_SIZE) {
                Logger.warn("Неверный размер: {}", size);
                return null;
            }

            ByteBuffer buf = ByteBuffer.allocate(size);
            ByteArrayOutputStream arrByte = new ByteArrayOutputStream();
            while (buf.hasRemaining()) {
                int c = socketChannel.read(buf);
                if (c == -1) {
                    Logger.warn("Ошибка при попытке чтения тела ответа от сервера :(");
                    throw new IOException("Сервер закрыл соединение");
                }
            }
            buf.flip();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            String req = Serialize.tryDeserialize(bytes);
            if (req == null) {
                return null;
            }
            Logger.trace("Получен объект: {}", req);
            return req;
        } catch (Exception e) {
            Logger.error(e, "Ошибка при чтении ответа сервера, попробуйте ещё раз");
            return null;
        }
        

    }

    private boolean authenticateUser(Scanner scanner) throws IOException {
        Input.showText("Чтобы взаимодействовать с коллекцией авторизируйтесь:");
        Request authUser = null;
        try {
            while (true) {
                Logger.info("(Чтобы выйти из управления коллекцией введите exit(e), чтобы зарегистрировать нового пользователя введите new(n))");

                authUser = new Request(new ru.kessi.common.commandManager.AuthCommand());
                String reqLogin;
                String reqPass;
                reqLogin = Input.getParams("Введите логин: ");
                if("exit".equals(reqLogin) || "e".equals(reqLogin)) {
                    return false;
                }
                if("new".equals(reqLogin) || "n".equals(reqLogin)) {
                    authUser = new Request(new ru.kessi.common.commandManager.RegNewUserCommand());
                    reqLogin = Input.getParams("Введите желаемый логин: ");
                    reqPass = Input.getParams("Введите желаемый пароль: ");
                } else {
                    reqPass = Input.getParams("Введите пароль: ");
                    if("exit".equals(reqPass) || "e".equals(reqPass)) {
                        return false;
                    }
                    if("new".equals(reqPass) || "n".equals(reqPass)) {
                        authUser = new Request(new ru.kessi.common.commandManager.RegNewUserCommand());
                        reqLogin = Input.getParams("Введите желаемый логин: ");
                        reqPass = Input.getParams("Введите желаемый пароль: ");
                    }
                }

                authUser.setLogin(reqLogin);
                authUser.setPassword(reqPass);


                Logger.debug("Отправляем запрос на авторизацию...");

                byte[] serializedObject = Serialize.serializeObject(authUser);
                ByteBuffer buffer = ByteBuffer.allocate(4 + serializedObject.length);
                buffer.putInt(serializedObject.length);
                buffer.put(serializedObject);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                }

                String ans = answerServer();

                if ("auth correct".equals(ans)) {
                    Logger.info("Авторизация успешна! Добро пожаловать, {}", reqLogin);
                    this.login = reqLogin;
                    this.password = reqPass;
                    return true;
                } else if ("reg correct".equals(ans)) {
                    Logger.info("Регистрация пользователя {} прошла успешна! Теперь для доступа к коллекции войдите в созданный аккаунт", reqLogin);
                } else if ("auth fail".equals(ans) || "reg fail".equals(ans)) {
                    Logger.info("Ошибка: Неверный логин или пароль. Попробуйте снова");
                }
            
            }
        } catch (IOException e) {
            Logger.error(e, "Ошибка при попытке авторизации пользователя, попробуйте ещё раз");
            throw e;
        } catch (Exception e) {
            Logger.error(e, "Критическая ошибка при попытке авторизации пользователя");
            return false;
        }
    }
}
/*ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
while (lengthBuffer.hasRemaining()) {
    int c = clientChannel.read(lengthBuffer);
    if (c == -1) {
        clientChannel.close();
        arrByteMapForClients.remove(clientChannel);
        Logger.info("Клиент отключился");
        return;
    } else if (c == 0) {
        // Нет данных, ждём следующего события
        Logger.info("Нет данных, ждём следующего события");
        return;
    }   
}
lengthBuffer.flip();
int size = lengthBuffer.getInt();
Logger.info("][" + size);
//lengthBuffer.clear();

if(size <= 0) {
    Logger.info("Неверный размер");
    return;
}

ByteBuffer buf = ByteBuffer.allocate(size);
ByteArrayOutputStream arrByte = arrByteMapForClients.get(clientChannel);

while (buf.hasRemaining()) {
    int c = clientChannel.read(buf);
    if (c == -1) {
        clientChannel.close();
        arrByteMapForClients.remove(clientChannel);
        Logger.info("Клиент отключился");
        return;
    }
}

buf.flip();
byte[] bytes = new byte[buf.remaining()];
buf.get(bytes);
//buf.clear();
//arrByte.write(bytes);
Object obj = Serialize.tryDeserialize(bytes);

if (obj != null) {
    Logger.info("Получен объект: " + obj);
}
 */

/*
 * Клиентский модуль должен в интерактивном режиме считывать
 * команды, передавать их для выполнения на сервер и выводить результаты
 * выполнения.
 * 
 * Объекты между клиентом и сервером должны передаваться в сериализованном виде.
 * Клиент должен корректно обрабатывать временную недоступность сервера.
 * Обмен данными между клиентом и сервером должен осуществляться по протоколу
 * TCP
 * Для обмена данными на клиенте необходимо использовать потоки ввода-вывода
 * 
 * Обязанности клиентского приложения:
 * 
 * Чтение команд из консоли.
 * Валидация вводимых данных.
 * Сериализация введённой команды и её аргументов.
 * Отправка полученной команды и её аргументов на сервер.
 * Обработка ответа от сервера (вывод результата исполнения команды в консоль).
 * Команду save из клиентского приложения необходимо убрать.
 * Команда exit завершает работу клиентского приложения.
 * Важно! Команды и их аргументы должны представлять из себя объекты классов.
 * Недопустим обмен "простыми" строками. Так, для команды add или её аналога
 * необходимо сформировать объект, содержащий тип команды и объект, который
 * должен храниться в вашей коллекции.
 */