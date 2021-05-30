package com.schedule.util;

import com.schedule.model.Schedule;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;


public class Aes256 {
    private SecretKey secretKey;

    public Aes256() {
        try {
            FileInputStream fileInputStream = new FileInputStream("encryptKey.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            secretKey = (SecretKey) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            secretKey = null;
        }
    }

    //Метод для перешифрования всхе записей в таблице расписаний
    public List<Schedule> encryptAllRecordWithNewKey(List<Schedule> allSchedules) {
        List<Schedule> decryptedScheduleList = null;
        try {
            if (secretKey != null && allSchedules != null && allSchedules.size() > 0)
                decryptedScheduleList = decryptScheduleList(allSchedules);

            this.secretKey = keyGeneration();
            FileOutputStream outputStream = new FileOutputStream("encryptKey.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(secretKey);
            objectOutputStream.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occurred while encrypted with start server " + e);
        }
        if(decryptedScheduleList == null || decryptedScheduleList.size() == 0)
            return Collections.emptyList();
        else
        return encryptScheduleList(decryptedScheduleList);
    }
    //Метод для генерации ключа размером 256 бит
    public SecretKey keyGeneration() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        return keyGenerator.generateKey();
    }
    //Метод для шифрования поля
    public String encrypt(final String data) {


        byte[] decodedKey = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secretKey.getEncoded()));

        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error occured while encrypting data", e);
        }

    }
    //Метод для дешифрования поля
    public String decrypt(
            final String encryptedString) {


        byte[] decodedKey = Base64.getDecoder().decode(Base64.getEncoder().encodeToString(secretKey.getEncoded()));

        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            return new String(cipherText);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error occured while decrypting data", e);
        }
    }

    //Метод для шифрования копии расписания
    public Schedule encryptScheduleCopy(Schedule originalSchedule) {
        Schedule schedule = originalSchedule.clone();
        schedule.setId(originalSchedule.getId());
        return encryptSchedule(schedule);
    }
    //Метод для шифрования расписания
    public Schedule encryptSchedule(Schedule schedule) {
        schedule.setStartTime(encrypt(schedule.getStartTime()));
        schedule.setEndTime(encrypt(schedule.getEndTime()));
        schedule.setDate(encrypt(schedule.getDate()));
        return schedule;
    }
    //Метод для дешифрования копии расписания
    public Schedule decryptScheduleCopy(Schedule originalSchedule) {
        Schedule schedule = originalSchedule.clone();
        schedule.setId(originalSchedule.getId());
        schedule.setUserId(originalSchedule.getUserId());
        return decryptSchedule(schedule);
    }
    //Метод для дешифрования расписания
    public Schedule decryptSchedule(Schedule schedule) {
        schedule.setStartTime(decrypt(schedule.getStartTime()));
        schedule.setEndTime(decrypt(schedule.getEndTime()));
        schedule.setDate(decrypt(schedule.getDate()));
        return schedule;
    }
    //Метод для дешифрования копии коллекции расписаний
    public List<Schedule> decryptScheduleListCopy(List<Schedule> scheduleList) {
        return scheduleList.stream().map(this::decryptScheduleCopy).collect(Collectors.toList());
    }
    //Метод для дешифрования коллекции расписаний
    public List<Schedule> decryptScheduleList(List<Schedule> scheduleList) {
        scheduleList.forEach(this::decryptSchedule);
        return scheduleList;
    }

    //Метод для шифрования коллекции расписаний
    private List<Schedule> encryptScheduleList(List<Schedule> scheduleList) {
        scheduleList.forEach(this::encryptSchedule);
        return scheduleList;
    }
}
