package de.lmu.ifi.bio.splicing.util;

/**
 * Created by uhligc on 2/15/14.
 */
public class GenomicUtils {
    public static String convertToStrandPlus(String seq) {
        char[] chars = seq.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (char c : chars) {
            switch (c) {
                case 'A':
                    sb.append('T');
                    break;
                case 'T':
                    sb.append('A');
                    break;
                case 'C':
                    sb.append('G');
                    break;
                case 'G':
                    sb.append('C');
                    break;
                default:
                    break;
            }
        }

        return sb.toString();
    }

    public static String cleanString(String seq) {
        char[] chars = seq.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c == 'A' || c == 'C' || c == 'T' || c == 'G') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
