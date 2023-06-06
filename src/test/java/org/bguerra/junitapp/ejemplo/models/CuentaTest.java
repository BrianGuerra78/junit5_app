package org.bguerra.junitapp.ejemplo.models;

import org.bguerra.junitapp.ejemplo.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)//para tomarlo por medio de la clase y no del metodo
class CuentaTest {

    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
//Ciclo de vida
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("Iniciando el metodo.");
        //System.out.println(" ejecutando: " + testInfo.getDisplayName() + " "
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " "
                + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
//Ciclo de vida
    void tearDown() {
        System.out.println("Finalizando el metodo de prueba.");
    }

    @BeforeAll
    static void beforeAll() {
        //void beforeAll() {//solo si se agrega @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        System.out.println("Inicializando el test");
    }

    @AfterAll//Ciclo de vida
    static void afterAll() {
        //void afterAll() {//solo si se agrega @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")//etiqueta de la tarea
    @Nested//indica que es una clase anidada de test
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo {
        @Test
        @DisplayName("el nombre!")
        void testNombreCuenta() {
        //void testNombreCuenta(TestInfo testInfo, TestReporter testReporter) {
           /* System.out.println(" ejecutando: " + testInfo.getDisplayName() + " "
                    + testInfo.getTestMethod().orElse(null).getName()
                    + " con las etiquetas " + testInfo.getTags());*/
            //Cuenta cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
            //cuenta.setPersona("Brian");
            System.out.println(testInfo.getTags());
            if (testInfo.getTags().contains("cuenta")){
                System.out.println("hacer algo con la etiqueta cuenta");
            }
            String esperado = "Brian";
            String real = cuenta.getPersona();
            assertNotNull(real, () -> "La cuenta no puede ser nula");//da mas informacion sobre el error
            //Assertions.assertEquals(esperado, real);
            assertEquals(esperado, real, () -> "El nombre no es el que se esperaba: esperado " + esperado
                    + " el obtenido fue " + real);
            assertTrue(real.equals("Brian"), () -> "Nombre cuenta esperado debe ser igual a la real");
        }

        @Test
        @DisplayName("el saldo, que no sea null, mayor que cero, valor eserado.")
        void testSaldoCuenta() {
            //cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Testeando referencias que sean iguales con el metodo equals.")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            //assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);
        }
    }

    @Nested//indica que es una clase anidada de test
    class CuentaOperacionesTest {
        @Tag("cuenta")//etiqueta de la tarea
        @Test
        void testDebitoCuenta() {
            //Cuenta cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")//etiqueta de la tarea
        @Test
        void testCreditoCuenta() {
            //Cuenta cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")//etiqueta de la tarea
        @Tag("banco")
        @Test
        void testTransferirDineroCuenta() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Brian", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }
    }

    @Tag("cuenta")//etiqueta de la tarea
    @Tag("error")
    @Test
    void testDineroInsuficienteExceptionCuenta() {
        //Cuenta cuenta = new Cuenta("Brian", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            //Exception exception = assertThrows(NumberFormatException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Tag("cuenta")//etiqueta de la tarea
    @Tag("banco")
    @Test
    @Disabled//desabilita la ejecucion de la prueba
    @DisplayName("Probando relaciones entre las cuentas y el banco con assertAll.")
    void testRelacionBancoCuentas() {
        fail();//aseguras que falle el metodo
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Brian", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(() -> {//muestra todos los errores encontrados
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                            () -> "El valor del saldo de la cuenta2 no es el esperado");
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                            () -> "El valor del saldo de la cuenta2 no es el esperado");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size(),
                            () -> "El banco no tiene las cuentas esperadas");
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Brian", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Brian"))
                            .findFirst()
                            .get().getPersona()
                    );
                },
                () -> {
                    assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Brian")));
                }
        );

        /*assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Brian"))
                .findFirst().isPresent()
        );*/
    }

    @Nested//indica que es una clase anidada de test
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
//permite ejecutar solo en windows
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs(OS.MAC)
//permite ejecutar solo en mac
        void testSoloMac() {
        }

        @Test
        @EnabledOnOs(OS.LINUX)
//permite ejecutar solo en linux
        void testSoloLinux() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
//no permite ejecutar en windows
        void testNoWindows() {
        }
    }

    @Nested//indica que es una clase anidada de test
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
//solo se permite ejecutar con la version jdk8, una version distinta no funciona
        void tstSoloJDK8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_15)
//solo se permite ejecutar con la version jdk15, una version distinta no funciona
        void tstSoloJDK15() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_15)
        void testNoJDK15() {
        }
    }

    @Nested//indica que es una clase anidada de test
    class SistemPropertiesTest {
        @Test
        void imprimirSystemProperties() {//muestra las propiedades del sistema
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = "15.0.1")
//solo si la version de las propiedades coincide
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
//se desabilita para una arquitectura diferente
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
//se desabilita para una arquitectura diferente
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "briguerr")
//solo si el usuario del equipo coincide
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {// solo si esta configurado el ambiente de desarrollo
        }
    }

    @Nested//indica que es una clase anidada de test
    class VariableAmbienteTest {
        @Test
        void imprimirVaribalesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-18.0.2.1.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testProcesadores() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "dev")
        void testEnv() {

        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIROMENT", matches = "prod")
        void testEnvProd() {

        }
    }

    @Test
    @DisplayName("testSaldoCuentaDev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("testSaldoCuentaDev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando Debito Cuenta Repetir!")
    @RepeatedTest(value = 5, name = "{displayName} - Repeticion numero {currentRepetition} de {totalRepetitions}")
//repite el test varias veces
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")//etiqueta de la tarea
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        //@ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        @ValueSource(doubles = {100, 200, 300, 500, 700, 1000.12345})
            //void testDebitoCuenta(String monto) {
        void testDebitoCuentaValueSource(double monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4.500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Brian", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510.500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Tag("param")//etiqueta de la tarea
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            //TimeUnit.SECONDS.sleep(2);
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }
}