package top.summus.bean;

import org.springframework.stereotype.Component;

@Component
public class UserService {
    public UserService() {
        System.out.println("init UserService");
    }

    public void run() {
        System.out.println("running");
    }
}
