package de.lmu.ifi.bio.splicing.superimpose;

/**
 *
 * @author harrert
 */
public class EventDetect {

    public static String[] events(String iso1, String iso2) {
        StringBuilder as1 = new StringBuilder();
        for (int i = 0; i < Math.min(iso1.length(), iso2.length()); i++) {
            if (iso1.charAt(i) == iso2.charAt(i)) {
                as1.append('*');
            } else if (iso2.charAt(i) == ' ') {
                as1.append('D');
            } else if (iso1.charAt(i) == ' ') {
                as1.append('I');
            }
        }
        String as_1 = as1.toString();
        StringBuilder dna1 = new StringBuilder();
        StringBuilder as2 = new StringBuilder();
        StringBuilder dna2 = new StringBuilder();
        for (int i = 0; i < as1.length(); i++) {
            String R = "";
            String D = "";
            dna1.append(as_1.charAt(i)).append(as_1.charAt(i)).append(as_1.charAt(i));
            if (as_1.charAt(i) == '*') {
                as2.append('*');
                dna2.append('*').append('*').append('*');
            } else if (as_1.charAt(i) == 'D') {
                while (as_1.charAt(i) == 'D') {
                    i++;
                    R += "R";
                    D += "D";
                }
                if (as_1.charAt(i) == '*') {
                    as2.append(D);
                    dna2.append(D).append(D).append(D);
                    while (as_1.charAt(i) == '*') {
                        as2.append(' ');
                        dna2.append(' ').append(' ').append(' ');
                    }
                } else {
                    as2.append(R);
                    dna2.append(R).append(R).append(R);
                    while (as_1.charAt(i) == 'I') {
                        as2.append(' ');
                        dna2.append(' ').append(' ').append(' ');
                    }
                }
            } else {
                while (as_1.charAt(i) == 'D') {
                    i++;
                    R += "R";
                    D += "I";
                }
                if (as_1.charAt(i) == '*') {
                    as2.append(D);
                    dna2.append(D).append(D).append(D);
                    while (as_1.charAt(i) == '*') {
                        as2.append(' ');
                        dna2.append(' ').append(' ').append(' ');
                    }
                } else {
                    as2.append(R);
                    dna2.append(R).append(R).append(R);
                    while (as_1.charAt(i) == 'D') {
                        as2.append(' ');
                        dna2.append(' ').append(' ').append(' ');
                    }
                }
            }
        }
        return new String[]{as_1, dna1.toString(), as2.toString(), dna2.toString()};
    }
}
