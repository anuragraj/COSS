package com.compomics.main;

//import com.compomics.coss.controller.decoyGeneration.FragmentIon;
import com.compomics.coss.controller.MainFrameController;
import com.compomics.coss.controller.decoyGeneration.FragmentIon;
import com.compomics.coss.controller.decoyGeneration.GenerateDecoyLib;
//import com.compomics.coss.controller.decoyGeneration.MergeFiles;
import com.compomics.coss.controller.decoyGeneration.ReverseSequence;
import org.apache.log4j.Logger;
import java.util.logging.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

/**
 *
 * @author Genet
 */
public class TempClass {
    private static final Logger LOG = Logger.getLogger( MainFrameController.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            File file_lib=new File("C:/human_hcd/test_lib.msp");
            //File file_decoy=new File("C:/human_hcd_itraq/human_hcd_selected.msp_decoy.msp");
//
//            System.out.println("Appending decoy to library");
//            MergeFiles m = new MergeFiles(file_lib, file_decoy);
//            m.Merge();
//            System.out.println("\nDecoy spectra appended to file " + file_lib.getName());

//            System.out.println("getFragmentIon");
            String aa_sequence = "AAAAAAAVSGNNASDEPSR"; //unmodified
//            
//            String aa_sequence = "AAAAQDEITGDGTTTVVCLVGELLR";   //modified CAM at 17th aa
//            
            Map modifications = new HashMap<Integer, List<String>>();
            List mods = new ArrayList<String>();
//            `
//            //only for modified peptide
//            //1(17,C,CAM)
//            mods.add("CAM");
//            modifications.put(17, mods);
//
//
//
//            System.out.println(aa_sequence);
//            FragmentIon instance = new FragmentIon(aa_sequence, modifications);
////
//            Map result = instance.getFragmentIon();
//            for (int i = 0; i < result.size() / 3; i++) {
//                System.out.println(result.get("a" + Integer.toString(i+1)) + "    ");
//                System.out.println(result.get("b" + Integer.toString(i+1)) + "    ");
//                System.out.println(result.get("y" + Integer.toString(i+1)) + "    ");
//                System.out.println("\n");
//            }

            
            GenerateDecoyLib gen = new ReverseSequence(file_lib, LOG );
            gen.generate();




        } catch (Exception ex) {//InterruptedException ex) {
            System.out.println(ex.toString());
        }
    }

}
