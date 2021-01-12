package top.summus;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Hello world!
 *
 */
@ComponentScan("top.summus")
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        AnnotationConfigApplicationContext context=new AnnotationConfigApplicationContext(App.class);
    }
}
