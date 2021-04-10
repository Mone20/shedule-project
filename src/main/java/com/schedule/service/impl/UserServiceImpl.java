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

    @Override
    public List<ScheduleUser> getAll() {
        return (List<ScheduleUser>) userRepository.findAll();
    }

    @Override
    public List<ScheduleUser> getByRole(String role) {
        return userRepository.findByRoleId(roleRepository.findByName(role).getId());
    }

    @Override
    public ScheduleUser getById(Integer id) {
        return null;
    }

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

    @Override
    public ScheduleUser getByLoginAndPassword(String header) {
        if(authorization(header))
            return userRepository.findByLogin(getLoginAndPasswordFromHeader(header).getKey());
        return null;
    }

    @Override
    public boolean authorization(String header){
        Pair<String,String> loginPassword = getLoginAndPasswordFromHeader(header);
        if(!userRepository.existsByLogin(loginPassword.getKey()))
            return false;
        ScheduleUser user = userRepository.findByLogin(loginPassword.getKey());
        if (BCrypt.checkpw(loginPassword.getValue(), user.getPassword()))
            return true;
        return false;
    }

    public Pair<String,String> getLoginAndPasswordFromHeader(String header){
        header = header.replaceAll("Basic","").trim();
        byte[] decodedHeaderBytes = decoder.decode(header);
        String decodedHeader = new String(decodedHeaderBytes, StandardCharsets.UTF_8);;
        String login = decodedHeader.substring(0,decodedHeader.indexOf(":"));
        String password = decodedHeader.substring(decodedHeader.indexOf(":")+1);
        return new Pair<String,String>(login,password);
    }
}
