package hr.projekt.todoapplication.model;

import hr.projekt.todoapplication.exceptions.InvalidEventInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class EventInputHelper {
    private static final Logger log = LoggerFactory.getLogger(EventInputHelper.class);

    /**
     * Namijenjen za onemogucavanje slucajnog pokusaja stvaranja instanci klase.
     * <p>
     *     Buduci da klasa sadrzi staticke metode, njezin konstruktor je privatni kako bi se
     *     sprijecilo stvaranje objekata ove klase.
     * </p>
     */
    private EventInputHelper() {}

    /**
     * Trazi unos korisnika za odredno polje i provjerava da nije prazno.
     *
     * @param sc skener koji cita unos korisnika
     * @param fieldName naziv polja u koje se podaci upisuju
     * @return uneseni tekst
     * @throws InvalidEventInputException ako korisnik unese prazan tekst
     */
    public static String prompt(Scanner sc, String fieldName) throws InvalidEventInputException {
        log.trace("Zapocet unos za polje: {}", fieldName);
        System.out.print(fieldName + ": ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            log.error("Korisnik nije unio trazene podatke u trazeno polje");
            throw new InvalidEventInputException(fieldName + " ne smije biti prazan!");
        }
        log.info("Uspjesan unos za trazeno polje.");
        return input;
    }

    /**
     * Trazi unos datuma i vremena od korisnika te ga pretvara u {@link LocalDateTime}
     * <p>
     *     Ocekivani format unosa je <b>d.M.yyyy. HH:mm</b>,
     *     primjerice: <code>4.5.2005. 16:45</code>
     * </p>
     *
     * @param sc skener koji cita unos korisnika
     * @return ispravno uneseni format datuma
     * @throws InvalidEventInputException ako korisnik unese neispravan format datuma
     * @throws DateTimeParseException ako unos datuma ne odgovara ocekivanom formatu
     */
    public static LocalDateTime promptDate(Scanner sc) throws InvalidEventInputException {
        log.trace("Zapocet unos datuma i vremena dogadaja.");
        System.out.print("Datum i vrijeme (npr. 4.5.2025. 16:45): ");
        String input = sc.nextLine().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy. HH:mm");
        try {
            LocalDateTime date = LocalDateTime.parse(input, formatter);
            log.info("Uspjesno dodan datum i vrijeme");
            return date;
        } catch (DateTimeParseException _) {
            log.error("Neispravan format datuma.");
            throw new InvalidEventInputException("Neispravan format datuma!");
        }
    }
}
