package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class MainTest {

    private static boolean fxAvailable;

    @BeforeAll
    public static void startToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            fxAvailable = latch.await(5, TimeUnit.SECONDS);
        } catch (IllegalStateException e) {
            fxAvailable = true; // toolkit already started
        } catch (Throwable e) {
            fxAvailable = false; // no display available
        }
    }

    @Test
    public void testMainExtendsApplication() {
        assertTrue(Application.class.isAssignableFrom(Main.class));
    }

    @Test
    public void testHasPublicStaticMainMethod() throws Exception {
        Method main = Main.class.getMethod("main", String[].class);
        assertTrue(Modifier.isPublic(main.getModifiers()));
        assertTrue(Modifier.isStatic(main.getModifiers()));
        assertEquals(void.class, main.getReturnType());
    }

    @Test
    public void testHasPublicStartMethod() throws Exception {
        Method start = Main.class.getMethod("start", Stage.class);
        assertTrue(Modifier.isPublic(start.getModifiers()));
    }

    @Test
    public void testLocaleForEachLanguage() {
        assertEquals(new Locale("fi", "FI"), Main.localeFor("Finnish"));
        assertEquals(new Locale("sv", "SE"), Main.localeFor("Swedish"));
        assertEquals(new Locale("ja", "JP"), Main.localeFor("Japanese"));
        assertEquals(new Locale("ar", "SA"), Main.localeFor("Arabic"));
        assertEquals(new Locale("en", "US"), Main.localeFor("English"));
    }

    @Test
    public void testLocaleForUnknownFallsBackToEnglish() {
        assertEquals(new Locale("en", "US"), Main.localeFor("Klingon"));
    }

    @Test
    public void testStartShowsWindowWithTitle() throws Exception {
        assumeTrue(fxAvailable, "JavaFX toolkit not available, skipping UI test");

        AtomicReference<String> title = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                new Main(Locale.US).start(stage);
                title.set(stage.getTitle());
                assertNotNull(stage.getScene());
                stage.close();
            } catch (Throwable e) {
                error.set(e);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS), "UI did not start in time");
        if (error.get() != null) {
            fail(error.get());
        }
        assertEquals("Temperature Converter", title.get());
    }

    @Test
    public void testViewLoadsInEveryLanguage() throws Exception {
        assumeTrue(fxAvailable, "JavaFX toolkit not available, skipping UI test");

        for (String language : new String[] {"English", "Finnish", "Swedish", "Japanese", "Arabic"}) {
            Locale locale = Main.localeFor(language);
            AtomicReference<NodeOrientation> orientation = new AtomicReference<>();
            AtomicReference<Throwable> error = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {
                try {
                    Stage stage = new Stage();
                    new Main(locale).start(stage);
                    orientation.set(stage.getScene().getRoot().getNodeOrientation());
                    stage.close();
                } catch (Throwable e) {
                    error.set(e);
                } finally {
                    latch.countDown();
                }
            });

            assertTrue(latch.await(10, TimeUnit.SECONDS), "UI did not start in time");
            if (error.get() != null) {
                fail(language + " failed to load", error.get());
            }
            NodeOrientation expected = language.equals("Arabic")
                    ? NodeOrientation.RIGHT_TO_LEFT
                    : NodeOrientation.INHERIT;
            assertEquals(expected, orientation.get(), language);
        }
    }
}
