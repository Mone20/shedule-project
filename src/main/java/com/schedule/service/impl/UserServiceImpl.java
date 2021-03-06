package com.schedule.service.impl;


import com.schedule.model.ScheduleUser;
import com.schedule.repository.RoleRepository;
import com.schedule.repository.UserRepository;
import com.schedule.service.UserService;
import javafx.util.Pair;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final Base64.Decoder decoder = Base64.getDecoder();


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository)  {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    //Метод для получения всех пользователей системы
    @Override
    public List<ScheduleUser> getAll() {
        return (List<ScheduleUser>) userRepository.findAll();
    }
    //Метод для получения пользователя по роли
    @Override
    public List<ScheduleUser> getByRole(String role) {
        return userRepository.findByRoleId(roleRepository.findByName(role).getId());
    }
    //Метод для получения пользователя по id
    @Override
    public ScheduleUser getById(Integer id) {
        return null;
    }
    //Метод для регистрации пользователя
    @Override
    public ScheduleUser create(String header) {
        Pair<String,String> loginPassword = getLoginAndPasswordFromHeader(header);
        String login = loginPassword.getKey();
        String password = loginPassword.getValue();
        if(userRepository.existsByLogin(login))
            return null;
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        ScheduleUser user = new ScheduleUser(login,hashed,roleRepository.findByName("user"));
        userRepository.save(user);
        return user;
    }
    //Метод для получения пользователя их заголовка Authorization
    @Override
    public ScheduleUser getByLoginAndPassword(String header) {
        if(!authorization(header).getRole().getName().isEmpty())
            return userRepository.findByLogin(getLoginAndPasswordFromHeader(header).getKey());
        return null;
    }
    //Метод для авторизации пользователя
    @Override
    public ScheduleUser authorization(String header){
        Pair<String,String> loginPassword = getLoginAndPasswordFromHeader(header);
        if(!userRepository.existsByLogin(loginPassword.getKey()))
            return null;
        ScheduleUser user = userRepository.findByLogin(loginPassword.getKey());
        if (BCrypt.checkpw(loginPassword.getValue(), user.getPassword()))
            return user;
        return null;
    }

    //Метод для получения логина и пароля из заголовка Authorization
    public Pair<String,String> getLoginAndPasswordFromHeader(String header){
        header = header.replaceAll("Basic","").trim();
        byte[] decodedHeaderBytes = decoder.decode(header);
        String decodedHeader = new String(decodedHeaderBytes, StandardCharsets.UTF_8);;
        String login = decodedHeader.substring(0,decodedHeader.indexOf(":"));
        String password = decodedHeader.substring(decodedHeader.indexOf(":")+1);
        return new Pair<String,String>(login,password);
    }
}
