package com.shedule.service.impl;


import com.shedule.model.SheduleUser;
import com.shedule.repository.RoleRepository;
import com.shedule.repository.UserRepository;
import com.shedule.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final Base64.Decoder decoder = Base64.getDecoder();


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<SheduleUser> getAll() {
        return (List<SheduleUser>) userRepository.findAll();
    }

    @Override
    public List<SheduleUser> getByRole(String role) {
        return userRepository.findByRoleId(roleRepository.findByName(role).getId());
    }

    @Override
    public SheduleUser getById(Integer id) {
        return null;
    }

    @Override
    public SheduleUser create(String header) {
        header = header.replaceAll("Basic","").trim();
        byte[] decodedHeaderBytes = decoder.decode(header);
        String decodedHeader = new String(decodedHeaderBytes, StandardCharsets.UTF_8);;
        String login = decodedHeader.substring(0,decodedHeader.indexOf(":"));
        String password = decodedHeader.substring(decodedHeader.indexOf(":")+1);
        if(userRepository.existsByLogin(login))
            return null;
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        SheduleUser user = new SheduleUser(login,hashed,roleRepository.findByName("user"));
        userRepository.save(user);
        return user;
    }

    @Override
    public SheduleUser getByLoginAndPassword(String header) {
        header = header.replaceAll("Basic","").trim();
        byte[] decodedHeaderBytes = decoder.decode(header);
        String decodedHeader = new String(decodedHeaderBytes, StandardCharsets.UTF_8);;
        String login = decodedHeader.substring(0,decodedHeader.indexOf(":"));
        String password = decodedHeader.substring(decodedHeader.indexOf(":")+1);
        if(!userRepository.existsByLogin(login))
            return null;
        SheduleUser user = userRepository.findByLogin(login);
        if (BCrypt.checkpw(password, user.getPassword()))
        return user;
        return null;
    }
}
