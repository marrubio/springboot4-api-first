package es.marugi.spring.api.arch;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
    packages = "es.marugi.spring.api",
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class,
        NamingConventionTest.ExcludeGeneratedCode.class
    }
)
@SuppressWarnings("unused")
public class NamingConventionTest {

    @ArchTest
    static void controllers_should_have_controller_suffix(JavaClasses importedClasses) {
        classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true)
            .check(importedClasses);
    }

    @ArchTest
    static void rest_controllers_should_have_controller_suffix(JavaClasses importedClasses) {
        classes()
            .that().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true)
            .check(importedClasses);
    }

    @ArchTest
    static void service_interfaces_should_have_service_suffix(JavaClasses importedClasses) {
        classes()
            .that().resideInAPackage("..service..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("Service")
            .allowEmptyShould(true)
            .check(importedClasses);
    }

    @ArchTest
    static void service_implementations_should_have_impl_suffix(JavaClasses importedClasses) {
        classes()
            .that().resideInAPackage("..service..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Impl")
            .allowEmptyShould(true)
            .check(importedClasses);
    }

    static class ExcludeGeneratedCode implements ImportOption {
    @Override
    public boolean includes(Location location) {
        return !location.toString().contains("generated");
    }
    }
}
