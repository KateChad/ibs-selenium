import net.bytebuddy.asm.Advice;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class RegistrationForm {
    public static Stream<Arguments> data(){
        return Stream.of(
                Arguments.of("Чадова Екатерина Эдуардовна", "(915) 744-6770", "г. Краснодар ул. Автомеханическа 2 кв 42"),
                Arguments.of("Маслова Анна Николаевна", "(955) 698-7463", "г. Архангельск ул. Красная 45 кв 5"),
                Arguments.of("Кривоульский Николай Инокеньтьевич", "(906) 581-3354", "г. Москва ул. Зеленая 14 кв 158"));
    }

}
