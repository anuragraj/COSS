/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.coss.controller;

import com.compomics.coss.model.ComparisonResult;
import com.compomics.coss.model.MatchedLibSpectra;
import com.compomics.ms2io.Spectrum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.compomics.coss.model.ConfigData;

/**
 *
 * @author Genet
 */
public class ImportExport {

    List<ComparisonResult> result;
    ConfigData configData;

    public ImportExport(List<ComparisonResult> result, ConfigData configData) {
        this.result = result;
        this.configData = configData;

    }

    /**
     * saves the result to user selected file
     *
     * @param type refers how result be saved, as excel import or coss object
     * that can be displayed later type=0 for coss object type=1 to save result
     * in excel format
     * @throws java.io.IOException
     */
    public void saveResult(int type) throws IOException {

        if (result != null) {

            String parent = configData.getExperimentalSpecFile().getParent();
            JFileChooser fileChooser = new JFileChooser(parent);
            fileChooser.setDialogTitle("Specify a file to save");

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File filename = fileChooser.getSelectedFile();
                String path = filename.getParent();
                String fullName = filename.getName();
                String fname = "";
                if (fullName.contains(".")) {
                    fname = filename.getName().substring(0, filename.getName().indexOf("."));
                } else {
                    fname = fullName;
                }

//                if (filename.exists()) {
//                    int optionResult = JOptionPane.showConfirmDialog(mainView, "Are you sure, you wamt to overite the existing file?");
//                    if (optionResult == JOptionPane.YES_OPTION) {
//                        saveOpt(type, path, fname);
//                    } else if (optionResult == JOptionPane.NO_OPTION) {
//                        saveResult(type);
//                    }
//                } else {
                saveOpt(type, path, fname);
//                }
            }

        } else {
//            Log.info("No comparison result.");
        }

    }

    /**
     * save result of command line search
     * @param type
     * @throws IOException 
     */
    public void saveResult_CL(int type) throws IOException {

        if (result != null) {
            String fullName = configData.getExperimentalSpecFile().getName();
            String fname = "";
            if (fullName.contains(".")) {
                fname = fullName.substring(0, fullName.indexOf("."));
            } else {
                fname = fullName;
            }
            String path = configData.getExperimentalSpecFile().getParent();
            saveOpt(type, path, fname);
        }

    }

    private void saveOpt(int type, String path, String fname) throws IOException {
        switch (type) {
            case 0:
                saveResultObject(path + "\\" + fname + ".cos");
                break;
            case 1:
                saveResultExcel(path + "\\" + fname);
                break;
            case 2: // save as csv text file
                saveAsText(path + "\\" + fname, ",", ".csv");
                break;
            case 3:// save as tab delimited text file
                saveAsText(path + "\\" + fname, "\t", ".txt");
                break;
            default:
                saveResultExcel(path + "\\" + fname);
                break;
        }

    }

    private void saveAsMzIdntml() {
        // uk.ac.ebi.jmzidml.
    }

    /**
     * Save results as COSS readable result objects
     *
     * @param filename
     */
    private void saveResultObject(String filename) throws IOException {
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(result);
            oos.close();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } finally {
            fos.close();
        }
    }

    /**
     * Save results as excel file
     */
    private void saveResultExcel(String filename) {

        FileOutputStream fileOut = null;
        try {

            String[] columns = {"Title", "Library", "Scan No.", "Sequence", "Prec. Mass", "Charge", "Score", "Validation(FDR)", "#filteredQueryPeaks", "#filteredLibraryPeaks", "SumIntQuery", "SumIntLib", "#MatchedPeaks", "MatchedIntQuery", "MatchedIntLib"};
            //List<Employee> employees =  new ArrayList<>();

            // Create a Workbook
            Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
            /* CreationHelper helps us create instances of various things like DataFormat,
            Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
            CreationHelper createHelper = workbook.getCreationHelper();
            // Create a Sheet
            Sheet sheet = workbook.createSheet("Coss_Result");
            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(false);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            // Create a Row
            Row headerRow = sheet.createRow(0);
            // Create cells
            int len = columns.length;
            for (int i = 0; i < len; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }   // Create Cell Style for formatting Date
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            // Create Other rows and cells with employees data
            int rowNum = 1;
            Spectrum spec;

            for (ComparisonResult res : result) {
                List<MatchedLibSpectra> mSpec = res.getMatchedLibSpec();
                // int lenMspec = mSpec.size();
                //for (int s = 0; s < lenMspec; s++) {
                int s = 0;
                Row row = sheet.createRow(rowNum);
                spec = res.getEspSpectrum();
                row.createCell(0).setCellValue(spec.getTitle());
                row.createCell(1).setCellValue(mSpec.get(s).getSource());
                row.createCell(2).setCellValue(spec.getScanNumber());
                row.createCell(3).setCellValue(mSpec.get(s).getSequence());
                row.createCell(4).setCellValue(spec.getPCMass());
                row.createCell(5).setCellValue(spec.getCharge());
                row.createCell(6).setCellValue(res.getTopScore());
                if (configData.isDecoyAvailable()) {
                    row.createCell(7).setCellValue(res.getFDR());

                } else {
                    row.createCell(7).setCellValue("NA");
                }
                row.createCell(8).setCellValue(mSpec.get(s).getTotalFilteredNumPeaks_Exp());
                row.createCell(9).setCellValue(mSpec.get(s).getTotalFilteredNumPeaks_Lib());
                row.createCell(10).setCellValue(mSpec.get(s).getSumFilteredIntensity_Exp());
                row.createCell(11).setCellValue(mSpec.get(s).getSumFilteredIntensity_Lib());
                row.createCell(12).setCellValue(mSpec.get(s).getNumMatchedPeaks());
                row.createCell(13).setCellValue(mSpec.get(s).getSumMatchedInt_Exp());
                row.createCell(14).setCellValue(mSpec.get(s).getSumMatchedInt_Lib());
                rowNum++;

                //}
            }   // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }   // Write the output to a file

            fileOut = new FileOutputStream(filename + ".xlsx");
            workbook.write(fileOut);
            //fileOut.close();
            // Closing the workbook
            workbook.close();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } finally {
            try {
                fileOut.close();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAsText(String filename, String delimiter, String extsn) throws IOException {
        Spectrum spec;

        String[] columns = {"Title", "Library", "Scan No.", "Sequence", "Prec. Mass", "Charge", "Score", "Validation(FDR)", "#filteredQueryPeaks", "#filteredLibraryPeaks", "SumIntQuery", "SumIntLib", "#MatchedPeaks", "MatchedIntQuery", "MatchedIntLib"};
        FileWriter fileOut = new FileWriter(filename + extsn);

        String delm=delimiter;
        //writing the column name
        fileOut.write(Arrays.asList(columns).stream().collect(Collectors.joining(delm)));
        fileOut.write("\n");

        
        for (ComparisonResult res : result) {
            List<MatchedLibSpectra> mSpec = res.getMatchedLibSpec();
            // int lenMspec = mSpec.size();
            //for (int s = 0; s < lenMspec; s++) {
            int s = 0;
            
            spec = res.getEspSpectrum();
            fileOut.write(spec.getTitle() + delm);
        
            fileOut.write(Integer.toString(mSpec.get(s).getSource()) + delm);
         
            fileOut.write(spec.getScanNumber() + delm);
          
            fileOut.write(mSpec.get(s).getSequence());
         
            fileOut.write(Double.toString(spec.getPCMass()) + delm);
           
            fileOut.write(spec.getCharge()+ delm);
           
            fileOut.write(Double.toString(res.getTopScore()) + delm);
          
            if (configData.isDecoyAvailable()) {
                fileOut.write(Double.toString(res.getFDR()) + delm);
              

            } else {
                fileOut.write("NA" + delm);
             
            }
            fileOut.write(Integer.toString(mSpec.get(s).getTotalFilteredNumPeaks_Exp()) + delm);
           
            fileOut.write(Integer.toString(mSpec.get(s).getTotalFilteredNumPeaks_Lib()) + delm);
           
            fileOut.write(Double.toString(mSpec.get(s).getSumFilteredIntensity_Exp()) + delm);
            
            fileOut.write(Double.toString(mSpec.get(s).getSumFilteredIntensity_Lib()) + delm);
           
            fileOut.write(Integer.toString(mSpec.get(s).getNumMatchedPeaks()) + delm);
           
            fileOut.write(Double.toString(mSpec.get(s).getSumMatchedInt_Exp()) +delm);
           
            fileOut.write(Double.toString(mSpec.get(s).getSumMatchedInt_Lib()) + delm);
            fileOut.write("\n");

        }
        fileOut.flush();
        fileOut.close();

    }

 

    /**
     * Import previously generated COSS result
     */
    public void importResult() {
        String parent = "D:\\";
        if (configData.getExperimentalSpecFile() != null) {
            parent = configData.getExperimentalSpecFile().getParent();
        }
        JFileChooser chooser = new JFileChooser(parent);
        chooser.setDialogTitle("Choose COSS result file");
        chooser.setFileFilter(new FileNameExtensionFilter("COSS Result", "cos"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        int selected = chooser.showOpenDialog(null);
        if (selected == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().toString();
            FileInputStream fis = null;
            ObjectInputStream ois = null;
            try {
                fis = new FileInputStream(fileName);
                ois = new ObjectInputStream(fis);
                result = null;
                result = (List<ComparisonResult>) ois.readObject();

            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } finally {
                try {
                    ois.close();
                    fis.close();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(MainFrameController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }

        }
    }

    
  

 

}
