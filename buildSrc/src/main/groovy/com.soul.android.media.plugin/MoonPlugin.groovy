import org.gradle.api.Plugin

public class MoonPlugin implements Plugin{

    @Override
    void apply(Object target) {
        println("this is MoonPlugin")
    }
}