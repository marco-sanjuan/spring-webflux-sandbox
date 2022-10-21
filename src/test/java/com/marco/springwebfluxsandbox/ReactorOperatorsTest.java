package com.marco.springwebfluxsandbox;

import com.marco.springwebfluxsandbox.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ReactorOperatorsTest {

    private static final Person PERSON_1 = Person.builder().name("Markus").lastName("Turica").age(21).build();
    private static final Person PERSON_2 = Person.builder().name("Peter").lastName("Minator").age(33).build();

    @Test
    void map() {
        Mono<String> o = Mono.just(PERSON_1)
                .map(Person::getName);

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectComplete();
    }

    @Test
    void flatMap_mono() {
        Mono<String> o = Mono.just(PERSON_1)
                .flatMap(person -> Mono.just(person.getName()));

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectComplete();
    }

    @Test
    void flatMap_flux() {
        Flux<String> o = Flux.just(PERSON_1, PERSON_2)
                .flatMap(person -> Mono.just(person.getName()));

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectNext(PERSON_2.getName())
                .expectComplete();
    }

}
