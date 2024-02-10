package com.escuela.openrewrite;

import org.openrewrite.Recipe;
import org.openrewrite.java.tree.J;

public class StaticRecipe extends Recipe {


    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }


//    J.ClassDeclaration cd = super.visitClassDeclaration(classDeclaration, executionContext);
//
//            if (cd.getKind() != J.ClassDeclaration.Kind.Type.Class || cd.hasModifier(J.Modifier.Type.Abstract)
//            || cd.hasModifier(J.Modifier.Type.Final) || cd.getType() == null) {
//        return cd;
//    }



}



