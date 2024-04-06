package com.luidmidev.template.spring.services.questionnaires;

import com.luidmidev.template.spring.services.questionnaires.models.Questionnaire;
import com.luidmidev.template.spring.services.questionnaires.repositories.QuestionnaireAnswersRepository;
import com.luidmidev.template.spring.services.questionnaires.models.Answer;
import com.luidmidev.template.spring.services.questionnaires.models.Question;
import com.luidmidev.template.spring.services.questionnaires.models.QuestionnaireAnswers;
import com.luidmidev.template.spring.services.questionnaires.repositories.QuestionnaireRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.luidmidev.template.spring.services.questionnaires.QuestionType.*;
import static com.luidmidev.template.spring.services.questionnaires.models.Answer.findAnswerByNumber;
import static com.luidmidev.template.spring.services.questionnaires.validations.QValidator.*;
import static java.lang.Boolean.TRUE;
import static java.util.List.of;

@Log4j2
@Service
public class SampleService {


    private final QuestionnaireAnswersRepository questionnaireAnswersRepository;

    @Getter
    private final List<Question> questions;
    static final List<String> MONTHS = of("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");

    public SampleService(QuestionnaireAnswersRepository questionnaireAnswersRepository, QuestionnaireRepository questionnaireRepository) {

        this.questionnaireAnswersRepository = questionnaireAnswersRepository;

        this.questions = of(
                new Question("Nombre del socio", TEXT, required()),
                new Question("Cédula de identidad", TEXT, ecuadorCi(), required()),
                new Question("Telefono celular", TEL, required(), number()),
                new Question("Correo electrónico", TEXT, email(), required()),
                new Question("Nombre de la finca", TEXT, required()),
                new Question("Pertenece usted a alguna asociación, infique el nombre", TEXT),
                new Question("Especifique su etnia", RADIO, of("Afroecuatoriano", "Indígena", "Montubio", "Mestizo", "Blanco"), TRUE, required()),
                new Question("Año de la siembra", NUMBER, greaterThan(1900), required()),
                new Question("Número de hectáreas", NUMBER, positiveOrZero(), required()),
                new Question("Número de plantas", NUMBER, positiveOrZero(), required()),
                new Question("Número de trabajadores (incluyase)", NUMBER, positiveOrZero(), required()),
                new Question("¿Cuántas mujeres trabajan en la finca?", NUMBER, positiveOrZero(), required()),
                new Question("¿Cuántos trabajadores imigrantes trabajan en la finca?", NUMBER, positiveOrZero(), required()),
                new Question("¿Cuántos empleados mayores trabajan en la finca?", NUMBER, positiveOrZero(), required()),
                new Question("¿Cuántos empleados menores a 30 años trabajan en la finca?", NUMBER, positiveOrZero(), required()),
                new Question("Distancia de siembra", NUMBER, positiveOrZero(), required()),
                new Question("Variedad", CHECKBOX, of("Criolla", "Hibrida", "Nativa", "Gigante brasileña"), TRUE, required()),
                new Question("Vivero en que compró su planta (Empresa)", TEXT, required()),
                new Question("Cantidad de Kilogramos producidos al año por hectárea", NUMBER, positiveOrZero(), required()),
                new Question("Pico alto de producción (Kilogramo/mes)", NUMBER, positiveOrZero(), required()),
                new Question("Meses altos de producción", CHECKBOX, MONTHS, required()),
                new Question("Meses bajos de producción", CHECKBOX, MONTHS, required()),
                new Question("Certificaciones", CHECKBOX, of("Fair Trade", "Rainforest Alliance", "UTZ", "Organic"), TRUE, required()),
                new Question("Tipo de cultivo", RADIO, of("Convencional", "Orgánico"), required()),
                new Question("Etapa de cultivo", RADIO, of("Polinización", "Enfundado", "Cosecha"), required()),
                new Question("¿Cuantas flores hasta el momento?", NUMBER, positiveOrZero(), required()),
                new Question("¿Cuantos frutos enfundados?", NUMBER, positiveOrZero(), required()),
                new Question("¿La plantación incluye riego?", RADIO, of("Si", "No"), required()),
                new Question("Seleccione la ubicación de su finca", LOCATION, required()),
                new Question("Provincica, cantón, parroquia", CUSTOM, required())
        );

        log.info("Saving questionnaire");

        var questionnaire = new Questionnaire();
        questionnaire.setTitle("Cuestionario de agricultura");
        questionnaire.setDescription("Cuestionario para agricultores");
        questionnaire.setQuestions(questions);
        questionnaireRepository.save(questionnaire);

        var questionnaire2 = questionnaireRepository.findById(questionnaire.getId()).orElseThrow();
        log.info("Questionnaire saved: {}", questionnaire2);

    }


    public void saveAnswers(List<Answer> answers) {

        log.info("Guardando respuestas del usuario {}", answers);

        for (var question : questions) {
            var answer = findAnswerByNumber(question.getNumber(), answers);
            question.validateAnswer(answer);
        }


        log.info("answers validated correctly");
        var questionAnswers = new QuestionnaireAnswers();
        questionAnswers.setAnswers(answers);
        questionAnswers.setCreationDate();
        questionnaireAnswersRepository.save(questionAnswers);
        log.info("Respuestas guardadas correctamente");
    }
}
