package com.luidmidev.template.spring.services.quizz;

import com.luidmidev.template.spring.repositories.QuestionAnswerRepository;
import com.luidmidev.template.spring.services.quizz.models.Answer;
import com.luidmidev.template.spring.services.quizz.models.Question;
import com.luidmidev.template.spring.services.quizz.models.QuestionAnswers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.luidmidev.template.spring.services.quizz.QuestionType.*;
import static com.luidmidev.template.spring.services.quizz.models.Answer.findAnswerById;
import static com.luidmidev.template.spring.services.quizz.validations.QValidator.*;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;

@Log4j2
@Service
public class SampleService {


    private final QuestionAnswerRepository repository;

    @Getter
    private final List<Question> questions;
    static final List<String> MONTHS = of("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    public SampleService(QuestionAnswerRepository repository) {

        this.repository = repository;

        this.questions = of(
                new Question(1L, "Nombre del socio", TEXT, required()),
                new Question(2L, "Cédula de identidad", TEXT, ciValidator(), required()),
                new Question(4L, "Telefono celular", TEL, required(), number()),
                new Question(5L, "Correo electrónico", TEXT, email(), required()),
                new Question(6L, "Nombre de la finca", TEXT, required()),
                new Question(7L, "Pertenece usted a alguna asociación, infique el nombre", TEXT),
                new Question(8L, "Especifique su etnia", RADIO, of("Afroecuatoriano", "Indígena", "Montubio", "Mestizo", "Blanco"), TRUE, required()),
                new Question(9L, "Año de la siembra", NUMBER, greaterThan(1900), required()),
                new Question(10L, "Número de hectáreas", NUMBER, isPositiveOrZero(), required()),
                new Question(11L, "Número de plantas", NUMBER, isPositiveOrZero(), required()),
                new Question(12L, "Número de trabajadores (incluyase)", NUMBER, isPositiveOrZero(), required()),
                new Question(13L, "¿Cuántas mujeres trabajan en la finca?", NUMBER, isPositiveOrZero(), required()),
                new Question(14L, "¿Cuántos trabajadores imigrantes trabajan en la finca?", NUMBER, isPositiveOrZero(), required()),
                new Question(15L, "¿Cuántos empleados mayores trabajan en la finca?", NUMBER, isPositiveOrZero(), required()),
                new Question(16L, "¿Cuántos empleados menores a 30 años trabajan en la finca?", NUMBER, isPositiveOrZero(), required()),
                new Question(17L, "Distancia de siembra", NUMBER, isPositiveOrZero(), required()),
                new Question(18L, "Variedad", CHECKBOX, of("Criolla", "Hibrida", "Nativa", "Gigante brasileña"), TRUE, required()),
                new Question(19L, "Vivero en que compró su planta (Empresa)", TEXT, required()),
                new Question(20L, "Cantidad de Kilogramos producidos al año por hectárea", NUMBER, isPositiveOrZero(), required()),
                new Question(21L, "Pico alto de producción (Kilogramo/mes)", NUMBER, isPositiveOrZero(), required()),
                new Question(22L, "Meses altos de producción", CHECKBOX, MONTHS, required()),
                new Question(23L, "Meses bajos de producción", CHECKBOX, MONTHS, required()),
                new Question(24L, "Certificaciones", CHECKBOX, of("Fair Trade", "Rainforest Alliance", "UTZ", "Organic"), TRUE, required()),
                new Question(25L, "Tipo de cultivo", RADIO, of("Convencional", "Orgánico"), required()),
                new Question(26L, "Etapa de cultivo", RADIO, of("Polinización", "Enfundado", "Cosecha"), required()),
                new Question(27L, "¿Cuantas flores hasta el momento?", NUMBER, isPositiveOrZero(), required()),
                new Question(28L, "¿Cuantos frutos enfundados?", NUMBER, isPositiveOrZero(), required()),
                new Question(29L, "¿La plantación incluye riego?", RADIO, of("Si", "No"), required()),
                new Question(30L, "Seleccione la ubicación de su finca", LOCATION, required()),
                new Question(31L, "Provincica, cantón, parroquia", CUSTOM, required())
        );
    }


    public void saveAnswers(List<Answer> answers) {

        log.info("Guardando respuestas del usuario {}", answers);

        for (var question : questions) {
            var answer = findAnswerById(question.getId(), answers);
            question.validateAnswer(answer);
        }


        log.info("answers validated correctly");
        var questionAnswers = new QuestionAnswers();
        questionAnswers.setAnswers(answers);
        questionAnswers.setCreationDate();
        repository.save(questionAnswers);
        log.info("Respuestas guardadas correctamente");
    }
}
