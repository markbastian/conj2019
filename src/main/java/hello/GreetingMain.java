package hello;

public class GreetingMain {
    public static void main(String[] args) {
        GreetingController greetingController = new GreetingController();
        System.out.println(greetingController.greeting("Mark"));
    }
}
