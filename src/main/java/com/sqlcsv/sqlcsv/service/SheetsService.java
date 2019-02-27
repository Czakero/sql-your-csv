package com.sqlcsv.sqlcsv.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.sqlcsv.sqlcsv.google.GoogleAuthorizationFlow;
import com.sqlcsv.sqlcsv.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SheetsService implements ISheetsService {
    private SheetsParser sheetsParser;

    @Autowired
    public SheetsService(SheetsParser sheetsParser) {
        this.sheetsParser = sheetsParser;
    }

    public Table getSheetFromSpreadsheet(String spreadsheetId, String sheetName, String userId) throws IOException, GeneralSecurityException {
        Sheets sheetsService = GoogleAuthorizationFlow.getSheetsService(userId);
        ValueRange queryOutput = sheetsService.spreadsheets().values().get(spreadsheetId, sheetName).execute();
        List<String[]> data = queryOutput
                .getValues()
                .stream()
                .map(list -> list
                        .stream()
                        .map(element -> (String) element)
                        .toArray(String[]::new))
                .collect(Collectors.toList());
        return sheetsParser.getTableFromSheetValues(sheetName, data);
    }

    public List<String> getSheetsNamesFromSpreadsheet(String spreadsheetId, String userId) throws IOException, GeneralSecurityException {
        Sheets sheetsService = GoogleAuthorizationFlow.getSheetsService(userId);
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        List<Sheet> sheetsInSpreadsheet = spreadsheet.getSheets();
        return sheetsInSpreadsheet.stream().map(sheet -> sheet.getProperties().getTitle()).collect(Collectors.toList());
    }
}
