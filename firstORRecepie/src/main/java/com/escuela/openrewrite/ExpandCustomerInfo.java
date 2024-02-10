package com.escuela.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.J.Modifier.Type;

import java.util.stream.Collectors;

public class ExpandCustomerInfo extends Recipe {

    @NonNull
    String fullyQualifiedClassName;

    @JsonCreator
    public ExpandCustomerInfo(@NonNull @JsonProperty("fullyQualifiedClassName") String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    @Override
    public String getDisplayName() {
        return "Expand Customer Info";
    }

    @Override
    public String getDescription() {
        return "Expand the `CustomerInfo` class with new fields.";
    }

    // OpenRewrite provides a managed environment in which it discovers, instantiates, and wires configuration into Recipes.
    // This recipe has no configuration and delegates to its visitor when it is run.
    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            // Used to identify the method declaration that will be refactored
            private final MethodMatcher methodMatcher = new MethodMatcher(fullyQualifiedClassName+" setCustomerInfo(String)");
//            private final MethodMatcher methodMatcher = new MethodMatcher("com.escuela.openrewrite.Customer setCustomerInfo(String)");


            // Template used to insert two additional parameters into the "setCustomerInfo()" method declaration
            private final JavaTemplate addMethodParametersTemplate = JavaTemplate.builder("Date dateOfBirth, String firstName, #{}")
                    .imports("java.util.Date")
                    .contextSensitive()
                    .build();

            // Template used to add a method body to the "setCustomerInfo()" method declaration
            private final JavaTemplate addMethodBodyTemplate = JavaTemplate.builder(" ").build();

            // Template used to add statements to the method body of the "setCustomerInfo()" method
            private final JavaTemplate addStatementsTemplate = JavaTemplate.builder(
                            "this.dateOfBirth = dateOfBirth;\n" +
                                    "this.firstName = firstName;\n" +
                                    "this.lastName = lastName;\n")
                    .contextSensitive()
                    .build();

            @Override
            public MethodDeclaration visitMethodDeclaration(MethodDeclaration methodDeclaration, ExecutionContext executionContext) {
                if (!methodMatcher.matches(methodDeclaration.getMethodType())) {
                    return methodDeclaration;
                }

                // Remove the abstract modifier from the method
                methodDeclaration = methodDeclaration.withModifiers(methodDeclaration.getModifiers().stream()
                        .filter(modifier -> modifier.getType() != Type.Abstract)
                        .collect(Collectors.toList()));

                // Add two parameters to the method declaration by inserting them in front of the first argument
                methodDeclaration =
                        addMethodParametersTemplate.apply(updateCursor(methodDeclaration),
                                methodDeclaration.getCoordinates().replaceParameters(),
                                methodDeclaration.getParameters().get(0));

                // Add a method body and format it
                methodDeclaration = maybeAutoFormat(
                        methodDeclaration, addMethodBodyTemplate.apply(updateCursor(methodDeclaration), methodDeclaration.getCoordinates().replaceBody()),
                        executionContext
                );

                // Safe to assert since we just added a body to the method
                assert methodDeclaration.getBody() != null;

                // Add the assignment statements to the "setCustomerInfo()" method body
                methodDeclaration = addStatementsTemplate.apply(updateCursor(methodDeclaration), methodDeclaration.getBody().getCoordinates().lastStatement());

                return methodDeclaration;
            }
        };
    }
}