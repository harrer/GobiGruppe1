package de.lmu.ifi.bio.splicing.homology;

/**
 *
 * @author tobias
 */
public class AlignmentMax {
    
    private int i,j;
    private int max;
    private final String mode;
    
    public AlignmentMax(int i, int j, int max, String mode){
        this.i = i;
        this.j = j;
        this.max = max;
        this.mode = mode;
    }
    
    public void reset(){
        this.i = 0;
        this.j = 0;
        this.max = Integer.MIN_VALUE;
    }
    
    public int[] getMax(){
        return new int[]{i,j,max};
    }
    
    public void setMax(int i, int j, int max){
        this.i = i;
        this.j = j;
        this.max = max;
    }
}