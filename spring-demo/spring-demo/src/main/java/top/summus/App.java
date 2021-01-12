package top.summus;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import top.summus.bean.UserService;

/** Hello world! */
@ComponentScan("top.summus")
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(App.class);
        UserService userService = (UserService) context.getBean("userService");
        userService.run();
    }
}
