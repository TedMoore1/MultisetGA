package org.evosuite.ga.multisetga;

import java.util.ArrayList;
import java.util.List;

public class DTraceGenerator {
    public static String getTraceContent(List<Vector> data) {
        // Generate variables.
        if (data.isEmpty()) {
            throw new RuntimeException("Data to generate dtrace is empty. This should never happen");
        }

        Vector firstVector = data.iterator().next();
        ArrayList<String> variableNames = new ArrayList<>();
        for (int i = 0; i < firstVector.getData().length; i++) {
            variableNames.add("v" + i);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("decl-version 2.0\n");
        sb.append("var-comparability none\n\n");
        sb.append("ppt Faker.fakemethod(int");
        // At least one int required.
        for (int k = 0; k < variableNames.size() - 1; k++) {
            sb.append(",\\_int");
        }
        sb.append("):::DATAGEN\n");
        sb.append("ppt-type enter\n");

        // For now only int variables are supported
        for (String variableName : variableNames) {
            sb.append("variable ");
            sb.append(variableName);
            sb.append("\n");
            sb.append("  var-kind variable\n");
            sb.append("  dec-type int\n");
            sb.append("  rep-type int\n");
            sb.append("  flags is_param\n");
            sb.append("  comparability 22\n");
        }
        sb.append("\n");

        for (Vector v : data) {
            sb.append("Faker.fakemethod(int");
            for (int _k = 0; _k < variableNames.size() - 1; _k++) {
                sb.append(",\\_int");
            }
            sb.append("):::DATAGEN\n");
            sb.append("this_invocation_nonce\n");
            // For every variable, insert the value,
            // start with and end with 1, like: 1\nvarname\nvalue
            int[] dat = v.getData();
            int currentIndex = 0;
            for (String _k : variableNames) {
                sb.append("1\n");
                sb.append(_k);
                sb.append("\n");
                sb.append(dat[currentIndex]);
                sb.append("\n");
                currentIndex++;
            }
            sb.append("1\n");
            sb.append("\n");
        }

        return sb.toString();
    }
}