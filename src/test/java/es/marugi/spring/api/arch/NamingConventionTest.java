package es.marugi.spring.api.arch;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
    packages = "es.marugi.spring.api",
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class,
        ExcludeGeneratedCode.class
    }
)
public class NamingConventionTest {


}

class ExcludeGeneratedCode implements ImportOption {
    @Override
    public boolean includes(Location location) {
        return !location.toString().contains("generated");
    }
}
