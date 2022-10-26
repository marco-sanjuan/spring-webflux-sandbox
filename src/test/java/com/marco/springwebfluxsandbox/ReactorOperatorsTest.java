package com.marco.springwebfluxsandbox;

import java.time.Duration;
import java.util.concurrent.Callable;

import com.marco.springwebfluxsandbox.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ReactorOperatorsTest {

    private static final Person PERSON_1 = Person.builder().name("Markus").lastName("Turica").age(21).build();
    private static final Person PERSON_2 = Person.builder().name("Peter").lastName("Minator").age(33).build();

    private final Flux<Integer> evenNumbers = Flux
            .range(1, 4)
            .filter(x -> x % 2 == 0); // i.e. 2, 4

    private final Flux<Integer> oddNumbers = Flux
            .range(1, 5)
            .filter(x -> x % 2 > 0);  // ie. 1, 3, 5

    @Test
    void map() {
        Mono<String> o = Mono.just(PERSON_1)
                .map(Person::getName);

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectComplete()
                .verify();
    }

    @Test
    void flatMap_mono() {
        Mono<String> o = Mono.just(PERSON_1)
                .flatMap(person -> Mono.just(person.getName()));

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectComplete()
                .verify();
    }

    @Test
    void flatMap_flux() {
        Flux<String> o = Flux.just(PERSON_1, PERSON_2)
                .flatMap(person -> Mono.just(person.getName()));

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectNext(PERSON_2.getName())
                .expectComplete()
                .verify();
    }

    @Test
    void firstWithSignal() {
        Mono<String> o = Mono.just(PERSON_1)
                .delayElement(Duration.ofSeconds(1))
                .map(Person::getName);

        Mono<String> o2 = Mono.just(PERSON_2)
                .map(Person::getName);

        Mono<String> r = Mono.firstWithSignal(o, o2);

        StepVerifier.create(r)
                .expectNext(PERSON_2.getName())
                .expectComplete()
                .verify();
    }

    @Test
    void from() {
        Mono<String> o = Mono.just(PERSON_1)
                .map(Person::getName);

        Mono<String> o2 = Mono.from(o)
                .map(s -> s + "<--- tunned");

        StepVerifier.create(o2)
                .expectNext(PERSON_1.getName() + "<--- tunned")
                .expectComplete()
                .verify();
    }

    @Test
    void fromCallable() {

        Callable<Mono<Person>> callable = () -> Mono.just(PERSON_1);

        Mono<String> o = Mono.fromCallable(callable)
                .flatMap(personMono -> personMono.map(Person::getName));

        StepVerifier.create(o)
                .expectNext(PERSON_1.getName())
                .expectComplete()
                .verify();
    }

    @Test
    public void concat_and_concatWith() {
        Flux<Integer> fluxOfIntegers = Flux.concat(
                evenNumbers,
                oddNumbers);

        Flux<Integer> fluxOfIntegers2 = evenNumbers.concatWith(oddNumbers);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();

        StepVerifier.create(fluxOfIntegers2)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }

    @Test
    public void combineLatest() {
        Flux<Integer> fluxOfIntegers = Flux.combineLatest(
                evenNumbers,
                oddNumbers,
                (a, b) -> a + b);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(5) // 4 + 1
                .expectNext(7) // 4 + 3
                .expectNext(9) // 4 + 5
                .expectComplete()
                .verify();
    }

    @Test
    public void merge() {
        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers,
                oddNumbers);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }

    @Test
    public void givenFluxes_whenZipIsInvoked_thenZip() {
        Flux<Integer> fluxOfIntegers = Flux.zip(
                evenNumbers,
                oddNumbers,
                (a, b) -> a + b);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(3) // 2 + 1
                .expectNext(7) // 4 + 3
                .expectComplete()
                .verify();
    }
}
