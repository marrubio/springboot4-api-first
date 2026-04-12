package es.marugi.spring.api.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(
        packages = "es.marugi.spring.api",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class OnionArchitectureTest {

    @ArchTest
    static final ArchRule onion_architecture_is_respected = onionArchitecture()
            //.domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application.service..")
            .adapter("cli", "..adapter.cli..")
            .adapter("persistence", "..adapter.out.persistence..")
            .adapter("rest", "..adapter.in.rest..")
            .allowEmptyShould(true)
           ;

}