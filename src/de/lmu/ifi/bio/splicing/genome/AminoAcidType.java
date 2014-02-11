package de.lmu.ifi.bio.splicing.genome;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class AminoAcidType
{
    public char oneLetter;
    public String threeLetter;
    public String name;
    public String label;

    String residueFormula;
    String[] sideChainAtoms; 

    //Ref: D.R.Lide Handbook of Chemistry and Physics



    public double pKa;// - log dissociation constant for the -COOH group
    public double pKb; // - log dissociation constant for -NH3+ group
    public double pKk; // - log dissocation constant for any other gorup
    public double pI; // pH for the isolectric pout /

        
    public static AminoAcidType NTERM=null;
    public static AminoAcidType CTERM=null;


    Vector<String> codons = new Vector<String>();

    public  AminoAcidType()
    {

    }
    private AminoAcidType(String threeLetter, char oneLetter)
    {
        this.threeLetter=threeLetter; this.oneLetter=oneLetter;
    }
    //classifications hydrophob small etc, 
    enum AA_PROPERTY
    {
        /** http://www.russelllab.org/aas/ */

        HIDROPHOBIC("Hydrophobic amino acids are those with side-chains that do not like to reside in an aqueous (i.e. water) environment. For this reason, one generally finds these amino acids buried within the hydrophobic core of the protein, or within the lipid portion of the membrane"),
        POLAR("Polar amino acids are those with side-chains that prefer to reside in an aqueous (i.e. water) environment. For this reason, one generally finds these amino acids exposed on the surface of a protein."),
        SMALL("tiny or small"),
        ALIPHATIC("Strictly speaking, aliphatic implies that the protein side chain contains only carbon or hydrogen atoms. However, it is convenient to consider Methionine in this category. Although its side-chain contains a sulphur atom, it is largely non-reactive, meaning that Methionine effectively susbsitutes well with the true aliphatic amino acaids."),
        CHARGED("It is false to presume that Histidine is always protonated at typical pHs. The side chain has a pKa of approximately 6.5, which means that only about 10% of of the species will be protonated. Of course, the precise pKa of an amino acid depends on the local environment."),
        AROMATIC("A side chain is aromatic when it contains an aromatic ring system. The strict definition has to do with the number of electrons contained within the ring. Generally, aromatic ring systems are planar, and electons are shared over the whole ring structure."),
        NEGATIVE("Amino acids that are usually negative (i.e. de-protonated) at physiological pH:"),
        POSITIVE("Amino acids that are usually positive (i.e. protonated) at physiological pH:"),
        CBRANCH("Whereas most amino acids contain only one non-hydrogen substituent attached to their C-beta carbon, C-beta branched amino acids contain two (two carbons in Valine or Isoleucine; one carbon and one oxygen in Theronine) . This means that there is a lot more bulkiness near to the protein backbone, and thus means that these amino acids are more restricted in the conformations the main-chain can adopt. Perhaps the most pronounced effect of this is that it is more difficult for these amino acids to adopt an alpha-helical conformation, though it is easy and even preferred for them to lie within beta-sheets.")
        ;
        String desc;
        AA_PROPERTY(String d)
        {
            this.desc=d;
        }
    }


    
    public static Vector<AminoAcidType> STANDARD_AAS = new Vector<AminoAcidType>();

    public static Vector<AminoAcidType> MODIFIED_AAS = new Vector<AminoAcidType>();
    
    static final int LOOKUPSIZE='Z'-'A'+1;

    static AminoAcidType[] oneLetterLookup = new AminoAcidType[LOOKUPSIZE];
    public static HashMap<String, AminoAcidType> threeLetterLookup = new HashMap<String, AminoAcidType>();

    static final int CODON_LOOKUPSIZE='Z'+1;
    static char[][][] codonLookup = new char[CODON_LOOKUPSIZE][CODON_LOOKUPSIZE][CODON_LOOKUPSIZE];
      
    static TreeMap<String,Character> code = new TreeMap<String, Character>();



        
    public static AminoAcidType get(char oneLetter)
    {
        if (oneLetter<'A' || oneLetter>'Z')
        {
            throw new RuntimeException("unknown amino acid type: "+oneLetter);
        }
        return oneLetterLookup[oneLetter-'A'];
    }


    static
    {

        STANDARD_AAS.add(new AminoAcidType("ALA", 'A'));
        STANDARD_AAS.add(new AminoAcidType("ARG", 'R'));
          
        STANDARD_AAS.add(new AminoAcidType("ASN", 'N'));
        STANDARD_AAS.add(new AminoAcidType("ASP", 'D'));
        STANDARD_AAS.add(new AminoAcidType("CYS", 'C'));
        STANDARD_AAS.add(new AminoAcidType("GLU", 'E'));
        STANDARD_AAS.add(new AminoAcidType("GLN", 'Q'));
        STANDARD_AAS.add(new AminoAcidType("GLY", 'G'));
        STANDARD_AAS.add(new AminoAcidType("HIS", 'H'));
        
        //MODIFIED_AAS.add(new AminoAcidType("HYP", 'O"), );
        STANDARD_AAS.add(new AminoAcidType("ILE", 'I'));
        STANDARD_AAS.add(new AminoAcidType("LEU", 'L'));
        STANDARD_AAS.add(new AminoAcidType("LYS", 'K'));
        STANDARD_AAS.add(new AminoAcidType("MET", 'M'));
        STANDARD_AAS.add(new AminoAcidType("PHE", 'F'));
        STANDARD_AAS.add(new AminoAcidType("PRO", 'P'));
        //modifierd.add(new AminoAcidType("GLP", 'u'));
        STANDARD_AAS.add(new AminoAcidType("SER", 'S'));
        STANDARD_AAS.add(new AminoAcidType("THR", 'T'));
        STANDARD_AAS.add(new AminoAcidType("VAL", 'V'));
        STANDARD_AAS.add(new AminoAcidType("TYR", 'Y'));
       // STANDARD_AAS.add(new AminoAcidType("TRY", 'W'));
        //ADDED
        STANDARD_AAS.add(new AminoAcidType("TRP", 'W'));

        for (AminoAcidType aat: STANDARD_AAS)
        {
            oneLetterLookup[aat.oneLetter-'A']=aat;
            threeLetterLookup.put(aat.threeLetter, aat);
        }


        
        code.put("GCT", 'A');
        code.put("GCC", 'A');
        code.put("GCA", 'A');
        code.put("GCG", 'A');
        code.put("TTA", 'L');
        code.put("TTG", 'L');
        code.put("CTT", 'L');
        code.put("CTC", 'L');
        code.put("CTA", 'L');
        code.put("CTG", 'L');
        code.put("CGT", 'R');
        code.put("CGC", 'R');
        code.put("CGA", 'R');
        code.put("CGG", 'R');
        code.put("AGA", 'R');
        code.put("AGG", 'R');
        code.put("AAA", 'K');
        code.put("AAG", 'K');
        code.put("AAT", 'N');
        code.put("AAC", 'N');
        code.put("ATG", 'M');
        code.put("GAT", 'D');
        code.put("GAC", 'D');
        code.put("TTT", 'F');
        code.put("TTC", 'F');
        code.put("TGT", 'C');
        code.put("TGC", 'C');
        code.put("CCT", 'P');
        code.put("CCC", 'P');
        code.put("CCA", 'P');
        code.put("CCG", 'P');
        code.put("CAA", 'Q');
        code.put("CAG", 'Q');
        code.put("TCT", 'S');
        code.put("TCC", 'S');
        code.put("TCA", 'S');
        code.put("TCG", 'S');
        code.put("AGT", 'S');
        code.put("AGC", 'S');
        code.put("GAA", 'E');
        code.put("GAG", 'E');
        code.put("ACT", 'T');
        code.put("ACC", 'T');
        code.put("ACA", 'T');
        code.put("ACG", 'T');
        code.put("GGT", 'G');
        code.put("GGC", 'G');
        code.put("GGA", 'G');
        code.put("GGG", 'G');
        code.put("TGG", 'W');
        code.put("CAT", 'H');
        code.put("CAC", 'H');
        code.put("TAT", 'Y');
        code.put("TAC", 'Y');
        code.put("ATT", 'I');
        code.put("ATC", 'I');
        code.put("ATA", 'I');
        code.put("GTT", 'V');
        code.put("GTC", 'V');
        code.put("GTA", 'V');
        code.put("GTG", 'V');

        code.put("TAG", '*');
        code.put("TGA", '*');
        code.put("TAA", '*');


        for (int i=0; i<CODON_LOOKUPSIZE; i++)
        {
            for (int j=0; j<CODON_LOOKUPSIZE; j++)
            {
                for (int k=0; k<CODON_LOOKUPSIZE; k++)
                {
                    codonLookup[i][j][k]='X';
                }
            }
        }
        for (Map.Entry<String, Character> e : code.entrySet())
        {
            String codon = e.getKey();
            
            int index = e.getValue()-'A';
            if (index<0 || index>=oneLetterLookup.length)
            {
                continue;
            }
            
            codonLookup[codon.charAt(0)-'A'][codon.charAt(1)-'A'][codon.charAt(2)-'A']=e.getValue();
        }
    }

        
    public static char get(String codon)
    {
        return codonLookup[codon.charAt(0)-'A'][codon.charAt(1)-'A'][codon.charAt(2)-'A'];
    }

    public static char get(char[] array, int startpos)
    {
        return codonLookup[array[startpos]-'A'][array[startpos+1]-'A'][array[startpos+2]-'A'];
    }

    @Override
    public String toString(){
    	return this.oneLetter+"";
    }
   
}
