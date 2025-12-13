package hr.projekt.todoapplication.model.event;

import java.io.Serializable;

/**
 * Pohranjuje informacije o prioritetu i kategoriji dogadaja.
 * <p>
 *     Ova klasa sluzi za cuvanje metapodataka dogadaja,
 *     ukljucujuci njegovu kategoriju i razinu prioriteta.
 * </p>
 *
 * @param priority razina prioriteta dogadaja
 * @param category kategorija dogadaja, npr. <code>OSOBNO</code> ili <code>POSAO</code>
 */
public record EventInfo(PriorityLevel priority, EventCategory category) implements Serializable { }
