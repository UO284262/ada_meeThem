package com.ada.ada_meethem.util;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.espresso.ViewInteraction;

public class TestUtils {

    public static final int DEFAULT_TIMEOUT = 1000000;

    // Espera a que aparezca un elemento en la vista
    public static void waitFor(ViewInteraction viewInteraction, long timeoutMillis) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < endTime) {
            try {
                viewInteraction.check(matches(isDisplayed()));
                return; // Si la comprobación pasa, salimos del bucle de espera
            } catch (Throwable ignored) {
                // Si la comprobación falla, continuamos esperando
            }
            // Esperar un breve periodo antes de intentar nuevamente
            Thread.sleep(100);
        }
        // Si la espera excede el tiempo máximo, lanzar una excepción
        throw new AssertionError("Timed out waiting for view to be visible.");
    }

}
