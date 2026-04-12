package es.marugi.spring.api.arch;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
    packages = "es.marugi.spring.api",
    importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class NamingConventionTest {

    /**
     * Services should have names ending with 'Service' or 'ServiceImpl' and reside in the service package.
     */
    @ArchTest
    static ArchRule services_should_be_suffixed =
            classes()
                    .that().resideInAPackage("..service..")
                    .and().areAnnotatedWith(Service.class)
                    .should().haveNameMatching(".*Service(Impl)?");

    @ArchTest
    static ArchRule services_shound_be_in_service_package =
            classes().that().areAnnotatedWith(Service.class)
                    .or().haveNameMatching(".*Service")
                    .should().resideInAPackage("..service..");
    /**
     * Controllers should not have 'Gui' in their name.
     */
    @ArchTest
    static ArchRule controllers_should_not_have_Gui_in_name =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().haveSimpleNameNotContaining("Gui");

    /**
     * Controllers should have names ending with 'Controller'.
     */
    @ArchTest
    static ArchRule controllers_should_be_suffixed =
            classes()
                    .that().resideInAPackage("..controller..")
                    .or().areAnnotatedWith(Controller.class)
                    .should().haveSimpleNameEndingWith("Controller");

    /**
     * Classes named 'Controller' should reside in a controller package.
     */
    @ArchTest
    static ArchRule classes_named_controller_should_be_in_a_controller_package =
            classes()
                    .that().haveSimpleNameContaining("Controller")
                    .should().resideInAPackage("..controller..");

}