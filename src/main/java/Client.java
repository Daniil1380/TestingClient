
import com.google.common.primitives.Bytes;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Client {

    static InputStream inputStream;

    public static void main(String[] args) throws IOException {
        Socket clientSocket = new Socket("localhost",61337);
        OutputStream outputStream = clientSocket.getOutputStream();
        inputStream = clientSocket.getInputStream();
        RemoteTestingMessage remoteTestingMessageHello;
        do {
            outputStream.write(greeting().getMessageBytes());
            remoteTestingMessageHello = readMessage();
            if (remoteTestingMessageHello.getHeader().getRCode() != 0) {
                System.out.println("ОШИБКА: Пароль не верный. Повторите вход");
            }
            else {
                System.out.println("Вы успешно вошли в свой профиль!");
                RemoteTestingMessage remoteTestingMessageResult = readMessage();
                System.out.println("Ваш результат с прошлого теста: " + remoteTestingMessageResult.getResourceRecords().get(0).getDataString());
                RemoteTestingMessage remoteTestingMessageVariants = readMessage();
                System.out.println();
                System.out.println("Доступные темы на выбор: ");
                int j = 0;
                for (ResourceRecord resourceRecord : remoteTestingMessageVariants.getResourceRecords()) {
                    j++;
                    System.out.println(j + ": " + resourceRecord.getDataString());
                }
                outputStream.write(pickAnswer((byte) 4).getMessageBytes());
                RemoteTestingMessage test;
                do {
                    test = readMessage();
                    if (test.getHeader().getRCode() == 2) {
                        System.out.println("ОШИБКА: Нет такого теста. Введите номер тест заново");
                        outputStream.write(pickAnswer((byte) 4).getMessageBytes());
                    }
                    else if (test.getHeader().getRCode() != 0) {
                        System.out.println("ОШИБКА: Нет такого варианта ответа");
                    }
                    else if (test.getHeader().getMode() == 2) {
                        System.out.println("Ваш результат: " + test.getResourceRecords().get(0));
                    }
                    else {
                        int i = 0;
                        for (ResourceRecord resourceRecord : test.getResourceRecords()) {
                            if (i != 0){
                                System.out.println(i + ": " + resourceRecord.getDataString());
                            }
                            else {
                                System.out.println(resourceRecord.getDataString());
                            }
                            i++;
                        }
                    }
                    if (test.getHeader().getMode() != 2 && test.getHeader().getRCode() != 2) {
                        outputStream.write(pickAnswer((byte) 5).getMessageBytes());
                    }
                } while (test.getHeader().getMode() != 2);
            }
        } while (remoteTestingMessageHello.getHeader().getRCode() != 0);
    }

    private static  RemoteTestingMessage readMessage() throws IOException {
        byte[] headerBytes = inputStream.readNBytes(6);
        Header header = new Header(headerBytes);
        if (Integer.toUnsignedLong(header.getRrCount()) > 0) {
            List<Byte> resourceRecordsBytesList = new ArrayList<>(Bytes.asList(headerBytes));
            for (long i = 0; i < Integer.toUnsignedLong(header.getRrCount()); i++) {
                byte[] dataLength = inputStream.readNBytes(2);
                resourceRecordsBytesList.addAll(Bytes.asList(dataLength));

                byte[] dataBytes = inputStream.readNBytes(Short.toUnsignedInt(Util.convertToShort(dataLength)));
                resourceRecordsBytesList.addAll(Bytes.asList(dataBytes));
            }
            Byte[] bytes = resourceRecordsBytesList.toArray(new Byte[0]);
            return new RemoteTestingMessage(ArrayUtils.toPrimitive(bytes));
        }
        return new RemoteTestingMessage(headerBytes);
    }

    private static RemoteTestingMessage greeting(){
        Scanner scanner = new Scanner(System.in);
        RemoteTestingMessage helloMessage = new RemoteTestingMessage();
        Header header = new Header();
        header.setCs(false);
        header.setMode((byte) 0);
        header.setRrCount(2);
        helloMessage.setHeader(header);
        List<ResourceRecord> resourceRecordList = new ArrayList<ResourceRecord>();
        ResourceRecord resourceRecordLogin = new ResourceRecord();
        String login = scanner.nextLine();
        resourceRecordLogin.setData(login.getBytes());
        resourceRecordLogin.setLength((short) login.getBytes().length);

        ResourceRecord resourceRecordPassword = new ResourceRecord();
        String password = scanner.nextLine();
        resourceRecordPassword.setData(password.getBytes());
        resourceRecordPassword.setLength((short) password.getBytes().length);
        resourceRecordList.add(resourceRecordLogin);
        resourceRecordList.add(resourceRecordPassword);
        helloMessage.setResourceRecords(resourceRecordList);
        return helloMessage;
    }

    private static RemoteTestingMessage pickAnswer(byte mode){
        Scanner scanner = new Scanner(System.in);
        RemoteTestingMessage message = new RemoteTestingMessage();
        Header header = new Header();
        header.setCs(false);
        header.setMode(mode);
        header.setRrCount(1);
        message.setHeader(header);
        List<ResourceRecord> resourceRecordList = new ArrayList<>();
        ResourceRecord changeTheme = new ResourceRecord();
        String theme = scanner.nextLine();
        changeTheme.setData(theme.getBytes());
        changeTheme.setLength((short) theme.getBytes().length);
        resourceRecordList.add(changeTheme);
        message.setResourceRecords(resourceRecordList);
        return message;
    }
}