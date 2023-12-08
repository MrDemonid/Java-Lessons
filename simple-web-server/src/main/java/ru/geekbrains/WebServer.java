package ru.geekbrains;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebServer {

    public static void main(String[] args) {
        System.out.println("Start server...");

        // создаём класс для работы с сокетами
        try (ServerSocket serverSocket = new ServerSocket(8088)) {
            // socket - розетка, т.е. виртуальная розетка сетевой карты.
            Socket socket = serverSocket.accept(); // ждем пока не будет обращения на порт 8080
            System.out.println("New client connected!");

            // считываем что нам пришло в пакете TCP/IP
            // socket.getInputStream() - считывает байты, поступившие от подключившегося клиента
            // InputStreamReader() - преобразует поступивший байт в символьный вид
            // BufferedReader() - буфирезирует поступающие символы, позволяя читать данные сразу строками.
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // ждем пока в буфере появится строка
            while (!input.ready());

            // выводим на экран
            while (input.ready())
            {
                System.out.println(input.readLine());
            }
            /*
            Запустим hrome и перейдем на адрес: localhost:8088/tehno/index.html
            Получим что-то вроде этого:

            Start server...
            New client connected!
            GET /tehno/index.html HTTP/1.1
            Host: localhost:8088
            Connection: keep-alive
            Cache-Control: max-age=0
            sec-ch-ua: "Google Chrome";v="119", "Chromium";v="119", "Not?A_Brand";v="24"
            ....

            GET - это как раз запрос страницы.
            Собственно в запросе только два обязательных поля: GET (запрос файла)
            и путь запрашиваемой страницы, всё остальное - опционально.
            */

            /*
            Чтобы что-то записать, то нужно воспользоваться методом
            socket.socket.getOutputStream()
            но он работает на уровне TCP/IP, то есть с байтами,
            поэтому перенаправим вывод на текстовый вывод:
            PrintWriter output = new PrintWriter(socket.getOutputStream());
            теперь всё что пишем в output.println() будет перенаправляться
            на socket.getOutputStream(), в понятном ему формате.


            */
            PrintWriter output = new PrintWriter(socket.getOutputStream());
            /*
            Сначала пишем версию HTTP, а затем код данного ответа, который показывает
            насколько успешно был выполнен запрос:
                200 - запрос выполнен успешно
                300 - документ был куда-то перемещен и предлагает браузеру перейти по новому адресу
                404 - страница не найдена
                4xx - ошибка на стороне клиента (например запрошена некорректная страница)
                5xx - ошибка на стороне сервера
            За кодом пишется какое-то текстовое представление этого кода.
            */
            output.println("HTTP/1.1 200 OK");
            /*
            Дальше желательно указать кодировку
            "Content-Type:" - заголовок: тип контента:
                                text/html - текст в формате html
            и говорим, что этот текст будет в кодировке UTF-8
             */
            output.println("Content-Type: text/html; charset=utf-8");
            /*
            Далее сообщаем, что все заголовки закончились и начинается содержимое
            запрошенного файла/ресурса.
            */
            output.println(); // это и есть признак конца заголовков
            /*
            Всё что ниже - это и есть содержимое запрошенного ресурса/файла
            */
            output.println("<h1>Привет от сервера!</h1>");
            /*
            Поскольку всё что мы добавляли в PrintWriter() буферизируется,
            нам нужно сбросить буфер, гарантировав отправку клиенту.
            */
            output.flush();

            // если весь этот код планируется в цикле, то нужно принудительно закрыть
            // ввод/вывод:
            input.close();
            output.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
