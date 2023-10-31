package com.escuela.openrewrite;

import org.junit.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class SayHelloRecipeTest implements RewriteTest {


    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SayHelloRecipe("com.escuela.openrewrite.FooBar"));
    }

    @Test
    public void addsHelloToFooBar() {
        rewriteRun(
                java(
                        """
                            package com.escuela.openrewrite;

                            class FooBar {
                            }
                        """,
                        """
                            package com.escuela.openrewrite;

                            class FooBar {
                                public String hello() {
                                    return "Hello from com.yourorg.FooBar!";
                                }
                            }
                        """
                )
        );
    }


    @Test
    public void doesNotChangeOtherClasses() {
        rewriteRun(
                java(
                        """
                            package com.escuela.openrewrite;
                
                            class Bash {
                            }
                        """
                )
        );
    }

    @Test
    public void doesNotChangeExistingHello() {
        rewriteRun(
                java(
                        """
                            package com.escuela.openrewrite;
                
                            class FooBar {
                                public String hello() { return ""; }
                            }
                        """
                )
        );
    }

}
